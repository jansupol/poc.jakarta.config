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

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

final class StandaloneInstantiator implements Instantiator {
    private static StandaloneClassInstantiator standaloneClassInstantiator = new StandaloneClassInstantiator();
    private static StandaloneInterfaceInstantiator standaloneInterfaceInstantiator = new StandaloneInterfaceInstantiator();

    @Override
    public <T> T load(Class<T> clazz, Type type) {
        if (Collection.class.equals(clazz)) {
            LinkedList<Object> list = new LinkedList<>();
            list.add(load(ReflectUtil.getClass(type), ReflectUtil.getType(type)));
            return (T) list;
        }
        if (Set.class.equals(clazz)) {
            Set<Object> set = new HashSet<>();
            set.add(load(ReflectUtil.getClass(type), ReflectUtil.getType(type)));
            return (T) set;
        }

        if (clazz.isInterface()) {
            return standaloneInterfaceInstantiator.load(clazz, type);
        } else {
            return standaloneClassInstantiator.load(clazz, type);
        }
    }
}
