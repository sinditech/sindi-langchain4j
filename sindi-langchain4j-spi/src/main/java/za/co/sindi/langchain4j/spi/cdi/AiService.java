package za.co.sindi.langchain4j.spi.cdi;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.moderation.ModerationModel;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.tool.ToolProvider;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Stereotype;

/**
 * @author Buhake Sindi
 * @since 08 August 2024
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Stereotype
public @interface AiService {
	
	Class<? extends Annotation> scope() default RequestScoped.class;

	/**
     * This attribute specifies the name of a {@link ChatModel} bean that should be used by this AI Service.
     */
    String chatModel() default "#default";

    /**
     * This attribute specifies the name of a {@link StreamingChatModel} bean that should be used by this AI Service.
     */
    String streamingChatModel() default "";

    /**
     * This attribute specifies the name of a {@link ChatMemory} bean that should be used by this AI Service.
     */
    String chatMemory() default "";

    /**
     * This attribute specifies the name of a {@link ChatMemoryProvider} bean that should be used by this AI Service.
     */
    String chatMemoryProvider() default "";
    
//    /**
//     * This attribute specifies the name of a {@link ChatMemoryStore} bean that should be used by this AI Service.
//     */
//    String chatMemoryStore() default "";

    /**
     * This attribute specifies the name of a {@link ContentRetriever} bean that should be used by this AI Service.
     */
    String contentRetriever() default "";

    /**
     * This attribute specifies the name of a {@link RetrievalAugmentor} bean that should be used by this AI Service.
     */
    String retrievalAugmentor() default "";

    /**
     * This attribute specifies the names of beans containing methods annotated with {@link Tool} that should be used by this AI Service.
     */
    Class<?>[] tools() default {};
    
    /**
     * This attribute specifies the names of beans containing methods annotated with {@link ModerationModel} that should be used by this AI Service.
     */
    String moderationModel() default "";
    
    /**
     * This attribute specifies the names of beans containing methods annotated with {@link ToolProvider} that should be used by this AI Service.
     */
    String toolProvider() default "";

//    int chatMemoryMaxMessages() default 10;
}
