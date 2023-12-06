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

package poc.jakarta.config;

import jakarta.config.ConfigException;
import jakarta.config.Loader;
import jakarta.config.TypeToken;
import poc.jakarta.config.internal.loader.ConfigLoaderContext;
import poc.jakarta.config.internal.loader.ReflectUtil;
import poc.jakarta.config.internal.loader.PathMatcher;
import poc.jakarta.config.internal.node.ConfigNode;
import poc.jakarta.config.internal.node.ConfigPath;
import poc.jakarta.config.internal.source.DefaultPropertyFileSource;
import poc.jakarta.config.internal.util.AnnotationReader;
import poc.jakarta.config.source.ConfigSource;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public final class PocConfigLoader implements Loader {
    private static final AtomicReference<Loader> cachedLoader = new AtomicReference<>(null);

    private ConfigNode configRoot;
    private final Builder builder;

    public PocConfigLoader() {
        builder = new Builder();
        builder.matcher = new PathMatcher(null);
        cachedLoader.compareAndSet(null, this);
    }

    public PocConfigLoader(Builder builder) {
        this.builder = new Builder(builder);
    }

    @Override
    public <T> T load(Class<T> type) {
        if (Loader.class.equals(type)) {
            Loader loader = cachedLoader.get();
            return (T) (loader != null ? loader : this);
        }

        final ConfigLoaderContext context = configLoaderContext(type);
        return context.injector().load(type, type, context);
    }

    @Override
    public <T> T load(TypeToken<T> type) {
        return load(ReflectUtil.getClass(type), ReflectUtil.getType(type));
    }

    @Override
    public Loader path(String s) {
        final PocConfigLoader loader = new PocConfigLoader(builder);
        loader.builder.matcher = new PathMatcher(s);
        return loader;
    }

    public <T> T load(Class<T> clazz, Type genericType) {
        final ConfigLoaderContext context = configLoaderContext(clazz);
        return context.injector().load(clazz, genericType, context);
    }

    public <T> T loadSingle(Class<T> clazz) {
        return loadSingle(clazz, clazz);
    }

    public <T> T loadSingle(TypeToken<T> type) {
        return loadSingle(ReflectUtil.getClass(type), ReflectUtil.getType(type));
    }

    public <T> T loadSingle(Class<T> clazz, Type genericType) {
        final ConfigLoaderContext context = configLoaderContext(clazz);
        return context.injector().loadSingle(clazz, genericType, context);
    }

    public static Builder builder() {
        return new Builder();
    }

    private ConfigNode configRoot() {
        if (configRoot == null) {
            synchronized (this) {
                if (configRoot == null) {
                    Iterator<ConfigSource> its = builder.sources.iterator();
                    configRoot = (ConfigNode) its.next().load(null);
                    while (its.hasNext()) {
                        its.next().load(configRoot);
                    }
                }
            }
        }
        return configRoot;
    }

    private ConfigLoaderContext configLoaderContext(Class<?> type) {
        final Optional<String> annotationPath = AnnotationReader.readPath(type);
        final String annotationPathValue = builder.options.throwOnMissingConfigurationAnnotation
                ? annotationPath.orElseThrow(() -> new ConfigException("No @Configuration annotation found"))
                : annotationPath.orElse("");
        final ConfigPath path = builder.matcher.isSpecified()
                ? builder.matcher.path()
                : ConfigPath.from(annotationPathValue).build();

        final ConfigLoaderContext context = ConfigLoaderContext
                .builder(configRoot())
                .path(path)
                .options(builder.options)
                .build();
        return context;
    }

    public static class Builder {
        private final List<ConfigSource> sources = new LinkedList<>();
        private PathMatcher matcher;
        private ConfigLoaderContext.Options options = new ConfigLoaderContext.Options();
        private Builder() {
            sources.add(new DefaultPropertyFileSource());
        }

        private Builder(Builder builder) {
            this.sources.addAll(builder.sources);
            this.matcher = builder.matcher;
            this.options = builder.options;
        }

        public Builder source(ConfigSource source) {
            sources.add(source);
            return this;
        }

        /**
         * Throw when the Config structure field name is not found in the Config Tree, or not.
         * @param optional Do not throw when {@code true}. The default is {@code false}.
         * @return The {@link Builder}
         */
        public Builder optionalFields(boolean optional) {
            this.options.throwOnMissingProperty = !optional;
            return this;
        }

        /**
         * Throw when the {@link jakarta.config.Configuration} annotation is mission, or not,
         * @param optional Do not throw when {@code true}. The default is {@code false}.
         * @return The {@link Builder}
         */
        public Builder optionalAnnotation(boolean optional) {
            this.options.throwOnMissingConfigurationAnnotation = !optional;
            return this;
        }

        private Builder path(PathMatcher matcher) {
            this.matcher = matcher;
            return this;
        }

        /**
         * Specify the sub-Node of Config Tree by the path from the root.
         * @param path The dot separated path to a requested Config Node
         * @return The {@link Builder}
         */
        public Builder path(String path) {
            return path(new PathMatcher(path));
        }

        public PocConfigLoader build() {
            if (matcher == null) {
                matcher = new PathMatcher(null);
            }
            return new PocConfigLoader(this);
        }

    }
}
