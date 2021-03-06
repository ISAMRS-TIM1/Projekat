package isamrs.tim1.repository;

import java.util.ArrayList;
import java.util.Set;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import isamrs.tim1.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(value = "select v from Vehicle v where v.model = :model and v.producer = :producer")
	Vehicle findOneByModelAndProducer(@Param("model") String model, @Param("producer") String producer);

	@Lock(LockModeType.PESSIMISTIC_READ)
	@Query(value = "select v from Vehicle v where v.model = :model and v.producer = :producer")
	Vehicle findOneByModelAndProducerForRead(@Param("model") String model, @Param("producer") String producer);

	@Query(value = "select distinct v.producer from vehicles v", nativeQuery = true)
	ArrayList<String> getAllProducers();

	@Query(value = "select v.model from vehicles v where v.producer = ?1", nativeQuery = true)
	ArrayList<String> getModelsForProducer(String producer);

	@Query(value = "select distinct v.model from vehicles v", nativeQuery = true)
	ArrayList<String> getAllModels();

	@Query(value = "select distinct * from vehicles v inner join branch_offices bo on v.rentacar = bo.rentacar inner join locations l on bo.location = l.location_id where v.deleted = false and v.producer in (?1) and v.model in (?2) and v.FUEL_TYPE in (?3) and v.VEHICLE_TYPE in (?4) and v.PRICE_PER_DAY <= ?5 and cast(v.YEAR_OF_PRODUCTION as int) between ?6 and ?7 and v.AVERAGE_GRADE between ?8 and ?9 and v.NUMBER_OF_SEATS >= ?10 and l.country like ?11", nativeQuery = true)
	Set<Vehicle> searchByParametersBranch(ArrayList<String> producers, ArrayList<String> models,
			ArrayList<String> fuelTypes, ArrayList<String> vehicleTypes, int maxPrice, int minYear, int maxYear,
			double minGrade, double maxGrade, int numberOfSeats, String country);

	@Query(value = "select distinct bo.name from vehicles v inner join branch_offices bo on v.rentacar = bo.rentacar where v.vehicle_id = ?1 and bo.deleted = false", nativeQuery = true)
	Set<String> branchOfficesForVehicle(Integer vehicleId);

	@Query(value = "select l.country from vehicles v inner join branch_offices bo on v.rentacar = bo.rentacar inner join locations l on bo.location = l.location_id where v.vehicle_id = ?2 and bo.name = ?1", nativeQuery = true)
	String checkCountry(String branch, Integer vehicle);
}
