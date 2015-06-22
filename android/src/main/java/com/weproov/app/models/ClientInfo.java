package com.weproov.app.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Table(name = "client", id = BaseColumns._ID)
public class ClientInfo extends BaseModel implements Parcelable {


	@Expose
	@Column(name = "lastname")
	@SerializedName("last_name")
	public String lastname;

	@Expose
	@Column(name = "firstname")
	@SerializedName("first_name")
	public String firstname;

	@Expose
	@Column(name = "email")
	@SerializedName("email")
	public String email;

	@Expose
	@Column(name = "company")
	@SerializedName("company")
	public String company;

	@Expose
	@Column(name = "phone")
	@SerializedName("phone")
	public String phone;

	@Column(name = "driving_licence")
	public Uri driving_licence;

	@Column(name = "id_card")
	public Uri id_card;

	@Column(name = "uploaded")
	public boolean uploaded = false;


	public ClientInfo() {
		super();
	}

	@Override
	public String toString() {
		return "ClientInfo{" +
				"lastname='" + lastname + '\'' +
				", firstname='" + firstname + '\'' +
				", email='" + email + '\'' +
				", company='" + company + '\'' +
				", driving_licence=" + driving_licence +
				", id_card=" + id_card +
				", uploaded=" + uploaded +
				'}';
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(this.lastname);
		dest.writeString(this.firstname);
		dest.writeString(this.email);
		dest.writeString(this.company);
		dest.writeString(this.phone);
		dest.writeParcelable(this.driving_licence, 0);
		dest.writeParcelable(this.id_card, 0);
		dest.writeByte(uploaded ? (byte) 1 : (byte) 0);
	}

	protected ClientInfo(Parcel in) {
		super(in);
		this.lastname = in.readString();
		this.firstname = in.readString();
		this.email = in.readString();
		this.company = in.readString();
		this.phone = in.readString();
		this.driving_licence = in.readParcelable(Uri.class.getClassLoader());
		this.id_card = in.readParcelable(Uri.class.getClassLoader());
		this.uploaded = in.readByte() != 0;
	}

	public static final Creator<ClientInfo> CREATOR = new Creator<ClientInfo>() {
		public ClientInfo createFromParcel(Parcel source) {
			return new ClientInfo(source);
		}

		public ClientInfo[] newArray(int size) {
			return new ClientInfo[size];
		}
	};
}
