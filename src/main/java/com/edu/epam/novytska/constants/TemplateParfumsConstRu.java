package com.edu.epam.novytska.constants;

public enum TemplateParfumsConstRu {

    AROMATS("Группы ароматов"),
    BASE_NOTES("Ноты базы"),
    HEART_NOTES("Ноты сердца"),
    HIGH_NOTES("Верхние ноты"),
    BRAND("Бренд"),
    COUNTRY("Страна"),
    VOLUME("Объем"),
    SEX("Пол"),
    LAUNCH_DATE("Год начала выпуска");

    private String field;

    TemplateParfumsConstRu(String field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return field;
    }
}
