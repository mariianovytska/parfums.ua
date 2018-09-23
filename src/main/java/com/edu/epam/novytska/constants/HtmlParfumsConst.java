package com.edu.epam.novytska.constants;

public enum  HtmlParfumsConst {

    CATEGORY_CLASS("js-link-item"),
    CATEGORY_HREF_ATTR("href"),
    PAGINATION_CLASS("js-pagination__link-last"),
    PAGINATION_ATTRIBUTE("data-page"),
    CATEGORY_LINK("/category/"),
    PAGINATION_LINK("/none/"),
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
