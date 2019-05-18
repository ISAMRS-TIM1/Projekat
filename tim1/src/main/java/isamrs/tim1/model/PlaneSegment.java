package isamrs.tim1.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "PlaneSegment")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PlaneSegment implements Serializable {

	private static final long serialVersionUID = 3125198304242174732L;
	
	public PlaneSegment() {
		super();
	}
	
	public PlaneSegment(PlaneSegmentClass segmentClass) {
		super();
		seats = new HashSet<Seat>();
		this.segmentClass = segmentClass;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "planeSegment_id", unique = true, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "flight")
	@JsonIgnore
	private Flight flight;

	@OneToMany(mappedBy = "planeSegment", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<Seat> seats;
	
	@Column(name = "class")
	@Enumerated(EnumType.STRING)
	PlaneSegmentClass segmentClass;
	
	public boolean checkSeatExistence(int row, int column) {
		boolean exist = false;
		for (Seat s : this.getSeats()) {
			if (s.getRow() == row && s.getColumn() == column) {
				exist = true;
				break;
			}
		}
		return exist;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Seat> getSeats() {
		return seats;
	}

	public void setSeats(Set<Seat> seats) {
		this.seats = seats;
	}
	
	public PlaneSegmentClass getSegmentClass() {
		return segmentClass;
	}

	public void setSegmentClass(PlaneSegmentClass segmentClass) {
		this.segmentClass = segmentClass;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Flight getFlight() {
		return flight;
	}

	public void setFlight(Flight flight) {
		this.flight = flight;
	}

}
