package isamrs.tim1.model;

import java.util.HashSet;
import java.util.Set;

public class RegisteredUser extends User {

	private Set<RegisteredUser> friends;

	public RegisteredUser() {
		friends = new HashSet<RegisteredUser>();
	}

	public Set<RegisteredUser> getFriends() {
		return friends;
	}

	public void setFriends(Set<RegisteredUser> friends) {
		this.friends = friends;
	}

	private static final long serialVersionUID = 4453092532257405053L;

}
