package com.weproov.app.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

@Table(name = "renter", id = BaseColumns._ID)
public class RenterInfo extends BaseModel implements Parcelable {


	@Expose
	@Column(name = "lastname")
	public String lastname;

	@Expose
	@Column(name = "firstname")
	public String firstname;

	@Expose
	@Column(name = "email")
	public String email;

	@Expose
	@Column(name = "company")
	public String company;

	@Column(name = "driving_licence")
	public Uri driving_licence;

	@Column(name = "id_card")
	public Uri id_card;

	@Column(name = "uploaded")
	public boolean uploaded = false;


	public RenterInfo() {
		super();
	}

	protected RenterInfo(Parcel in) {
		super();
		lastname = in.readString();
		firstname = in.readString();
		email = in.readString();
		company = in.readString();

		driving_licence = in.readParcelable(Uri.class.getClassLoader());
		id_card = in.readParcelable(Uri.class.getClassLoader());
		;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(lastname);
		dest.writeString(firstname);
		dest.writeString(email);
		dest.writeString(company);

		dest.writeParcelable(driving_licence, flags);
		dest.writeParcelable(id_card, flags);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<RenterInfo> CREATOR = new Parcelable.Creator<RenterInfo>() {
		@Override
		public RenterInfo createFromParcel(Parcel in) {
			return new RenterInfo(in);
		}

		@Override
		public RenterInfo[] newArray(int size) {
			return new RenterInfo[size];
		}
	};

	@Override
	public String toString() {
		return "RenterInfo{" +
				"lastname='" + lastname + '\'' +
				", firstname='" + firstname + '\'' +
				", email='" + email + '\'' +
				", company='" + company + '\'' +
				", driving_licence=" + driving_licence +
				", id_card=" + id_card +
				'}';
	}
}