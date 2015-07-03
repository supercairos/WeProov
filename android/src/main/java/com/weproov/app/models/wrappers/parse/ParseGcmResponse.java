package com.weproov.app.models.wrappers.parse;

import com.google.gson.annotations.Expose;
import com.weproov.app.utils.constants.GcmConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class ParseGcmResponse {

	@Expose
	String deviceType = "android";

	@Expose
	String pushType = "gcm";

	@Expose
	String deviceToken;

	@Expose
	String GCMSenderId = GcmConstants.SENDER_ID;

	@Expose
	String timeZone = TimeZone.getDefault().getID();

	@Expose
	List<String> channels = new ArrayList<>();

	@Expose
	ParsePointer user;

	public ParseGcmResponse(String deviceToken, String userServerId) {
		this.deviceToken = deviceToken;
		this.channels.add("");
		this.user = new ParsePointer(userServerId, "_User");
	}

	public ParseGcmResponse(String deviceToken) {
		this.deviceToken = deviceToken;
		this.channels.add("");
	}
}
