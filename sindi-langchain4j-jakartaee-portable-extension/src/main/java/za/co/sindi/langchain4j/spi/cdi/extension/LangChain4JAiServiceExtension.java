package za.co.sindi.langchain4j.spi.cdi.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
	private static final Set<Class<?>> detectedAIServicesDeclaredInterfaces = new HashSet<>();

    public <T> void processAnnotatedType(@Observes @WithAnnotations(AiService.class) ProcessAnnotatedType<T> pat) {
    	LOGGER.info("Scanning type: " + pat.getAnnotatedType().getJavaClass().getName());
    	if (pat.getAnnotatedType().getJavaClass().isInterface()) {
            LOGGER.info("processAnnotatedType register " + pat.getAnnotatedType().getJavaClass().getName());
            detectedAIServicesDeclaredInterfaces.add(pat.getAnnotatedType().getJavaClass());
        } else {
            LOGGER.warning("processAnnotatedType reject " + pat.getAnnotatedType().getJavaClass().getName()
                    + " which is not an interface");
            pat.veto();
        }
     }
	
	public void processInjectionPoints(@Observes ProcessInjectionPoint<?, ?> event) {
        if (event.getInjectionPoint().getBean() == null) {
        	Class<?> rawType = Reflections.getRawType(event.getInjectionPoint().getType());
        	if (classSatisfies(rawType, AiService.class))
        		detectedAIServicesDeclaredInterfaces.add(rawType);
        }
        
        if (Instance.class.equals(Reflections.getRawType(event.getInjectionPoint().getType()))) {
        	Class<?> parameterizedType = Reflections.getRawType(getFacadeType(event.getInjectionPoint()));
        	if (classSatisfies(parameterizedType, AiService.class))
        		detectedAIServicesDeclaredInterfaces.add(parameterizedType);
        }
    }
	
	public void afterBeanDiscovery(@Observes AfterBeanDiscovery afterBeanDiscovery, BeanManager beanManager) {
		LOGGER.info("Finished the scanning process.");
//		InjectionPoint ip = componentInjectionPoints.iterator().next();
//		LOGGER.info("" + ip.getMember().getDeclaringClass() + " - " + ip.getClass() + " - " + ip.getType());
//		LOGGER.info("Raw Type: " + Reflections.getRawType(ip.getType()));
		
		for (Class<?> aiServiceClass : detectedAIServicesDeclaredInterfaces) {
            LOGGER.info("afterBeanDiscovery create synthetic :  " + aiServiceClass.getName());
            final AiService annotation = Annotations.findAnnotation(aiServiceClass, AiService.class);
            afterBeanDiscovery.addBean()
                    .types(aiServiceClass)
                    .scope(annotation.scope())
                    .name(Strings.uncapitalize(aiServiceClass.getSimpleName()) + "ServiceProxy") //Without this, the container won't create a CreationalContext
                    .createWith(creationalContext -> createAiServices(annotation, aiServiceClass, beanManager));
        }
	}
	
	private <T extends Annotation> boolean classSatisfies(Class<?> clazz, Class<T> annotationClass) {
		if (!clazz.isInterface()) return false;
		T annotation = Annotations.findAnnotation(clazz, annotationClass);
		return (annotation != null);
	}
	
	private Type getFacadeType(InjectionPoint injectionPoint) {
        Type genericType = injectionPoint.getType();
        if (genericType instanceof ParameterizedType) {
            return ((ParameterizedType) genericType).getActualTypeArguments()[0];
        }
        return null;
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
