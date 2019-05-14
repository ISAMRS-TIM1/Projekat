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

	@Query(value = "select max(v.pricePerDay) from vehicles v", nativeQuery = true)
	Integer getMaxPricePerDay();

	@Query(value = "select max(v.numberOfSeats) from vehicles v", nativeQuery = true)
	Integer getMaxSeatsForVehicle();

	@Query(value = "select min(cast(v.yearOfProduction as int)) from vehicles v", nativeQuery = true)
	Integer getMinYear();

	@Query(value = "select max(cast(v.yearOfProduction as int)) from vehicles v", nativeQuery = true)
	Integer getMaxYear();

	@Query(value = "select * from vehicles v where v.producer in (?1) and v.model in (?2) and v.fuelType in (?3) and v.vehicleType in (?4) and v.pricePerDay <= ?5 and cast(v.yearOfProduction as int) between ?6 and ?7 and v.averageGrade between ?8 and ?9 and v.numberOfSeats = ?10")
	ArrayList<Vehicle> searchByParameters(ArrayList<String> producers, ArrayList<String> models,
			ArrayList<String> fuelTypes, ArrayList<String> vehicleTypes, int maxPrice, int minYear, int maxYear,
			double minGrade, double maxGrade, int numberOfSeats);

}
