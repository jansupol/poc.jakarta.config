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
package poc.jakarta.config.internal.source;

import poc.jakarta.config.internal.node.ConfigNode;
import poc.jakarta.config.source.ConfigSource;

/**
 * Object representing the actual config source.
 */
public interface Source extends ConfigSource<ConfigNode> {
    /**
     * Loads a config and adds the config to parent config, or creates a new config, if parent is {@code null}.
     * @param parent Nullable {@link ConfigNode} config
     * @return merged configs from parent and this source.
     */
    ConfigNode load(ConfigNode parent);
}
