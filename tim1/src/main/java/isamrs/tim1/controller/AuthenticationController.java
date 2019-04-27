package isamrs.tim1.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.MessageDTO.ToasterType;
import isamrs.tim1.model.Airline;
import isamrs.tim1.model.AirlineAdmin;
import isamrs.tim1.model.Authority;
import isamrs.tim1.model.Hotel;
import isamrs.tim1.model.HotelAdmin;
import isamrs.tim1.model.RegisteredUser;
import isamrs.tim1.model.RentACar;
import isamrs.tim1.model.RentACarAdmin;
import isamrs.tim1.model.Service;
import isamrs.tim1.model.ServiceGrade;
import isamrs.tim1.model.User;
import isamrs.tim1.model.UserReservation;
import isamrs.tim1.model.UserTokenState;
import isamrs.tim1.model.UserType;
import isamrs.tim1.repository.ServiceRepository;
import isamrs.tim1.security.TokenUtils;
import isamrs.tim1.security.auth.JwtAuthenticationRequest;
import isamrs.tim1.service.CustomUserDetailsService;
import isamrs.tim1.service.EmailService;
import isamrs.tim1.service.UserService;

@RestController
public class AuthenticationController {
	@Autowired
	TokenUtils tokenUtils;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private EmailService mailService;

	@Autowired
	private UserService userService;

	@Autowired
	private ServiceRepository serviceRepository;

	@RequestMapping(value = "auth/confirm", method = RequestMethod.GET)
	public RedirectView confirmRegistration(@RequestParam String token) {
		User ru = userService.findUserByToken(token);
		if (ru != null) {
			ru.setVerified(true);
			userDetailsService.saveUser(ru);
			return new RedirectView("/registration/verified.html");
		}
		return null;
	}

	@RequestMapping(value = "auth/register", method = RequestMethod.POST)
	public ResponseEntity<?> register(@Valid @RequestBody User user) {
		if (this.userDetailsService.usernameTaken(user.getEmail()) == true) {
			return new ResponseEntity<MessageDTO>(new MessageDTO("Email is taken.", "Error"), HttpStatus.OK);
		}

		RegisteredUser ru = new RegisteredUser();
		ru.setId(null);
		ru.setEmail(user.getEmail());
		ru.setPassword(this.userDetailsService.encodePassword(user.getPassword()));
		ru.setAddress(user.getAddress());
		List<Authority> authorities = new ArrayList<Authority>();
		Authority a = new Authority();
		a.setType(UserType.ROLE_REGISTEREDUSER);
		authorities.add(a);
		ru.setAuthorities(authorities);
		ru.setDiscountPoints(0);
		ru.setEnabled(true);
		ru.setPasswordChanged(true);
		ru.setVerified(false);
		ru.setFriends(new HashSet<RegisteredUser>());
		ru.setFirstName(user.getFirstName());
		ru.setLastName(user.getLastName());
		ru.setLastPasswordResetDate(new Timestamp(System.currentTimeMillis()));
		ru.setPhoneNumber(user.getPhoneNumber());
		ru.setUserReservations(new HashSet<UserReservation>());
		ru.setServiceGrades(new HashSet<ServiceGrade>());

		if (this.userDetailsService.saveUser(ru)) {
			mailService.sendMailAsync(ru);
			return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		}
		return new ResponseEntity<Boolean>(false, HttpStatus.OK);
	}

