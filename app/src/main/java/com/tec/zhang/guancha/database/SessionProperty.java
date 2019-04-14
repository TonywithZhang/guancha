package com.tec.zhang.guancha.database;

import org.litepal.crud.LitePalSupport;

public class SessionProperty extends LitePalSupport {
    String csrfState;

    public String getCsrfState() {
        return csrfState;
    }

    public void setCsrfState(String csrfState) {
        this.csrfState = csrfState;
    }
}
