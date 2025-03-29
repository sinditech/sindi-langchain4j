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
			    .projectId(config.getProjectId())
			    .defaultRequestParameters(config.getDefaultRequestParameters())
			    .modelName(config.getModelName())
			    .temperature(config.getTemperature())
			    .topP(config.getTopP())
			    .stop(config.getStop())
			    .maxTokens(config.getMaxTokens())
			    .maxCompletionTokens(config.getMaxCompletionTokens())
			    .presencePenalty(config.getPresencePenalty())
			    .frequencyPenalty(config.getFrequencyPenalty())
			    .logitBias(config.getLogitBias())
			    .supportedCapabilities(config.getSupportedCapabilities())
			    .responseFormat(config.getResponseFormat())
			    .strictJsonSchema(config.getStrictJsonSchema())
			    .seed(config.getSeed())
			    .user(config.getUser())
			    .strictTools(config.getStrictTools())
			    .parallelToolCalls(config.getParallelToolCalls())
			    .store(config.getStore())
			    .metadata(config.getMetadata())
			    .serviceTier(config.getServiceTier())
			    .timeout(config.getTimeout())
			    .maxRetries(config.getMaxRetries())
			    .logRequests(config.getLogRequests())
			    .logResponses(config.getLogResponses())
			    .tokenizer(config.getTokenizer())
			    .customHeaders(config.getCustomHeaders())
			    .listeners(config.getListeners())
	            .build();
	}
}
