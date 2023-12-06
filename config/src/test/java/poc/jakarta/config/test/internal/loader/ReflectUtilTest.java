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

package poc.jakarta.config.test.internal.loader;

import jakarta.config.TypeToken;

import poc.jakarta.config.internal.loader.ReflectUtil;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

public class ReflectUtilTest {
    @Test
    public void testClass() {
        TypeToken<Collection<ReflectUtilTest>> typeToken = new TypeToken<>() {
        };
        Assertions.assertEquals(Collection.class, ReflectUtil.getClass(typeToken));
        Assertions.assertEquals(Collection.class, ReflectUtil.getClass(typeToken.type()));

        TypeToken<List<Collection<ReflectUtilTest>>> listToken = new TypeToken<>() {
        };
        Assertions.assertEquals(List.class, ReflectUtil.getClass(listToken));
        Assertions.assertEquals(List.class, ReflectUtil.getClass(listToken.type()));

        TypeToken<ReflectUtilTest> typeToken2 = new TypeToken<>() {
        };
        Assertions.assertEquals(ReflectUtilTest.class, ReflectUtil.getClass(typeToken2));
    }

    @Test
    public void testType() {
        TypeToken<Collection<ReflectUtilTest>> typeToken = new TypeToken<>() {
        };
        Assertions.assertEquals(ReflectUtilTest.class, ReflectUtil.getType(typeToken));

        TypeToken<ReflectUtilTest> typeToken2 = new TypeToken<>() {
        };
        Assertions.assertEquals(ReflectUtilTest.class, ReflectUtil.getClass(typeToken2));
    }
}
