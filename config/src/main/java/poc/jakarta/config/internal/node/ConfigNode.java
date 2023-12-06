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

package poc.jakarta.config.internal.node;

import poc.jakarta.config.node.NodeData;
import poc.jakarta.config.value.ConfigNodeValue;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

public final class ConfigNode implements Collection<ConfigNode>, NodeData {
    private Optional<ConfigNodeValue> value;
    private final SortedMap<ConfigPath.ConfigPathSegment, ConfigNode> children;
    private ConfigNode parent;
    private final ConfigPath.ConfigPathSegment key;

    public ConfigNode(ConfigPath.ConfigPathSegment key) {
        this.key = key;
        children = new TreeMap<>();
        parent = null;
        value = Optional.empty();
    }

    public static ConfigNodeBuilder from(ConfigPath configPath) {
        return ConfigNodeBuilder.from(configPath);
    }

    public ConfigNodeBuilder toBuilder() {
        return ConfigNodeBuilder.from(this);
    }

    @Override
    public String key() {
        return key.getName();
    }

    /* package */ ConfigNode value(ConfigNodeValue value) {
        this.value = Optional.ofNullable(value);
        return this;
    }

    @Override
    public Optional<ConfigNodeValue> value() {
        return value == null ? Optional.empty() : value;
    }

    @Override
    public ConfigNodeValue valueOrNull() {
        return value == null ? null : value.get();
    }

    @Override
    public Collection<? extends NodeData> properties() {
        return children.values();
    }

    /* package */ Optional<ConfigNode> navigate(String path) {
        return navigate(ConfigPath.from(path).build());
    }

    /* package */ Optional<ConfigNode> navigate(ConfigPath path) {
        return navigate(path.getSegments());
    }

    /* package */ Optional<ConfigNode> navigate(List<ConfigPath.ConfigPathSegment> segments) {
        return navigate(segments.iterator());
    }

    /* package */ ConfigNode release() {
        if (parent != null) {
            parent.release(this);
        }
        return this;
    }

    /* package */ Optional<ConfigNode> navigate(ConfigPath.ConfigPathSegment segment) {
        final ConfigNode node = children.get(segment);
        return Optional.ofNullable(node);
    }

    // Can be both leaf and sub-node
    /* package */ boolean isLeaf() {
        return value.isPresent();
    }

    // Can be both leaf and sub-node
    /* package */ boolean isSubNode() {
        return !children.isEmpty();
    }

    private Optional<ConfigNode> navigate(Iterator<ConfigPath.ConfigPathSegment> segment) {
        Optional<ConfigNode> next = Optional.of(this);
        while (segment.hasNext()) {
            next = next.flatMap(configNode -> configNode.navigate(segment.next()));
        }
        return next;
    }

    private void release(ConfigNode configNode) {
        children.remove(configNode.key);
    }

    // Collection

    @Override
    public int size() {
        return children.size();
    }

    @Override
    public boolean isEmpty() {
        return children.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (ConfigPath.ConfigPathSegment.class.isInstance(o)) {
            return children.keySet().contains(o);
        } else {
            return children.values().contains(o);
        }
    }

    @Override
    public Iterator<ConfigNode> iterator() {
        return children.values().iterator();
    }

    @Override
    public Object[] toArray() {
        return children.values().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return children.values().toArray(a);
    }

    @Override
    public boolean add(ConfigNode configNode) {
        configNode.parent = this;
        return children.put(configNode.key, configNode) != null;
    }

    /* package */ ConfigNode addChild(ConfigNode child) {
        add(child);
        return this;
    }

    @Override
    public boolean remove(Object o) {
        return removeChild(o) != null;
    }

    private ConfigNode removeChild(Object o) {
        if (ConfigPath.ConfigPathSegment.class.isInstance(o)) {
            return children.remove(o);
        } else if (ConfigNode.class.isInstance(o)) {
            return children.remove(((ConfigNode) o).key);
        } else {
            return null;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return children.values().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends ConfigNode> c) {
        for (ConfigNode node : c) {
            children.put(node.key, node);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean ret = true;
        for (Object node : c) {
            if (ConfigPath.ConfigPathSegment.class.isInstance(node)) {
                ret &= children.remove((ConfigPath.ConfigPathSegment) node) != null;
            } else if (ConfigNode.class.isInstance(node)) {
                ret &= children.remove(((ConfigNode) node).key) != null;
            } else {
                ret &= false;
            }
        }
        return ret;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        final SortedMap<ConfigPath.ConfigPathSegment, ConfigNode> retained = new TreeMap<>();
        for (Object o : c) {
            if (contains(o)) {
                final ConfigNode node = removeChild(o);
                retained.put(node.key, node);
            }
        }
        children.clear();
        children.putAll(retained);
        return true;
    }

    @Override
    public void clear() {
        children.clear();
    }
}
