# Populace
Populace is flexible library for walking and populating object graphs. It is often used to facilitate solid test coverage
of serialisation mechanisms where complex domain models are used and full object graphs need to be populated.

Populace supports integration points to allow the behaviour of how the object graph is walked, how new entities are
instantiated, and how existing fields are mutated. This allows Populace to be fully customised to match both the domain
model and the needs of your project.

## Usage
Populace will soon be available on Maven Central. For now you can download the latest release from the
[releases page](https://github.com/datalorax/populace/releases).

Populace is comprised of two core features:

1. *Graph walking* - `GraphWalker` allows a user supplied `visitor` to visit all of the fields on an object, and all of
the fields of objects referenced by that object, in a recursive manner. The walk can be customised by installing different
`FieldFilter`s, to control what fields and included or excluded from the walk, and by installing `Inspector`s, to control
what fields an instance exposes, or, in the case of collection types, what children the field exposes.
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

FieldVisitor visitor = FieldVisitors.chain(
    SetAccessibleFieldVisitor.INSTANCE,
    field -> System.out.println(field.getName() + "=" + field.getValue()));

walker.walk(contacts, visitor);
```

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
