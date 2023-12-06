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

package poc.jakarta.config.node;

import poc.jakarta.config.value.ConfigNodeValue;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * <p>
 *     A view representing a Config Node and all direct sub-nodes as {@link ChildKeyValue key value} pairs.
 *     (The sub-Nodes without a value are not part of this view).
 *     When using Data, it is always possible to assert:
 * </p>
 * <blockquote><pre>
 * Data data = ...
 * data.properties().forEach(p -> {assert p.valueOrNull() != null;});
 * data.properties().forEach(p -> {assert p.properties().isEmpty();});
 * </pre></blockquote>
 */
public final class Data implements NodeData {

    /**
     * Key value pair
     */
    public static class ChildKeyValue implements NodeData {
        private final String key;
        private final ConfigNodeValue value;

        public ChildKeyValue(String key, ConfigNodeValue value) {
            this.key = key;
            this.value = value;
        }

        public String key() {
            return key;
        }

        public Optional<ConfigNodeValue> value() {
            return Optional.of(value);
        }

        @Override
        public ConfigNodeValue valueOrNull() {
            return value;
        }

        @Override
        public Collection<NodeData> properties() {
            return Collections.emptyList();
        }
    }

    private final String key;
    private final ConfigNodeValue value;
    private List<ChildKeyValue> properties;
    private Set<String> subnodes = new HashSet<>();

    public Data(String key, ConfigNodeValue value) {
        properties = new LinkedList<>();
        this.value = value;
        this.key = key;
    }

    @Override
    public String key() {
        return key;
    }

    /**
     * <p>
     *   Return the optional value of a Config Node.
     * </p>
     * <p>
     *   For instance, if property would be <code>key=value</code>, the Config Node <code>key</code> would have <code>value</code>
     *   value.
     *   When the property would be <code>my.key=value</code>, the Config Node <code>my</code> would not have any value, but
     *   Config Node <code>key</code> would have value <code>value</code>.
     * </p>
     *
     * @return Optinal value of a Config Node
     */
    public Optional<ConfigNodeValue> value() {
        return Optional.ofNullable(value);
    }

    @Override
    public ConfigNodeValue valueOrNull() {
        return value;
    }

    /**
     * Return all the direct key value pair of a Config Node represented by this {@link Data} object.
     * @return
     */
    public List<ChildKeyValue> properties() {
        return properties;
    }

    /**
     * Return optional key-value pair of a direct sub-node of the Config Node represented by this {@link Data} object.
     * @param key The key of the expected key value pair.
     * @return Key-value pair of a sub-node if there is a pair with the requested key.
     */
    public Optional<ChildKeyValue> property(String key) {
        for (ChildKeyValue child : properties) {
            if (child.key.equals(key)) {
                return Optional.of(child);
            }
        }
        return Optional.empty();
    }

    /**
     * Return {@code true} if the Config Node represented by this
     * @param path the path towards a new Config Node
     * @return
     */
    public boolean hasSubNode(String path) {
        return subnodes.contains(key);
    }

}
