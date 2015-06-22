package com.weproov.app.models.events;

import com.weproov.app.models.ProovCode;

public class ProovCodeEvent {
    private final ProovCode mProovCode;

    public ProovCode getProovCode() {
        return mProovCode;
    }

    public ProovCodeEvent(ProovCode proovCode) {
        mProovCode = proovCode;
    }

}
