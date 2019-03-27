package isamrs.tim1.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Hotels")
public class Hotel extends Service {

	public Hotel() {
		super();
	}

	@OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<HotelAdditionalService> additionalServices;
	
	@OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<HotelRoom> rooms;

	public Set<HotelAdditionalService> getAdditionalServices() {
		return additionalServices;
	}

	public void setAdditionalServices(Set<HotelAdditionalService> additionalServices) {
		this.additionalServices = additionalServices;
	}

	public Set<HotelRoom> getRooms() {
		return rooms;
	}

	public void setRooms(Set<HotelRoom> rooms) {
		this.rooms = rooms;
	}

	private static final long serialVersionUID = -3964984592975561537L;
}
