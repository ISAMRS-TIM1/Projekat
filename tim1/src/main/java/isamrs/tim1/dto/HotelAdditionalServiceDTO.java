package isamrs.tim1.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import isamrs.tim1.model.HotelAdditionalService;

public class HotelAdditionalServiceDTO {

	@NotBlank
	private String name;
	
	@Min(1)
	private double price;

	public HotelAdditionalServiceDTO(String name, double price) {
		super();
		this.name = name;
		this.price = price;
	}

	public HotelAdditionalServiceDTO(HotelAdditionalService has) {
		super();
		this.name = has.getName();
		this.price = has.getPrice();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}
