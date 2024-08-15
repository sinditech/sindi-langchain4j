package za.co.sindi.langchain4j.service.vertexai;

import java.util.List;
import java.util.Map;

import com.google.cloud.vertexai.api.Schema;

import dev.langchain4j.model.vertexai.HarmCategory;
import dev.langchain4j.model.vertexai.SafetyThreshold;
import dev.langchain4j.model.vertexai.ToolCallingMode;

/**
 * @author Buhake Sindi
 * @since 10 August 2024
 */
public interface VertexAiGeminiConfig {
	
//	private String project;
//	private String location;
//	private String modelName;
//	private Float temperature;
//	private Integer maxOutputTokens;
//	private Integer topK;
//	private Float topP;
//	private Integer maxRetries;
//	private String responseMimeType;
//	private Schema responseSchema;
//	private Map<HarmCategory, SafetyThreshold> safetySettings;
//	private Boolean useGoogleSearch;
//	private String vertexSearchDatastore;
//	private ToolCallingMode toolCallingMode;
//	private List<String> allowedFunctionNames;
//	private Boolean logRequests;
//	private Boolean logResponses;
	
	/**
	 * @return the project
	 */
	public String getProject();
	
	/**
	 * @return the location
	 */
	public String getLocation();
	
	
	/**
	 * @return the modelName
	 */
	public String getModelName();
	
	/**
	 * @return the temperature
	 */
	default Float getTemperature() {
		return null;
	}
	
	/**
	 * @return the maxOutputTokens
	 */
	default Integer getMaxOutputTokens() {
		return null;
	}
	
	/**
	 * @return the topK
	 */
	default Integer getTopK() {
		return null;
	}
	
	/**
	 * @return the topP
	 */
	default Float getTopP() {
		return null;
	}
	
	/**
	 * @return the maxRetries
	 */
	default Integer getMaxRetries() {
		return null;
	}
	
	/**
	 * @return the responseMimeType
	 */
	default String getResponseMimeType() {
		return null;
	}
	
	/**
	 * @return the responseSchema
	 */
	default Schema getResponseSchema() {
		return null;
	}
	
	/**
	 * @return the safetySettings
	 */
	default Map<HarmCategory, SafetyThreshold> getSafetySettings() {
		return null;
	}
	
	/**
	 * @return the useGoogleSearch
	 */
	default Boolean getUseGoogleSearch() {
		return null;
	}
	
	/**
	 * @return the vertexSearchDatastore
	 */
	default String getVertexSearchDatastore() {
		return null;
	}
	
	/**
	 * @return the toolCallingMode
	 */
	default ToolCallingMode getToolCallingMode() {
		return null;
	}
	
	/**
	 * @return the allowedFunctionNames
	 */
	default List<String> getAllowedFunctionNames() {
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
}
