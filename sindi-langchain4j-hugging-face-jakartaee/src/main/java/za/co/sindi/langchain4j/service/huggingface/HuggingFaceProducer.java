package za.co.sindi.langchain4j.service.huggingface;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.huggingface.HuggingFaceChatModel;
import dev.langchain4j.model.huggingface.HuggingFaceEmbeddingModel;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;

/**
 * @author Buhake Sindi
 * @since 10 August 2024
 */
@Dependent
public class HuggingFaceProducer {
	
	@Inject
	private HuggingFaceChatLanguageConfig chatLanguageConfig;
	
	@Inject
	private HuggingFaceEmbeddingConfig emdeddingConfig;

	@Produces
	public ChatLanguageModel getChatLanguageModel(InjectionPoint point) {
		return HuggingFaceChatModel.builder()
									  .accessToken(chatLanguageConfig.getAccessToken())
	                                  .modelId(chatLanguageConfig.getModelId())
	                                  .timeout(chatLanguageConfig.getTimeout())
	                                  .temperature(chatLanguageConfig.getTemperature())
	                                  .maxNewTokens(chatLanguageConfig.getMaxNewTokens())
	                                  .returnFullText(chatLanguageConfig.getReturnFullText())
	                                  .waitForModel(chatLanguageConfig.getWaitForModel())
	                                  .build();
	}
	
	@Produces
	public EmbeddingModel getEmbeddingModel(InjectionPoint point) {
		return HuggingFaceEmbeddingModel.builder()
									  .accessToken(emdeddingConfig.getAccessToken())
	                                  .modelId(emdeddingConfig.getModelId())
	                                  .timeout(emdeddingConfig.getTimeout())
	                                  .waitForModel(emdeddingConfig.getWaitForModel())
	                                  .build();
	}
}
