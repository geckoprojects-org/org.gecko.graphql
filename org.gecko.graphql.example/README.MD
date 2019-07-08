# org.gecko.graphql.example 

Beispielprojekt für GraphQL mit einem vereinfachten HR-Modell. Bildet Personen mit Kontakten (incl. Telefonnummern), Status und Jubiläen ab.

Verwendet das `GraphqlServlet` aus dem Bundle `org.gecko.graphql.servlet`. Dieses wird mittels der `ServletContextHelper`-Implementierung
`org.gecko.graphql.example.GraphqlServletContext` unter dem Pfad `/graphql` registriert.

Das `GraphqlServlet` hat statische Referenzen auf drei Services.

* `org.gecko.graphql.JsonMapper`
* `org.gecko.graphql.QueryInvoker`
* `org.gecko.graphql.SchemaProvider`

Es wird nur aktiv, wenn alle drei dieser Services im Framework laufen.

## JsonMapper
JSON ist das Austauschformat für GraphQL. Der `JsonMapper` dient der De-/Serialisierung von Java `Map`s von und zu JSON Strings.
Das Bundle `org.gecko.graphql.json.gson` stellt mit dem `GsonMapper` eine auf Gson basierende Implementierung bereit, die als DS-Komponente diesen
Service registriert. 

## QueryInvoker
Der `QueryInvoker` ist für die Ausführung von GraphQL Queries verantwortlich. Das Bundle `org.gecko.graphql` enthält eine einfache Implementierung,
den `DefaultQueryInvoker`. Diese registriert als DS-Komponente den `QueryInvoker` Service. 

## SchemaProvider
Zentral für GraphQL ist das _Schema_. Es definiert die zur Verfügung stehenden _Queries_, _Mutations_ und _Subscriptions_. Als Implementierung verwenden wir
`graphql-java`. Das `graphql.schema.GraphQLSchema` kann dabei entweder aus der sogenannten _Schema Definition Language_ (SDL) geparst oder programmatisch
erstellt werden. 

Wir verfolgen den programmatischen Ansatz. Dabei werden _Queries_, _Mutations_ und _Subscriptions_ jeweils als 
`graphql.schema.GraphQLObjectType`-Instanzen abgebildet. Die eigentliche Implementierung stellt die `graphql.schema.GraphQLCodeRegistry` zur Verfügung sie
verbindet die in den Typdefinitionen (`GraphQLObjectType`) spezifizierten Felder mit `graphql.schema.DataFetcher`-Instanzen, die für das Lesen und Schreiben der
Felder verantwortlich sind.

Das Bundle `org.gecko.graphql` enthält die `SchemaProvider`-Implementierung `org.gecko.graphql.WhiteboardSchemaProvider`, die als DS-Komponente den 
`SchemaProvider` Service registriert. Nach dem Whiteboard-Pattern sammelt diese Implementierung Instanzen des `org.gecko.graphql.SchemaContributor` Services
und erzeugt aus diesen jeweils ein aktuell gültiges Schema. Dabei ist jeder `SchemaContributor` eine inhaltliche zusammengehörige Menge von
`graphql.schema.GraphQLFieldDefinition`-Instanzen und zugehörigen `org.gecko.graphql.DataFetcherCoordinates`.


## Quickstart

* `ServletContextHelper`-Komponente (leer) implementieren und mit `@org.gecko.graphql.annotation.GraphqlServletContext` annotieren
(Pfad zum GraphQL-Endpunkt)
* `GraphQLOutputType`-Instanzen für alle Modellobjekte definieren, Feldnamenskonstanten anlegen
*  `SchemaContributor`-Instanz(en) implementieren
	* Queries, Mutations, Subscriptions als `GraphQLFieldDefinition`s implementieren
	* zughörige `DataFetcher` implementieren und mittels `DataFetcherCoordinates` zuordnen
 

## TODO

* Mutation-Beispiel
* Subscription-Beispiel
