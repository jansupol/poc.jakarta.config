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

import jakarta.config.NoSuchObjectException;
import poc.jakarta.config.internal.node.ConfigPath;
import poc.jakarta.config.node.Data;
import poc.jakarta.config.value.ConfigNodeValue;
import poc.jakarta.config.value.NodeValueConvertor;

import java.lang.reflect.Type;
import java.util.Optional;

abstract class AbstractStandaloneInjector implements Injector {
    private final boolean isClassInjector;

    protected AbstractStandaloneInjector(boolean isClassInjector) {
        this.isClassInjector = isClassInjector;
    }

    @Override
    public <T> T load(Class<T> clazz, Type type, ConfigLoaderContext ctx) {
        T t = ctx.builder.instantiator.load(clazz, type);
        inject(clazz, t, ctx);
        return t;
    }

    public <T> T loadSingle(Class<T> clazz, Type type, ConfigLoaderContext ctx) {
        Data data = fetchData(ctx);
        if (data.value().isEmpty()) {
            throw new NoSuchObjectException("No value for key " + ctx.builder.path.toString());
        }
        return ctx.builder.convertors.convert(clazz, type, data.valueOrNull());

    }

    protected <T> void inject(Class<T> type, T t, ConfigLoaderContext ctx) {
        inject(type, t, fetchData(ctx), ctx);
    }

    protected <T> void inject(T t, Injectable<T> injectable, Data data, ConfigLoaderContext ctx) {
        Optional<Data.ChildKeyValue> property = data.property(injectable.name());
        Optional<NodeValueConvertor> nodeValueConvertor = Optional.empty();
        ConfigNodeValue nodeValue = null;
        if (property.isPresent()) {
            nodeValue = property.get().valueOrNull(); // value is always present, filtered only nodes with value to Data
            final ConfigNodeValue value = nodeValue;
            nodeValueConvertor = ctx.builder.convertors.findConvertor(
                    injectable.getType(), injectable.getGenericType(), nodeValue.defaultType());
            // The convertor might not exist if the leaf has the same path as a sub-node - do not throw an exception
            nodeValueConvertor.ifPresent((convertor) -> {
                Object converted = convertor.convert(injectable.getType(), injectable.getGenericType(), value.defaultValue());
                injectable.inject(t, converted);
            });
        } else if (isClassInjector && ctx.builder.options.throwOnMissingProperty) {
            String path = ctx.builder.path.toString();
            throw new NoSuchObjectException("No Config Value for property="
                    + path
                    + (path.isEmpty() ? "" : ".")
                    + injectable.name()
                    + " (" + t.getClass() + "." + injectable.name() + ")"
            );
        }
        Optional<Data> subData = null;
        if (nodeValueConvertor.isEmpty()) {
            // sub-node structure ?
            ConfigPath newPath = ConfigPath.from(ctx.builder.path).append(injectable.name()).build();
            subData = ConfigLoaderContext.configNodeWalker.walk(ctx.builder.root, newPath);
            subData.ifPresent((d) -> {
                ConfigLoaderContext subCtx = new ConfigLoaderContext.Builder(ctx.builder)
                        .path(newPath)
                        .build();
                Object subInstance = ctx.builder.instantiator.load(injectable.getType(), injectable.getGenericType());
                ctx.injector().inject((Class<T>) injectable.getType(), (T) subInstance, d, subCtx);
                injectable.inject(t, subInstance);
            });
        }
        if (property.isPresent() && nodeValueConvertor.isEmpty() && subData.isEmpty()) {
            // If there is a property of the same name as the injectable that should be injected
            // And there is no convertor for the property
            // And it is not a Config sub-node
            // throw the exception
            ctx.builder.convertors.convert(injectable.getType(), injectable.getGenericType(), nodeValue);
        }
    }

    protected Data fetchData(ConfigLoaderContext ctx) {
        Optional<Data> data = ConfigLoaderContext.configNodeWalker.walk(ctx.builder.root, ctx.builder.path);
        if (data.isEmpty()) {
            throw new NoSuchObjectException("Unknown path " + ctx.builder.path);
        }
        return data.get();
    }
}
