package isamrs.tim1.model;

import java.io.Serializable;

public class UserTokenState implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6884073795891534131L;
	private String accessToken;
	private Long expiresIn;
	private UserType userType;
	private boolean passwordChanged;

	public UserTokenState() {
		this.accessToken = null;
		this.expiresIn = null;
		this.setUserType(null);
		this.passwordChanged = false;
	}

	public UserTokenState(String accessToken, long expiresIn, UserType userType, boolean passwordChanged) {
		this.accessToken = accessToken;
		this.expiresIn = expiresIn;
		this.setUserType(userType);
		this.passwordChanged = passwordChanged;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(Long expiresIn) {
		this.expiresIn = expiresIn;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public boolean isPasswordChanged() {
		return passwordChanged;
	}

	public void setPasswordChanged(boolean passwordChanged) {
		this.passwordChanged = passwordChanged;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
