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

import java.lang.reflect.Field;
import java.lang.reflect.Type;

final class FieldInjectable<T> implements Injectable<T> {
    private final Field field;

    FieldInjectable(Field field) {
        this.field = field;
    }

    @Override
    public void inject(T instance, Object value) {
        ReflectUtil.ensureAccessible(field, instance);
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new ConfigException(e);
        }
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }

    @Override
    public Type getGenericType() {
        return field.getGenericType();
    }

    @Override
    public String name() {
        return field.getName();
    }

}
