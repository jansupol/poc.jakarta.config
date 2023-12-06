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

/**
 * Implementation of this interface is responsible for creating the instance of a provided class.
 * The {@link Instantiator} is not responsible for filling the data from the Config to be provided when invoking getters.
 */
public interface Instantiator {
    /**
     * Creates an instance of a class.
     * @param clazz the user provided class/interface class to be instantiated.
     * @param type  the user provided type to be instantiated.
     * @param <T>   the user provided type.
     * @return      an instance of the type provided.
     */
    <T> T load(Class<T> clazz, Type type);
}
