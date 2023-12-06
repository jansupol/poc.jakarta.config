/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package poc.jakarta.config.test.tck.tests.negative;

import jakarta.config.ConfigException;
import jakarta.config.Loader;
import jakarta.config.NoSuchObjectException;
import poc.jakarta.config.test.tck.common.AnyConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NegativeLoaderTest {
    @Test
    public void testFailWhenNotAnnotatedInterface() {
        try {
            Loader.bootstrap().path("my.configuration").load(NotAnnotatedConfiguration.class);
            Assertions.fail("Expected ConfigException has not been thrown when the interface does not contain @Configuration");
        } catch (ConfigException configException) {
            // pass
        }
    }

    @Test
    public void testFailWhenUnknownPath() {
        try {
            Loader.bootstrap().path("my.config").load(AnyConfiguration.class);
            Assertions.fail("Expected NoSuchObjectException has not been thrown when the configuration object not found");
        } catch (NoSuchObjectException noSuchObjectException) {
            // pass
        }
    }
}
