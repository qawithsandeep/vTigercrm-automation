package com.saroz.vtiger.utilities;
import java.io.File;
import java.io.IOException;
import io.qameta.allure.internal.shadowed.jackson.databind.JsonNode;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;

public class JsonReaderUtil {

	    private static final ObjectMapper mapper = new ObjectMapper();

	    // Read complete JSON file
	    public static JsonNode getJsonData(String fileName) {
	        try {
	            File file = new File("src/test/resources/testdata/" + fileName);
	            return mapper.readTree(file);
	        } catch (IOException e) {
	            throw new RuntimeException("Failed to read JSON file: " + fileName, e);
	        }
	    }

	    // Read specific value using key
	    public static String getValue(String fileName, String key) {
	        JsonNode jsonNode = getJsonData(fileName);
	        return jsonNode.get(key).asText();
	    }

	    // Read nested value
	    public static String getNestedValue(String fileName, String parentKey, String childKey) {
	        JsonNode jsonNode = getJsonData(fileName);
	        return jsonNode.get(parentKey).get(childKey).asText();
	    }
	}

	


