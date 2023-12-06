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

import jakarta.config.TypeToken;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class ReflectUtil {
    public static <T> Class<T> getClass(TypeToken<T> tokenType) {
        return (Class<T>) tokenType.erase();
    }

    public static Type getType(TypeToken<?> typeToken) {
        Type type = typeToken.type();
        return getType(type);
    }

    public static Class<?> getClass(Type type) {
        if (ParameterizedType.class.isInstance(type)) {
            return getClass(((ParameterizedType) type).getRawType());
        } else {
            return (Class<?>) type;
        }
    }

    public static Type getType(Type type) {
        if (ParameterizedType.class.isInstance(type)) {
            return ((ParameterizedType) type).getActualTypeArguments()[0];
        } else {
            return type;
        }
    }

    public static void ensureAccessible(AccessibleObject accessible, Object target) {
        try {
            if (!accessible.canAccess(target)) {
                accessible.setAccessible(true);
            }
        } catch (Exception e) {
            // consume. It will fail later with invoking the executable
            e.printStackTrace();
        }
    }
}
