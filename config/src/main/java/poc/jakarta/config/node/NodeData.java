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
import java.util.Optional;

/**
 * The Config Node representation for SPI implementation.
 */
public interface NodeData {
    /**
     * The key of this value. Represents the path in the Config tree between two nodes.
     * @return key
     */
    String key();

    /**
     * Returns optional value of the Config Node. The value might not been present, for instance when the Config Node has
     * child nodes with values.
     * @return Optional {@link ConfigNodeValue}
     */
    Optional<ConfigNodeValue> value();

    /**
     * Return {@link ConfigNodeValue}. If not present, returns null. Useful for cases where values are known to be present.
     * @return {@link ConfigNodeValue} or null.
     */
    ConfigNodeValue valueOrNull();

    /**
     * Return the key-value properties for the current Config Node in the form of children Config sub-Nodes.
     * Must never return {@code null.}
     * @return The Config sub-Nodes of t6he current Config Node.
     */
    Collection<? extends NodeData> properties();
}
