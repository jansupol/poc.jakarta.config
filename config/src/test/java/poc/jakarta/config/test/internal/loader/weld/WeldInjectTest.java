/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package poc.jakarta.config.test.internal.loader.weld;

import jakarta.config.Configuration;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class WeldInjectTest {
    protected static SeContainer container;

    @BeforeAll
    public static void setup() {
        SeContainerInitializer containerInitializer = SeContainerInitializer.newInstance();
        container = containerInitializer.initialize();
    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (container != null && container.isRunning()) {
            container.close();
        }
    }

    @Configuration(path = "property2")
    public static interface Property2 {
        String sub1();
        String sub2();
    }

    @ApplicationScoped
    public static class AnyBean {
        @Inject
        Property2 property2;

        @Inject
        InnerBean inner;

        public InnerBean getInner() {
            return inner;
        }
        public Property2 property2() {
            return property2;
        }
    }

    @ApplicationScoped
    public static class InnerBean {
        @Override
        public String toString() {
            return getClass().getName();
        }
    }

    @Test
    public void testProperty2() {
        Property2 property2 = CDI.current().select(Property2.class).get();
        Assertions.assertNotNull(property2);

        Assertions.assertNotNull(property2.sub1());
        Assertions.assertNotNull(property2.sub2());

        Assertions.assertEquals("value21", property2.sub1());
        Assertions.assertEquals("value22", property2.sub2());
    }

    @Test
    public void testAnyBean() {
        AnyBean anyBean = CDI.current().select(AnyBean.class).get();
        Assertions.assertNotNull(anyBean);
        Assertions.assertNotNull(anyBean.property2());

        Assertions.assertEquals(InnerBean.class.getName(), anyBean.getInner().toString());

        Assertions.assertNotNull(anyBean.property2().sub1());
        Assertions.assertNotNull(anyBean.property2().sub2());

        Assertions.assertEquals("value21", anyBean.property2().sub1());
        Assertions.assertEquals("value22", anyBean.property2().sub2());
    }
}
