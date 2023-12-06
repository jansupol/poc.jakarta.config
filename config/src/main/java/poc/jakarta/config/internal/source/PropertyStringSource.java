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
import poc.jakarta.config.internal.node.ConfigNodeBuilder;
import poc.jakarta.config.internal.node.ConfigPath;
import poc.jakarta.config.internal.value.CollectionNodeValue;
import poc.jakarta.config.internal.value.StringNodeValue;

import java.util.StringTokenizer;

public final class PropertyStringSource implements Source {
    public final String source;

    public PropertyStringSource(String source) {
        this.source = source;
    }

    @Override
    public ConfigNode load(ConfigNode parent) {
        return load(parent, source);
    }

    private static ConfigNode load(ConfigNode parent, String file) {
        return new PropertyFileParser(parent, file).parse().get();
    }

    private static class PropertyFileParser {
        private final String config;
        private ConfigNodeBuilder builder;

        private PropertyFileParser(ConfigNode parent, String config) {
            this.config = config;
            if (parent != null) {
                builder = parent.toBuilder();
            }
        }

        private PropertyFileParser parse() {
            StringTokenizer tokenizer = new StringTokenizer(config, "\n");
            while (tokenizer.hasMoreTokens()) {
                parseRow(tokenizer.nextToken().trim());
            }
            return this;
        }

        private void parseRow(String row) {
            if (row.startsWith("#") || row.isBlank()) { // Comment or empty
                return;
            }

            String[] keyVal = row.split("=", 2);
            if (keyVal.length != 2) {
                throw new ConfigException("Unable to parse property row " + row);
            }

            ConfigPath path = ConfigPath.from(keyVal[0]).build();
            if (builder == null) {
                builder = ConfigNode.from(path);
            } else {
                builder.path(path);
            }
            parseValue(keyVal[1]);
        }

        private void parseValue(String value) {
            String[] values = value.split(",");
            if (values.length == 1) {
                builder.value(new StringNodeValue(value.trim()));
            } else {
                StringNodeValue[] stringNodeValues = new StringNodeValue[values.length];
                for (int i = 0; i != values.length; i++) {
                    stringNodeValues[i] = new StringNodeValue(values[i].trim());
                }
                builder.value(new CollectionNodeValue(stringNodeValues));
            }
        }

        private ConfigNode get() {
            return builder.build();
        }
    }
}
