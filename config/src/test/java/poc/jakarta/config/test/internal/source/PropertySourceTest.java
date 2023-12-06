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


package poc.jakarta.config.test.internal.source;

import poc.jakarta.config.internal.node.ConfigNode;
import poc.jakarta.config.test.internal.node.ConfigNodeWrapper;
import poc.jakarta.config.internal.source.PropertyStringSource;
import poc.jakarta.config.internal.value.convertors.ValueConvertors;
import poc.jakarta.config.value.NodeValueConvertors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Iterator;

public class PropertySourceTest {
    private final NodeValueConvertors convertors = new ValueConvertors(Collections.emptyList());
    @Test
    public void testOneSegmentValues() {
        String property = "key1=value1\nkey2=value2";
        ConfigNode configNode = new PropertyStringSource(property).load(null);

        Iterator<ConfigNode> it = configNode.iterator();
        ConfigNode node = it.next();
        Assertions.assertEquals("key1", new ConfigNodeWrapper(node).getKey());
        Assertions.assertEquals("value1",
                new ConfigNodeWrapper(node).getValue().get().get(String.class, String.class, convertors));

        node = it.next();
        Assertions.assertEquals("key2", new ConfigNodeWrapper(node).getKey());
        Assertions.assertEquals("value2",
                new ConfigNodeWrapper(node).getValue().get().get(String.class, String.class, convertors));
    }

    @Test
    public void testTwoSegmentValues() {
        String property = "key1.k1=value1\nkey1.k2=value2\nkey2=value3";
        ConfigNode configNode = new PropertyStringSource(property).load(null);

        Iterator<ConfigNode> it = configNode.iterator();
        ConfigNode node = it.next();
        Assertions.assertEquals("key1", new ConfigNodeWrapper(node).getKey());
        Assertions.assertTrue(new ConfigNodeWrapper(node).getValue().isEmpty());

        ConfigNode[] child = node.toArray(new ConfigNode[0]);
        Assertions.assertEquals(2, child.length);
        Assertions.assertEquals("k1", new ConfigNodeWrapper(child[0]).getKey());
        Assertions.assertEquals("value1",
                new ConfigNodeWrapper(child[0]).getValue().get().get(String.class, String.class, convertors));
        Assertions.assertEquals("k2", new ConfigNodeWrapper(child[1]).getKey());
        Assertions.assertEquals("value2",
                new ConfigNodeWrapper(child[1]).getValue().get().get(String.class, String.class, convertors));

        node = it.next();
        Assertions.assertEquals("key2", new ConfigNodeWrapper(node).getKey());
        Assertions.assertEquals("value3",
                new ConfigNodeWrapper(node).getValue().get().get(String.class, String.class, convertors));
    }
}
