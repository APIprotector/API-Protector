module api.protector.cli {
    requires api.protector.core;
    requires commons.cli;
    requires org.slf4j;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;

    exports com.apiprotector.cli;
}