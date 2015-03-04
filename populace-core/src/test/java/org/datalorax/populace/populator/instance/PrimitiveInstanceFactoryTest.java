package org.datalorax.populace.populator.instance;

import org.datalorax.populace.type.TypeUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PrimitiveInstanceFactoryTest {
    @Test(dataProvider = "primitive")
    public void shouldSupportPrimitiveType(Class<?> type) throws Exception {
        // Given:
        final Class<?> boxed = TypeUtils.getBoxedTypeForPrimitive(type);
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
        return asObjectArray(TypeUtils.getPrimitiveTypes());
    }

    @DataProvider(name = "boxed")
    public Object[][] getBoxedPrimitives() {
        return asObjectArray(TypeUtils.getBoxedPrimitiveTypes());
    }

    private static Object[][] asObjectArray(final List<Class<?>> types) {
        final Object[][] data = new Object[types.size()][];
        int i = 0;
        for (Class<?> type : types) {
            data[i++] = new Object[] {type};
        }
        return data;
    }
}