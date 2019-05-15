package isamrs.tim1.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Reservation implements Serializable {

	private static final long serialVersionUID = 3260448960552608553L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "reservation_id", unique = true, nullable = false)
	private Long id;

	@Column(name = "dateOfReservation", unique = false, nullable = true)
	private Date dateOfReservation;

	@Column(name = "done", unique = false, nullable = true)
	private Boolean done;

	@Column(name = "price", unique = false, nullable = true)
	private Double price;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user")
	private UserReservation user;

	public Reservation() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDateOfReservation() {
		return dateOfReservation;
	}

	public void setDateOfReservation(Date dateOfReservation) {
		this.dateOfReservation = dateOfReservation;
	}

	public boolean isDone() {
		return done.booleanValue();
	}

	public void setDone(Boolean done) {
		this.done = done;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public UserReservation getUser() {
		return user;
	}

	public void setUser(UserReservation user) {
		this.user = user;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
