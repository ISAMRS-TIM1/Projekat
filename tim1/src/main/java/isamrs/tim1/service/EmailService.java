package isamrs.tim1.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import isamrs.tim1.model.FlightReservation;
import isamrs.tim1.model.RegisteredUser;
import isamrs.tim1.model.User;
import isamrs.tim1.model.VerificationToken;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private Environment env;

	@Autowired
	private VerificationTokenService verificationTokenService;

	@Async
	public void sendMailAsync(RegisteredUser ru) {
		String token = UUID.randomUUID().toString();
		VerificationToken vtoken = new VerificationToken();
		vtoken.setId(null);
		vtoken.setToken(token);
		vtoken.setUser(ru);
		verificationTokenService.saveToken(vtoken);

		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(ru.getEmail());
		mail.setFrom(env.getProperty("spring.mail.username"));
		mail.setSubject("Flights - registration");
		String content = null;
		try {
			content = String.format(
					"Hello %s,\nTo finish the registration, click on link below:\nhttp://localhost:8000/auth/confirm?token=%s",
					ru.getEmail(), URLEncoder.encode(token, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		mail.setText(content);
		mailSender.send(mail);
	}
	
	@Async
	public void sendMailToAdmin(User admin, String password) {

		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(admin.getEmail());
		mail.setFrom(env.getProperty("spring.mail.username"));
		mail.setSubject("Flights - registration");
		String content = String.format(
					"Hello %s %s,\nYour password is: %s",
					admin.getFirstName(), admin.getLastName(), password);
		mail.setText(content);
		mailSender.send(mail);
	}
	
	@Async
	public void sendMailToFriend(RegisteredUser friend, Long id, String inviter) {
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(friend.getEmail());
		mail.setFrom(env.getProperty("spring.mail.username"));
		mail.setSubject("Flight reservation invitation");
		String content = String.format(
					"Hello %s %s,\nYour friend %s invited you to flight. "
					+ "Details are available on link below:\nhttp://localhost:8000/api/flightInvitation?id=%d", 
					friend.getFirstName(), friend.getLastName(), inviter, id);
		mail.setText(content);
		mailSender.send(mail);
	}

	@Async
	public void sendFlightReservationMail(RegisteredUser ru, FlightReservation fr) {
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(ru.getEmail());
		mail.setFrom(env.getProperty("spring.mail.username"));
		mail.setSubject("Flight reservation");
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		String depTime = sdf.format(fr.getFlight().getDepartureTime());
		String landTime = sdf.format(fr.getFlight().getLandingTime());
		String content = String.format(
					"Hello %s %s,\nYour flight reservation is successfully made."
					+ "\nStart destination: %s\nEnd destination %s\nDeparture time: %s\nLanding time: %s\n"
					+ "Number of seats: %d\nPrice %.2f\n", 
					ru.getFirstName(), ru.getLastName(), fr.getFlight().getStartDestination().getName(), 
					fr.getFlight().getEndDestination().getName(), depTime, landTime, fr.getPassengerSeats().size(), fr.getPrice());
		mail.setText(content);
		mailSender.send(mail);
	}
}
