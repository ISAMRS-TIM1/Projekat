package isamrs.tim1.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import isamrs.tim1.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
	@Query(value = "select * from vehicles v where v.rentACar = ?1 and v.MODEL like ?2 and v.PRODUCER like ?3 and cast(v.YEAR_OF_PRODUCTION as char) like ?4 and cast(v.NUMBER_OF_SEATS as char) like ?5 and cast(v.FUEL_TYPE as char) like ?6 and cast(v.VEHICLE_TYPE as char) like ?7", nativeQuery = true)
	ArrayList<Vehicle> findByParameters(Integer rentACarID, String model, String producer, String yearOfProduction,
			String numberOfSeats, String fuelType, String vehicleType);
}
