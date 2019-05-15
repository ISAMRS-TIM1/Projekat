package isamrs.tim1.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import isamrs.tim1.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
	Vehicle findOneByModelAndProducer(String model, String producer);

	@Query(value = "select distinct v.producer from vehicles v", nativeQuery = true)
	ArrayList<String> getAllProducers();

	@Query(value = "select v.model from vehicles v where v.producer = ?1", nativeQuery = true)
	ArrayList<String> getModelsForProducer(String producer);

	@Query(value = "select distinct v.model from vehicles v", nativeQuery = true)
	ArrayList<String> getAllModels();

	@Query(value = "select max(v.PRICE_PER_DAY) from vehicles v", nativeQuery = true)
	Integer getMaxPricePerDay();

	@Query(value = "select max(v.NUMBER_OF_SEATS) from vehicles v", nativeQuery = true)
	Integer getMaxSeatsForVehicle();

	@Query(value = "select min(cast(v.YEAR_OF_PRODUCTION as int)) from vehicles v", nativeQuery = true)
	Integer getMinYear();

	@Query(value = "select max(cast(v.YEAR_OF_PRODUCTION as int)) from vehicles v", nativeQuery = true)
	Integer getMaxYear();

	@Query(value = "select * from vehicles v where v.deleted = false and v.producer in (?1) and v.model in (?2) and v.FUEL_TYPE in (?3) and v.VEHICLE_TYPE in (?4) and v.PRICE_PER_DAY <= ?5 and cast(v.YEAR_OF_PRODUCTION as int) between ?6 and ?7 and v.AVERAGE_GRADE between ?8 and ?9 and v.NUMBER_OF_SEATS = ?10", nativeQuery = true)
	ArrayList<Vehicle> searchByParameters(ArrayList<String> producers, ArrayList<String> models,
			ArrayList<String> fuelTypes, ArrayList<String> vehicleTypes, int maxPrice, int minYear, int maxYear,
			double minGrade, double maxGrade, int numberOfSeats);

}
