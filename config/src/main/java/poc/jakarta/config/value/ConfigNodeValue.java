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

import java.lang.reflect.Type;

/**
 * <p>
 *   The representation of a Config Node value. For instance, if the property is
 *   <pre>
 *   key1=value1
 *   </pre>
 *   The Config Node represented by <code>key1</code> path would have <code>value1</code> value.
 * </p>
 */
public interface ConfigNodeValue {
    /**
     * Get the actual value of this Config Node converted to a user type.
     * @param type The user requested {@link Class}
     * @param genericType The user requested {@link java.lang.reflect.ParameterizedType}
     * @param convertors The available {@link NodeValueConvertors}
     * @param <T> Expected user type
     * @return The converted value
     */
    default <T> T get(Class<T> type, Type genericType, NodeValueConvertors convertors) {
        return convertors.convert(type, genericType, this);
    }

    default <T> T get(Class<T> type, Type genericType, NodeValueConvertor convertor) {
        return convertor.convert(type, genericType, this);
    }

    /**
     * The default Java type representing this Config Node value. For instance, it could be {@link String},
     * or, in case of an array, {@link java.lang.reflect.Array}.
     * @return The default Java Type of this Config Node value.
     */
    Class<?> defaultType();

    /**
     * The actual value of this Config Node. It could be a {@link String}, or, in case of an array, an array of
     * {@link ConfigNodeValue}s.
     * @return The actual value of this Config Node
     */
    Object defaultValue();
}
