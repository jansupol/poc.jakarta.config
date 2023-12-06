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

package poc.jakarta.config.test.internal.loader.weld;

import org.jboss.weld.bootstrap.spi.BeanDiscoveryMode;
import org.jboss.weld.environment.deployment.WeldBeanDeploymentArchive;
import org.jboss.weld.environment.deployment.discovery.DefaultBeanArchiveScanner;
import org.jboss.weld.environment.deployment.discovery.ReflectionDiscoveryStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FixedDefaultDiscoveryStrategy extends ReflectionDiscoveryStrategy {

    public FixedDefaultDiscoveryStrategy() {
        super(null, null, null, BeanDiscoveryMode.ANNOTATED);
    }

    @Override
    public Set<WeldBeanDeploymentArchive> performDiscovery() {
        if (scanner == null) {
            setScanner();
        }
        return super.performDiscovery();
    }

    private void setScanner() {
        this.setScanner(new DefaultBeanArchiveScanner(resourceLoader, bootstrap, BeanDiscoveryMode.ANNOTATED) {
            @Override
            public List<ScanResult> scan() {
                List<ScanResult> results = super.scan();
                Map<String, ScanResult> unique = new HashMap<>();
                for (ScanResult result : results) {
                    unique.put(result.getBeanArchiveId(), result);
                }
                return new ArrayList<>(unique.values());
            }
        });
    }
}
