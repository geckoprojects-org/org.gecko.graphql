# The OSGi GraphQLWhiteboard 

The OSGi GraphQL Whiteboard is a simple mechanism to expose your OSGi services via GraphQL. Please note that this is no official OSGi Specification, even if the name suggests this.

## Note

Please note, that the project is mostly focused on the EMF support at the Moment. This means, the Services returning simple Pojos have not gotten a lot of attention and might be buggy. Please create Bugs or pitch in, if you find issues around this topic.

Complex Objects as incoming Data Types for mutations are currently only supported in the EMF Version. Without EMF incoming Objects will only be represented as Maps.  

## Tutorial and Examples

Two examples, one with simple Pojos and one with EMF, are provided. Look at `org.gecko.whiteboard.graphql.example` and `org.gecko.whiteboard.graphql.emf.example`, respectively. These should help you understand how to properly set your services in order to be picked up via GraphQL. All the steps are documented in [here](https://gitlab.com/gecko.io/geckographql/-/blob/develop/org.gecko.whiteboard.graphql.example/README.md)

## Requirements

The whiteboard provides a Servlet, that handles all GraphQL in different manners for you. As such, it makes use of the  [OSGi Http Whiteboad](https://osgi.org/specification/osgi.cmpn/7.0.0/service.http.whiteboard.html) and thus has a strong requirement for such an implementation. Thus it also requires a R/ compatible OSGi implementation.

The current implementation is based on GraphQL (Version `19.3.1` - patched version of officially released version `19.3`) and Java 17 as a minimum.

## Artifacts and Repositories

### Maven

Release Repository - https://devel.data-in-motion.biz/nexus/repository/dim-release/
Snapshot Repository - https://devel.data-in-motion.biz/nexus/repository/dim-snapshot/

Artifacts
 - `org.gecko.graphql:org.gecko.whiteboard.graphql.api:1.0.0`
 - `org.gecko.graphql:org.gecko.whiteboard.graphql.impl:1.0.0`

For EMF Support:
 - `org.gecko.graphql:org.gecko.whiteboard.graphql.emf:1.0.0`


POM Repository:

Coming soon.

### OBR

Release:  https://devel.data-in-motion.biz/repository/gecko/release/geckoGraphQL/
Snapshot: https://devel.data-in-motion.biz/repository/gecko/snapshot/geckoGraphQL/


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
	property address : Address[1];
	property relatives : Person[*];
}
```

The data model is highly recursive and can look in the end like a graph because an ``` Address ``` can have ```Person```s living there, that have in turn relatives that can have addresses with persons living there and so forth. 

In REST it could look as follows:

```pseudocode
GET /address
GET /address/{id}
GET /address/{id}/person
GET /address/{id}/person/{id}
GET /person
GET /person/{id}
GET /person/{id}/relatives ?
GET /person/{id}/relatives/{id} ? 

```

This only covers a couple of the possible use cases, that highly depend on what one intents to do with this model. Besides the question on how to structure your endpoint, one needs to decide on how many levels of the hierarchy one wants to return and how to handle this. If you build and API for a brought audience this is is often hard to answer and might have multiple answers.

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

The Whiteboard gives you the ability to register your Service with a property or two and have it picked up by the Whiteboard. The Service Interfaces are then parsed and a Schema is created out of it and the Classes it uses in its methods, if possible.

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

@Component(scope = PROTOTYPE) // The prototype is useful here, because the Service can be called in parallel and every caller should get its exclusive instance
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
	"GeckoGraphQLWhiteboard~test": 
  	{
  		"osgi.http.whiteboard.target" : "(id=foo)",
  		"osgi.http.whiteboard.servlet.pattern" : "/special/graphql/*"
	}
}
```

### adding default response headers

If you for example want to set some fixed Headers like ```Access-Control-Allow-Origin``` , you can add some special service properties. Every property that starts with the prefix ```osgi.graphql.response.header.``` will be set as a response header.

```json
{
    ":configurator:resource-version": 1,
	"GeckoGraphQLWhiteboard~test": 
  	{
  		"osgi.graphql.response.header.Access-Control-Allow-Origin" : "*"
	}
}
```

## Union Types

GraphQL does not support inheritance per say. It supports concepts close to it. Take the following example:

```pseudocode
class Person {
	attribute id: String[1] {id};
	attribute name : String[1];
}

class BusinessPerson extends Person {
	attribute companyName: String:
}
```

 In GraphQL inheritance is handled via Interfaces. This means we would have the following schema:

 ```pseudocode
interface Person {
  id: ID
  name: String
}

interface BusinessPerson {
  id: ID
  name: String
  companyName: String
}

type BusinessPersonImpl implements Person & BusinessPerson {
  id: ID
  name: String
  companyName: String
}
 ```

As you can see, we don't have any relation between Person and BusinessPerson, except the duplicated attributes, but as the BusinessPersonImpl implements both it is kind of close.
As GrphQL is more oriented towards JS, the following setup is different:

 ```pseudocode

type PersonService {
  getAllPersons(): [Person]!
}
 ```

In Java you can expect a List of Persons and you may find a few BusinessPersons in it as well and you can cast if necessary. In GraphQL this, does not help, because the schema says that the Method returns a Person and thus will only be read as such.    

Union Type to the rescue. It allows to declare multiple return types for a schema attribute. The following code acts as an example:

```java
public interface GraphQLQueryInterface{
	@GraphqlUnionType({Person.class, BusinessPerson.class})
	List<Person> getAllPersons();
}

```

## GraphQL and EMF

In order to use GraphQL with EMF the following Artifacts are necessary:

- `org.gecko.graphql:org.gecko.whiteboard.graphql.emf:1.0.0`
- `org.gecko.emf:org.gecko.emf.osgi.ecore:2.2.4`
- `org.gecko.emf:org.gecko.emf.osgi.model.info.api:1.0.0`
- `org.gecko.emf:org.gecko.emf.osgi.model.info.impl:1.0.0`
- `org.gecko.emf:org.gecko.emf.osgi.component:2.2.8`

With this dependencies The GraphQL Whiteboard will now be able to understand Services returning generated EMF Objects. The Schema is then generated using the registered EPackage. In Addition to that incoming Objects can now be complex EMF Objects as well.
