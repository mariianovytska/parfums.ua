package com.edu.epam.novytska.constants;

public enum  HtmlParfumsConst {

    PAGINATION_CLASS("js-pagination__link-last"),
    PRODUCTS_ON_PAGE("/60"),
    PAGINATION_ATTRIBUTE("data-page"),
    PRODUCT_BLOCK_SELECTOR("div[data-name]"),
    PRODUCT_BLOCK_URL_ATTR("data-url"),
    PRODUCT_NAME_CLASS("productpage__desctitle"),
    PRODUCT_DESC_ID("tab-1"),
    PRODUCT_DESC_TAG("p"),
    PRODUCT_SPECS_CLASS("productpage__specitem"),
    HTML_TAG("html"),
    HTML_LANG_ATTR("lang");

    private String htmlEl;

    HtmlParfumsConst(String htmlEl) {
        this.htmlEl = htmlEl;
    }

    @Override
    public String toString() {
        return htmlEl;
    }
}
