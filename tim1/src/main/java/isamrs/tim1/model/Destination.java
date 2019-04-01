package isamrs.tim1.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Destinations")
public class Destination {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "destination_id", unique = true, nullable = false)
	private Long id;

	public Destination() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
