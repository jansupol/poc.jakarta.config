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
package poc.jakarta.config.internal.util;

import jakarta.config.Configuration;

import java.lang.annotation.Annotation;
import java.util.Optional;

public class AnnotationReader {

    private static final Class<? extends Annotation> CONFIG_ANNOTATION = Configuration.class;
    public static Optional<String> readPath(Class<?> type) {
        Configuration mapping = (Configuration) type.getDeclaredAnnotation(CONFIG_ANNOTATION);
        if (mapping != null) {
            return Optional.of(mapping.path());
        } else {
            return Optional.empty();
        }
    }
}
