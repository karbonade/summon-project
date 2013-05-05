package com.drikvy.summon;

import android.app.Application;

public class SummonApplication extends Application {

	private String userFacebookId;
	private String userFacebookName;
	
	public String getUserFacebookId() {
		return userFacebookId;
	}
	
	public void setUserFacebookId(String facebookId) {
		this.userFacebookId = facebookId;
	}
	
	public String getUserFacebookName() {
		return userFacebookName;
	}
	
	public void setUserFacebookName(String facebookName) {
		this.userFacebookName = facebookName;
	}
	
}
