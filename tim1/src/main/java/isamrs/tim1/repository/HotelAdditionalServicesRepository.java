package isamrs.tim1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import isamrs.tim1.model.HotelAdditionalService;

public interface HotelAdditionalServicesRepository extends JpaRepository<HotelAdditionalService, Integer> {
	@Query(value = "select * from hotel_additional_services has where has.name = ?1 and has.hotel = ?2 and has.deleted = false", nativeQuery = true)
	HotelAdditionalService findOneByNameAndHotel(String name, Integer hotel);
}