	@RequestMapping(value = "auth/login", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest,
			HttpServletResponse response) throws AuthenticationException, IOException {

		final Authentication authentication;
		try {
			authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					authenticationRequest.getUsername(), authenticationRequest.getPassword()));
		} catch (BadCredentialsException e) {
			return new ResponseEntity<MessageDTO>(new MessageDTO("Wrong email or password.", "Error"), HttpStatus.OK);
		}
		User user = (User) authentication.getPrincipal();

		/*if (!user.isVerified()) {
			return new ResponseEntity<MessageDTO>(new MessageDTO("Account is not verified. Check your email.", "Error"),
					HttpStatus.OK);
		}*/
		// Ubaci username + password u kontext
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Kreiraj token
		String jwt = tokenUtils.generateToken(user.getUsername());
		int expiresIn = tokenUtils.getExpiredIn();
		UserType userType = null;
		boolean valid = false;
		
		if (user instanceof RegisteredUser) {
			userType = UserType.ROLE_REGISTEREDUSER;
			valid = true;
		} else if (user instanceof HotelAdmin) {
			userType = UserType.ROLE_HOTELADMIN;
			valid = user.isPasswordChanged();
		} else if (user instanceof RentACarAdmin) {
			userType = UserType.ROLE_RENTADMIN;
			valid = user.isPasswordChanged();
		} else if (user instanceof AirlineAdmin) {
			userType = UserType.ROLE_AIRADMIN;
			valid = user.isPasswordChanged();
		} else if (user.getClass().equals(User.class)) {
			userType = UserType.ROLE_SYSADMIN;
			valid = true;
		}

		// Vrati token kao odgovor na uspesno autentifikaciju
		return new ResponseEntity<UserTokenState>(new UserTokenState(jwt, expiresIn, userType, valid), HttpStatus.OK);
	}

	@RequestMapping(value = "auth/registerAdmin/{serviceName}", method = RequestMethod.POST)
	public ResponseEntity<MessageDTO> registerAirlineAdmin(@Valid @RequestBody User user,
			@PathVariable("serviceName") String serviceName) {

		Service service = serviceRepository.findOneByName(serviceName);
		if (service == null)
			return new ResponseEntity<MessageDTO>(new MessageDTO("Service to which is admin added is not existent", ToasterType.ERROR.toString()), HttpStatus.BAD_REQUEST);
		User admin = null;
		Authority a = new Authority();

		if (service instanceof Airline) {
			admin = new AirlineAdmin();
			((Airline) service).getAdmins().add((AirlineAdmin) admin);
			((AirlineAdmin) admin).setAirline((Airline) service);
			a.setType(UserType.ROLE_AIRADMIN);
		} else if (service instanceof Hotel) {
			admin = new HotelAdmin();
			((Hotel) service).getAdmins().add((HotelAdmin) admin);
			((HotelAdmin) admin).setHotel((Hotel) service);
			a.setType(UserType.ROLE_HOTELADMIN);
		} else if (service instanceof RentACar) {
			admin = new RentACarAdmin();
			((RentACar) service).getAdmins().add((RentACarAdmin) admin);
			((RentACarAdmin) admin).setRentACar((RentACar) service);
			a.setType(UserType.ROLE_RENTADMIN);
		}

		admin.setId(null);
		admin.setEmail(user.getEmail());
		admin.setPassword(this.userDetailsService.encodePassword(user.getPassword()));
		admin.setAddress(user.getAddress());
		List<Authority> authorities = new ArrayList<Authority>();
		authorities.add(a);
		admin.setAuthorities(authorities);
		admin.setEnabled(true);
		admin.setPasswordChanged(false);
		admin.setVerified(true);
		admin.setFirstName(user.getFirstName());
		admin.setLastName(user.getLastName());
		admin.setLastPasswordResetDate(new Timestamp(System.currentTimeMillis()));
		admin.setPhoneNumber(user.getPhoneNumber());
		if (this.userDetailsService.saveUser(admin)) {
			mailService.sendMailToAdmin(admin, user.getPassword());
			return new ResponseEntity<MessageDTO>(new MessageDTO("Admin added successfully", ToasterType.SUCCESS.toString()), HttpStatus.OK);
		}
		return new ResponseEntity<MessageDTO>(new MessageDTO("Email already in use", ToasterType.ERROR.toString()), HttpStatus.OK);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "auth/changePassword", method = RequestMethod.PUT)
	public ResponseEntity<UserTokenState> changePassword(@RequestBody String password) {
		User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		userDetailsService.changePassword(password);
		String jwt = tokenUtils.generateToken(u.getUsername());
		int expiresIn = tokenUtils.getExpiredIn();
		List<Authority> a = (List<Authority>) u.getAuthorities();
		return new ResponseEntity<UserTokenState>(new UserTokenState(jwt, expiresIn, a.get(0).getType(), true), HttpStatus.OK);
	}
}
