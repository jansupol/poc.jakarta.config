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

package poc.jakarta.config.value;

import jakarta.config.ConfigException;

import java.lang.reflect.Type;
import java.util.Optional;

/**
 * An implementation of this interface is a manager class containing all {@link NodeValueConvertor}s.
 * The implementation is capable of finding a proper Config Node value and convert it to a requested type.
 * The default supported types are primitive types and the respective java classes, {@link Number} and its subclasses,
 * {@link String}, and the collections {@link java.util.Collection}, {@link java.util.List}, and {@link java.util.Set}
 */
public interface NodeValueConvertors {
    /**
     * Find the convertor for the {@link ConfigNodeValue}.
     * @param type The user requested {@link Class}
     * @param genericType The user requested {@link java.lang.reflect.ParameterizedType}
     * @param defaultType {@link ConfigNodeValue#defaultType()}
     * @return Optinal {@link NodeValueConvertor} for the user types and the {@link ConfigNodeValue} default type
     * @see ConfigNodeValue
     */
    Optional<NodeValueConvertor> findConvertor(Class<?> type, Type genericType, Class<?> defaultType);

    /**
     * Converts the {@link ConfigNodeValue} to a user provided type.
     * @param type The user requested {@link Class}
     * @param genericType The user requested {@link java.lang.reflect.ParameterizedType}
     * @param value {@link ConfigNodeValue#defaultValue()}
     * @param <T> Expected user type
     * @return The converted value of {@link ConfigNodeValue} converted to a user defined type.
     */
    default <T> T convert(Class<T> type, Type genericType, ConfigNodeValue value) {
       return convert(type, genericType, value.defaultType(), value.defaultValue());
    }

    /**
     * Converts the provided value of a provided type to a user provided type.
     * @param type The user requested {@link Class}
     * @param genericType The user requested {@link java.lang.reflect.ParameterizedType}
     * @param valueType The type of the value to be converted to a user provided type
     * @param value The value to be converted to a user provided type
     * @param <T> Expected user type
     * @return The converted value to a user provided type
     */
    default <T> T convert(Class<T> type, Type genericType, Class<?> valueType, Object value) {
        Optional<NodeValueConvertor> convertor = findConvertor(type, genericType, valueType);
        return convertor
                .orElseThrow(() -> new ConfigException(
                        "Unable to convert type " + type + " generic type " + genericType + " from " + valueType))
                .convert(type, genericType, value);
    }
}
