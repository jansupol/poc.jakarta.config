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

import jakarta.config.ConfigException;
import poc.jakarta.config.internal.node.ConfigNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class PropertyFileSource implements Source {
    private final File resource;

    public PropertyFileSource(File resource) {
        this.resource = resource;
    }

    @Override
    public ConfigNode load(ConfigNode parent) {
        try {
            // Lazy
            String file = new String(new FileInputStream(resource).readAllBytes(), StandardCharsets.UTF_8);
            return new PropertyStringSource(file).load(parent);
        } catch (IOException e) {
            throw new ConfigException(e);
        }
    }

}
