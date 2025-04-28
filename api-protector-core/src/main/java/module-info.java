module api.protector.core {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires io.swagger.v3.oas.models;
    requires openapi.diff.core;
    requires org.apache.commons.collections4;

    exports com.apiprotector.core;
}