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

package poc.jakarta.config.source;

import poc.jakarta.config.node.NodeData;

/**
 * Public interface representing the Jakarta Config Source.
 * The interface is not meant to be implemented outside this project.
 */
public interface ConfigSource<T extends NodeData> {
    /**
     * Load source and add the data to current root or make a new root node iff {@code null}.
     * @param root The root node
     * @return The root node with the actual data
     */
    T load(T root);
}
