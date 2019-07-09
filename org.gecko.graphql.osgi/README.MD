# org.gecko.graphql.osgi 


```
{
  frameworkDTO {
    bundles {
      id
      lastModified
      state
      symbolicName
      version
    }
    services {
      id
      bundle
      properties
      usingBundles
    }
    properties
  }
}
```


```
mutation{
  stopBundle(id:12,option:1)
}

```


```
mutation{
  startBundle(id:12,option:1)
}

```