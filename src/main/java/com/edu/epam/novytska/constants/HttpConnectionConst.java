package com.edu.epam.novytska.constants;

public enum  HttpConnectionConst {

    HTTP_ERROR("HTTP error fetching URL: "),
    DEFAULT_UA("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36"),
    USER_AGENT("User-Agent");

    private String conConst;

    HttpConnectionConst(String conConst) {
        this.conConst = conConst;
    }

    @Override
    public String toString() {
        return conConst;
    }
}
