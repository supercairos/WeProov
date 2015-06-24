package com.weproov.app.models.events;

public class ProovCodeFailureEvent {

	public static final int WHY_ALREADY_USED = 0;
	public static final int WHY_NOT_FOUND = 1;
	public static final int WHY_NETWORK_ERROR = 2;

	public int why;

	public ProovCodeFailureEvent(int why) {
		this.why = why;
	}
}
