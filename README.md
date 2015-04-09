# Populace
Populace is flexible library for walking and populating object graphs. It is often used to facilitate solid test coverage
of serialisation mechanisms where complex domain models are used and full object graphs need to be populated.

Populace supports integration points to allow the behaviour of how the object graph is walked, how new entities are
instantiated, and how existing fields are mutated. This allows Populace to be fully customised to match both the domain
model and the needs of your project.

## Usage
Populace is comprised of two core features:

1. *Graph walking* - `GraphWalker` allows a user supplied `fieldVisitor` and `elementVisitor` to visit all of the fields
and, in the case of container types such as `List` or `Set`, any child elements on an object, and all of the fields and
elements of objects referenced by that object, in a recursive manner. The walk can be customised by installing different
`FieldFilter`s, to control what fields are included or excluded from the walk, and by installing `Inspector`s, to control
what fields an instance exposes, or, in the case of collection types, what child elements the field exposes.
2. *Graph population* - `GraphPopulator` walks the object graph, performing customisable operations on each field, such as
ensuring the field is not null or empty, or mutating any existing value. This process can be customised by way installing
`Mutator`s, which control how field values are changed and `InstanceFactory`s, which control how new instances of different
types are instantiated.

Additional modules provide specialised extensions to the core Populace library:

1. *[JaxB Annotations support](https://github.com/datalorax/populace/tree/master/populace-module-jaxb-annotations)* -
  provides support for the applicable subset of JaxB annotations. For example, it provides an `InstanceFactory` that
  recognises the [@XmlJavaTypeAdapter](http://docs.oracle.com/javase/8/docs/api/javax/xml/bind/annotation/adapters/XmlJavaTypeAdapter.html)
  annotation, which is commonly used to link interfaces to concrete types. The factory can then instantiate the appropriate
  concrete implementation when a interface field is encountered.
  See the [read me](https://github.com/datalorax/populace/tree/master/populace-module-jaxb-annotations/README.md) for more info

### Examples

The examples below use the following simple domain model:

```java
public class Contacts {
    private List<Person> people = new ArrayList<>();

    public List<Person> getPeople() {
        return people;
    }
}

public class Person {
    private final String name;
    private final Address address;

    public Person(final String name, final Address address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    // Used by Populace.
    private Person() {
        name = null;
        address = null;
    }
}

public class Address {
    private final List<String> lines;

    public Address(List<String> lines) {
        this.lines = Collections.unmodifiableList(new ArrayList<>(lines));
    }

    public List<String> getLines() {
        return lines;
    }

    // Used by Populace
    private Address() {
        lines = null;
    }
}
```

#### Walk example
You can walk all the fields of a populated `Contacts` list as follows, (NB: Without the `SetAccessibleFieldVisitor`
visitor the code would throw a `IllegalAccessException` as the fields are private).

```java
Contacts contacts = createPopulatedContacts();
GraphWalker walker = GraphWalker.newBuilder().build();

FieldVisitor fieldVisitor = FieldVisitors.chain(
    SetAccessibleFieldVisitor.INSTANCE,
    field -> System.out.println(field.getName() + "=" + field.getValue()));

ElementVisitor elementVisitor = e -> System.out.println("element = " + e.getValue());

walker.walk(contacts, fieldVisitor, elementVisitor);
```

The `fieldVisitor` will be called back on all fields encountered while walking and the `elementVisitor` would be called
back on for all child elements of container types, such as the `Address` lines.

#### Populate example
You can create a populated instance of `Contacts` with the following code:

```java
GraphPopulator populator = GraphPopulator.newBuilder().build();
Contacts contacts = populator.populate(Contacts.class);
```

Or you can populate an existing instance of `Contacts` with the following code:

```java
GraphPopulator populator = GraphPopulator.newBuilder().build();
Contacts contacts = populator.populate(new Contacts());
```

### Known limitations
#### `Collection` fields
Due to the restrictions of the `Collection` API it is not possible to mutate collections of immutable types,
(i.e. types where the mutator returns a new instance, rather than mutating the existing element). This is because there
 is no reliable way to replace a single element in a `Collection`.

By default Populace will log a warning when immutable Collection elements are encountered.

Most standard library sub-types of `Collection`, such as `List` and `Set` are supported, through their own specialised
types. If your code contains collection types, or those of third-party libraries not supported by Populace, you can
implement you own custom `Inspector` to handle the type.

#### Null `Object` fields
There is not enough type information to populate fields of type `Object` that are null - what object should be created?
 By default, Populace will just log a warning when a null `Object` field is encountered. However, this is customisable:
 you can install your own `NullObjectStrategy` into the `InstanceFactories` collection used by Populace.

## Download

Populace is available from maven central.

### Maven
If you're using [Maven](http://maven.apache.org/), then add the following to your `pom.xml`:
```
<dependencies>
  <dependency>
      <groupId>com.github.datalorax.populace</groupId>
      <artifactId>populace-core</artifactId>
      <version>1.0.0</version>
  </dependency>
</dependencies>
```

### Gradle
If you're using [Gradle](http://www.gradle.org/), then add the following to your `build.gradle`:

```
dependencies {
    compile 'com.github.datalorax.populace:populace-core:1.0.0'
}
```

# SBT
If you're using [SBT](http://code.google.com/p/simple-build-tool/), then add the following to your project:

```
libraryDependencies += "com.github.datalorax.populace" % "populace-core" % "1.0.0"
```

## License

Populace is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.

## How to build
[![Build Status](https://travis-ci.org/datalorax/populace.svg?branch=master)](https://travis-ci.org/datalorax/populace)

### Prerequisites
Before building the code you'll need:

* JDK 1.8 or above
* To clone this repo.

### Howto: Compile and run the tests.
Run the following commmand from the root directory of the project:
> `gradlew`

The script automatically downloads the correct Gradle version, so you won't have to do anything else. Gradle will then
compile the project, run the tests, generate the java docs and build the project jars.
If everything runs successfully, each module's jar will be located under its build/libs directory.

To see a full list of tasks available run:
> `gradlew tasks`

### Howto: Create IntelliJ project files
Gradle includes a task that creates all files required to develop populace in IntelliJ. Run the following command:
> `gradlew idea`

Then import the project into your workspace or open the root populace.ipr project file.

### Howto: Create Eclipse project files
Gradle includes a task that creates all files required to develop populace in Eclipse. Run the following command:
> `gradlew ecplise`

Then import the project into your workspace.

## Contributing
Contributing is good! Please get stuck in, but first read [notes on contributing](./CONTRIBUTING.md)
