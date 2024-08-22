package za.co.sindi.langchain4j.spi;

import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.sindi.AiService;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.build.compatible.spi.Parameters;
import jakarta.enterprise.inject.build.compatible.spi.SyntheticBeanCreator;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;

public class AIServiceCreator implements SyntheticBeanCreator<Object> {
    private static final Logger LOGGER = Logger.getLogger(AIServiceCreator.class.getName());

    @Override
    public Object create(Instance<Object> lookup, Parameters params) {
        Class<?> interfaceClass = params.get(LangChain4JAiServiceBuildCompatibleExtension.PARAM_INTERFACE_CLASS, Class.class);
        AiService annotation = interfaceClass.getAnnotation(AiService.class);

        CDI<Object> cdi = CDI.current();
        ChatLanguageModel chatLanguageModel = getChatLanguageModel(annotation);
        ContentRetriever contentRetriever = getContentRetriever(annotation);
        try {
            AiServices<?> aiServices = AiServices.builder(interfaceClass)
                    .chatLanguageModel(chatLanguageModel);
            if (annotation.tools() != null && annotation.tools().length > 0) {
            	aiServices.tools(Stream.of(annotation.tools())
                            .map(c -> cdi.select(c).get())
                            .collect(Collectors.toList()));
            }
            aiServices.chatMemory(MessageWindowChatMemory.withMaxMessages(annotation.chatMemoryMaxMessages()));
            if (contentRetriever != null)
                aiServices.contentRetriever(contentRetriever);
//
//            Instance<ContentRetriever> contentRetrievers = cdi.select(ContentRetriever.class);
//            if (contentRetrievers.isResolvable())
//                aiServices.contentRetriever(contentRetrievers.get());

            return aiServices.build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ChatLanguageModel getChatLanguageModel(AiService annotation) {
        if (annotation.chatModel().isBlank())
            return CDI.current().select(ChatLanguageModel.class).get();
        return CDI.current().select(ChatLanguageModel.class, NamedLiteral.of(annotation.chatModel())).get();
    }

    private static ContentRetriever getContentRetriever(AiService annotation) {
        if (annotation.contentRetriever().isBlank()) {
            Instance<ContentRetriever> contentRetrievers = CDI.current().select(ContentRetriever.class);
            if (contentRetrievers.isResolvable())
                return contentRetrievers.get();
        }

        Instance<ContentRetriever> contentRetrievers = CDI.current().select(ContentRetriever.class,
                NamedLiteral.of(annotation.contentRetriever()));
        if (contentRetrievers.isResolvable())
            return contentRetrievers.get();
        return null;
    }
}
