package za.co.sindi.langchain4j.service.openai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

/**
 * @author Buhake Sindi
 * @since 10 August 2024
 */
@Dependent
public class OpenAiProducer {
	
	@Inject
	private OpenAiChatLanguageConfig config;

	@Produces
	public ChatLanguageModel getChatLanguageModel(InjectionPoint p) {
		return OpenAiChatModel.builder()
				.baseUrl(config.getBaseUrl())
                .apiKey(config.getApiKey())
                .organizationId(config.getOrganizationId())
                .modelName(config.getModelName())
                .temperature(config.getTemperature())
                .topP(config.getTopP())
                .stop(config.getStop())
                .maxTokens(config.getMaxTokens())
                .presencePenalty(config.getPresencePenalty())
                .frequencyPenalty(config.getFrequencyPenalty())
                .logitBias(config.getLogitBias())
                .responseFormat(config.getResponseFormat())
                .seed(config.getSeed())
                .user(config.getUser())
                .timeout(config.getTimeout())
                .maxRetries(config.getMaxRetries())
                .proxy(config.getProxy())
                .logRequests(config.getLogRequests())
                .logResponses(config.getLogResponses())
                .tokenizer(config.getTokenizer())
                .customHeaders(config.getCustomHeaders())
                .listeners(config.getListeners())
	            .build();
	}
}
