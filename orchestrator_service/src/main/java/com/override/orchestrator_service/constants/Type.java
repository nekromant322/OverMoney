package com.override.orchestrator_service.constants;

public enum Type {
    INCOME("Доходы"),
    EXPENSE("Расходы");

    private String value;

    Type(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
