# JaxB Annotation module for Populace

This module provides support for the applicable subset of JaxB annotations that can be utilised to control graph walking
and population.

## Usage
The default configuration for this module can be installed like this:

```java
GraphPopulator populator = PopulaceJaxB.install(GraphPopulator.newBuilder()).build();
```

Alternatively, individual customisations can be applied manually.

## Supported annotations

1. @XmlTransient - the `ExcludeXmlTransientFields` filter excludes any fields annotated with `@XmlTransient`.
1. @XmlJavaTypeAdapter - the `JaxBInstanceFactory` will attempt to instantiate any type is annotated with
`@XmlJavaTypeAdatper` by first instantiating the `@XmlAdatper`s bound type and then using the `@XmlAdatper` to marshal
to the value type.

## Known limitations
1. `@XmlTransient` is not currently detected if on getter or setter methods.
