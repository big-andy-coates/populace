# JaxB Annotation module for Populace

This module provides support for the applicable subset of JaxB annotations that can be utilised to control graph walking
and population.

## Usage
The default configuration for this module can be installed as follows:

```java
GraphPopulator populator = PopulaceJaxB.install(GraphPopulator.newBuilder()).build();
```

Alternatively, individual customisations can be applied manually.

### Examples

The examples below use the following simple domain model:

```java
public class Contacts {
    @XmlElement
    private List<Person> people = new ArrayList<>();

    public List<Person> getPeople() {
        return people;
    }
}

@XmlJavaTypeAdapter(PersonAdapter.class)
public interface Person {
    @XmlElement
    String getName();

    @XmlElement
    Address getAddress();

    @XmlElement
    CreditCardDetails getCardDetails();
}

public class PersonImpl implements Person {
    private String name;

    private Address address;

    @XmlTransient   // Won't be visited
    private CreditCardDetails cardDetails;

    public PersonImpl(final String name, final Address address) {
        this.name = name;
        this.address = address;
    }

    // Used by Populace.
    private PersonImpl() {
        name = null;
        address = null;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public CreditCardDetails getCardDetails() {
        return cardDetails;
    }
}

public class PersonAdapter extends XmlAdapter<PersonImpl, Person> {
    @Override
    public Person unmarshal(final PersonImpl v) throws Exception {
        return v;
    }

    @Override
    public PersonImpl marshal(final Person v) throws Exception {
        return (PersonImpl)v;
    }
}

public class CreditCardDetails {
    private String cardNumber;

    public String getCardNumber() {
        return cardNumber;
    }
}
```

#### Populate example
You can create a populated instance of `Contacts` with the following code:

```java
GraphPopulator populator = PopulaceJaxB.install(GraphPopulator.newBuilder()).build();
Contacts contacts = populator.populate(Contacts.class);
```

The presence of the `@XmlJavaTypeAdapter` annotation on the `Person` interface tells Populace what type to instantiate
when it encounters a field of type `Person`. While the presence of the `@XmlTransient` annotation on the `cardDetails`
field tells Populace to skip this field.

## Supported annotations

1. @XmlTransient - the `ExcludeXmlTransientFields` filter excludes any fields annotated with `@XmlTransient`.
1. @XmlJavaTypeAdapter - the `JaxBInstanceFactory` will attempt to instantiate any type is annotated with
`@XmlJavaTypeAdatper` by first instantiating the `@XmlAdatper`s bound type and then using the `@XmlAdatper` to marshal
to the value type.

## Known limitations
1. `@XmlTransient` is not currently detected if on getter or setter methods.
