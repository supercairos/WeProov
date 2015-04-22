package com.weproov.app.models.events;

public class NetworkErrorEvent {
	public Throwable throwable;

	public NetworkErrorEvent(Throwable throwable) {
		this.throwable = throwable;
	}
}
