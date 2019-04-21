package isamrs.tim1.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import isamrs.tim1.dto.ServiceDTO;

@Entity
@Table(name = "Airlines")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Airline extends Service implements Serializable {

	@OneToMany(mappedBy = "airline", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<Flight> flights;

	@OneToMany(mappedBy = "airline", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<AirlineAdmin> admins;

	@OneToMany(mappedBy = "airline", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Destination> destinations;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<QuickFlightReservation> quickReservations;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<FlightReservation> normalReservations;

	@OneToMany(mappedBy = "airline", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<PlaneSegment> planeSegments;

	private static final long serialVersionUID = 2322929543693920541L;

	public Airline() {
		super();
		flights = new HashSet<Flight>();
		admins = new HashSet<AirlineAdmin>();
		destinations = new HashSet<Destination>();
		quickReservations = new HashSet<QuickFlightReservation>();
		planeSegments = new HashSet<PlaneSegment>();
		planeSegments.add(new PlaneSegment(PlaneSegmentClass.FIRST));
		planeSegments.add(new PlaneSegment(PlaneSegmentClass.BUSINESS));
		planeSegments.add(new PlaneSegment(PlaneSegmentClass.ECONOMY));
		normalReservations = new HashSet<FlightReservation>();
	}

	public Airline(ServiceDTO airline) {
		super(airline);
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

	public Set<QuickFlightReservation> getQuickReservations() {
		return quickReservations;
	}

	public void setQuickReservations(Set<QuickFlightReservation> quickReservations) {
		this.quickReservations = quickReservations;
	}

	public Set<PlaneSegment> getPlaneSegments() {
		return planeSegments;
	}

	public void setPlaneSegments(Set<PlaneSegment> planeSegments) {
		this.planeSegments = planeSegments;
	}

	
	public Set<FlightReservation> getNormalReservations() {
		return normalReservations;
	}

	public void setNormalReservations(Set<FlightReservation> normalReservations) {
		this.normalReservations = normalReservations;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
