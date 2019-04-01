package isamrs.tim1.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Airlines")
public class Airline extends Service implements Serializable {

	@OneToMany(mappedBy = "airline", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<Flight> flights;

	@OneToMany(mappedBy = "airline", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<AirlineAdmin> admins;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(name = "airline_destinations", joinColumns = @JoinColumn(name = "airline_id", referencedColumnName = "service_id"), inverseJoinColumns = @JoinColumn(name = "destination_id", referencedColumnName = "destination_id"))
	private Set<Destination> destinations;

	@OneToMany(mappedBy = "airline", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<QuickFlightReservationTicket> quickReservations;

	@OneToMany(mappedBy = "airline", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<PlaneSegment> planeSegments;

	private static final long serialVersionUID = 2322929543693920541L;

	public Airline() {
		super();
		admins = new HashSet<AirlineAdmin>();
		destinations = new HashSet<Destination>();
		quickReservations = new HashSet<QuickFlightReservationTicket>();
		planeSegments = new HashSet<PlaneSegment>();
	}

	public Set<Flight> getFlights() {
		return flights;
	}

	public void setFlights(Set<Flight> flights) {
		this.flights = flights;
	}

	public Set<AirlineAdmin> getAdmins() {
		return admins;
	}

	public void setAdmins(Set<AirlineAdmin> admins) {
		this.admins = admins;
	}

	public Set<Destination> getDestinations() {
		return destinations;
	}

	public void setDestinations(Set<Destination> destinations) {
		this.destinations = destinations;
	}

	public Set<QuickFlightReservationTicket> getQuickReservations() {
		return quickReservations;
	}

	public void setQuickReservations(Set<QuickFlightReservationTicket> quickReservations) {
		this.quickReservations = quickReservations;
	}

	public Set<PlaneSegment> getPlaneSegments() {
		return planeSegments;
	}

	public void setPlaneSegments(Set<PlaneSegment> planeSegments) {
		this.planeSegments = planeSegments;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
