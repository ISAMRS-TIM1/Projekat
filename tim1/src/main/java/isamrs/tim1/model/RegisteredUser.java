package isamrs.tim1.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "RegisteredUsers")
public class RegisteredUser extends User {
	
	@OneToMany(mappedBy = "friends", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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
