# Populace-core

This page provides information on how to use and customise the core graph walking and populating features of Populace

## Graph Walking

Put simply, you can use `GraphWalker` to walk the fields and (container) elements of an object graph.

You provide [`Visitors`](#visitors) that will be called back as each field/element that is encountered. You can
 optionally control what fields and elements are included / excluded from the walk by installing [`Filters`](#filters),
 and you can control what fields and elements are exposed from custom types by installing [`Inspectors`](#inspectors).

### Filters

Filters can be installed to stop specific fields or elements from being walked, (or visiting). Field and Element filters
are installed when building a walker instance, as shown below:

```java
GraphWalker walker = GraphWalker.newBuilder()
    .withFieldFilter(field -> field.getName().contains("includeMe")
    .withElementFilter(element -> element.getValue() != null)
    .build();
```

`FieldFilters` defines some common field filters e.g. to exclude certain types of fields. By default the
walker excludes static and transient fields, though this can be overridden.

Filters can be combined using the standard `Predicate` logical operators, e.g. AND, OR and NOT.

#### Instance tracking

Populace comes with a special type of field and element filter that can be used to avoid circular-references, which
would otherwise cause a stack-overflow, and to stop the same instance, which may be set on multiple fields or elements,
being visited more than once.

The `InstanceTracker` is not installed by default, but can be installed as shown below.

**Note**: The instance tracker is stateful and mutable. Therefore, multiple calls to `walk` that re-use the same
`InstanceTracker` will carry over the list of visited instances from one walk to the next.  This is generally not
 desired.  If not desired, ensure you call `clear` on the instance tracker between each walk.

```java
InstanceTracker tracker = new InstanceTracker();
GraphWalker.Builder builder = GraphWalker.newBuilder();
GraphWalker walker = builder
    .withFieldFilter(builder.getFieldFilter().and(tracker.getFieldFilter()))
    .withElementFilter(builder.getElementFilter().and(tracker.getElementFilter()))
    .build();
```

### Inspectors

`Inspectors` are similar to [`Filters`](#filters) in some ways. Where as [`Filters`](#filters) control which of the
types / instances fields and elements are walked, `Inspectors` actual define the set of exposed fields/elements.

`Inspectors` should be generic, in that they should expose the fields and elements in a generic, none use-case specific,
manner, so that they can be reused across many use-cases and applications. On the other hand, Filters can to be specific
to the use-case.

Populace defines a whole set of inspectors to cover most common types. (If you feel there is a inspector missing for
some common type then please raise a ticket, or even better a PR). You may need to write your own inspectors, especially
if you have custom container types that are not derived from the standard `List`, `Set` or `Map` interfaces.

Inspectors are installed when building a walker. Inspectors can be installed to handle specific types, any subtype of
some type or any type within a package. See [Registering Customisations](#registering-customisations) for an
explanation of the different levels. See below for an example:

```java
final GraphWalker.Builder builder = GraphWalker.newBuilder();
final GraphWalker walker = builder.withInspectors(builder.inspectors()
        .withPackageInspector("java.util", TerminalInspector.INSTANCE)  // Don't look inside java.until types
        .build())
    .build();
```

### Visitors

The visitors you install will be called back as each non-filtered field and element visited. The visitor can mutate the
value of the field/mutator if required, using the `setValue` methods on the supplied `FieldInfo`/`ElementInfo` instance.

Visitors are passed to the `walk` method itself, like so:

```java
walker.walk(typeToWalk, fieldVisitor, elementVisitor);
```

## Graph Populating

Graph population builds on the graph walking functionality, adding the ability to populate and/or mutate existing fields
 and elements as they are encountered.

You can control this process by installing [`Mutators`](#mutators), which are responsible for populating/mutating values,
 and [`InstanceFactories`](#instance-factories), which are responsible for providing instances of types where needed.

### Mutators

Mutators allow you to install custom code to control how certain types are mutated, giving you complete control over how
your object graphs are populated.

Populace comes complete with a lot of standard Mutators that can be composed to achieve most tasks, and you are free to
implement your own as needed. By default, a new populator comes pre-configured with a sensible set of mutators, that
will handle most common types, though these can be overridden if needed.

Mutators can be installed to mutate specific types, subtypes of some type, any type belonging to a package, and
default mutators for array and non-array types can also be configured. See
[Registering Customisations](#registering-customisations) for an explanation of the different levels. See below for an
example:


```java
GraphPopulator.Builder builder = GraphPopulator.newBuilder();
GraphPopulator populator = builder
    .withMutators(builder.mutators()
        .withSpecificMutator(String.class, new CustomStringMutator())
        .build())
    .build();
```

### Instance Factories

Instance Factories allow you to install custom code to control how certain types are created. Populace needs this to
allow fields that are currently null, or container types that are currently empty, to be populated.

Populace comes complete with a lot of factories for standard types and any type that can be constructed using its
default constructor. For other types you will need to implement custom instance factories.

Instance factories can be installed for specific types, subtypes of some type, any type belonging to a package, and
default factories for array and non-array types can also be configured. See
[Registering Customisations](#registering-customisations) for an explanation of the different levels.

In addition, a special type of instance factory, called a `NullObjectStrategy`, can be installed to handle any null
`Object` s i.e. fields or elements where no type information is available, i.e. they are either of type `Object` or their
 type information can't be resolved, and the current value is null. In such situations Populace does not have enough
 information to populate the element, but you may chose to do handle this however you like by installing a custom
 `NullObjectStrategy`.

Instance factories can be installed as shown below:

```java
GraphPopulator.Builder builder = GraphPopulator.newBuilder();
GraphPopulator populator = builder
   .withInstanceFactories(builder.instanceFactories()
        .withSpecificFactory(MyType.class, new MyTypeFactory())
        .withNullObjectStrategy(new CustomNullObjectStrategy())
        .build())
   .build();
```

## Registering Customisations

Many of the customisations available in Populace, such as `Mutators`, `Inspectors` etc, are registered at one of several
 levels. These levels are, from most specific to least, listed below:
* **specific types** i.e. you can register a handler from a specific class e.g. `String`, or a specific parameterised type
e.g. `MyType<Integer>`, or a specific array type e.g. `long[]`. Such handlers will be used in preference to any other handler.
* **super types** i.e. you can register a handler for a super type such as `List` and it will be used to handle any
derived types that do not have a more specific handler installed.
* **packages** i.e. you can register a handler to handle any types that belong to specific package e.g. `java.lang.util`.
The handler will be used for any type that belongs to the package that do not have a more specific handler installed.
* **default** this is the default handler to use if the type is not an array type and no other, more specific, handler is
installed.
* **array default** arrays are treated slightly differently. The default array handler will be used where no specific
handler is registered for an array type.

To understand what type will be used at runtime to look up suitable handlers you will first need to understand how Populace
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
or have bounds, e.g. consider the field 'example' in the following code snippet below, Populace will use all
available type information in parent/containing classes to resolve the type variables or bounds to concrete types.

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

If the runtime type of the field is a normal class, then the more specific runtime type will be used.

If the runtime type of the field is a type that has type arguments, then Populace will parameterise the runtime type
using all available type information in the field's compile-time type and parent/container type information, e.g. if a
field type is `List<String>` and the field value is an instance of `ArrayList`, then Populace will resolve the type to
`ArrayList<String>`. Populace will also attempt to resolve any type variables and bounds, as needed.

## Null elements

Similar to [Null Fields](#null-fields), null elements have only the compile time type information of their container.
Populace will attempt to resolve this type information as much as possible, before using it to look up customisations.

## Non-null elements

As per [Non-null Fields](#non-null-fields), non-null elements will also make use of the runtime type information
available to build a more specific type to use to look up customisations.
