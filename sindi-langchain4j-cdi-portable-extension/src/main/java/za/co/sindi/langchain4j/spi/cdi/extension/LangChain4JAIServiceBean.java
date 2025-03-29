package za.co.sindi.langchain4j.spi.cdi.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.moderation.ModerationModel;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.enterprise.inject.spi.InterceptionFactory;
import jakarta.enterprise.inject.spi.PassivationCapable;
import jakarta.enterprise.util.AnnotationLiteral;
import za.co.sindi.commons.utils.Strings;
import za.co.sindi.langchain4j.spi.cdi.AiService;

/**
 * @author Buhake Sindi
 * @since 21 November 2024
 */
public class LangChain4JAIServiceBean<T> implements Bean<T>, PassivationCapable {

    private final Class<T> aiServiceInterfaceClass;

    private final BeanManager beanManager;

    private final Class<? extends Annotation> scope;

    private Set<Annotation> interceptorBindings;

    /**
     * @param aiServiceInterfaceClass
     * @param beanManager
     */
    public LangChain4JAIServiceBean(Class<T> aiServiceInterfaceClass, BeanManager beanManager) {
        super();
        final AiService annotation = (this.aiServiceInterfaceClass = aiServiceInterfaceClass)
                .getAnnotation(AiService.class);
        this.scope = annotation.scope();
        this.beanManager = beanManager;
    }

    /*
     * (non-Javadoc)
     *
     * @see jakarta.enterprise.inject.spi.PassivationCapable#getId()
     */
    @Override
    public String getId() {
        return aiServiceInterfaceClass.getName();
    }

    /*
     * (non-Javadoc)
     *
     * @see jakarta.enterprise.context.spi.Contextual#create(jakarta.enterprise.context.spi.CreationalContext)
     */
    @Override
    public T create(CreationalContext<T> creationalContext) {
        T instance = createAiServices();
        if (!getInterceptorBindings().isEmpty()) {
            InterceptionFactory<T> factory = beanManager.createInterceptionFactory(creationalContext, aiServiceInterfaceClass);
            interceptorBindings.stream().forEach(factory.configure()::add);
            instance = factory.createInterceptedInstance(instance);
        }

        return instance;
    }

    /*
     * (non-Javadoc)
     *
     * @see jakarta.enterprise.context.spi.Contextual#destroy(java.lang.Object,
     * jakarta.enterprise.context.spi.CreationalContext)
     */
    @Override
    public void destroy(T instance, CreationalContext<T> creationalContext) {
    }

    /*
     * (non-Javadoc)
     *
     * @see jakarta.enterprise.inject.spi.BeanAttributes#getTypes()
     */
    @Override
    public Set<Type> getTypes() {
        return Collections.singleton(aiServiceInterfaceClass);
    }

    /*
     * (non-Javadoc)
     *
     * @see jakarta.enterprise.inject.spi.BeanAttributes#getQualifiers()
     */
    @Override
    public Set<Annotation> getQualifiers() {
        Set<Annotation> annotations = new HashSet<>();
        annotations.add(new AnnotationLiteral<Default>() {
        });
        annotations.add(new AnnotationLiteral<Any>() {
        });
        return Collections.unmodifiableSet(annotations);
    }

    /*
     * (non-Javadoc)
     *
     * @see jakarta.enterprise.inject.spi.BeanAttributes#getScope()
     */
    @Override
    public Class<? extends Annotation> getScope() {
        return scope;
    }

    /*
     * (non-Javadoc)
     *
     * @see jakarta.enterprise.inject.spi.BeanAttributes#getName()
     */
    @Override
    public String getName() {
        return Strings.uncapitalize(aiServiceInterfaceClass.getSimpleName()) + "ServiceProxy";
    }

