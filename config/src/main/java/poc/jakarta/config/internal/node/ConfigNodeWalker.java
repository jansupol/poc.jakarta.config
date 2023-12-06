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

import poc.jakarta.config.node.Data;
import java.util.Optional;

public final class ConfigNodeWalker {

    private static Data toData(ConfigNode configNode) {
        Data node = new Data(configNode.key(), configNode.value().orElseGet(() -> null));
        configNode.stream()
                .filter(ConfigNode::isLeaf)
                .forEach(n -> node.properties().add(toChild(n)));
        return node;
    }

    private static Data.ChildKeyValue toChild(ConfigNode node) {
        return new Data.ChildKeyValue(node.key(), node.value().get());
    }

    public Optional<Data> walk(ConfigNode root, ConfigPath prefix) {
        Optional<ConfigNode> navigate = root.navigate(prefix);
        return navigate.map(ConfigNodeWalker::toData);
    }
}
