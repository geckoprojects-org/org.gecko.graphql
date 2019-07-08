package org.gecko.graphql;

import java.util.Map;

public interface JsonMapper {

	public static final String MEDIA_TYPE = "application/json;charset=UTF-8";


	public String toJson(Map<String, Object> map);


	public Map<String, Object> toMap(String json);
}
