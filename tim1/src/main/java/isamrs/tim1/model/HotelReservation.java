package isamrs.tim1.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "HotelReservations")
public class HotelReservation extends Reservation {

	private static final long serialVersionUID = 4087468028517776623L;

}
