package isamrs.tim1.common;

import com.google.api.client.util.Key;

public class GeoResponse {
	@Key
	private Address address;

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

}
