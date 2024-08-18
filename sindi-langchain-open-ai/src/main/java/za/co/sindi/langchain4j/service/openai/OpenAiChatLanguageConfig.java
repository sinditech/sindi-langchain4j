package za.co.sindi.langchain4j.service.openai;

import java.net.Proxy;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.chat.listener.ChatModelListener;

/**
 * @author Buhake Sindi
 * @since 10 August 2024
 */
public interface OpenAiChatLanguageConfig {
	
//	private final String baseUrl;
//	private final String apiKey;
//	private final String organizationId;
//	private final String modelName;
//    private final Double temperature;
//    private final Double topP;
//    private final List<String> stop;
//    private final Integer maxTokens;
//    private final Double presencePenalty;
//    private final Double frequencyPenalty;
//    private final Map<String, Integer> logitBias;
//    private final String responseFormat;
//    private final Integer seed;
//    private final String user;
//    private final Integer maxRetries;
//    private final Tokenizer tokenizer;
//    private final List<ChatModelListener> listeners;
    
	/**
	 * @return the baseUrl
	 */
	default String getBaseUrl() {
		return null;
	}
	
	/**
	 * @return the apiKey
	 */
	public String getApiKey();
	
	/**
	 * @return the organizationId
	 */
	default String getOrganizationId() {
		return null;
	}
	
	/**
	 * @return the modelName
	 */
	public String getModelName();
	
	/**
	 * @return the temperature
	 */
	default Double getTemperature() {
		return null;
	}
	
	/**
	 * @return the topP
	 */
	default Double getTopP() {
		return null;
	}
	
	/**
	 * @return the stop
	 */
	default List<String> getStop() {
		return null;
	}
	
	/**
	 * @return the maxTokens
	 */
	default Integer getMaxTokens() {
		return null;
	}
	
	/**
	 * @return the presencePenalty
	 */
	default Double getPresencePenalty() {
		return null;
	}
	
	/**
	 * @return the frequencyPenalty
	 */
	default Double getFrequencyPenalty() {
		return null;
	}
	
	/**
	 * @return the logitBias
	 */
	default Map<String, Integer> getLogitBias() {
		return null;
	}
	
	/**
	 * @return the responseFormat
	 */
	default String getResponseFormat() {
		return null;
	}
	
	/**
	 * @return the seed
	 */
	default Integer getSeed() {
		return null;
	}
	
	/**
	 * @return the user
	 */
	default String getUser() {
		return null;
	}
	
	default Duration getTimeout() {
		return null;
	}
	
	/**
	 * @return the maxRetries
	 */
	default Integer getMaxRetries() {
		return null;
	}
	
	default Proxy getProxy() {
		return null;
	}
	
	default Boolean getLogRequests() {
		return null;
	}
	
	default Boolean getLogResponses() {
		return null;
	}
	
	/**
	 * @return the tokenizer
	 */
	default Tokenizer getTokenizer() {
		return null;
	}
	
	default  Map<String, String> getCustomHeaders() {
		return null;
	}
	
	/**
	 * @return the listeners
	 */
	default List<ChatModelListener> getListeners() {
		return null;
	}
}
