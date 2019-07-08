package org.gecko.graphql.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gecko.graphql.JsonMapper;
import org.gecko.graphql.QueryInvoker;
import org.gecko.graphql.SchemaProvider;
import org.gecko.graphql.annotation.GraphqlEndpoint;
import org.gecko.graphql.annotation.GraphqlServletContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardContextSelect;
import org.osgi.service.http.whiteboard.propertytypes.HttpWhiteboardServletPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.introspection.IntrospectionQuery;
import graphql.schema.GraphQLSchema;

@Component(
		scope = ServiceScope.SINGLETON,
		service = Servlet.class)
@GraphqlEndpoint
@HttpWhiteboardContextSelect(GraphqlServletContext.FILTER)
@HttpWhiteboardServletPattern("/*")
public class GraphqlServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory.getLogger(GraphqlServlet.class);

	private static final long serialVersionUID = 1L;

	public static final String APPLICATION_GRAPHQL = "application/graphql";

	public static final String OPERATION_NAME_PARAM = "operationName";
	public static final String QUERY_PARAM = "query";
	public static final String VARIABLES_PARAM = "variables";

	public static final String INTROSPECTION_PATH = "/schema.json";

	@Reference
	private JsonMapper jsonMapper;

	@Reference
	private QueryInvoker queryInvoker;

	@Reference
	private SchemaProvider schemaProvider;


	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOG.debug("GET");

		if (req.getPathInfo() != null && req.getPathInfo().contentEquals(INTROSPECTION_PATH)) {
			writeResponse(
					resp,
					queryInvoker.execute(
							schemaProvider.schemaInstance(),
							ExecutionInput.newExecutionInput(IntrospectionQuery.INTROSPECTION_QUERY).build()));

			return;
		}

		final String query = req.getParameter(QUERY_PARAM);

		if (query == null || query.trim().isEmpty()) {
			resp.sendError(400, "Missing GraphQL query parameter");

			return;
		}

		writeResponse(resp, queryInvoker.execute(schemaProvider.schemaInstance(), inputFromQueryParams(req)));
	}


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOG.debug("POST");

		final String query = req.getParameter(QUERY_PARAM);
		final GraphQLSchema schema = schemaProvider.schemaInstance();

		if (query != null) {
			// handle like GET if query parameter present
			writeResponse(resp, queryInvoker.execute(schema, inputFromQueryParams(req)));
		} else if (APPLICATION_GRAPHQL.equalsIgnoreCase(req.getContentType())) {
			// treat POST body as GraphQL query string
			writeResponse(resp, queryInvoker.execute(schema, ExecutionInput.newExecutionInput(body(req)).build()));
		} else if (JsonMapper.MEDIA_TYPE.toLowerCase().startsWith(req.getContentType().toLowerCase())) {
			// treat POST body as JSON including query, operationName, variables
			final Map<String, Object> inputMap = jsonMapper.toMap(body(req));

			if (!inputMap.containsKey(QUERY_PARAM)) {
				resp.sendError(400, "Request body is missing field \"query\".");

				return;
			}

			// create variables map (if any)
			final Map<String, Object> variables = new HashMap<>();
			final Object variablesEntry = inputMap.get(VARIABLES_PARAM);

			if (variablesEntry instanceof Map) {
				final Map<?, ?> varMap = (Map<?, ?>) variablesEntry;
				varMap.forEach((k, v) -> variables.put(String.valueOf(k), v));
			}

			writeResponse(resp, queryInvoker.execute(schema, ExecutionInput.newExecutionInput()
					.operationName(inputMap.containsKey(OPERATION_NAME_PARAM) ? String.valueOf(inputMap.get(OPERATION_NAME_PARAM)) : null)
					.query(String.valueOf(inputMap.get(QUERY_PARAM)))
					.variables(variables)
					.build()));
		} else {
			// malformed request
			resp.sendError(400, "Unsupported Content-Type: " + req.getContentType());
		}
	}


	private ExecutionInput inputFromQueryParams(HttpServletRequest req) {
		final String operationName = req.getParameter(OPERATION_NAME_PARAM);
		final String query = req.getParameter(QUERY_PARAM);
		final String variablesParam = req.getParameter(VARIABLES_PARAM);

		final Map<String, Object> variables = jsonMapper.toMap(variablesParam);

		final ExecutionInput input = ExecutionInput.newExecutionInput(query)
				.operationName(operationName)
				.variables(variables)
				.build();

		return input;
	}


	private String body(HttpServletRequest req) throws IOException {
		return req.getReader().lines().collect(Collectors.joining());
	}


	private void writeResponse(HttpServletResponse resp, ExecutionResult result) throws IOException {
		final String json = jsonMapper.toJson(result.toSpecification());

		resp.setContentType(JsonMapper.MEDIA_TYPE);
		resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
		resp.getWriter().print(json);
	}


	@Activate
	private void activate() {
		LOG.debug("{} activated \n\t    JSON mapper: {}\n\t  query invoker: {}\n\tschema provider: {}",
				getClass().getSimpleName(),
				jsonMapper,
				queryInvoker,
				schemaProvider);
	}
}
