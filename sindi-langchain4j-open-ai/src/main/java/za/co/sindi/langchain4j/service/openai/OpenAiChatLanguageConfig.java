package za.co.sindi.langchain4j.service.openai;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import dev.langchain4j.model.chat.Capability;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.request.ChatRequestParameters;

/**
 * @author Buhake Sindi
 * @since 10 August 2024
 */
public interface OpenAiChatLanguageConfig {
	
//	private String baseUrl;
//    private String apiKey;
//    private String organizationId;
//    private String projectId;
//
//    private ChatRequestParameters defaultRequestParameters;
//    private String modelName;
//    private Double temperature;
//    private Double topP;
//    private List<String> stop;
//    private Integer maxTokens;
//    private Integer maxCompletionTokens;
//    private Double presencePenalty;
//    private Double frequencyPenalty;
//    private Map<String, Integer> logitBias;
//    private Set<Capability> supportedCapabilities;
//    private String responseFormat;
//    private Boolean strictJsonSchema;
//    private Integer seed;
//    private String user;
//    private Boolean strictTools;
//    private Boolean parallelToolCalls;
//    private Boolean store;
//    private Map<String, String> metadata;
//    private String serviceTier;
//    private Duration timeout;
//    private Integer maxRetries;
//    private Boolean logRequests;
//    private Boolean logResponses;
//    private Map<String, String> customHeaders;
//    private List<ChatModelListener> listeners;
//    private Boolean returnThinking;
//	  private Logger logger;
	
	/**
	 * @return the baseUrl
	 */
	public String getBaseUrl();
	
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
	 * @return the projectId
	 */
	default String getProjectId() {
		return null;
	}
	
	/**
	 * @return the defaultRequestParameters
	 */
	default ChatRequestParameters getDefaultRequestParameters() {
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
	 * @return the maxCompletionTokens
	 */
	default Integer getMaxCompletionTokens() {
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
	 * @return the supportedCapabilities
	 */
	default Set<Capability> getSupportedCapabilities() {
		return null;
	}
	
	/**
	 * @return the responseFormat
	 */
	default String getResponseFormat() {
		return null;
	}
	
	/**
	 * @return the strictJsonSchema
	 */
	default Boolean getStrictJsonSchema() {
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
	
	/**
	 * @return the strictTools
	 */
	default Boolean getStrictTools() {
		return null;
	}
	
	/**
	 * @return the parallelToolCalls
	 */
	default Boolean getParallelToolCalls() {
		return null;
	}
	
	/**
	 * @return the store
	 */
	default Boolean getStore() {
		return null;
	}
	
	/**
	 * @return the metadata
	 */
	default Map<String, String> getMetadata() {
		return null;
	}
	
	/**
	 * @return the serviceTier
	 */
	default String getServiceTier() {
		return null;
	}
	
	/**
	 * @return the timeout
	 */
	default Duration getTimeout() {
		return null;
	}
	
	/**
	 * @return the maxRetries
	 */
	default Integer getMaxRetries() {
		return null;
	}
	
	/**
	 * @return the logRequests
	 */
	default Boolean getLogRequests() {
		return null;
	}
	
	/**
	 * @return the logResponses
	 */
	default Boolean getLogResponses() {
		return null;
	}
		
	/**
	 * @return the customHeaders
	 */
	default Map<String, String> getCustomHeaders() {
		return null;
	}
	
	/**
	 * @return the listeners
	 */
	default List<ChatModelListener> getListeners() {
		return null;
	}
	
	/**
	 * @return returnThinking
	 */
	default Boolean getReturnThinking() {
		return null;
	}
	
	/**
	 * @return logger
	 */
	default Logger getLogger() {
		return null;
	}
}
