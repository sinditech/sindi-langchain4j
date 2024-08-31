package za.co.sindi.langchain4j.spi.cdi.extension;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.sindi.AiService;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.ProcessInjectionPoint;
import jakarta.enterprise.inject.spi.WithAnnotations;
import jakarta.enterprise.inject.spi.configurator.BeanConfigurator;
import za.co.sindi.commons.utils.Annotations;
import za.co.sindi.commons.utils.Reflections;
import za.co.sindi.commons.utils.Strings;

/**
 * @author Buhake Sindi
 * @since 11 August 2024
 */
public class LangChain4JAiServiceExtension implements Extension {

	private static final Logger LOGGER = Logger.getLogger(LangChain4JAiServiceExtension.class.getName());
	private Set<AnnotatedType<?>> annotatedTypes;
	private Set<InjectionPoint> componentInjectionPoints;
    private Set<InjectionPoint> instanceInjectionPoints;

    public LangChain4JAiServiceExtension() {
    	this.annotatedTypes = new HashSet<>();
        this.componentInjectionPoints = new HashSet<>();
        this.instanceInjectionPoints = new HashSet<>();
    }
    
    public <T> void processAnnotatedType(@Observes @WithAnnotations(AiService.class) ProcessAnnotatedType<T> pat) {
    	LOGGER.info("Scanning type: " + pat.getAnnotatedType().getJavaClass().getName());
    	if (pat.getAnnotatedType().getJavaClass().isInterface()) {
            LOGGER.info("processAnnotatedType register " + pat.getAnnotatedType().getJavaClass().getName());
            annotatedTypes.add(pat.getAnnotatedType());
        } else {
            LOGGER.warning("processAnnotatedType reject " + pat.getAnnotatedType().getJavaClass().getName()
                    + " which is not an interface");
            pat.veto();
        }
    	annotatedTypes.add(pat.getAnnotatedType());
     }
	
	public void processInjectionPoints(@Observes ProcessInjectionPoint<?, ?> event) {
        if (event.getInjectionPoint().getBean() == null) {
            componentInjectionPoints.add(event.getInjectionPoint());
        }
        
        if (Instance.class.equals(Reflections.getRawType(event.getInjectionPoint().getType()))) {
            instanceInjectionPoints.add(event.getInjectionPoint());
        }
    }
	
	public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager beanManager) {
		LOGGER.info("Finished the scanning process.");
//		InjectionPoint ip = componentInjectionPoints.iterator().next();
//		LOGGER.info("" + ip.getMember().getDeclaringClass() + " - " + ip.getClass() + " - " + ip.getType());
//		LOGGER.info("Raw Type: " + Reflections.getRawType(ip.getType()));
		
		for (AnnotatedType<?> annotatedType : annotatedTypes) {
			LOGGER.info("Adding @AiService of interface '" + annotatedType.getJavaClass().getName()  + "', discovered during processAnnotatedType(), for component injection.");
			AiService aiServiceAnnotation = Annotations.findAnnotation(annotatedType.getJavaClass(), AiService.class);
			addBean(abd, beanManager, annotatedType.getJavaClass(), aiServiceAnnotation, false);
		}
		
		for (InjectionPoint ip : componentInjectionPoints) {
			Class<?> rawType = Reflections.getRawType(ip.getType());
			AiService aiServiceAnnotation = Annotations.findAnnotation(rawType, AiService.class);
			addBean(abd, beanManager, rawType, aiServiceAnnotation, false);
		}
		
		for (InjectionPoint ip : instanceInjectionPoints) {
			Class<?> rawType = Reflections.getRawType(ip.getType());
			AiService aiServiceAnnotation = Annotations.findAnnotation(rawType, AiService.class);
			addBean(abd, beanManager, rawType, aiServiceAnnotation, true);
		}
	}
	
	private void addBean(AfterBeanDiscovery abd, BeanManager beanManager, Class<?> interfaceClass, AiService aiServiceAnnotation, boolean produce) {
		if (!interfaceClass.isInterface() || aiServiceAnnotation == null) return ;
		
		BeanConfigurator<Object> bc = abd.addBean()
				.scope(aiServiceAnnotation.scope())
				.types(interfaceClass)
				.name(Strings.uncapitalize(interfaceClass.getSimpleName()) + "ServiceProxy");
		
		if (produce) {
			bc.produceWith(c -> createAiServices(aiServiceAnnotation, interfaceClass, beanManager));
		} else {
			bc.createWith(c -> createAiServices(aiServiceAnnotation, interfaceClass, beanManager));
		}
		
		LOGGER.info("Added @AiService of interface type '" + interfaceClass.getName()  + "' for " + (produce ? "instance" : "component")  + " injection.");
	}
	
	private Object createAiServices(final AiService aiServiceAnnotation, final Class<?> interfaceClass, BeanManager beanManager) {
		ChatLanguageModel chatLanguageModel = getChatLanguageModel(aiServiceAnnotation, beanManager);
        ContentRetriever contentRetriever = getContentRetriever(aiServiceAnnotation, beanManager);
		
		AiServices<?> aiServices = AiServices.builder(interfaceClass)
                .chatLanguageModel(chatLanguageModel);
      	if (aiServiceAnnotation.tools() != null && aiServiceAnnotation.tools().length > 0) {
        	aiServices.tools(Stream.of(aiServiceAnnotation.tools())
                        .map(c -> getBean(null, c, beanManager))
                        .collect(Collectors.toList()));
        }
        aiServices.chatMemory(MessageWindowChatMemory.withMaxMessages(aiServiceAnnotation.chatMemoryMaxMessages()));
        
        if (contentRetriever != null)
            aiServices.contentRetriever(contentRetriever);

        return aiServices.build();
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T getBean(String beanName, Class<T> beanType, BeanManager beanManager) {
		Bean<?> bean = beanManager.resolve(!Strings.isNullOrEmpty(beanName) ?  beanManager.getBeans(beanName) : beanManager.getBeans(beanType));
		if (bean == null) return null;
		
		CreationalContext<?> context = beanManager.createCreationalContext(bean);
		return (T) beanManager.getReference(bean, beanType, context);
	}
	
	private static ChatLanguageModel getChatLanguageModel(AiService annotation, BeanManager beanManager) {
		return getBean(annotation.chatModel(), ChatLanguageModel.class, beanManager);
    }

    private static ContentRetriever getContentRetriever(AiService annotation, BeanManager beanManager) {
    	return getBean(annotation.contentRetriever(), ContentRetriever.class, beanManager);
    }
}
