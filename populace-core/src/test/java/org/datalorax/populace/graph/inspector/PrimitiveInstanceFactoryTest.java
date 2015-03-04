package org.datalorax.populace.graph.inspector;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PrimitiveInstanceFactoryTest {
    private static final Object[][] PRIMITIVE_TYPES = {
        {boolean.class}, {byte.class}, {char.class}, {short.class}, {int.class}, {long.class}, {float.class}, {double.class}
    };

    private static final Object[][] BOX_PRIMITIVE_TYPES = {
        {Boolean.class}, {Byte.class}, {Character.class}, {Short.class}, {Integer.class}, {Long.class}, {Float.class}, {Double.class}
    };

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_BOXED_TYPES = new HashMap<Class<?>, Class<?>>() {{
       put(boolean.class, Boolean.class);
       put(byte.class, Byte.class);
       put(char.class, Character.class);
       put(short.class, Short.class);
       put(int.class, Integer.class);
       put(long.class, Long.class);
       put(float.class, Float.class);
       put(double.class, Double.class);
    }};

    @Test(dataProvider = "primitive")
    public void shouldSupportPrimitiveType(Class<?> type) throws Exception {
        // Given:
        final Class<?> boxed = PRIMITIVE_TO_BOXED_TYPES.get(type);
        // When:
        final Object result = PrimitiveInstanceFactory.INSTANCE.createInstance(type, null);

        // Then:
        assertThat(result, is(notNullValue()));
        assertThat(result, is(instanceOf(boxed)));
    }

    @Test(dataProvider = "boxed")
    public void shouldSupportBoxedPrimitiveType(Class<?> type) throws Exception {
        // When:
        final Object result = PrimitiveInstanceFactory.INSTANCE.createInstance(type, null);

        // Then:
        assertThat(result, is(notNullValue()));
        assertThat(result, is(instanceOf(type)));
    }

    @Test(dataProvider = "primitive")
    public void shouldSupportPrimitiveTypes(Class<?> type) throws Exception {
        assertThat(PrimitiveInstanceFactory.INSTANCE.supportsType(type), is(true));
    }

    @Test(dataProvider = "boxed")
    public void shouldSupportBoxedPrimitiveTypes(Class<?> type) throws Exception {
        assertThat(PrimitiveInstanceFactory.INSTANCE.supportsType(type), is(true));
    }

    @Test
    public void shouldNotSupportNonPrimitiveTypes() throws Exception {
        assertThat(PrimitiveInstanceFactory.INSTANCE.supportsType(String.class), is(false));
    }

    @DataProvider(name = "primitive")
    public Object[][] getPrimitives() {
        return PRIMITIVE_TYPES;
    }

    @DataProvider(name = "boxed")
    public Object[][] getBoxedPrimitives() {
        return BOX_PRIMITIVE_TYPES;
    }
}