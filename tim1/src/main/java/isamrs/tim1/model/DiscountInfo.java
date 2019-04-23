package isamrs.tim1.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DiscountInfo")
public class DiscountInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "discountInfo_id", unique = true, nullable = false)
	private Long id;

	@Column(name = "discountPrecentagePerPoint", unique = false, nullable = false)
	private double discountPercentagePerPoint;

	@Column(name = "kmsNeededForPoint", unique = false, nullable = false)
	private double kmsNeededForPoint;

	public DiscountInfo() {
		super();
	}

	public double getDiscountPercentagePerPoint() {
		return discountPercentagePerPoint;
	}

	public void setDiscountPercentagePerPoint(double discountPercentagePerPoint) {
		this.discountPercentagePerPoint = discountPercentagePerPoint;
	}

	public double getKmsNeededForPoint() {
		return kmsNeededForPoint;
	}

	public void setKmsNeededForPoint(double kmsNeededForPoint) {
		this.kmsNeededForPoint = kmsNeededForPoint;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
