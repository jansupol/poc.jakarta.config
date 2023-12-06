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

import poc.jakarta.config.node.Data;

import java.lang.reflect.Type;

public final class StandaloneInjector implements Injector {
    private static final Injector standaloneClassInjector = new StandaloneClassInjector();
    private static final Injector standaloneInterfaceInjector = new StandaloneInterfaceInjector();

    @Override
    public <T> T load(Class<T> clazz, Type type, ConfigLoaderContext ctx) {
        return clazz.isInterface()
                ? standaloneInterfaceInjector.load(clazz, type, ctx)
                : standaloneClassInjector.load(clazz, type, ctx);
    }

    @Override
    public <T> T loadSingle(Class<T> clazz, Type type, ConfigLoaderContext ctx) {
        return standaloneClassInjector.loadSingle(clazz, type, ctx);
    }

    @Override
    public <T> void inject(Class<T> clazz, T t, Data data, ConfigLoaderContext ctx) {
        if (clazz.isInterface()) {
            standaloneInterfaceInjector.inject(clazz, t, data, ctx);
        } else {
            standaloneClassInjector.inject(clazz, t, data, ctx);
        }
    }
}
