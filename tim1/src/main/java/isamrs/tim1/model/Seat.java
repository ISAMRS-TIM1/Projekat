package isamrs.tim1.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Seat {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "seat_id", unique = true, nullable = false)
	private Long id;
	
	@Version
	private Integer version;

	@Column(name = "row", unique = false, nullable = false)
	private Integer row;

	@Column(name = "column", unique = false, nullable = false)
	private Integer column;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "planeSegment")
	@JsonIgnore
	private PlaneSegment planeSegment;
	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "passengerSeat_id", referencedColumnName = "passengerSeat_id")
	@JsonIgnore
    private PassengerSeat passengerSeat;

	public Seat() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Integer getRow() {
		return row;
	}

	public void setRow(Integer row) {
		this.row = row;
	}

	public Integer getColumn() {
		return column;
	}

	public void setColumn(Integer i) {
		this.column = i;
	}

	public PlaneSegment getPlaneSegment() {
		return planeSegment;
	}

	public void setPlaneSegment(PlaneSegment planeSegment) {
		this.planeSegment = planeSegment;
	}

	public PassengerSeat getPassengerSeat() {
		return passengerSeat;
	}

	public void setPassengerSeat(PassengerSeat passengerSeat) {
		this.passengerSeat = passengerSeat;
	}
}
