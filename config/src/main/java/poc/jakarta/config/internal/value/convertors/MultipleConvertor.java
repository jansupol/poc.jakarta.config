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
package poc.jakarta.config.internal.value.convertors;

import poc.jakarta.config.value.ConfigNodeValue;
import poc.jakarta.config.value.NodeValueConvertor;
import poc.jakarta.config.value.NodeValueConvertors;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

abstract class MultipleConvertor<TYPE> implements NodeValueConvertor {

    private final Class<TYPE> expectedClass;
    protected final NodeValueConvertors convertors;

    MultipleConvertor(Class<TYPE> expectedClass, NodeValueConvertors convertors) {
        this.expectedClass = expectedClass;
        this.convertors = convertors;
    }

    @Override
    public boolean isConvertible(Class<?> type, Type genericType, Class<?> defaultType) {
        if (!expectedClass.equals(type) || !ParameterizedType.class.isInstance(genericType)) {
            return false;
        }
        Type actualTypeArg = ((ParameterizedType) genericType).getActualTypeArguments()[0];
        if (Array.class.equals(defaultType) || (actualTypeArg.equals(defaultType))) {
            return true;
        }
        return false;
    }

    @Override
    public <T> T convert(Class<T> type, Type genericType, Object defaultValue) {
        if (defaultValue.getClass().isArray()) {
            return convert(type, genericType, (ConfigNodeValue[]) defaultValue);
        } else {
            return convertSingleton(type, genericType, defaultValue);
        }
    }

    protected <T> T convert(Class<T> type, Type genericType, ConfigNodeValue[] defaultValue) {
        List<Object> list = new ArrayList<>(defaultValue.length);
        Type actualTypeArg = ((ParameterizedType) genericType).getActualTypeArguments()[0];
        for (ConfigNodeValue value : defaultValue) {
            list.add(convertors.convert((Class<?>) actualTypeArg, actualTypeArg, value));
        }
        return (T) list;
    }

    protected abstract <T> T convertSingleton(Class<T> type, Type genericType, Object defaultValue);
}
