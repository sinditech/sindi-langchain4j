package za.co.sindi.langchain4j.spi.cdi.bce;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;
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
import dev.langchain4j.service.sindi.AiService;
import dev.langchain4j.service.tool.ToolProvider;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.build.compatible.spi.Parameters;
import jakarta.enterprise.inject.build.compatible.spi.SyntheticBeanCreator;
import jakarta.enterprise.inject.literal.NamedLiteral;

public class AIServiceCreator implements SyntheticBeanCreator<Object> {
    private static final Logger LOGGER = Logger.getLogger(AIServiceCreator.class.getName());

    @Override
    public Object create(Instance<Object> lookup, Parameters params) {
        Class<?> interfaceClass = params.get(LangChain4JAiServiceBuildCompatibleExtension.PARAM_INTERFACE_CLASS, Class.class);
        AiService annotation = interfaceClass.getAnnotation(AiService.class);

        ChatLanguageModel chatLanguageModel = getChatLanguageModel(lookup, annotation);
        StreamingChatLanguageModel streamingChatLanguageModel = getStreamingChatLanguageModel(lookup, annotation);
        ContentRetriever contentRetriever = getContentRetriever(lookup, annotation);
        try {
            AiServices<?> aiServices = AiServices.builder(interfaceClass);
            if (chatLanguageModel != null) 
            	aiServices.chatLanguageModel(chatLanguageModel);
            
            if (streamingChatLanguageModel != null) 
            	aiServices.streamingChatLanguageModel(streamingChatLanguageModel);
        	
            ToolProvider toolProvider = getToolProvider(lookup, annotation);
            if (toolProvider != null) {
            	aiServices.toolProvider(toolProvider);
            } else if (annotation.tools() != null && annotation.tools().length > 0) {
            	aiServices.tools(Stream.of(annotation.tools())
                            .map(c -> {
								try {
									return c.getConstructor((Class<?>[])null).newInstance((Object[])null);
								} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
										| InvocationTargetException | NoSuchMethodException | SecurityException e) {
									// TODO Auto-generated catch block
									throw new RuntimeException(e);
								}
							}  /* lookup.select(c).get() */)
                            .collect(Collectors.toList()));
            }
            if (contentRetriever != null)
                aiServices.contentRetriever(contentRetriever);
            
            ChatMemory chatMemory = getChatMemory(lookup, annotation);
            if (chatMemory != null) {
                aiServices.chatMemory(chatMemory);
            }
            
            ChatMemoryProvider chatMemoryProvider = getChatMemoryProvider(lookup, annotation);
            if (chatMemoryProvider != null) {
                aiServices.chatMemoryProvider(chatMemoryProvider);
            }

            ModerationModel moderationModel = getModerationModel(lookup, annotation);
            if (moderationModel != null) {
                aiServices.moderationModel(moderationModel);
            }
            
            RetrievalAugmentor retrievalAugmentor = getRetrievalAugmentor(lookup, annotation);
            if (retrievalAugmentor != null) {
            	aiServices.retrievalAugmentor(retrievalAugmentor);
            }
            
            return aiServices.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static <T> T getInstance(Instance<Object> lookup, String name, Class<T> clazz) {
    	Instance<T> instance = null;
    	if (name != null && !name.isBlank()) {
    		if ("#default".equals(name)) {
    			instance = lookup.select(clazz);
			} else {
				instance = lookup.select(clazz, NamedLiteral.of(name));
			}
    	}
    	
        if (instance != null && instance.isResolvable()) return instance.get();
        
        return null;
    }

    private static ChatLanguageModel getChatLanguageModel(Instance<Object> lookup, AiService annotation) {
    	return getInstance(lookup, annotation.chatModel(), ChatLanguageModel.class);
    }
	
	private static StreamingChatLanguageModel getStreamingChatLanguageModel(Instance<Object> lookup, AiService annotation) {
		return getInstance(lookup, annotation.streamingChatModel(), StreamingChatLanguageModel.class);
    }
	
	private static ChatMemory getChatMemory(Instance<Object> lookup, AiService annotation) {
    	return getInstance(lookup, annotation.chatMemory(), ChatMemory.class);
    }

    private static ContentRetriever getContentRetriever(Instance<Object> lookup, AiService annotation) {
    	return getInstance(lookup, annotation.contentRetriever(), ContentRetriever.class);
    }
    
    private static ModerationModel getModerationModel(Instance<Object> lookup, AiService annotation) {
    	return getInstance(lookup, annotation.moderationModel(), ModerationModel.class);
    }
    
    private static ChatMemoryProvider getChatMemoryProvider(Instance<Object> lookup, AiService annotation) {
    	return getInstance(lookup, annotation.chatMemoryProvider(), ChatMemoryProvider.class);
    }
    
    private static RetrievalAugmentor getRetrievalAugmentor(Instance<Object> lookup, AiService annotation) {
    	return getInstance(lookup, annotation.retrievalAugmentor(), RetrievalAugmentor.class);
    }
    
    private static ToolProvider getToolProvider(Instance<Object> lookup, AiService annotation) {
    	return getInstance(lookup, annotation.toolProvider(), ToolProvider.class);
    }
}
