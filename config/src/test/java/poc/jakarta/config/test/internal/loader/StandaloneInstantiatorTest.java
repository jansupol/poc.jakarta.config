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

package poc.jakarta.config.test.internal.loader;

import jakarta.config.TypeToken;
import poc.jakarta.config.internal.loader.Instantiator;
import poc.jakarta.config.internal.loader.ReflectUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class StandaloneInstantiatorTest {
    static class ConfigMapping {
        String key1;
        String key2;
    }

    @Test
    public void testCollection() {
        Instantiator instantiator = newStandaloneInstantiator();
        TypeToken<Collection<ConfigMapping>> token = new TypeToken<>() {
        };
        Collection<ConfigMapping> instance = instantiator.load(ReflectUtil.getClass(token), ReflectUtil.getType(token));
        Assertions.assertNotNull(instance);
        Assertions.assertTrue(List.class.isInstance(instance));

        TypeToken<Set<ConfigMapping>> setToken = new TypeToken<>() {
        };
        Set<ConfigMapping> set = instantiator.load(ReflectUtil.getClass(setToken), ReflectUtil.getType(setToken));
        Assertions.assertNotNull(set);
        Assertions.assertTrue(Set.class.isInstance(set));
    }

    @Test
    public void testMapping() {
        Instantiator instantiator = newStandaloneClassInstantiator();
        TypeToken<ConfigMapping> token = new TypeToken<>() {
        };
        ConfigMapping instance = instantiator.load(ReflectUtil.getClass(token), ReflectUtil.getType(token));
        Assertions.assertNotNull(instance);
        Assertions.assertTrue(ConfigMapping.class.isInstance(instance));
    }

    private Instantiator newStandaloneInstantiator() {
        return newInstantiator("poc.jakarta.config.internal.loader.StandaloneInstantiator");
    }

    private Instantiator newStandaloneClassInstantiator() {
        return newInstantiator("poc.jakarta.config.internal.loader.StandaloneClassInstantiator");
    }

    private Instantiator newInstantiator(String instantiatorName) {
        try {
            Class clazz = Class.forName(instantiatorName);
            Constructor constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return (Instantiator) constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
