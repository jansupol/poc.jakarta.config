import poc.jakarta.config.PocConfigLoader;

module poc.jakarta.config {
    requires jakarta.cdi;
    requires jakarta.config.api;

    exports poc.jakarta.config;
    exports poc.jakarta.config.source;
    exports poc.jakarta.config.value;

    opens poc.jakarta.config.internal.loader.weld to weld.core.impl;
    exports poc.jakarta.config.internal.loader.weld to weld.core.impl;

    provides jakarta.config.Loader with PocConfigLoader;
    provides jakarta.enterprise.inject.spi.Extension with poc.jakarta.config.internal.loader.weld.CdiConfigExtension;

    // Tests
    exports poc.jakarta.config.internal.loader to poc.jakarta.config.test;
    exports poc.jakarta.config.internal.node to poc.jakarta.config.test;
    exports poc.jakarta.config.internal.source to poc.jakarta.config.test;
    exports poc.jakarta.config.internal.value.convertors to poc.jakarta.config.test;
    opens poc.jakarta.config.internal.loader to poc.jakarta.config.test;
    opens poc.jakarta.config.internal.node to poc.jakarta.config.test;
}