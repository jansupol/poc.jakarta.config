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

import jakarta.config.NoSuchObjectException;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

final class MethodInjectable<T> implements Injectable<T> {
    private final Method method;
    private Object value;

    public MethodInjectable(Method method) {
        this.method = method;
    }

    @Override
    public void inject(T instance, Object value) {
        this.value = value;
    }

    @Override
    public Class<?> getType() {
        return method.getReturnType();
    }

    @Override
    public Type getGenericType() {
        return method.getGenericReturnType();
    }

    @Override
    public String name() {
        return method.getName();
    }

    Object getValue() {
        if (value != null) {
            return value;
        }
        throw new NoSuchObjectException("No config value found for name " + name());
    }
}
