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

package poc.jakarta.config.internal.loader;

import jakarta.config.ConfigException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

final class StandaloneInterfaceInstantiator implements Instantiator {

    @Override
    public <T> T load(Class<T> clazz, Type type) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz, MethodReturnedValueHolder.class},
                new MethodReturnedValueHolderImpl<>(clazz));
    }

    /*
     * Must be public for JPMS, otherwise the JDK proxy fails
     */
    public static interface MethodReturnedValueHolder<T> extends InvocationHandler {
        /**
         *
         * @param arg The argument so that the user provided method won't collide
         * @return
         */
        MethodReturnedValueHolder toMethodReturnedValueHolder(Object arg);
    }

    class MethodReturnedValueHolderImpl<T> implements MethodReturnedValueHolder<T> {
        private final Class<T> clazz;
        private final Map<String, MethodInjectable<T>> retValues = new HashMap<>();
        private MethodReturnedValueHolderImpl(Class<T> clazz) {
            this.clazz = clazz;
            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.isBridge() && !method.isDefault() && !method.isSynthetic() && method.getParameterCount() == 0) {
                    retValues.put(method.getName(), new MethodInjectable<>(method));
                } else {
                    throw new ConfigException("Could not instantiate an instance with method " + method.getName());
                }
            }
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("toMethodReturnedValueHolder") && args.length == 1) {
                return toMethodReturnedValueHolder(args[0]);
            }
            if (method.getName().equals("toString")) {
                return clazz.getName();
            }
            return retValues.get(method.getName()).getValue();
        }

        @Override
        public MethodReturnedValueHolderImpl toMethodReturnedValueHolder(Object arg) {
            return this;
        }

        Collection<MethodInjectable<T>> getInjectables(Object arg) {
            return retValues.values();
        }
    }
}
