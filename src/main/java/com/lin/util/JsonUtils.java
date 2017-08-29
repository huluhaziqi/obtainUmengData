package com.lin.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonUtils {

	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static String obj2JsonStr(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new IllegalArgumentException();
		}
	}

	public static <T> T jsonStr2Obj(String jsonStr, Class<T> objClass) {
		if (jsonStr == null || objClass == null) {
			return null;
		}
		try {
			return objectMapper.readValue(jsonStr, objClass);
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new IllegalArgumentException();
		}
	}
	
	public static <T> T jsonStr2Obj(String jsonStr, TypeReference<T> typeReference) {
		if (jsonStr == null || typeReference == null) {
			return null;
		}
		try {
			return objectMapper.readValue(jsonStr, typeReference);
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new IllegalArgumentException();
		}
	}

	public static Map<?, ?> jsonStr2Map(String jsonStr) {
		if (jsonStr == null) {
			return null;
		}
		try {
			return objectMapper.readValue(jsonStr, Map.class);
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new IllegalArgumentException();
		}
	}


	public static <T> ArrayList<T> jsonStr2List(String jsonStr, Class<T> elementClass) {
		if (jsonStr == null || elementClass == null) {
			return null;
		}
		try {
			JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, elementClass);
			return objectMapper.readValue(jsonStr, javaType);
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new IllegalArgumentException();
		}
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> obj2Map(Object obj) {
		Map<String, Object> mappedObject = objectMapper.convertValue(obj, Map.class);
		return mappedObject;
	}

    @SuppressWarnings("rawtypes")
    public static <T> T map2Obj(Map jsonMap, TypeReference<T> typeReference) {
        if (jsonMap == null || typeReference == null) {
            return null;
        }
        try {
            return objectMapper.convertValue(jsonMap, typeReference);
        } catch (Exception e) {
        	logger.error(e.getMessage());
			throw new IllegalArgumentException();
        }
    }

	public static JsonNode str2JsonNode(String str) {
		try {
			return objectMapper.readTree(str);
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw new IllegalArgumentException();
		}
	}

}