    /*
     * (non-Javadoc)
     *
     * @see jakarta.enterprise.inject.spi.BeanAttributes#getStereotypes()
     */
    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.singleton(AiService.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see jakarta.enterprise.inject.spi.BeanAttributes#isAlternative()
     */
    @Override
    public boolean isAlternative() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see jakarta.enterprise.inject.spi.Bean#getBeanClass()
     */
    @Override
    public Class<?> getBeanClass() {
        return aiServiceInterfaceClass;
    }

    /*
     * (non-Javadoc)
     *
     * @see jakarta.enterprise.inject.spi.Bean#getInjectionPoints()
     */
    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    /**
     * @return the interceptorBindings
     */
    public Set<Annotation> getInterceptorBindings() {
        if (interceptorBindings == null)
            interceptorBindings = new HashSet<>();
        return interceptorBindings;
    }

    @Override
    public String toString() {
        return "AiService [ interfaceType: " + aiServiceInterfaceClass.getSimpleName() + " ] with Qualifiers ["
                + getQualifiers() + "]";
    }
	
	private T createAiServices() {
		final AiService aiServiceAnnotation = aiServiceInterfaceClass.getAnnotation(AiService.class); 
		ChatLanguageModel chatLanguageModel = getChatLanguageModel(aiServiceAnnotation, beanManager);
		StreamingChatLanguageModel streamingChatLanguageModel = getStreamingChatLanguageModel(aiServiceAnnotation, beanManager);
        ContentRetriever contentRetriever = getContentRetriever(aiServiceAnnotation, beanManager);
        ToolProvider toolProvider = getToolProvider(aiServiceAnnotation, beanManager);
        
        AiServices<T> aiServices = AiServices.builder(aiServiceInterfaceClass);
        if (chatLanguageModel != null) 
        	aiServices.chatLanguageModel(chatLanguageModel);
        
        if (streamingChatLanguageModel != null) 
        	aiServices.streamingChatLanguageModel(streamingChatLanguageModel);
    	
        if (toolProvider != null) {
        	aiServices.toolProvider(toolProvider);
        } else if (aiServiceAnnotation.tools() != null && aiServiceAnnotation.tools().length > 0) {
        	aiServices.tools(Stream.of(aiServiceAnnotation.tools())
                        .map(c -> {
							try {
								return c.getConstructor((Class<?>[])null).newInstance((Object[])null);
							} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
									| InvocationTargetException | NoSuchMethodException | SecurityException e) {
								// TODO Auto-generated catch block
								throw new RuntimeException(e);
							}
						} /* getBean(c, beanManager) */)
                        .collect(Collectors.toList()));
        }
        if (contentRetriever != null)
            aiServices.contentRetriever(contentRetriever);
        
        ChatMemory chatMemory = getChatMemory(aiServiceAnnotation, beanManager);
        if (chatMemory != null) {
            aiServices.chatMemory(chatMemory);
        }
        
        ChatMemoryProvider chatMemoryProvider = getChatMemoryProvider(aiServiceAnnotation, beanManager);
        if (chatMemoryProvider != null) {
            aiServices.chatMemoryProvider(chatMemoryProvider);
        }

        ModerationModel moderationModel = getModerationModel(aiServiceAnnotation, beanManager);
        if (moderationModel != null) {
            aiServices.moderationModel(moderationModel);
        }
        
        RetrievalAugmentor retrievalAugmentor = getRetrievalAugmentor(aiServiceAnnotation, beanManager);
        if (retrievalAugmentor != null) {
        	aiServices.retrievalAugmentor(retrievalAugmentor);
        }

        return aiServices.build();
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T getBean(String beanName, Class<T> beanType, BeanManager beanManager) {
		if (beanName == null || beanName.isBlank()) return null;
		
		Bean<?> bean  = "#default".equals(beanName) ? beanManager.resolve(beanManager.getBeans(beanType)) : beanManager.resolve(beanManager.getBeans(beanName));
		if (bean == null) return null;
		
		CreationalContext<?> context = beanManager.createCreationalContext(bean);
		return (T) beanManager.getReference(bean, beanType, context);
	}
	
	private static ChatLanguageModel getChatLanguageModel(AiService annotation, BeanManager beanManager) {
		return getBean(annotation.chatModel(), ChatLanguageModel.class, beanManager);
    }
	
	private static StreamingChatLanguageModel getStreamingChatLanguageModel(AiService annotation, BeanManager beanManager) {
		return getBean(annotation.streamingChatModel(), StreamingChatLanguageModel.class, beanManager);
    }
	
	private static ChatMemory getChatMemory(AiService annotation, BeanManager beanManager) {
		return getBean(annotation.chatMemory(), ChatMemory.class, beanManager);
    }

    private static ContentRetriever getContentRetriever(AiService annotation, BeanManager beanManager) {
    	return getBean(annotation.contentRetriever(), ContentRetriever.class, beanManager);
    }
    
    private static ModerationModel getModerationModel(AiService annotation, BeanManager beanManager) {
    	return getBean(annotation.moderationModel(), ModerationModel.class, beanManager);
    }
    
    private static ChatMemoryProvider getChatMemoryProvider(AiService annotation, BeanManager beanManager) {
    	return getBean(annotation.chatMemoryProvider(), ChatMemoryProvider.class, beanManager);
    }
    
    private static RetrievalAugmentor getRetrievalAugmentor(AiService annotation, BeanManager beanManager) {
    	return getBean(annotation.retrievalAugmentor(), RetrievalAugmentor.class, beanManager);
    }
    
    private static ToolProvider getToolProvider(AiService annotation, BeanManager beanManager) {
    	return getBean(annotation.toolProvider(), ToolProvider.class, beanManager);
    }
}
