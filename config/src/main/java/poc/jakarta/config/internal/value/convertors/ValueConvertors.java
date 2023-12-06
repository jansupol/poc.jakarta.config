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
import poc.jakarta.config.value.NodeValueConvertors;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public final class ValueConvertors implements NodeValueConvertors {
    private final List<NodeValueConvertor> convertors = new LinkedList<>();

    public ValueConvertors(Collection<NodeValueConvertor> convertors) {
        this.convertors.addAll(convertors);
        this.convertors.add(new StringConvertor());
        this.convertors.add(new CollectionConvertor(this));
        this.convertors.add(new ListConvertor(this));
        this.convertors.add(new SetConvertor(this));
        this.convertors.add(new PrimitiveTypeConvertor());
    }

    @Override
    public Optional<NodeValueConvertor> findConvertor(Class<?> type, Type genericType, Class<?> defaultType) {
        for (NodeValueConvertor convertor : convertors) {
            if (convertor.isConvertible(type, genericType, defaultType)) {
                return Optional.of(convertor);
            }
        }
        return Optional.empty();
    }
}
