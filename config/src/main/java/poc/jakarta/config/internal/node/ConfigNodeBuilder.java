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

import poc.jakarta.config.value.ConfigNodeValue;

import java.util.Iterator;
import java.util.Optional;

public final class ConfigNodeBuilder {
    private ConfigNodeBuilder(ConfigNode root) {
        this.root = root;
    }

    static ConfigNodeBuilder from(ConfigPath path) {
        return new ConfigNodeBuilder(new ConfigNode(new ConfigPath.ConfigPathSegment(""))).path(path);
    }

    static ConfigNodeBuilder from(ConfigNode configNode) {
        return new ConfigNodeBuilder(configNode);
    }

    private final ConfigNode root;
    private ConfigNode point;

    public ConfigNodeBuilder path(ConfigPath path) {
        point = root;

        Iterator<ConfigPath.ConfigPathSegment> segmentIterator = path.getSegments().iterator();
        while (segmentIterator.hasNext()) {
            ConfigPath.ConfigPathSegment segment = segmentIterator.next();
            Optional<ConfigNode> child = point.navigate(segment);
            point = child.orElseGet(() -> {
                ConfigNode newNode = new ConfigNode(segment);
                point.addChild(newNode);
                return newNode;
            });
        }

        return this;
    }

    /**
     * Add value to current node
     * @param value {@link ConfigNodeValue}
     * @return this builder
     */
    public ConfigNodeBuilder value(ConfigNodeValue value) {
        point.value(value);
        return this;
    }

    public ConfigNode build() {
        return root;
    }
}
