package com.weproov.app.logic.controllers;

import com.weproov.app.logic.providers.BusProvider;
import com.weproov.app.models.ProovCode;
import com.weproov.app.models.User;
import com.weproov.app.models.events.ProovCodeEvent;
import com.weproov.app.models.events.ProovCodeFailureEvent;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.models.wrappers.parse.ParseProovCodeQuery;
import com.weproov.app.models.wrappers.parse.ParseQueryWrapper;

import java.util.List;

public class ProovCodeTask {

    private static final ProovCode.IProovCodeService SERVICE = ProovCode.getService();
    private static final BusProvider.MainThreadBus BUS = BusProvider.getInstance();

    public static void getAsync(final String identifier) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BUS.post(new ProovCodeEvent(get(identifier)));
                } catch (NetworkException e) {
                    BUS.post(new ProovCodeFailureEvent());
                }
            }
        }).start();
    }

    public static ProovCode get(String identifier) throws NetworkException {
        ParseQueryWrapper<ProovCode> codes = SERVICE.get(new ParseProovCodeQuery(identifier));
        if (codes.results != null && codes.results.size() > 0) {
            return codes.results.get(0);
        } else if (codes.results != null) {
            return null;
        }

        throw new NetworkException("The JSON was wrong!");
    }

}
