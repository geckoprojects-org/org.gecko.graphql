# The OSGi GraphQLWhiteboard 

The OSGi GraphQL Whiteboard is a simple mechanism to expose your OSGi services via GraphQL. Please note that this is no official OSGi Specification, even if the name suggests this.

## Requirements

The whiteboard provides a Servlet, that handles all GraphQL in different manners for you. As such, it makes use of the  [OSGi Http Whiteboad](https://osgi.org/specification/osgi.cmpn/7.0.0/service.http.whiteboard.html) and thus has a strong requirement for such an implementation. Thus it also requires a R/ compatible OSGi implementation.

The current implementation is based on GraphQL for Java in Version 11.

## The Concept

If your are interested in the technical Details on how it works you can jump this section.

### The Use Case

Consider an API for the following Model

```pseudocode
class Address {
	attribute id: String[1] {id};
	attribute street : String;
	attribute zipCode : String;
	attribute number : String;
	attribute city : String;
	property residents : Person[*];
}

class Person {
	attribute id: String[1] {id};
	attribute name : String[1];
	property addres : Address[1]:
	property relatives : Person[*];
}
```

The data model  is highly recursive and can look in the end like a graph because an ``` Address ``` can have ```Person```s living there, that have in turn relatives that can have addresses with persons living there and so forth. 

In REST it could look as follows:

```pseudocode
GET /address
GET /address/{id}
GET /address/{id}/person
GET /address/{id}/person/{id}
GET /person
GET /person/{id}
GET /person/{id}/relativs ?
GET /person/{id}/relativs/{id} ? 

```

This only covers a couple of the possible use cases, that highly depend on what one intents to do with this model. Besides the question on how to structure your endpoint, one needs to decide on how many levels of the hierarchy one wants to return and how to handle this. If you build and API for a brought audience this is is often hard to answer and might have multiple Ansers.

### A solution with GraphQL

In GraphQL you would simply define a Schema like this:

```json
type Address {
  id: ID!
  street: String
  number: String
  zipcode: String
  city: String
  residents: [Person]!
}

type Person {
  id: ID!
  name: String!
  relatives: [Person]!
  address: [Address]!
}

type AddressService {
  getAddresses(id: String): [Address]!
}

```

and add ```DataFetcher```s to every junctions like this:

```java
public class RelativesFetcherImpl implements DataFetcher<List<Person>> {
  	/*
  	 * (non-Javadoc)
  	 * @see graphql.schema.DataFetcher#get(graphql.schema.DataFetchingEnvironment)
  	 */
  	@Override
  	public List<Person> get(DataFetchingEnvironment environment) throws Exception {
         	Person p = environment.getSource();
         	return p.getRelatives();
  	}
}

public class GetAddressesFetcherImpl implements DataFetcher<List<Address>> {
   	@Reference
  	AddressQuery addressService;
 	
  	/*
  	 * (non-Javadoc)
  	 * @see graphql.schema.DataFetcher#get(graphql.schema.DataFetchingEnvironment)
  	 */
  	@Override
  	public List<Address> get(DataFetchingEnvironment environment) throws Exception {
         	String idArgument = environment.getArgument("id");
         	return addressService.getAddresses(idArgument);
  	}
}


```

### OSGi and GraphQL

There a few noteworthy parallels here:

* In OSGi we have Services, that provide Java Classes that execute logic (like ```Datafetchers```).
* The Java Classes are the Model that describes the Schema we are working with.

## The boring technical Details

The Whiteboard gives you the ability register your Service with a property or two and have it picked up by the Whiteboard. The Service Interfaces are then parsed and a Schema is created out of it and the Classes it uses in its methods, if possible.

### Configuration

A Whiteboard needs to be configured first. As initially mentioned, it needs a configured HTTPWhiteboard. An example using the the  [Apache Felix Http Service (A HTTP Whiteboard Implementation)](https://felix.apache.org/documentation/subprojects/apache-felix-http-service.html)  together with the [Configurator Spec](https://osgi.org/specification/osgi.cmpn/7.0.0/service.configurator.html)  can look as follows:

#### The Whiteboard with DS

```json
{
    ":configurator:resource-version": 1,
	"GeckoGraphQLWhiteboard": 
  	{
	}
}
```

Declare you Service with [Declarative Services](https://osgi.org/specification/osgi.cmpn/7.0.0/service.component.html) an Example like this:

```java
public interface AddressService {
	public List<Address> getAddresses(String personId);
}

@Component(scope = PROTOTYPE) // The prototype is usefull here, because the Service can be called in parallel and every caller should get its exclusive instance
@GraphqlQueryService // Marks all implemented Interfaces as part of the Query Schema
public class AddressServiceImpl implements AddressQuery {
   	
   	@Override
  	public List<Address> getAddresses(String personId) throws Exception {
         	String idArgument = environment.getArgument("id");
         	return addressService.getAddresses(idArgument);
  	}
}
```

#### The Whiteboard with CDI

TODO





The Result will be a GraphQL API running under http://<yourIPOrLocalhost>:8080/graphql what will provide the following Schema:

```
type Address {
  id: ID
  street: String
  number: String
  zipcode: String
  city: String
  residents: [Person]!
}

type Person {
  id: ID
  name: String
  relatives: [Person]!
  address: [Address]!
}

type AddressService {
  getAddresses(id: String): [Address]!
}
```



### Targeting a specific HTTP Whiteboard

As a Servlet, the GraphQLWhiteboard can be targeted to a specific HTTP Whiteboard

```json
{
    ":configurator:resource-version": 1,
    "org.apache.felix.http~test":
	{
		"org.osgi.service.http.port": "8082",
		"org.osgi.service.http.host": "0.0.0.0",
		"org.apache.felix.http.context_path": "/",
		"org.apache.felix.http.name": "my_http_whiteboard",
		"org.apache.felix.http.runtime.init.id": "foo"
	},
	"GeckoGraphQLWhiteboard~login": 
  	{
  		"osgi.http.whiteboard.target" : "(id=foo)",
  		"osgi.http.whiteboard.servlet.pattern" : "/special/graphql/*"
	}
}
```

