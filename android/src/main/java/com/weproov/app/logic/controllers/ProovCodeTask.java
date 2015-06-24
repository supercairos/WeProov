package com.weproov.app.logic.controllers;

import com.weproov.app.logic.providers.BusProvider;
import com.weproov.app.models.ProovCode;
import com.weproov.app.models.WeProov;
import com.weproov.app.models.events.ProovCodeEvent;
import com.weproov.app.models.events.ProovCodeFailureEvent;
import com.weproov.app.models.exceptions.AlreadyUsedProovCode;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.models.exceptions.WrongProovCode;
import com.weproov.app.models.wrappers.parse.ParsePointer;
import com.weproov.app.models.wrappers.parse.ParseProovCodeQuery;
import com.weproov.app.models.wrappers.parse.ParseQueryWrapper;
import retrofit.RetrofitError;

public class ProovCodeTask {

	private static final ProovCode.IProovCodeService SERVICE = ProovCode.getService();
	private static final BusProvider.MainThreadBus BUS = BusProvider.getInstance();

	public static void getAsync(final String identifier) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					BUS.post(new ProovCodeEvent(get(identifier)));
				} catch (NetworkException | RetrofitError e) {
					BUS.post(new ProovCodeFailureEvent(ProovCodeFailureEvent.WHY_NETWORK_ERROR));
				} catch (AlreadyUsedProovCode e) {
					BUS.post(new ProovCodeFailureEvent(ProovCodeFailureEvent.WHY_ALREADY_USED));
				} catch (WrongProovCode e) {
					BUS.post(new ProovCodeFailureEvent(ProovCodeFailureEvent.WHY_NOT_FOUND));
				}
			}
		}).start();
	}

	public static ProovCode get(String identifier) throws NetworkException, RetrofitError, AlreadyUsedProovCode, WrongProovCode {
		ParseQueryWrapper<ProovCode> codes = SERVICE.get(new ParseProovCodeQuery<>(identifier));
		if (codes.results != null && codes.results.size() > 0) {
			ProovCode code = codes.results.get(0);
			ParseQueryWrapper<WeProov> check = SERVICE.check(new ParseProovCodeQuery<>(new ParsePointer(code.id, "weproov")));
			if(check.results != null) {
				if (check.results.isEmpty()) {
					return code;
				}

				throw new AlreadyUsedProovCode();
			}

			throw new NetworkException("The JSON was wrong!");
		} else if (codes.results != null) {

			throw new WrongProovCode();
		}

		throw new NetworkException("The JSON was wrong!");
	}

}
