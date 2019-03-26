package isamrs.tim1.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

/*@MappedSuperclass
public abstract class Service implements Serializable {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "service_id", unique = true, nullable = false)
	private Integer id;

	@Column(name = "name", unique = true, nullable = false)
	private String name;

	@Column(name = "description", unique = false, nullable = false)
	private String description;

	@Column(name = "averageGrade", unique = false, nullable = true)
	private double averageGrade;

	@Column(name = "averagePrice", unique = false, nullable = true)
	private double averagePrice;

	@OneToOne
	@JoinColumn(name = "location_id")
	private Location location;


	private static final long serialVersionUID = -4269295838625755485L;
}*/
