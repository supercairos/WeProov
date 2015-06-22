package com.weproov.app.models;

import android.net.Uri;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.weproov.app.models.exceptions.NetworkException;
import com.weproov.app.models.wrappers.parse.ParseFile;
import com.weproov.app.models.wrappers.parse.ParseFileResponse;
import com.weproov.app.models.wrappers.parse.ParseGcmResponse;
import com.weproov.app.models.wrappers.parse.ParseRegisterResponse;
import com.weproov.app.utils.connections.ParseConnection;
import com.weproov.app.utils.connections.TypedUri;
import retrofit.client.Response;
import retrofit.http.*;

// This is not a persistable model since it will be saved onto the account manager;
public class User {
	// api endpoint
	private static final String MODULE = "/users";

	// endpoints
	private static final String POST_FILE = "/files/{filename}";
	private static final String POST_REGISTER = "/users";
	private static final String GET_LOGIN = "/login";
	private static final String PUT_GCM = "/installations";
	// private static final String PUT_GCM = "/gcm/{gcm_token}";

	private static final IUserService SERVICE = ParseConnection.ADAPTER.create(IUserService.class);

	@Expose
	@SerializedName("objectId")
	public String id;

	@Expose
	@SerializedName("prenom")
	public String firstname;

	@Expose
	@SerializedName("nom")
	public String lastname;

	@Expose
	@SerializedName("phone")
	private String phone;

	@Expose
	@SerializedName("username")
	private String email;

	// Parse hack to use the username as email and viceversa
	@SuppressWarnings("unused")
	@Expose
	@SerializedName("email")
	private String username;

	public void setEmail(String email) {
		this.email = email;
		this.username = email;
	}

	public String getEmail() {
		return this.email;
	}

	@Expose
	@SerializedName("sessionToken")
	public String token;

	@Expose
	@SerializedName("password")
	public String password;

	public Uri picture;

	@Expose
	@SerializedName("picture")
	public ParseFile parsePictureFile;

	public User(String email, String phone, String password, String firstname, String lastname, Uri picture) {
		this.email = email;
		this.phone = phone;
		this.username = email;
		this.firstname = firstname;
		this.lastname = lastname;
		this.password = password;
		this.picture = picture;
	}

	public static IUserService getService() {
		return SERVICE;
	}

	@Override
	public String toString() {
		return "User{" +
				"firstname='" + firstname + '\'' +
				", lastname='" + lastname + '\'' +
				", phone='" + phone + '\'' +
				", email='" + email + '\'' +
				", username='" + username + '\'' +
				", token='" + token + '\'' +
				", password='" + password + '\'' +
				", picture=" + picture +
				", parsePictureFile=" + parsePictureFile +
				'}';
	}

	public interface IUserService {

		@GET(GET_LOGIN)
		//User login(@Header("Authorization") String basicAuth) throws LoginException;
		User login(@Query("username") String email, @Query("password") String password) throws NetworkException;

		@POST(POST_FILE)
		// User register(@Body User user) throws LoginException;
		ParseFileResponse upload(@Path("filename") String filename, @Body TypedUri uri) throws NetworkException;

		@POST(POST_REGISTER)
		// User register(@Body User user) throws LoginException;
		ParseRegisterResponse register(@Body User user) throws NetworkException;

		@PUT(PUT_GCM)
		Response registerGcm(@Body ParseGcmResponse gcm) throws NetworkException;
		// User registerGcm(@Path("gcm_token") String token) throws LoginException;
	}
}
