package isamrs.tim1.common;

import com.google.api.client.util.Key;

public class Address {
	@Key
	private String country;

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

}
