package poc.jakarta.config.test;

import jakarta.config.Configuration;
import jakarta.config.Loader;
import poc.jakarta.config.PocConfigLoader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class PocConfigClassLoaderTest {
    @Configuration
    static class JakartaConfigProperties {
        String property1;
        static class Property2 {
            String sub1;
            String sub2;

            String sub; // null - not in the config file
        }
        Property2 property2;
        Collection<String> property3;

        static class Property5 {
            String sub1;
            String sub2;
        }
//        String property5;
        Property5 property5;
    }

    @Test
    public void testDefaultProperties() {
        JakartaConfigProperties properties = PocConfigLoader.builder()
                .optionalFields(true)
                .build()
                .load(JakartaConfigProperties.class);

        Assertions.assertNotNull(properties);
        Assertions.assertNotNull(properties.property1);
        Assertions.assertEquals("value1", properties.property1);

        Assertions.assertNotNull(properties.property2);
        Assertions.assertNotNull(properties.property2.sub1);
        Assertions.assertNotNull(properties.property2.sub2);
        Assertions.assertEquals("value21", properties.property2.sub1);
        Assertions.assertEquals("value22", properties.property2.sub2);

        Assertions.assertNull(properties.property2.sub);

        Assertions.assertNotNull(properties.property3);
        Assertions.assertEquals(ArrayList.class, properties.property3.getClass());
        Assertions.assertEquals(3, properties.property3.size());

        Iterator<String> property3Iterator = properties.property3.iterator();
        Assertions.assertEquals("value31", property3Iterator.next());
        Assertions.assertEquals("value32", property3Iterator.next());
        Assertions.assertEquals("value33", property3Iterator.next());
    }

    @Configuration
    static class SomeSub {
        String sub1;
        String sub2;
    }

    @Test
    public void testSub() {
        Loader loader = PocConfigLoader.builder().path("property2").build();
        SomeSub properties = loader.load(SomeSub.class);

        Assertions.assertNotNull(properties);
        Assertions.assertEquals("value21", properties.sub1);
        Assertions.assertEquals("value22", properties.sub2);
    }

    @Configuration
    static class Primitives {
        int int__;
        Character char__;
        float float__;
    }

    @Test
    public void testPrimitives() {
        Loader loader = PocConfigLoader.builder().path("property4").build();
        Primitives properties = loader.load(Primitives.class);

        Assertions.assertNotNull(properties);
        Assertions.assertEquals(100, properties.int__);
        Assertions.assertEquals(4.5f, properties.float__);
        Assertions.assertEquals('A', properties.char__);
    }

    @Test
    public void loadSingle() {
        PocConfigLoader loader = PocConfigLoader.builder().path("property2.sub1").optionalAnnotation(true).build();
        String loaded = loader.loadSingle(String.class);
        Assertions.assertEquals("value21", loaded);
    }
}
