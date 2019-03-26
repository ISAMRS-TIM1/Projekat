package isamrs.tim1.service;

import java.util.ArrayList;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import isamrs.tim1.model.BranchOffice;
import isamrs.tim1.model.FuelType;
import isamrs.tim1.model.Location;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.model.Vehicle;
import isamrs.tim1.model.VehicleType;
import isamrs.tim1.repository.RentACarRepository;
import isamrs.tim1.repository.VehicleRepository;

@Service
public class VehicleService {
	@Autowired
	private VehicleRepository vehicleRepository;
	// rentACarRepository added for making test objects, remove later
	@Autowired
	private RentACarRepository rentACarRepository;
	
	// making test objects and saving them in database
	public void makeObjets(){
		RentACar rent1 = new RentACar();
		rent1.setId(1);
		rent1.setAverageGrade(4.5);
		rent1.setAveragePrice(40.5);
		rent1.setBranchOffices(new HashSet<BranchOffice>());
		rent1.setDescription("desc1");
		rent1.setLocation(new Location());
		rent1.setName("ime1");
		rent1.setVehicles(new HashSet<Vehicle>());
		
		Vehicle v1 = new Vehicle();
		v1.setId(1);
		v1.setFuelType(FuelType.DIESEL);
		v1.setModel("m1");
		v1.setNumberOfSeats(4);
		v1.setProducer("p1");
		v1.setRentACar(rent1);
		v1.setVehicleType(VehicleType.CARAVAN);
		v1.setYearOfProduction("2000");
		
		Vehicle v2 = new Vehicle();
		v2.setId(2);
		v2.setFuelType(FuelType.DIESEL);
		v2.setModel("m2");
		v2.setNumberOfSeats(5);
		v2.setProducer("p1");
		v2.setRentACar(rent1);
		v2.setVehicleType(VehicleType.CARAVAN);
		v2.setYearOfProduction("2001");
		
		rent1.getVehicles().add(v1);
		rent1.getVehicles().add(v2);
		
		rentACarRepository.save(rent1);
	}
	// BUG: returns [] always
	public ArrayList<Vehicle> searchVehicles(Vehicle vehicle) {
		makeObjets();
		
		Integer rentACarID = vehicle.getRentACar().getId();
		// rentACarID is always null for some reason
		
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
		// changed rentACarID param for 1 here, still no success, so maybe the problem is in query
		ArrayList<Vehicle> searchResults = this.vehicleRepository.findByParameters(rentACarID, model, producer, year,
				numStr, ftStr, vtStr);

		return searchResults;
	}
}
