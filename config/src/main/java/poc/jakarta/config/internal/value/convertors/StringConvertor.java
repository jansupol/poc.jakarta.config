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

import poc.jakarta.config.value.NodeValueConvertor;

import java.lang.reflect.Type;

final class StringConvertor implements NodeValueConvertor {

    @Override
    public boolean isConvertible(Class<?> type, Type genericType, Class<?> defaultType) {
        return CharSequence.class.isAssignableFrom(type) && CharSequence.class.isAssignableFrom(defaultType);
    }

    @Override
    public <T> T convert(Class<T> type, Type genericType, Object defaultValue) {
        CharSequence value = (CharSequence) defaultValue;
        return (T) value.toString();
    }
}
