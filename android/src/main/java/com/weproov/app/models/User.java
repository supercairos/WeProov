package com.weproov.app.models;

import com.google.gson.annotations.Expose;
import com.weproov.app.utils.connections.Connection;
import retrofit.http.*;

// This is not a persistable model since it will be saved onto the account manager;
public class User {
    // api endpoint
    private static final String MODULE = "/users";

    // endpoints
    private static final String POST_REGISTER = "/register";
    private static final String GET_LOGIN = "/login";
    private static final String PUT_GCM = "/gcm/{gcm_token}";

    private static final IUserService SERVICE = Connection.ADAPTER.create(IUserService.class);

    @Expose
    public String firstname;
    @Expose
    public String lastname;
    @Expose
    public String password;
    @Expose
    public String email;
    @Expose
    public String token;

    public User(String email, String password, String firstname, String lastname) {
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public static IUserService getService() {
        return SERVICE;
    }

    @Override
    public String toString() {
        return "User{" +
                "firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    public static interface IUserService {

        @GET(MODULE + GET_LOGIN)
        User login(@Header("Authorization") String basicAuth);

        @POST(MODULE + POST_REGISTER)
        User register(@Body User user);

        @PUT(MODULE + PUT_GCM)
        User registerGcm(@Path("gcm_token") String token);
    }
}
