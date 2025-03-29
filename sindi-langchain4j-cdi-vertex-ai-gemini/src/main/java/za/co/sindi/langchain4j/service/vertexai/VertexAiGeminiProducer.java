package za.co.sindi.langchain4j.service.vertexai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import dev.langchain4j.model.vertexai.VertexAiGeminiStreamingChatModel;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

/**
 * @author Buhake Sindi
 * @since 10 August 2024
 */
@Dependent
public class VertexAiGeminiProducer {
	
	@Inject
	private VertexAiGeminiConfig config;

	@Produces
	public ChatLanguageModel getChatLanguageModel(InjectionPoint p) {
		return VertexAiGeminiChatModel.builder()
									  .project(config.getProject())
	                                  .location(config.getLocation())
	                                  .modelName(config.getModelName())
	                                  .temperature(config.getTemperature())
	                                  .maxOutputTokens(config.getMaxOutputTokens())
	                                  .topK(config.getTopK())
	                                  .topP(config.getTopP())
	                                  .maxRetries(config.getMaxRetries())
	                                  .responseMimeType(config.getResponseMimeType())
	                                  .responseSchema(config.getResponseSchema())
	                                  .safetySettings(config.getSafetySettings())
	                                  .useGoogleSearch(config.getUseGoogleSearch())
	                                  .vertexSearchDatastore(config.getVertexSearchDatastore())
	                                  .toolCallingMode(config.getToolCallingMode())
	                                  .allowedFunctionNames(config.getAllowedFunctionNames())
	                                  .logRequests(config.getLogRequests())
	                                  .logResponses(config.getLogResponses())
	                                  .build();
	}
	
	@Produces
	public StreamingChatLanguageModel getStreamingChatLanguageModel(InjectionPoint p) {
		return VertexAiGeminiStreamingChatModel.builder()
									  .project(config.getProject())
	                                  .location(config.getLocation())
	                                  .modelName(config.getModelName())
	                                  .temperature(config.getTemperature())
	                                  .maxOutputTokens(config.getMaxOutputTokens())
	                                  .topK(config.getTopK())
	                                  .topP(config.getTopP())
	                                  .responseMimeType(config.getResponseMimeType())
	                                  .responseSchema(config.getResponseSchema())
	                                  .safetySettings(config.getSafetySettings())
	                                  .useGoogleSearch(config.getUseGoogleSearch())
	                                  .vertexSearchDatastore(config.getVertexSearchDatastore())
	                                  .toolCallingMode(config.getToolCallingMode())
	                                  .allowedFunctionNames(config.getAllowedFunctionNames())
	                                  .logRequests(config.getLogRequests())
	                                  .logResponses(config.getLogResponses())
	                                  .build();
	}
}
