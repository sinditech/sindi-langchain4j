package za.co.sindi.langchain4j.service.huggingface;

import java.time.Duration;

/**
 * @author Buhake Sindi
 * @since 10 August 2024
 */
public interface HuggingFaceEmbeddingConfig {
	
//	String accessToken;
//    String modelId;
//    Duration timeout;
//    Boolean waitForModel;
    
	/**
	 * @return the accessToken
	 */
	public String getAccessToken();
	
	/**
	 * @return the modelId
	 */
	public String getModelId();
	
	/**
	 * @return the timeout
	 */
	default Duration getTimeout() {
		return null;
	}
	
	/**
	 * @return the waitForModel
	 */
	default Boolean getWaitForModel() {
		return null;
	}
}
