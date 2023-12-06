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
 * A single Type value convertor
 */
public interface NodeValueConvertor {

    /**
     * Defines whether this convertor is capable of converting the provided type to a user requested type
     * @param type The user requested {@link Class}
     * @param genericType The user requested {@link java.lang.reflect.ParameterizedType}
     * @param defaultType The type of the value provided by {@link ConfigNodeValue#defaultValue()}
     * @return {@code true} if this convertor is capable of performing requested type conversion.
     */
    boolean isConvertible(Class<?> type, Type genericType, Class<?> defaultType);

    /**
     * Converts the value to a user requested type
     * @param type The user requested {@link Class}
     * @param genericType The user requested {@link java.lang.reflect.ParameterizedType}
     * @param defaultValue the value of {@link ConfigNodeValue#defaultValue()}
     * @param <T> The requested user type
     * @return The converted value of a {@link ConfigNodeValue} to a user requested type
     */
    <T> T convert(Class<T> type, Type genericType, Object defaultValue);

}
