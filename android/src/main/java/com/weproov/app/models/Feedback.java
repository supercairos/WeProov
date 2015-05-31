package com.weproov.app.models;

import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.models.wrappers.slack.SlackMessage;
import com.weproov.app.utils.connections.SlackConnection;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.PUT;

public class Feedback extends BaseModel {

	private static final IFeedbackService SERVICE = SlackConnection.ADAPTER.create(IFeedbackService.class);
	public static IFeedbackService getService() {
		return SERVICE;
	}

	public String title;
	public String message;

	public Feedback() {
		super();
	}

	public Feedback(String title, String message) {
		super();
		this.title = title;
		this.message = message;
	}

	public interface IFeedbackService {

		@PUT("/")
		Response send(@Body SlackMessage message) throws NetworkException;
	}

}
