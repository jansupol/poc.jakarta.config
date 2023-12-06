module poc.jakarta.config.test {
    requires jakarta.cdi;
    requires jakarta.el;
    requires jakarta.config.api;

    requires poc.jakarta.config;

    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;
    requires org.hamcrest;

    requires weld.api;
    requires weld.spi;
    requires weld.core.impl;
    requires weld.environment.common;
    requires weld.se.core;

    requires org.jboss.logging;

    exports poc.jakarta.config.test.tck.tests.negative to org.junit.platform.commons;
    exports poc.jakarta.config.test.internal.loader to org.junit.platform.commons;
    exports poc.jakarta.config.test.internal.loader.weld to org.junit.platform.commons;
    exports poc.jakarta.config.test.internal.node to org.junit.platform.commons;
    exports poc.jakarta.config.test.internal.source to org.junit.platform.commons;
    exports poc.jakarta.config.test.internal.test to org.junit.platform.commons;

    opens poc.jakarta.config.test.internal.loader.weld
            to weld.core.impl, poc.jakarta.config;
    opens poc.jakarta.config.test
            to org.junit.platform.commons, poc.jakarta.config;
    opens poc.jakarta.config.test.tck.tests
            to org.junit.platform.commons, weld.core.impl, poc.jakarta.config;
    opens poc.jakarta.config.test.internal.loader
            to poc.jakarta.config;
}