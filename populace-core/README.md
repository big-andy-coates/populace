# Populace-core

This page will, in time, cover how to use the core graph walking and populating features of populace.

## Graph Walking

Put simply, you can use `GraphWalker` to walk the fields and (container) elements of an object graph.

You provide [`Visitors`](#visitors) that will be called back as each field/element that is encountered. You can
 optionally control what fields and elements are included / excluded from the walk by installing [`Filters`](#filters),
 and you can control what fields and elements are exposed from custom types by installing [`Inspectors`](#inspectors).

### Visitors

Documentation coming soon.
<!--- Todo(ac): and also link to java docs for all these types --->

### Filters

Documentation coming soon.
<!--- Todo(ac): --->

#### Instance tracking

Populace also comes with a special field and element filter implementation that can be used to avoid circular-references
causing a stack-overflow and to stop the same instance, which may be set on multiple fields or elements, being visited
twice.

The `InstanceTracker` is not installed by default, but can be installed as shown below.

**Note**: The instance tracker is stateful and mutable. Therefore, multiple calls to `walk` that re-use the same
`InstanceTracker` will carry over the list of visited instances from one walk to the next.  This is generally not
 desired.  If not desired, install a fresh `InstanceTracker` for each walk.

```java
InstanceTracker tracker = new InstanceTracker();
GraphWalker.Builder builder = GraphWalker.newBuilder();
GraphWalker walker = builder
    .withFieldFilter(builder.getFieldFilter().and(tracker.getFieldFilter()))
    .withElementFilter(builder.getElementFilter().and(tracker.getElementFilter()))
    .build();
```

### Inspectors

Documentation coming soon.
<!--- Todo(ac): --->

## Graph Populating

Graph population builds on the graph walking functionality, adding the ability to populate and/or mutate existing fields
 and elements as they are encountered.

You can control this process by installing [`Mutators`](#mutators), which are responsible for populating/mutating values,
 and [`InstanceFactories`](#instance-factories), which are responsible for providing instances of types where needed.

### Mutators

Documentation coming soon.
<!--- Todo(ac): --->

### Instance Factories

Documentation coming soon.
<!--- Todo(ac): --->

## Registering Customisations

Many of the customisations available in Populace, such as `Mutators`, `Inspectors` etc, are registered at one of several
 levels. These levels are, from most specific to least, listed below:
* **specific types** i.e. you can register a handler from a specific class e.g. `String`, or a specific parameterised type
e.g. MyType<Integer>, or a specific array type e.g. `long[]`. Such handlers will be used in preference to any other handler.
* **super types** i.e. you can register a handler for a super type such as `List` and it will be used to handle any
derived types that do not have a more specific handler installed.
* **packages** i.e. you can register a handler to handle any types that belong to `java.lang.util`. The handler will be
used for any type that belongs to the package that do not have a more specific handler installed.
* **default** this is the default handler to use if no others are available.
* **array default** arrays are treated slightly differently. The default array handler will be used where no specific
handler is registered for an array type.

To understand what type will be used at runtime to look up suitable handlers you will need to understand how Populace
 [determines runtime type information](#runtime-type-resolution).

# Runtime-type resolution

Populace determines the runtime type of a field or element by making use of all compile-time and runtime type information
available.  Fields and elements with a null value are treated differently to non-null values, as non-null values have
additional runtime type information.

## Null fields
When the value of the field is null the only information available is the compile type type of the field.

If the compile time type of the field is a normal class, e.g. `long` or `List`, then Populace will use this type
information when looking up configured customisations.

If the compile time type of the field is a parameterised type, e.g. `Map<String,Integer>`, then Populace will use the
full parameterised type when looking up customisations. In addition, if any of the type arguments are type variables,
or which have bounds, e.g. consider the field 'example' in the following code snippet below, Populace will use all
available type information in parent/containing classes to resolve the type variable or bounds to a concrete type.

```java
class SomeClass<T> {
    private final List<T> example;
}
```

If the compile time type of the field is a type variable, e.g. consider field 'example' in the code snippet below, then
Populace will again use all type information available in parent/containing classes to resolve this to a concrete type.

```java
class SomeClass<T> {
    private final T example;
}
```

## Non-null fields
When the value of the field is not null, then Populace has additional type information available to it, and it will use
this information to resolve the type of the field to a more specific type, if possible.

If the runtime type of the field is a normal class that is a subtype of the compile time type, then the more specific
runtime type will.

If the runtime type of the field is a parameterised type that is a subtype of the compile time type, e.g. consider the
field 'example' in the following code snippet below, then Populace will parameterise the runtime type using all available
type information. So in the case of the example below, the type of the field would be resolved to
`HashMap<String, Number>`. Populace will also attempt to resolve any type variables and bounds, as needed.

```java
class SomeClass {
    private final Map<String, Number> example = new HashMap<>();
}
```

## Null elements

Documentation coming soon.
<!--- Todo(ac): --->

## Non-null elements

Documentation coming soon.
<!--- Todo(ac): --->

## Null Object handling

Documentation coming soon.
<!--- Todo(ac): --->

