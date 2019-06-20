INSERT INTO AUTHORITIES VALUES(1, 'ROLE_AIRADMIN');
INSERT INTO AUTHORITIES VALUES(2, 'ROLE_HOTELADMIN');
INSERT INTO AUTHORITIES VALUES(3, 'ROLE_RENTADMIN');
INSERT INTO AUTHORITIES VALUES(4, 'ROLE_REGISTEREDUSER');

INSERT INTO LOCATIONS VALUES(5, 'France', 48.8, 2.35);
INSERT INTO LOCATIONS VALUES(6, 'Serbia', 44.81, 20.46);
INSERT INTO LOCATIONS VALUES(7, 'Germany', 52.36, 13.26);
INSERT INTO LOCATIONS VALUES(8, 'United Kingdom', 51.44, -0.2);
INSERT INTO LOCATIONS VALUES(9, 'Russia', 55.64, 37.66);
INSERT INTO LOCATIONS VALUES(10, 'USA', 40.5, -73.99);

INSERT INTO AIRLINES VALUES(11, 0, 'Paris Charles de Gaulle Airport, also known as Roissy Airport (name of the local district), 
	is the largest international airport in France and the second largest in Europe.', 'Charles de Gaulle', 5);
INSERT INTO AIRLINES VALUES(12, 0, 'Air Serbia is the flag carrier of Serbia. The company headquarters is located in Belgrade, Serbia, 
	and its main hub is Belgrade Nikola Tesla Airport.', 'AirSerbia', 6);

INSERT INTO HOTELS VALUES(13, 0, 'This stylish hotel lies in Belgrades popular Friedrichshain district, 
	right between the Mercedes Benz Arena and the East Side Gallery.', 'Belgrade’s hotel', 6);
INSERT INTO HOTELS VALUES(14, 0, 'Clayton Hotel City of London is ideally located for the city’s Financial District with a 10-minute walk 
	from Liverpool Street Station and close to some of London’s Iconic landmarks such as The Gherkin, Tower Bridge, 
	Old Spitalfields Market and Brick Lane.', 'Londons hotel', 8);

INSERT INTO RENTACARS VALUES(15, 0, 'Sixt is a global leading car rental provider with more than 4,000 locations worldwide.', 'Moscow Car Sixt', 9);
INSERT INTO RENTACARS VALUES(16, 0, 'A rental car from Enterprise Rent-A-Car is perfect for road trips, airport travel or to get around the city on weekends.', 'NY Enterprise', 10);

INSERT INTO USERS VALUES('AirlineAdmin', 17, 'Milana Stojica 5', 'a@a.com', 'true', 'Admir', 'Airlinovic', '2017-10-01 21:58:58.508-07',  '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'true', '+381635486254', 'true', null, null, null, 11);
INSERT INTO USER_AUTHORITY VALUES(17, 1);
INSERT INTO USERS VALUES('AirlineAdmin', 19, 'Petra Marica 25', 'aa@a.com', 'true', 'Nedjo', 'Alen', '2017-10-01 21:58:58.508-07',  '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'false', '+381635486254', 'true', null, null, null, 12);
INSERT INTO USER_AUTHORITY VALUES(19, 1);

INSERT INTO USERS VALUES('HotelAdmin', 21, 'Milana Stojica 6', 'h@h.com', 'true', 'Admin', 'Hotelovic', '2017-10-01 21:58:58.508-07',  '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'true', '+381635486254', 'true', null, null, 13, null);
INSERT INTO USER_AUTHORITY VALUES(21, 2);
INSERT INTO USERS VALUES('HotelAdmin', 23, 'Petra Marica 26', 'hh@h.com', 'true', 'Nikola', 'Nikolic', '2017-10-01 21:58:58.508-07',  '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'false', '+381635486254', 'true', null, null, 14, null);
INSERT INTO USER_AUTHORITY VALUES(23, 2);

INSERT INTO USERS VALUES('RentACarAdmin', 25, 'Milana Stojica 7', 'r@r.com', 'true', 'Admil', 'Karic', '2017-10-01 21:58:58.508-07',  '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'true', '+381635486254', 'true', null, 15, null, null);
INSERT INTO USER_AUTHORITY VALUES(25, 3);
INSERT INTO USERS VALUES('RentACarAdmin', 27, 'Petra Marica 27', 'rr@r.com', 'true', 'Marko', 'Markovic', '2017-10-01 21:58:58.508-07',  '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'false', '+381635486254', 'true', null, 16, null, null);
INSERT INTO USER_AUTHORITY VALUES(27, 3);

INSERT INTO USERS VALUES('RegisteredUser', 29, 'Milana Stojica 8', 'u@u.com', 'true', 'Miko', 'Mikic', '2017-10-01 21:58:58.508-07',  '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'true', '+381635486254', 'true', 0, null, null, null);
INSERT INTO USER_AUTHORITY VALUES(29, 4);
INSERT INTO USERS VALUES('RegisteredUser', 31, 'Petra Marica 28', 'uu@u.com', 'true', 'Ziko', 'Zikic', '2017-10-01 21:58:58.508-07',  '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'true', '+381635486254', 'true', 0, null, null, null);
INSERT INTO USER_AUTHORITY VALUES(31, 4);
INSERT INTO USERS VALUES('RegisteredUser', 33, 'Petra Marica 29', 'uuu@u.com', 'true', 'Rado', 'Radic', '2017-10-01 21:58:58.508-07',  '$2a$04$Vbug2lwwJGrvUXTj6z7ff.97IzVBkrJ1XfApfGNl.Z695zqcnPYra', 'true', '+381635486254', 'true', 5, null, null, null);
INSERT INTO USER_AUTHORITY VALUES(33, 4);

INSERT INTO DESTINATIONS VALUES(35, 'Paris', 11, 5);
INSERT INTO DESTINATIONS VALUES(36, 'Belgrade', 11, 6);
INSERT INTO DESTINATIONS VALUES(37, 'Berlin', 11, 7);
INSERT INTO DESTINATIONS VALUES(38, 'London', 12, 8);
INSERT INTO DESTINATIONS VALUES(39, 'Moscow', 12, 9);
INSERT INTO DESTINATIONS VALUES(40, 'New York', 12, 10);

INSERT INTO FLIGHTS VALUES(41, 0.0, 100.0, '2019-06-23 12:30:00', 80.0, 120.0, 'PABE2', 60, 1500, '2019-06-23 13:30:00', 'aced0005737200136a6176612e7574696c2e41727261794c6973747881d21d99c7619d03000149000473697a6578700000000077040000000078', 0, 10.0, null, null, 'false', 12, 36, 35);
INSERT INTO FLIGHTS VALUES(42, 0.0, 100.0, '2019-06-30 12:30:00', 80.0, 120.0, 'BEBE2', 60, 1500, '2019-06-30 13:30:00', 'aced0005737200136a6176612e7574696c2e41727261794c6973747881d21d99c7619d03000149000473697a6578700000000077040000000078', 0, 10.0, null, null, 'false', 12, 36, 37);

INSERT INTO PLANE_SEGMENT VALUES(43, 'ECONOMY', 41);
INSERT INTO PLANE_SEGMENT VALUES(44, 'FIRST', 41);
INSERT INTO PLANE_SEGMENT VALUES(45, 'BUSINESS', 41);
INSERT INTO PLANE_SEGMENT VALUES(46, 'ECONOMY', 42);
INSERT INTO PLANE_SEGMENT VALUES(47, 'FIRST', 42);
INSERT INTO PLANE_SEGMENT VALUES(48, 'BUSINESS', 42);

INSERT INTO HOTEL_ROOMS VALUES(79, 4.0, 20.0, 'false', 2, 1, 13);
INSERT INTO HOTEL_ROOMS VALUES(80, 3.0, 20.0, 'false', 3, 2, 14);

INSERT INTO BRANCH_OFFICES VALUES(81, 'false', 'Belgrade', 6, 15);
INSERT INTO BRANCH_OFFICES VALUES(82, 'false', 'London', 8, 15);
INSERT INTO BRANCH_OFFICES VALUES(83, 'false', 'Berlin', 7, 16);

INSERT INTO VEHICLES VALUES(84, 2, 4.0, 'false', 'DIESEL', 'Niva', 5, 20.0, 'Lada', 5, 'SUV', '2004', 15);
INSERT INTO VEHICLES VALUES(85, 3, 4.0, 'false', 'DIESEL', 'Astra', 5, 25.0, 'Opel', 5, 'LIMOUSINE', '2014', 15);
INSERT INTO VEHICLES VALUES(86, 3, 4.0, 'false', 'GASOLINE', 'Zafira', 5, 25.0, 'Opel', 5, 'LIMOUSINE', '2014', 16);

INSERT INTO HOTEL_RESERVATIONS VALUES('HotelReservation', 87, 'true', '2019-06-23 00:00:00', 5.0, 40.0, '2019-06-25 00:00:00', 0, null, 79);
INSERT INTO HOTELS_RESERVATIONS VALUES(13, 87);
INSERT INTO HOTEL_RESERVATIONS VALUES('HotelReservation', 89, 'false', '2019-06-30 00:00:00', null, 40.0, '2019-07-02 00:00:00', 0, null, 79);
INSERT INTO HOTELS_RESERVATIONS VALUES(13, 89);
INSERT INTO HOTEL_RESERVATIONS VALUES('QuickHotelReservation', 91, 'false', '2019-06-23 00:00:00', null, 40.0, '2019-06-25 00:00:00', 0, 20, 79);
INSERT INTO HOTELS_RESERVATIONS VALUES(13, 91);

INSERT INTO VEHICLE_RESERVATIONS VALUES('VehicleReservation', 93, 'true', '2019-06-23 00:00:00', 5.0, 40.0, '2019-06-25 00:00:00', 0, null, 81, 84);
INSERT INTO RENTACARS_RESERVATIONS VALUES(15, 93);
INSERT INTO VEHICLE_RESERVATIONS VALUES('QuickVehicleReservation', 95, 'false', '2019-06-30 00:00:00', 5.0, 40.0, '2019-07-02 00:00:00', 0, null, 81, 85);
INSERT INTO RENTACARS_RESERVATIONS VALUES(15, 95);

INSERT INTO FLIGHT_RESERVATIONS VALUES('FlightReservation', 97, '2019-06-20 18:27:23.177', 'true', 4.0, 120.0, 0, 0, null, 41, 87, 29, 93);
INSERT INTO AIRLINES_RESERVATIONS VALUES(12, 97);
INSERT INTO FLIGHT_RESERVATIONS VALUES('FlightReservation', 99, '2019-06-20 18:27:23.177', 'false', 4.0, 120.0, 0, 0, null, 42, 89, 29, null);
INSERT INTO AIRLINES_RESERVATIONS VALUES(12, 99);
INSERT INTO FLIGHT_RESERVATIONS VALUES('QuickFlightReservation', 101, '2019-06-20 18:27:23.177', 'false', 4.0, 120.0, 0, 0, 20.0, 42, null, null, null);
INSERT INTO AIRLINES_RESERVATIONS VALUES(12, 101);

INSERT INTO PASSENGER_SEATS VALUES(103, 'Miko', '123456789', 'Mikic', 97);
INSERT INTO PASSENGER_SEATS VALUES(104, 'Miko', '123456789', 'Mikic', 99);
INSERT INTO PASSENGER_SEATS VALUES(105, '', '', '', 101);

INSERT INTO SEAT VALUES(49, 1, 1, 0, null, 44);
INSERT INTO SEAT VALUES(50, 2, 1, 0, null, 44);
INSERT INTO SEAT VALUES(51, 3, 1, 0, 103, 44);
INSERT INTO SEAT VALUES(52, 4, 1, 0, null, 44);
INSERT INTO SEAT VALUES(53, 1, 2, 0, null, 44);

INSERT INTO SEAT VALUES(54, 1, 3, 0, null, 45);
INSERT INTO SEAT VALUES(55, 2, 3, 0, null, 45);
INSERT INTO SEAT VALUES(56, 3, 3, 0, null, 45);
INSERT INTO SEAT VALUES(57, 4, 3, 0, null, 45);
INSERT INTO SEAT VALUES(58, 1, 4, 0, null, 45);

INSERT INTO SEAT VALUES(59, 1, 5, 0, null, 43);
INSERT INTO SEAT VALUES(60, 2, 5, 0, null, 43);
INSERT INTO SEAT VALUES(61, 3, 5, 0, null, 43);
INSERT INTO SEAT VALUES(62, 4, 5, 0, null, 43);
INSERT INTO SEAT VALUES(63, 1, 6, 0, null, 43);

INSERT INTO SEAT VALUES(64, 1, 1, 0, null, 47);
INSERT INTO SEAT VALUES(65, 2, 1, 0, null, 47);
INSERT INTO SEAT VALUES(66, 3, 1, 0, 105, 47);
INSERT INTO SEAT VALUES(67, 4, 1, 0, null, 47);
INSERT INTO SEAT VALUES(68, 1, 2, 0, null, 47);

INSERT INTO SEAT VALUES(69, 1, 3, 0, null, 48);
INSERT INTO SEAT VALUES(70, 2, 3, 0, 104, 48);
INSERT INTO SEAT VALUES(71, 3, 3, 0, null, 48);
INSERT INTO SEAT VALUES(72, 4, 3, 0, null, 48);
INSERT INTO SEAT VALUES(73, 1, 4, 0, null, 48);

INSERT INTO SEAT VALUES(74, 1, 5, 0, null, 46);
INSERT INTO SEAT VALUES(75, 2, 5, 0, null, 46);
INSERT INTO SEAT VALUES(76, 3, 5, 0, null, 46);
INSERT INTO SEAT VALUES(77, 4, 5, 0, null, 46);
INSERT INTO SEAT VALUES(78, 1, 6, 0, null, 46);