package isamrs.tim1.repository;

import java.util.ArrayList;
import java.util.Set;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import isamrs.tim1.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Vehicle findOneByModelAndProducer(String model, String producer);

	@Query(value = "select distinct v.producer from vehicles v", nativeQuery = true)
	ArrayList<String> getAllProducers();

	@Query(value = "select v.model from vehicles v where v.producer = ?1", nativeQuery = true)
	ArrayList<String> getModelsForProducer(String producer);

	@Query(value = "select distinct v.model from vehicles v", nativeQuery = true)
	ArrayList<String> getAllModels();

	/*
	 * @Query(value =
	 * "select * from vehicles v where v.deleted = false and v.producer in (?1) and v.model in (?2) and v.FUEL_TYPE in (?3) and v.VEHICLE_TYPE in (?4) and v.PRICE_PER_DAY <= ?5 and cast(v.YEAR_OF_PRODUCTION as int) between ?6 and ?7 and v.AVERAGE_GRADE between ?8 and ?9 and v.NUMBER_OF_SEATS >= ?10"
	 * , nativeQuery = true) ArrayList<Vehicle> searchByParameters(ArrayList<String>
	 * producers, ArrayList<String> models, ArrayList<String> fuelTypes,
	 * ArrayList<String> vehicleTypes, int maxPrice, int minYear, int maxYear,
	 * double minGrade, double maxGrade, int numberOfSeats);
	 */

	@Query(value = "select distinct * from vehicles v inner join branch_offices bo on v.rentacar = bo.rentacar inner join locations l on bo.location = l.location_id where v.deleted = false and v.producer in (?1) and v.model in (?2) and v.FUEL_TYPE in (?3) and v.VEHICLE_TYPE in (?4) and v.PRICE_PER_DAY <= ?5 and cast(v.YEAR_OF_PRODUCTION as int) between ?6 and ?7 and v.AVERAGE_GRADE between ?8 and ?9 and v.NUMBER_OF_SEATS >= ?10 and l.country = ?11", nativeQuery = true)
	Set<Vehicle> searchByParametersBranch(ArrayList<String> producers, ArrayList<String> models,
			ArrayList<String> fuelTypes, ArrayList<String> vehicleTypes, int maxPrice, int minYear, int maxYear,
			double minGrade, double maxGrade, int numberOfSeats, String country);

	@Query(value = "select distinct * from vehicles v inner join rentacars r on v.rentacar = r.service_id inner join locations l on r.location = l.location_id where v.deleted = false and v.producer in (?1) and v.model in (?2) and v.FUEL_TYPE in (?3) and v.VEHICLE_TYPE in (?4) and v.PRICE_PER_DAY <= ?5 and cast(v.YEAR_OF_PRODUCTION as int) between ?6 and ?7 and v.AVERAGE_GRADE between ?8 and ?9 and v.NUMBER_OF_SEATS >= ?10 and l.country = ?11", nativeQuery = true)
	Set<Vehicle> searchByParametersRentacar(ArrayList<String> producers, ArrayList<String> models,
			ArrayList<String> fuelTypes, ArrayList<String> vehicleTypes, int maxPrice, int minYear, int maxYear,
			double minGrade, double maxGrade, int numberOfSeats, String country);
}
