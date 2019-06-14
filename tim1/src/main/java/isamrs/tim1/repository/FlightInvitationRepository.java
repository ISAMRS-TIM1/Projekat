package isamrs.tim1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import isamrs.tim1.model.FlightInvitation;


public interface FlightInvitationRepository extends JpaRepository<FlightInvitation, Integer> {

}
