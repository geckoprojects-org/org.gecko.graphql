/**
 * Copyright (c) 2012 - 2023 Data In Motion and others.
 * All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Data In Motion - initial API and implementation
 */
package org.gecko.whiteboard.graphql.emf.example.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.gecko.whiteboard.graphql.GeckoGraphQLValueConverter;
import org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder;
import org.gecko.whiteboard.graphql.GraphqlServiceRuntime;
import org.gecko.whiteboard.graphql.emf.example.service.api.ExampleMutationService;
import org.gecko.whiteboard.graphql.emf.example.service.api.ExampleQueryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.annotation.Testable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.common.annotation.InjectBundleContext;
import org.osgi.test.common.annotation.InjectService;
import org.osgi.test.common.service.ServiceAware;
import org.osgi.test.junit5.context.BundleContextExtension;
import org.osgi.test.junit5.service.ServiceExtension;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Testable
@ExtendWith(BundleContextExtension.class)
@ExtendWith(ServiceExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ExampleImplIntegrationTest {

	private static final String GRAPHQL_ENDPOINT = "http://localhost:8082/graphql";

	private static final ObjectMapper OBJECT_MAPPER;
	static {
		OBJECT_MAPPER = new ObjectMapper();
		OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		OBJECT_MAPPER.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		OBJECT_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, false);
		OBJECT_MAPPER.setSerializationInclusion(Include.NON_NULL);
	}

	@InjectBundleContext
	BundleContext bundleContext;

	private HttpClient client;

	@Order(value = -1)
	@Test
	public void testServices(
			@InjectService(cardinality = 1, timeout = 5000, filter = "(objectClass=org.gecko.whiteboard.graphql.GraphqlServiceRuntime)") ServiceAware<GraphqlServiceRuntime> graphqlServiceRuntimeAware,
			@InjectService(cardinality = 1, timeout = 5000, filter = "(objectClass=org.gecko.whiteboard.graphql.GraphqlSchemaTypeBuilder)") ServiceAware<GraphqlSchemaTypeBuilder> graphqlSchemaTypeBuilderAware,
			@InjectService(cardinality = 1, timeout = 5000, filter = "(component.name=GeckoGraphQLEMFObjectValueConverter)") ServiceAware<GeckoGraphQLValueConverter> emfObjectValueConverterAware,
			@InjectService(cardinality = 1, timeout = 5000, filter = "(component.name=GeckoGraphQLEMFObjectListValueConverter)") ServiceAware<GeckoGraphQLValueConverter> emfObjectListValueConverterAware,
			@InjectService(cardinality = 1, timeout = 5000, filter = "(objectClass=org.gecko.whiteboard.graphql.emf.example.service.api.ExampleQueryService)") ServiceAware<ExampleQueryService> exampleQueryServiceAware,
			@InjectService(cardinality = 1, timeout = 5000, filter = "(objectClass=org.gecko.whiteboard.graphql.emf.example.service.api.ExampleMutationService)") ServiceAware<ExampleMutationService> exampleMutationServiceAware) {

		assertThat(graphqlServiceRuntimeAware.getServices()).hasSize(1);
		ServiceReference<GraphqlServiceRuntime> graphqlServiceRuntimeRef = graphqlServiceRuntimeAware
				.getServiceReference();
		assertThat(graphqlServiceRuntimeRef).isNotNull();

		assertThat(graphqlSchemaTypeBuilderAware.getServices()).hasSize(1);
		ServiceReference<GraphqlSchemaTypeBuilder> graphqlSchemaTypeBuilderRef = graphqlSchemaTypeBuilderAware
				.getServiceReference();
		assertThat(graphqlSchemaTypeBuilderRef).isNotNull();

		assertThat(emfObjectValueConverterAware.getServices()).hasSize(1);
		ServiceReference<GeckoGraphQLValueConverter> emfObjectValueConverterRef = emfObjectValueConverterAware
				.getServiceReference();
		assertThat(emfObjectValueConverterRef).isNotNull();

		assertThat(emfObjectListValueConverterAware.getServices()).hasSize(1);
		ServiceReference<GeckoGraphQLValueConverter> emfObjectListValueConverterRef = emfObjectListValueConverterAware
				.getServiceReference();
		assertThat(emfObjectListValueConverterRef).isNotNull();

		assertThat(exampleQueryServiceAware.getServices()).hasSize(1);
		ServiceReference<ExampleQueryService> exampleQueryServiceRef = exampleQueryServiceAware.getServiceReference();
		assertThat(exampleQueryServiceRef).isNotNull();

		assertThat(exampleMutationServiceAware.getServices()).hasSize(1);
		ServiceReference<ExampleMutationService> exampleMutationServiceRef = exampleMutationServiceAware
				.getServiceReference();
		assertThat(exampleMutationServiceRef).isNotNull();
	}

	@Test
	public void testGetCatalogs() throws InterruptedException, TimeoutException, ExecutionException, IOException {
		// @formatter:off
		/*
			query getCatalogsQuery {
			  ExampleQuery {
			    getCatalogs {
			      id
			      name
			    }
			  }
			}
		 */
		// @formatter:on

		String payload = "{\"query\":\"query getCatalogsQuery {\\n  ExampleQuery {\\n    getCatalogs {\\n      id\\n      name\\n    }\\n  }\\n}\",\"variables\":{}}";

		Request post = client.POST(GRAPHQL_ENDPOINT);

		post.content(new StringContentProvider(payload), "application/json");

		ContentResponse response = post.send();

		assertThat(response.getStatus()).isEqualTo(200);

		JsonNode json = parseJSON(response.getContentAsString());

		JsonNode jsonGetCatalogs = json.at("/data/ExampleQuery/getCatalogs");
		assertThat(jsonGetCatalogs.isMissingNode()).isFalse();
		assertThat(jsonGetCatalogs.isArray()).isTrue();

		ArrayNode jsonGetCatalogsArray = (ArrayNode) jsonGetCatalogs;

		assertThat(jsonGetCatalogsArray.get(0).get("id").asText()).isEqualTo("Catalog1");
		assertThat(jsonGetCatalogsArray.get(0).get("name").asText()).isEqualTo("Catalog number one");
	}

	@Test
	public void testGetProducts() throws InterruptedException, TimeoutException, ExecutionException, IOException {
		// @formatter:off
		/*
			query getProductsQuery {
			  ExampleQuery {
			    getProducts(name: "Product1") {
			      id
			      name
			      active
			      currency
			      price
			      catalog {
			        id
			        name
			      }
			    }
			  }
			}		
		*/
		// @formatter:on

		String payload = "{\"query\":\"query getProductsQuery {\\n  ExampleQuery {\\n    getProducts(name: \\\"Product1\\\") {\\n      id\\n      name\\n      active\\n      currency\\n      price\\n      catalog {\\n        id\\n        name\\n      }\\n    }\\n  }\\n}\",\"variables\":{}}";

		Request post = client.POST(GRAPHQL_ENDPOINT);

		post.content(new StringContentProvider(payload), "application/json");

		ContentResponse response = post.send();

		assertThat(response.getStatus()).isEqualTo(200);

		JsonNode json = parseJSON(response.getContentAsString());

		JsonNode jsonGetProducts = json.at("/data/ExampleQuery/getProducts");
		assertThat(jsonGetProducts.isMissingNode()).isFalse();
		assertThat(jsonGetProducts.isArray()).isTrue();

		ArrayNode jsonGetProductsArray = (ArrayNode) jsonGetProducts;

		assertThat(jsonGetProductsArray.get(0).get("id").asText()).isEqualTo("Product1");
		assertThat(jsonGetProductsArray.get(0).get("name").asText()).isEqualTo("Product number one");
	}

	@Test
	public void testHalloWorld() throws InterruptedException, TimeoutException, ExecutionException, IOException {
		// @formatter:off
		/*
			mutation halloWorldMutation {
			  ExampleMutation {
			    halloWorld(name: "World")
			  }
			}
		*/
		// @formatter:on

		String payload = "{\"query\":\"mutation halloWorldMutation {\\n  ExampleMutation {\\n    halloWorld(name: \\\"World\\\")\\n  }\\n}\",\"variables\":{}}";

		Request post = client.POST(GRAPHQL_ENDPOINT);

		post.content(new StringContentProvider(payload), "application/json");

		ContentResponse response = post.send();

		assertThat(response.getStatus()).isEqualTo(200);

		JsonNode json = parseJSON(response.getContentAsString());

		JsonNode jsonHalloWorld = json.at("/data/ExampleMutation/halloWorld");
		assertThat(jsonHalloWorld.isMissingNode()).isFalse();
		assertThat(jsonHalloWorld.asText()).isEqualTo("Hallo World");
	}

	@Test
	public void testSaveCatalog() throws InterruptedException, TimeoutException, ExecutionException, IOException {
		// @formatter:off
		/*
			mutation saveCatalogMutation {
			  ExampleMutation {
			    saveCatalog(catalog: {
			      id: "Catalog2"
			      name: "Catalog number two"
			    }) {
			      id
			      name
			    }
			  }
			}		
		*/
		// @formatter:on

		String payload = "{\"query\":\"mutation saveCatalogMutation {\\n  ExampleMutation {\\n    saveCatalog(catalog: {\\n      id: \\\"Catalog2\\\"\\n      name: \\\"Catalog number two\\\"\\n    }) {\\n      id\\n      name\\n    }\\n  }\\n}\",\"variables\":{}}";

		Request post = client.POST(GRAPHQL_ENDPOINT);

		post.content(new StringContentProvider(payload), "application/json");

		ContentResponse response = post.send();

		assertThat(response.getStatus()).isEqualTo(200);

		JsonNode json = parseJSON(response.getContentAsString());

		JsonNode jsonSaveCatalog = json.at("/data/ExampleMutation/saveCatalog");
		assertThat(jsonSaveCatalog.isMissingNode()).isFalse();
		assertThat(jsonSaveCatalog.get("id").asText()).isEqualTo("Catalog2");
		assertThat(jsonSaveCatalog.get("name").asText()).isEqualTo("Catalog number two");
	}

	@Test
	public void testSaveProduct() throws InterruptedException, TimeoutException, ExecutionException, IOException {
		// @formatter:off
		/*
			mutation saveProductMutation {
			  ExampleMutation {
			    saveProduct(product: {      
			      id: "Product2"
			      name: "Product number two"
			    }) {
			      id
			      name
			    }
			  }
			}		
		*/
		// @formatter:on

		String payload = "{\"query\":\"mutation saveProductMutation {\\n  ExampleMutation {\\n    saveProduct(product: {      \\n      id: \\\"Product2\\\"\\n      name: \\\"Product number two\\\"\\n    }) {\\n      id\\n      name\\n    }\\n  }\\n}\\n\",\"variables\":{}}";

		Request post = client.POST(GRAPHQL_ENDPOINT);

		post.content(new StringContentProvider(payload), "application/json");

		ContentResponse response = post.send();

		assertThat(response.getStatus()).isEqualTo(200);

		JsonNode json = parseJSON(response.getContentAsString());

		JsonNode jsonSaveProduct = json.at("/data/ExampleMutation/saveProduct");
		assertThat(jsonSaveProduct.isMissingNode()).isFalse();
		assertThat(jsonSaveProduct.get("id").asText()).isEqualTo("Product2");
		assertThat(jsonSaveProduct.get("name").asText()).isEqualTo("Product number two");
	}

	@Test
	public void testSaveProducts() throws InterruptedException, TimeoutException, ExecutionException, IOException {
		// @formatter:off
		/*
			mutation saveProductsMutation {
			  ExampleMutation {
			    saveProducts(products: [{      
			      id: "Product2"
			      name: "Product number two"
			    }, {      
			      id: "Product3"
			      name: "Product number three"
			    }])
			  }
			}		
		*/
		// @formatter:on

		String payload = "{\"query\":\"mutation saveProductsMutation {\\n  ExampleMutation {\\n    saveProducts(products: [{      \\n      id: \\\"Product2\\\"\\n      name: \\\"Product number two\\\"\\n    }, {      \\n      id: \\\"Product3\\\"\\n      name: \\\"Product number three\\\"\\n    }])\\n  }\\n}\",\"variables\":{}}";

		Request post = client.POST(GRAPHQL_ENDPOINT);

		post.content(new StringContentProvider(payload), "application/json");

		ContentResponse response = post.send();

		assertThat(response.getStatus()).isEqualTo(200);

		JsonNode json = parseJSON(response.getContentAsString());

		JsonNode jsonSaveProducts = json.at("/data/ExampleMutation/saveProducts");
		assertThat(jsonSaveProducts.isMissingNode()).isFalse();
		assertThat(jsonSaveProducts.asText()).isEqualTo("Saved 2 product(s)");
	}

	public JsonNode parseJSON(String input) throws IOException {
		return OBJECT_MAPPER.reader().readTree(input);
	}

	@BeforeEach
	public void setup() {
		client = new HttpClient();

		assertThatNoException().isThrownBy(() -> client.start());
	}

	@AfterEach
	public void teardown() {
		try {
			client.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
