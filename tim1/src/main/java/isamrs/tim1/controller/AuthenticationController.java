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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import isamrs.tim1.dto.MessageDTO;
import isamrs.tim1.dto.UserDTO;
import isamrs.tim1.model.AirlineAdmin;
import isamrs.tim1.model.Authority;
import isamrs.tim1.model.HotelAdmin;
import isamrs.tim1.model.RegisteredUser;
import isamrs.tim1.model.RentACarAdmin;
import isamrs.tim1.model.ServiceGrade;
import isamrs.tim1.model.User;
import isamrs.tim1.model.UserReservation;
import isamrs.tim1.model.UserTokenState;
import isamrs.tim1.model.UserType;
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

	@RequestMapping(value = "auth/confirm", method = RequestMethod.GET)
	public RedirectView confirmRegistration(@RequestParam String token) {
		User ru = userService.findUserByToken(token);
		if (ru != null) {
			ru.setEnabled(true);
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
		ru.setEnabled(false);
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

		if (!user.isEnabled()) {
			return new ResponseEntity<MessageDTO>(new MessageDTO("Account is not verified. Check your email.", "Error"),
					HttpStatus.OK);
		}
		// Ubaci username + password u kontext
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Kreiraj token
		String jwt = tokenUtils.generateToken(user.getUsername());
		int expiresIn = tokenUtils.getExpiredIn();
		UserType userType = null;

		if (user instanceof RegisteredUser) {
			userType = UserType.ROLE_REGISTEREDUSER;
		} else if (user instanceof HotelAdmin) {
			userType = UserType.ROLE_HOTELADMIN;
		} else if (user instanceof RentACarAdmin) {
			userType = UserType.ROLE_RENTADMIN;
		} else if (user instanceof AirlineAdmin) {
			userType = UserType.ROLE_AIRADMIN;
		} else if (user.getClass().equals(User.class)){
			userType = UserType.ROLE_SYSADMIN;
		}

		// Vrati token kao odgovor na uspesno autentifikaciju
		return new ResponseEntity<UserTokenState>(new UserTokenState(jwt, expiresIn, userType), HttpStatus.OK);
	}

	@RequestMapping(value = "/api/getUserInfo", method = RequestMethod.GET)
	public ResponseEntity<UserDTO> getUserData() {
		// fix this
		User u = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return new ResponseEntity<UserDTO>(
				new UserDTO(u.getFirstName(), u.getLastName(), u.getPhoneNumber(), u.getAddress(), u.getEmail()),
				HttpStatus.OK);
	}
}
