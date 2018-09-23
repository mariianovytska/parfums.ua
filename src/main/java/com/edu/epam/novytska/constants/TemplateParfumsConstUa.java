package com.edu.epam.novytska.constants;

public enum TemplateParfumsConstUa {

    AROMATS("Групи ароматів"),
    BASE_NOTES("Ноти бази"),
    HEART_NOTES("Ноти серця"),
    HIGH_NOTES("Верхні ноти"),
    BRAND("Бренд"),
    COUNTRY("Країна"),
    VOLUME("Об'єм"),
    SEX("Стать"),
    LAUNCH_DATE("Pік початку");

    private String field;

    TemplateParfumsConstUa(String field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return field;
    }
}