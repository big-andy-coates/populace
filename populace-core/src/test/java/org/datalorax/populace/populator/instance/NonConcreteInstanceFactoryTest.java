package org.datalorax.populace.populator.instance;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class NonConcreteInstanceFactoryTest {
    private final Class<List> baseType = List.class;
    private final Class<ArrayList> defaultType = ArrayList.class;
    private InstanceFactory concreteFactory;
    private NonConcreteInstanceFactory factory;
    private Object parent;

    @BeforeMethod
    public void setUp() throws Exception {
        concreteFactory = mock(InstanceFactory.class);
        parent = mock(Object.class);

        factory = new NonConcreteInstanceFactory(baseType, defaultType, concreteFactory);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowIfNullBaseType() throws Exception {
        new NonConcreteInstanceFactory(null, defaultType, concreteFactory);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowIfNullDefaultType() throws Exception {
        new NonConcreteInstanceFactory(baseType, null, concreteFactory);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldThrowIfNullConcreteFactory() throws Exception {
        new NonConcreteInstanceFactory(baseType, defaultType, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowIfRequestedTypeNotSubtypeOfDefaultType() throws Exception {
        // When:
        factory.createInstance(String.class, null);
    }

    @Test
    public void shouldDelegateToConcreteFactoryForConcreteTypes() throws Exception {
        // When:
        factory.createInstance(Vector.class, parent);

        // Then:
        verify(concreteFactory).createInstance(Vector.class, parent);
    }

    @Test
    public void shouldUseConcreteFactoryToCreateDefaultType() throws Exception {
        // When:
        factory.createInstance(List.class, parent);

        // Then:
        verify(concreteFactory).createInstance(defaultType, parent);
    }

    @Test
    public void shouldReturnInstanceFromConcreteFactoryForConcreteTypes() throws Exception {
        // Given:
        final Vector expected = mock(Vector.class);
        when(concreteFactory.createInstance(Vector.class, parent)).thenReturn(expected);

        // When:
        final Vector instance = factory.createInstance(Vector.class, parent);

        // Then:
        assertThat(instance, is(expected));
    }

    @Test
    public void shouldReturnInstanceFromConcreteFactoryForNonConcreteTypes() throws Exception {
        // Given:
        final ArrayList expected = mock(ArrayList.class);
        when(concreteFactory.createInstance(ArrayList.class, parent)).thenReturn(expected);

        // When:
        final List instance = factory.createInstance(List.class, parent);

        // Then:
        assertThat(instance, is(expected));
    }
}