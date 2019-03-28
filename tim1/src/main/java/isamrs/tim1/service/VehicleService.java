package isamrs.tim1.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.dto.VehicleDTO;
import isamrs.tim1.model.FuelType;
import isamrs.tim1.model.Vehicle;
import isamrs.tim1.model.VehicleType;
import isamrs.tim1.repository.VehicleRepository;

@Service
public class VehicleService {
	@Autowired
	private VehicleRepository vehicleRepository;

	public ArrayList<VehicleDTO> searchVehicles(Vehicle vehicle) {
		String model = vehicle.getModel();

		if (model == null) {
			model = "%";
		}

		String producer = vehicle.getProducer();

		if (producer == null) {
			producer = "%";
		}

		String year = vehicle.getYearOfProduction();

		if (year == null) {
			year = "%";
		}

		Integer num = vehicle.getNumberOfSeats();
		String numStr;
		if (num == null) {
			numStr = "%";
		} else {
			numStr = num.toString();
		}

		FuelType ft = vehicle.getFuelType();
		String ftStr;
		if (ft == null) {
			ftStr = "%";
		} else {
			ftStr = ft.toString();
		}

		VehicleType vt = vehicle.getVehicleType();
		String vtStr;
		if (vt == null) {
			vtStr = "%";
		} else {
			vtStr = vt.toString();
		}

		ArrayList<Vehicle> searchResults = this.vehicleRepository.findByParameters(vehicle.getRentACar().getId(), model,
				producer, year, numStr, ftStr, vtStr);

		ArrayList<VehicleDTO> toBeReturned = new ArrayList<VehicleDTO>();
		for (Vehicle v : searchResults) {
			toBeReturned.add(new VehicleDTO(v));
		}

		return toBeReturned;
	}
}
