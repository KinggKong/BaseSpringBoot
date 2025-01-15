package com.global.project.enums;

public enum EnumTypeLanguage {
    VI("vi"),
    EN("en"),
    KO("ko");

    private final String value;

    EnumTypeLanguage(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
