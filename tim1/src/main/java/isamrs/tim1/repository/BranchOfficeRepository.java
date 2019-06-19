package isamrs.tim1.repository;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import isamrs.tim1.model.BranchOffice;

public interface BranchOfficeRepository extends JpaRepository<BranchOffice, Integer> {
	@Query(value = "select * from branch_offices b where b.name = ?1 and b.rentacar = ?2", nativeQuery = true)
	BranchOffice findOneByNameAndService(String name, Integer rentACar);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(value = "select b from BranchOffice b where b.name = :name")
	BranchOffice findOneByName(@Param("name") String name);

	@Lock(LockModeType.PESSIMISTIC_READ)
	@Query(value = "select b from BranchOffice b where b.name = :name")
	BranchOffice findOneByNameForRead(@Param("name") String name);
}
