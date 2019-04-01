package isamrs.tim1.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "HotelReservations")
public class HotelReservation extends Reservation {

	@Column(name = "fromDate", unique = false, nullable = false)
	private Date fromDate;

	@Column(name = "toDate", unique = false, nullable = false)
	private Date toDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hotelRoom")
	private HotelRoom hotelRoom;
	
	private static final long serialVersionUID = 4087468028517776623L;

}
