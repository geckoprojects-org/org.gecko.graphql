package org.gecko.graphql.json.gson;

import java.util.Collections;
import java.util.Map;

import org.gecko.graphql.JsonMapper;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@Component(
		service = JsonMapper.class)
public class GsonMapper implements JsonMapper {

	private static final Logger LOG = LoggerFactory.getLogger(GsonMapper.class);

	private Gson gson;


	@Override
	public String toJson(Map<String, Object> map) {
		return gson.toJson(map);
	}


	@Override
	public Map<String, Object> toMap(String json) {
		if (json == null || json.trim().isEmpty()) {
			return Collections.emptyMap();
		}

		if (!json.trim().startsWith("{")) {
			// not an object
			return Collections.emptyMap();
		}

		final Map<String, Object> map = gson.fromJson(json, new TypeToken<Map<String, Object>>() {
		}.getType());

		return map == null ? Collections.emptyMap() : map;
	}


	@Activate
	private void activate() {
		// Serializing nulls is explicitly required by the GraphQL specification.
		gson = new GsonBuilder().serializeNulls().create();

		LOG.debug("{} activated", getClass().getSimpleName());
	}
}
