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

import poc.jakarta.config.internal.node.ConfigNode;
import poc.jakarta.config.internal.node.ConfigNodeWalker;
import poc.jakarta.config.internal.node.ConfigPath;
import poc.jakarta.config.internal.util.AnnotationReader;
import poc.jakarta.config.internal.value.convertors.ValueConvertors;
import poc.jakarta.config.value.NodeValueConvertors;

import java.util.Collections;
import java.util.Optional;

public final class ConfigLoaderContext {
    static final ConfigNodeWalker configNodeWalker = new ConfigNodeWalker();

    /* package */ final Builder builder;

    private ConfigLoaderContext(Builder builder) {
        this.builder = builder;
    }

    public static Builder builder(ConfigNode root) {
        return new Builder(root);
    }

    public Injector injector() {
        return builder.injector;
    }

    public static class Builder {
        final ConfigNode root;
        ConfigPath path;
        Instantiator instantiator;
        Injector injector;
        NodeValueConvertors convertors;
        Options options = new Options(); // Default

        public Builder(ConfigNode root) {
            this.root = root;
        }

        /* package */ Builder(Builder other) {
            this(other.root);
            path = other.path;
            instantiator = other.instantiator;
            injector = other.injector;
            convertors = other.convertors;
            options = other.options;
        }

        public Builder path(String path) {
            this.path = ConfigPath.from(path).build();
            return this;
        }

        public Builder path(ConfigPath path) {
            this.path = path;
            return this;
        }

        public Builder options(Options options) {
            this.options = options;
            return this;
        }

        public Builder instantiator(Instantiator instantiator) {
            this.instantiator = instantiator;
            return this;
        }

        public ConfigLoaderContext build() {
            if (path == null) {
                path("");
            }
            if (instantiator == null) {
                instantiator = new StandaloneInstantiator();
            }
            if (injector == null) {
                injector = new StandaloneInjector();
            }
            if (convertors == null) {
                convertors = new ValueConvertors(Collections.emptyList());
            }
            return new ConfigLoaderContext(this);
        }
    }

    public static class Options {
        public boolean throwOnMissingProperty = true;
        public boolean throwOnMissingConfigurationAnnotation = true;
        public boolean exposeAllConfigProperties = true;

        public Options() {
            // default
        }

        public Options(Options other) {
            throwOnMissingConfigurationAnnotation = other.throwOnMissingConfigurationAnnotation;
            throwOnMissingProperty = other.throwOnMissingProperty;
            exposeAllConfigProperties = other.exposeAllConfigProperties;
        }
    }

}
