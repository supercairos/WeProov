package com.weproov.app.models.wrappers;

import com.google.gson.annotations.Expose;
import com.weproov.app.utils.constants.GcmConstants;

import java.util.ArrayList;
import java.util.List;

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
	List<String> channels =new ArrayList<>();

	public ParseGcmResponse(String deviceToken) {
		this.deviceToken = deviceToken;
	}
}
