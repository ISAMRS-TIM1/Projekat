package isamrs.tim1.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Seat implements Serializable {

	private static final long serialVersionUID = -577061793806755543L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "seat_id", unique = true, nullable = false)
	private Long id;

	@Column(name = "row", unique = false, nullable = false)
	private Integer row;

	@Column(name = "column", unique = false, nullable = false)
	private Integer column;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "planeSegment")
	@JsonIgnore
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

	public Integer getColumn() {
		return column;
	}

	public void setColumn(int i) {
		this.column = i;
	}

	public PlaneSegment getPlaneSegment() {
		return planeSegment;
	}

	public void setPlaneSegment(PlaneSegment planeSegment) {
		this.planeSegment = planeSegment;
	}

}
