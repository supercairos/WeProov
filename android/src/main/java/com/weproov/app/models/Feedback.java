package com.weproov.app.models;

import android.provider.BaseColumns;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.models.wrappers.slack.SlackMessage;
import com.weproov.app.utils.connections.SlackConnection;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.POST;

@Table(name = "feedbacks", id = BaseColumns._ID)
public class Feedback extends BaseModel {

	private static final IFeedbackService SERVICE = SlackConnection.ADAPTER.create(IFeedbackService.class);
	public static IFeedbackService getService() {
		return SERVICE;
	}

	@Column(name = "title")
	public String title;

	@Column(name = "message")
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

		@POST("/T03J4AJBA/B054EBHRB/P2REFxL613LQkarzy4Sy4LQe")
		Response send(@Body SlackMessage message) throws NetworkException;
	}

}
