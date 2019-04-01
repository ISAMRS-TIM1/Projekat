package isamrs.tim1.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Seat {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "seat_id", unique = true, nullable = false)
	private Long id;

	@Column(name = "row", unique = false, nullable = false)
	private Integer row;

	@Column(name = "label", unique = false, nullable = false)
	private String label;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "planeSegment")
	private PlaneSegment planeSegment;

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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public PlaneSegment getPlaneSegment() {
		return planeSegment;
	}

	public void setPlaneSegment(PlaneSegment planeSegment) {
		this.planeSegment = planeSegment;
	}

}
