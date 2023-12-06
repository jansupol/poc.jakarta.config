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

import jakarta.config.ConfigException;
import poc.jakarta.config.internal.node.ConfigNode;
import poc.jakarta.config.internal.source.PropertyStringSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class PropertyResourceFileSource implements ConfigSource<ConfigNode> {
    private final String resourceFileName;

    public PropertyResourceFileSource(String resourceFileName) {
        this.resourceFileName = resourceFileName;
    }

    @Override
    public ConfigNode load(ConfigNode parent) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceFileName);
            String file = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return new PropertyStringSource(file).load(parent);
        } catch (IOException e) {
            throw new ConfigException(e);
        }
    }
}
