const tokenKey = "jwtToken";
const tileLayerURL = "https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw";
const MAP_ZOOM = 8;
const MAX_MAP_ZOOM = 19;
const MAP_ID = 'mapbox.streets';

const logoutURL = "../logout";
const loadUserInfoURL = "../api/getUserInfo";
const saveChangesURL = "../api/editUser";
const getPlaneSeatsURL = "/api/getPlaneSeats";
const searchUsersURL = "/api/searchUsers";
const friendInvitationURL = "/api/sendInvitation";
const acceptInvitationURL = "/api/acceptInvitation";
const declineInvitationURL = "/api/declineInvitation";
const getFriendInvitationsURL = "/api/getFriendInvitations";
const getDestinationsURL = "/api/getDestinations";
const searchFlightsURL = "/api/searchFlights";
const getFriendsURL = "/api/getFriends";
const getReservationsURL = "/api/getReservations";

const searchHotelsURL = "/api/searchHotels";
const getHotelURL = "../api/getHotel";
const getDetailedHotelURL = "../api/getDetailedHotel";
const getDetailedFlightURL = "/api/getDetailedFlight";
const searchRoomsURL = '/api/searchRooms/';
const reserveFlightURL = "/api/reserveFlight";

const getVehicleProducersURL = "/api/getVehicleProducers";
const getModelsForProducerURL = "/api/getModels/";
const getAllVehicleTypesURL = "/api/getAllVehicleTypes";
const getAllFuelTypesURL = "/api/getAllFuelTypes";
const searchVehiclesURL = "/api/searchVehicles";

var userMail = "";
var hotelMap = null;

var shownHotel = null;

$(document)
		.ready(
				function() {
					var socket = new SockJS('/friendsEndpoint');
					var stompClient = Stomp.over(socket);
					stompClient.connect({}, function(frame) {
						stompClient.subscribe("/friendsInvitation/" + userMail,
								function(data) {
									getFriends();
								});
					});

					loadData();
					setUpToastr();
					getDestinations();
					getReservations();
					
					$('#friendsTable').DataTable({
						"paging" : false,
						"info" : false,
						"scrollY" : "17vw",
						"scrollCollapse" : true,
						"retrieve" : true,
					});

					$('#reservationsTable').DataTable({
						"paging" : false,
						"info" : false,
						"scrollY" : "17vw",
						"scrollCollapse" : true,
						"retrieve" : true,
					});

					$('#usersTable').DataTable({
						"paging" : false,
						"info" : false,
						"scrollCollapse" : true,
						"retrieve" : true,
					});
					
					$('#inviteFriendsTable').DataTable({
						"paging" : false,
						"info" : false,
						"scrollCollapse" : true,
						"retrieve" : true,
					});

					setUpTableFilter("#flightsTable");

					var flightsTable = $('#flightsTable').DataTable({
						"paging" : false,
						"info" : false,
						"scrollY" : "17vw",
						"scrollX" : true,
						"scrollCollapse" : true,
						"retrieve" : true,
						"orderCellsTop" : true
					});
					
					$('#showFlightModal').on('hidden.bs.modal', function() {
						flightsTable.$('tr.selected').removeClass('selected');
						$("#reserveDivPassengers").hide();
						$("#reserveDivFriends").hide();
						$("#reserveDiv").show();
						seatsToReserve = [];
					});
					
					$('#flightsTable tbody').on('click', 'tr', function() {
						flightsTable.$('tr.selected').removeClass('selected');
						$(this).addClass('selected');
						shownFlight = flightsTable.row(this).data()[0];
						loadFlight(shownFlight);
						$("#showFlightModal").modal();
					});

					$(".nav li").click(function() {
						$(this).addClass("active");
						$(this).siblings().removeClass("active");
					});

					$("#logout").click(function() {
						document.location.href = logoutURL;
					});

					$('.nav-tabs a').click(function() {
						$(this).tab('show');
					});

					$('.edit')
							.click(
									function() {
										if ($(this).siblings().first().is(
												'[readonly]')) {
											$(this).siblings().first()
													.removeAttr('readonly');
										} else {
											$(this).siblings().first().prop(
													'readonly', 'true');
										}
									});

					$('#friendsTable tbody')
							.on(
									'click',
									'td',
									function(event) {
										var tgt = $(event.target);
										if (tgt[0].innerHTML == "Accept") {
											var table = $("#friendsTable")
													.DataTable();
											var rowData = table.cell(this)
													.row().data();
											acceptInvitation(rowData[0], table
													.cell(this).row().index());
										} else if (tgt[0].innerHTML == "Decline") {
											var table = $("#friendsTable")
													.DataTable();
											var rowData = table.cell(this)
													.row().data();
											declineInvitation(rowData[0], table
													.cell(this).row().index());
										}
									});

					$('#usersTable tbody')
							.on(
									'click',
									'td',
									function(event) {
										var tgt = $(event.target);
										if (tgt[0].id == "sendInvButton") {
											var table = $("#usersTable")
													.DataTable();
											var rowData = table.cell(this)
													.row().data();
											friendInvitation(rowData[0],
													tgt[0].parentElement.id);
										}
									});

					$("#searchUserForm")
							.submit(
									function(e) {
										e.preventDefault();
										let firstName = $("#userFirstName")
												.val();
										let lastName = $("#userLastName").val();
										$
												.ajax({
													type : 'GET',
													url : searchUsersURL,
													headers : createAuthorizationTokenHeader(tokenKey),
													contentType : 'application/json',
													data : {
														"firstName" : firstName,
														"lastName" : lastName,
														"email" : userMail
													},
													success : function(data) {
														var table = $(
																'#usersTable')
																.DataTable();
														table.clear().draw();
														$
																.each(
																		data,
																		function(
																				i,
																				val) {
																			var sendInv = "<div id='status"
																					+ i
																					+ "'><button id='sendInvButton'"
																					+ " class='btn btn-default'>Send invitation</button></div>";
																			table.row
																					.add(
																							[
																									val.email,
																									val.firstName,
																									val.lastName,
																									sendInv ])
																					.draw(
																							false);
																		});
													},
													error : function(
															XMLHttpRequest,
															textStatus,
															errorThrown) {
														alert("AJAX ERROR: "
																+ textStatus);
													}
												});
									});

					$('#userEditForm').on(
							'submit',
							function(e) {
								e.preventDefault();
								let firstName = $('input[name="fname"]').val();
								let lastName = $('input[name="lname"]').val();
								let phone = $('input[name="phone"]').val();
								let address = $('input[name="address"]').val();
								let email = $('#email').text();

								$.ajax({
									type : 'PUT',
									url : saveChangesURL,
									contentType : 'application/json',
									dataType : "html",
									data : formToJSON(firstName, lastName,
											phone, address, email),
									success : function(data) {
										if (data != "") {
											toastr["error"](data);
										}
									},
									error : function(XMLHttpRequest,
											textStatus, errorThrown) {
										alert("AJAX ERROR: " + textStatus);
									}
								});
							});

					$('a[href="#profile"]').click(function() {
						loadData();
					});

					$('a[data-toggle="tab"]')
							.on(
									'shown.bs.tab',
									function(e) {
										$($.fn.dataTable.tables(true))
												.DataTable().columns.adjust();
									});

					setUpHotelsTab();
					
					$("#reserveDivFriends").hide();
					$("#reserveDivPassengers").hide();

					$('#startYear').datepicker({
						format : 'yyyy',
						minViewMode: 'years',
						autoclose: true
					}).on('changeDate', function(selected) {
						startDate =  $("#startYear").val();
				        $('#endYear').datepicker('setStartDate', startDate);
					});
					
					$('#endYear').datepicker({
						format : 'yyyy',
						minViewMode: 'years',
						autoclose: true
					});
					
					$('#selectModel').multiselect({
						includeSelectAllOption : true,
						nonSelectedText: 'Select model'
					});
					
					$("#vehicleGrade").slider({});
					
					$('#vehicleType').multiselect({
						includeSelectAllOption : true,
						nonSelectedText: 'Select car body type'
					});
					
					$('#fuelType').multiselect({
						includeSelectAllOption : true,
						nonSelectedText: 'Select fuel type'
					});
					
					$('a[href="#cars"]').click(function() {
						getVehicleProducers();
						getAllVehicleTypes();
						getAllFuelTypes();
					});
					
					$('#selectProducer').change(function() {
						let value = $('#selectProducer').val();
						
						if(value == "all") {
							$('#selectModel').prop('disabled', 'disabled');
							$('#selectModel').multiselect('dataprovider', []);
						} else {
							getModelsForProducer(value);
							$('#selectModel').prop('disabled', 'false');
						}
					});
					
					$('#searchVehiclesButton').click(function(e) {
						e.preventDefault();
						
						let producer = $('#selectProducer').val();
						let models = $('#selectModel').val();
						let vehicleTypes = $('#vehicleType').val();
						let fuelTypes = $('#fuelType').val();
						let priceTo = emptyToNull($('#priceTo').val());
						let numberOfSeats = emptyToNull($('#numberOfSeats').val());
						let startDate = emptyToNull($('#startYear').val());
						let endDate = emptyToNull($('#endYear').val());
						let minGrade = $("#vehicleGrade").slider('getValue')[0];
						let maxGrade = $("#vehicleGrade").slider('getValue')[1];
						
						searchVehicles(producer, models, vehicleTypes, fuelTypes, priceTo, numberOfSeats, startDate, endDate, minGrade, maxGrade);
					});
					
					$('#vehiclesTable').DataTable({
						"paging" : false,
						"info" : false,
						"orderCellsTop" : true,
						"fixedHeader" : true
					});
				});

function emptyToNull(value) {
	if(value == "") {
		return null;
	} else {
		return value;
	}
}

function vehicleSearchFormToJSON(producer, models, vehicleTypes, fuelTypes, priceMax, numberOfSeats, startDate, endDate, minGrade, maxGrade) {
	return JSON.stringify({
		"producer" : producer,
		"models" : models,
		"vehicleTypes" : vehicleTypes,
		"fuelTypes" : fuelTypes,
		"price" : priceMax,
		"seats": numberOfSeats,
		"startDate": startDate,
		"endDate": endDate,
		"minGrade": minGrade,
		"maxGrade": maxGrade
	});
}

function searchVehicles(producer, models, vehicleTypes, fuelTypes, priceMax, numberOfSeats, startDate, endDate, minGrade, maxGrade) {
	$.ajax({
		type : 'POST',
		url : searchVehiclesURL,
		//headers : createAuthorizationTokenHeader(tokenKey),
		contentType : "application/json",
		data : vehicleSearchFormToJSON(producer, models, vehicleTypes, fuelTypes, priceMax, numberOfSeats, startDate, endDate, minGrade, maxGrade),
		success : function(data) {
			if (data != null) {
				let table = $('#vehiclesTable').DataTable();
				table.clear().draw();
				
				for(let vehicle of data) {
					table.row.add([
						vehicle.model,
						vehicle.producer,
						vehicle.yearOfProduction,
						vehicle.numberOfSeats,
						vehicle.fuelType,
						vehicle.vehicleType,
						vehicle.pricePerDay,
						vehicle.averageGrade
					]).draw(false);
				}
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function getAllVehicleTypes() {
	$.ajax({
		type : 'GET',
		url : getAllVehicleTypesURL,
		dataType : "json",
		//headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			let options = [];
			for(let type of data) {
				options.push({label: type, value: type});
			}
			$('#vehicleType').multiselect('dataprovider', options);
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function getAllFuelTypes() {
	$.ajax({
		type : 'GET',
		url : getAllFuelTypesURL,
		dataType : "json",
		//headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			let options = [];
			for(let type of data) {
				options.push({label: type, value: type});
			}
			$('#fuelType').multiselect('dataprovider', options);
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function getVehicleProducers() {
	$.ajax({
		type : 'GET',
		url : getVehicleProducersURL,
		dataType : "json",
		//headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			let producers = $("#selectProducer");
			producers.empty();
			producers.append('<option value="all" selected>All producers</option>');
			for(let producer of data) {
				producers.append('<option value="' + producer + '">' + producer + '</option');
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function getModelsForProducer(producer) {
	$.ajax({
		type : 'GET',
		url : getModelsForProducerURL + producer,
		dataType : "json",
		//headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			let options = [];
			for(let model of data) {
				options.push({label: model, value: model});
			}
			$('#selectModel').multiselect('dataprovider', options);
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function setUpToastr() {
	toastr.options = {
		"closeButton" : true,
		"debug" : false,
		"newestOnTop" : false,
		"progressBar" : false,
		"positionClass" : "toast-top-center",
		"preventDuplicates" : false,
		"onclick" : null,
		"showDuration" : "300",
		"hideDuration" : "1000",
		"timeOut" : "3000",
		"extendedTimeOut" : "1000",
		"showEasing" : "swing",
		"hideEasing" : "linear",
		"showMethod" : "fadeIn",
		"hideMethod" : "fadeOut"
	}
}

function getDestinations() {
	$.ajax({
		type : 'GET',
		url : getDestinationsURL,
		headers : createAuthorizationTokenHeader(tokenKey),
		success : function(data) {
			if (data != null) {
				var start = $("#startDestination");
				var end = $("#endDestination");
				$.each(data, function(i, val) {
					start.append("<option value=" + val + ">" + val
							+ "</option>");
					end
							.append("<option value=" + val + ">" + val
									+ "</option>");
				});
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function searchFlights(e) {
	e.preventDefault();
	var startDestination = $("#startDestination option:selected").text();
	var endDestination = $("#endDestination  option:selected").text();
	if (startDestination == endDestination) {
		toastr["error"]
				("Start destination and end destination must not be the same.");
		return;
	}
	var departureTime = $("#departureTime").val();
	if (departureTime == null || departureTime == "") {
		toastr["error"]("Departure time is not valid.")
		return;
	}
	var landingTime = $("#landingTime").val();
	if (landingTime == null || landingTime == "") {
		toastr["error"]("Landing time is not valid.")
		return;
	}
	if (moment(landingTime).isBefore(departureTime)) {
		toastr["error"]
				("Landing time must be after or same as the departure time.");
		return;
	}
	$.ajax({
		type : 'POST',
		url : searchFlightsURL,
		headers : createAuthorizationTokenHeader(tokenKey),
		contentType : "application/json",
		data : JSON.stringify({
			"startDestination" : startDestination,
			"endDestination" : endDestination,
			"departureTime" : departureTime,
			"landingTime" : landingTime
		}),
		success : function(data) {
			if (data != null) {
				var table = $('#flightsTable').DataTable();
				table.clear().draw();
				$.each(data, function(i, val) {
					var date1 = moment(val.departureTime, 'DD.MM.YYYY hh:mm');
					var date2 = moment(val.landingTime, 'DD.MM.YYYY hh:mm');
					var diff = date2.diff(date1, 'minutes');
					table.row.add(
							[ val.flightCode, val.departureTime, val.landingTime, val.airline,
									val.numberOfConnections, diff + " min",
									val.firstClassPrice,
									val.businessClassPrice,
									val.economyClassPrice ]).draw(false);
				});
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function getFriends() {
	$
			.ajax({
				type : 'GET',
				url : getFriendInvitationsURL,
				headers : createAuthorizationTokenHeader(tokenKey),
				success : function(data) {
					if (data != null) {
						var table = $('#friendsTable').DataTable();
						table.clear().draw();
						$
								.each(
										data,
										function(i, val) {
											if (val.status == "Invitation pending") {
												var sendInv = "<div id='invFriendStatus'><button id='sendInvButton' class='btn btn-default'>Accept</button>";
												sendInv += "<button id='sendInvButton' class='btn btn-default'>Decline</button></div>";
												table.row.add(
														[ val.email,
																val.firstName,
																val.lastName,
																sendInv ])
														.draw(false);
											} else {
												table.row
														.add(
																[
																		val.email,
																		val.firstName,
																		val.lastName,
																		"<b>"
																				+ val.status
																				+ "</b>" ])
														.draw(false);
											}
										});
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("AJAX ERROR: " + textStatus);
				}
			});
}

function friendInvitation(invitedUser, idx) {
	idx = idx.substring(6);
	$.ajax({
		type : 'POST',
		url : friendInvitationURL,
		headers : createAuthorizationTokenHeader(tokenKey),
		data : invitedUser,
		success : function(data) {
			if (data.toastType == "success") {
				toastr[data.toastType](data.message);
				$("#status" + idx).html("<b>Invitation sent</b>");
				getFriends();
			} else {
				toastr[data.toastType](data.message);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function acceptInvitation(acceptedUser, idx) {
	$.ajax({
		type : 'POST',
		url : acceptInvitationURL,
		headers : createAuthorizationTokenHeader(tokenKey),
		data : acceptedUser,
		success : function(data) {
			if (data.toastType == "success") {
				toastr[data.toastType](data.message);
				getFriends();
			} else {
				toastr[data.toastType](data.message);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function declineInvitation(declinedUser, idx) {
	$.ajax({
		type : 'POST',
		url : declineInvitationURL,
		headers : createAuthorizationTokenHeader(tokenKey),
		contentType : 'application/json',
		data : declinedUser,
		success : function(data) {
			if (data.toastType == "success") {
				toastr[data.toastType](data.message);
				getFriends();
			} else {
				toastr[data.toastType](data.message);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

var firstPrice = 0;
var businessPrice = 0;
var economyPrice = 0;

function resetReservationModal() {
	firstClass = [];
	businessClass = [];
	economyClass = [];
	firstSeatLabel = 1;
	reservedSeats = [];
	$("#total").html(0);
	$("#counter").html(0);
	$("#selected-seats").empty();
	$("#userPassNumber").val("");
	$("#numOfBags").val(0);
	$("#passengersTable tr").remove();
}

function getPlaneSeats(code) {
	$.ajax({
		url : getPlaneSeatsURL,
		contentType : "application/json",
		data : {
			'flightCode' : code
		},
		dataType : "json",
		headers : createAuthorizationTokenHeader(tokenKey),
		success : function(data) {
			firstPrice = data["firstClassPrice"];
			businessPrice = data["businessClassPrice"];
			economyPrice = data["economyClassPrice"];
			resetReservationModal();
			renderPlaneSeats(data["planeSegments"], data["reservedSeats"]);
		}
	});
}

function loadData() {
	let token = getJwtToken("jwtToken");
	$
			.ajax({
				type : 'GET',
				url : loadUserInfoURL,
				dataType : "json",
				headers : createAuthorizationTokenHeader(tokenKey),
				success : function(data) {
					if (data != null) {
						$('input[name="fname"]').val(data.firstName);
						$('input[name="lname"]').val(data.lastName);
						$('input[name="phone"]').val(data.phone);
						$('input[name="address"]').val(data.address);
						$('#email').text(data.email);
						userMail = data.email;
						var table = $('#friendsTable').DataTable();
						table.clear().draw();
						$
								.each(
										data.friends,
										function(i, val) {
											if (val.status == "Invitation pending") {
												var sendInv = "<div id='invFriendStatus'><button id='sendInvButton' class='btn btn-default'>Accept</button>";
												sendInv += "<button id='sendInvButton' class='btn btn-default'>Decline</button></div>";
												table.row.add(
														[ val.email,
																val.firstName,
																val.lastName,
																sendInv ])
														.draw(false);
											} else {
												table.row
														.add(
																[
																		val.email,
																		val.firstName,
																		val.lastName,
																		"<b>"
																				+ val.status
																				+ "</b>" ])
														.draw(false);
											}
										});
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("AJAX ERROR: " + textStatus);
				}
			});
}

function formToJSON(firstName, lastName, phone, address, email) {
	return JSON.stringify({
		"firstName" : firstName,
		"lastName" : lastName,
		"phoneNumber" : phone,
		"address" : address,
		"email" : email
	});
}

/* SEAT CHART */
var firstSeatLabel = 1;
var firstClass = [];
var businessClass = [];
var economyClass = [];
var reservedSeats = [];
var seatsToReserve = [];

function showPlaneSeats(seats) {
	var $cart = $('#selected-seats'), $counter = $('#counter'), $total = $('#total'), sc = $(
			'#seat-map')
			.seatCharts(
					{
						map : seats,
						seats : {
							f : {
								price : firstPrice,
								classes : 'first-class', // your custom CSS
								// class
								category : 'First Class'
							},
							e : {
								price : economyPrice,
								classes : 'economy-class', // your custom CSS
								// class
								category : 'Economy Class'
							},
							b : {
								price : businessPrice,
								classes : 'business-class',
								category : 'Business Class'
							},
							l : {
								classes : 'blank-class',
								category : 'Blank seat'
							},
							a : {
								classes : 'unavailable',
								category : 'Already booked'
							}

						},
						naming : {
							top : false,
							left : false,
							getLabel : function(character, row, column) {
								return firstSeatLabel++;
							},
						},
						legend : {
							node : $('#legend'),
							items : [ [ 'f', 'available', 'First Class' ],
									[ 'b', 'available', 'Business Class' ],
									[ 'e', 'available', 'Economy Class' ],
									[ 'a', 'unavailable', 'Already Booked' ],
									[ 'l', 'available', 'Blank seat' ] ]
						},
						click : function() {
							if (this.status() == 'available') {
								// let's create a new <li> which we'll add to
								// the cart items
								if (this.settings.character == 'a')
									return;
								var seat = this.settings.id.split("_");
								if (seat[1] == 4 || seat[1] == 5) {
									seatsToReserve.push((seat[0] + "_" +  (seat[1] - 1)) + "_" + this.settings.character);
								}
								else {
									seatsToReserve.push(this.settings.id + "_" + this.settings.character);
								}
								console.log(seatsToReserve);
								$(
										'<li>'
												+ this.data().category
												+ ' Seat # '
												+ this.settings.label
												+ ': <b>$'
												+ this.data().price
												+ '</b> <a href="#" class="cancel-cart-item">[cancel]</a></li>')
										.attr('id',
												'cart-item-' + this.settings.id)
										.data('seatId', this.settings.id)
										.appendTo($cart);

								/*
								 * Lets update the counter and total
								 * 
								 * .find function will not find the current
								 * seat, because it will change its stauts only
								 * after return 'selected'. This is why we have
								 * to add 1 to the length and the current seat
								 * price to the total.
								 */
								$counter.text(sc.find('selected').length + 1);
								$total.text(recalculateTotal(sc)
										+ this.data().price);

								return 'selected';
							} else if (this.status() == 'selected') {
								// update the counter
								$counter.text(sc.find('selected').length - 1);
								// and total
								$total.text(recalculateTotal(sc)
										- this.data().price);

								// remove the item from our cart
								$('#cart-item-' + this.settings.id).remove();
								let index = seatsToReserve.indexOf(this.settings.id);
								seatsToReserve.splice(index, 1);
								console.log(seatsToReserve);
								// seat has been vacated
								return 'available';
							} else if (this.status() == 'unavailable') {
								// seat has been already booked
								return 'unavailable';
							} else {
								return this.style();
							}
						}
					});

	// this will handle "[cancel]" link clicks
	$('#selected-seats').on('click', '.cancel-cart-item', function() {
		// let's just trigger Click event on the appropriate seat, so we don't
		// have to repeat the logic here
		sc.get($(this).parents('li:first').data('seatId')).click();
	});
}

function recalculateTotal(sc) {
	var total = 0;

	// basically find every selected seat and sum its price
	sc.find('selected').each(function() {
		total += this.data().price;
	});

	return total;
}

function renderPlaneSeats(planeSegments, reserved) {
	if (planeSegments[0].length == 0 && planeSegments[1].length == 0
			&& planeSegments[2].length == 0) {
		showPlaneSeats([]);
	} else {
		reservedSeats = reserved;
		var maxRowFirst = 0;
		var segment;
		for (var i = 0; i < planeSegments.length; i++) {
			if (planeSegments[i].segmentClass == "FIRST") {
				segment = planeSegments[i];
				break;
			}
		}
		$.each(segment.seats, function(i, val) {
			if (val["row"] > maxRowFirst)
				maxRowFirst = val["row"];
		});
		initializeSeats(maxRowFirst, firstClass, segment.seats, 'f', 1);
		var maxRowBusiness = -1;
		for (var i = 0; i < planeSegments.length; i++) {
			if (planeSegments[i].segmentClass == "BUSINESS") {
				segment = planeSegments[i];
				break;
			}
		}
		$.each(segment.seats, function(i, val) {
			if (val["row"] > maxRowBusiness)
				maxRowBusiness = val["row"];
		});
		initializeSeats(maxRowBusiness - maxRowFirst, businessClass,
				segment.seats, 'b', 2);
		var maxRowEconomy = -1;
		for (var i = 0; i < planeSegments.length; i++) {
			if (planeSegments[i].segmentClass == "ECONOMY") {
				segment = planeSegments[i];
				break;
			}
		}
		$.each(segment.seats, function(i, val) {
			if (val["row"] > maxRowEconomy)
				maxRowEconomy = val["row"];
		});
		maxRowEconomy -= maxRowBusiness;
		initializeSeats(maxRowEconomy, economyClass, segment.seats, 'e', 3);
		var seats = firstClass.concat(businessClass).concat(economyClass);
		$('.seatCharts-row').remove();
		$('.seatCharts-legendItem').remove();
		$('#seat-map,#seat-map *').unbind().removeData();
		firstSeatLabel = 1;
		showPlaneSeats(seats);
	}
}

function initializeSeats(number, seatClass, planeSegment, label, cat) {
	var fixer = 0;
	if (cat == 2)
		fixer = firstClass.length;
	else if (cat == 3)
		fixer = firstClass.length + businessClass.length;
	for (var i = 0; i < number; i++)
		seatClass.push("_____");
	$.each(planeSegment, function(i, val) {
		var row = seatClass[val["row"] - 1 - fixer].split("");
		if (reservedSeats.includes(val["row"] + "_" + val["column"]))
			row[val["column"] - 1 + Math.floor(val["column"] / 3)] = 'a';
		else
			row[val["column"] - 1 + Math.floor(val["column"] / 3)] = label;
		seatClass[val["row"] - 1 - fixer] = row.join("");
	});
}

/* HOTELS TAB */

function setUpHotelsTab() {
	$("#searchHotelGrade").slider({});
	$('#searchRoomsDateRange').daterangepicker({
		minDate:new Date(),
		locale: {
		      format: 'DD/MM/YYYY'
		    },
	});
	$("#searchRoomsPrice").slider({});
	$("#searchRoomsGrade").slider({});
	
	setUpTablesHotelsTab();
	setUpShowHotelModal();

}

function setUpShowHotelModal() {
	$('#showHotelModal').on('shown.bs.modal', function() {
		setTimeout(function() {
			hotelMap.invalidateSize()
		}, 100);
		setTimeout(function() {
			hotelMap.invalidateSize()
		}, 1000);
	});

	$('#showHotelModal').on('hidden.bs.modal', function() {
		hotelsTable.$('tr.selected').removeClass('selected');
		hotelMap.off();
		hotelMap.remove();
		hotelMap = null;
	});	
}

function setUpTablesHotelsTab() {

	hotelsTable = $('#hotelsTable').DataTable({
		"paging" : false,
		"info" : false,
		"orderCellsTop" : true,
		"fixedHeader" : true
	});
	setUpTableFilter("#hotelsTable");

	roomsTable = $('#roomsTable').DataTable({
		"paging" : false,
		"info" : false,
		"orderCellsTop" : true,
		"fixedHeader" : true
	});
	setUpTableFilter("#roomsTable");

	additionalServicesTable = $('#additionalServicesTable').DataTable({
		"paging" : false,
		"info" : false,
		"orderCellsTop" : true,
		"fixedHeader" : true
	});
	setUpTableFilter("#additionalServicesTable");

	$('#hotelsTable tbody').on('click', 'tr', function() {
		hotelsTable.$('tr.selected').removeClass('selected');
		$(this).addClass('selected');
		shownHotel = hotelsTable.row(this).data()[0];
		loadHotel(shownHotel);
		$("#showHotelModal").modal();
	});
}

function searchHotels(e) {
	e.preventDefault();
	$.ajax({
		type : "GET",
		url : searchHotelsURL,
		contentType : "application/json",
		data : {
			'name' : $("#searchHotelName").val(),
			'fromGrade' : $("#searchHotelGrade").slider('getValue')[0],
			'toGrade' : $("#searchHotelGrade").slider('getValue')[1]
		},
		dataType : "json",
		headers : createAuthorizationTokenHeader(tokenKey),
		success : function(data) {
			renderHotels(data);
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function renderHotels(data) {
	if (data != null) {
		var table = $("#hotelsTable").DataTable();
		table.clear().draw();
		$.each(data, function(i, val) {
			table.row.add([ val.name, val.averageGrade ]).draw(false);
		});
	}
}

function loadHotel(name) {
	$.ajax({
		type : 'GET',
		url : getDetailedHotelURL,
		contentType : "application/json",
		data : {
			'name' : name
		},
		dataType : "json",
		headers : createAuthorizationTokenHeader(tokenKey),
		success : function(data) {
			if (data != null) {
				$("#hotelName").val(data["name"]);
				$("#hotelGrade").text(data["averageGrade"]);
				$("#hotelDescription").text(data["description"]);
				if (hotelMap != null) {
					hotelMap.off();
					hotelMap.remove();
					hotelMap = null;
				}
				hotelMap = setUpMap(data["latitude"], data["longitude"],
						'hotelMapDiv', false);
				renderRooms(data["rooms"]);
				renderAdditionalServices(data["additionalServices"]);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function loadFlight(code) {
	$.ajax({
		type : 'GET',
		url : getDetailedFlightURL,
		contentType : "application/json",
		data : {
			'flightCode' : code
		},
		dataType : "json",
		headers : createAuthorizationTokenHeader(tokenKey),
		success : function(data) {
			if (data != null) {
				localStorage.setItem("flightCode", code);
				localStorage.setItem("startDest", data["startDestination"]);
				localStorage.setItem("endDest", data["endDestination"]);
				localStorage.setItem("flightDate", data["departureTime"]);
				$("#startDest").text(data["startDestination"]);
				$("#endDest").text(data["endDestination"]);
				$("#depTime").text(data["departureTime"]);
				$("#landTime").text(data["landingTime"]);
				$("#flightAirline").text(data["airlineName"]);
				var date1 = moment(data["departureTime"], 'DD.MM.YYYY hh:mm');
				var date2 = moment(data["landingTime"], 'DD.MM.YYYY hh:mm');
				var diff = date2.diff(date1, 'minutes');
				$("#flightDuration").text(diff);
				$("#flightDistance").text(data["flightDistance"]);
				var conn = $("#flightConnections");
				if (data["connections"].length == 0) {
					conn.append("<option value=''></option>");
				}
				else {
					$.each(data["connections"], function(i, val) {
						conn.append("<option value=" + val + ">" + val
								+ "</option>");
					});
				}
				$("#flightConnections").text(data["description"]);
				$("#pricePerBag").text(data["pricePerBag"]);
				$("#averageGrade").text(data["averageGrade"]);
				getPlaneSeats(code);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function renderRooms(data) {
	roomsTable.clear().draw();
	$.each(data, function(i, val) {
		rowNode = roomsTable.row.add(
				[ val.roomNumber, val.price, val.numberOfPeople,
						val.averageGrade ]).draw(false).node();
	});
}
function renderAdditionalServices(data) {
	additionalServicesTable.clear().draw();
	$.each(data, function(i, val) {
		additionalServicesTable.row.add([ val.name, val.price ]).draw(false);
	});
}

function searchRooms(e){
	e.preventDefault();
	var drp = $('#searchRoomsDateRange').data('daterangepicker');
	$.ajax({
		type : "GET",
		url : searchRoomsURL + shownHotel,
		contentType : "application/json",
		data : {
			'fromDate' : drp.startDate.toDate(),
			'toDate' : drp.endDate.toDate(),
			'forPeople' : $('#searchRoomsForPeople').val(),
			'fromPrice' : $("#searchRoomsPrice").slider('getValue')[0],
			'toPrice' : $("#searchRoomsPrice").slider('getValue')[1],
			'fromGrade' : $("#searchRoomsGrade").slider('getValue')[0],
			'toGrade' : $("#searchRoomsGrade").slider('getValue')[1]
		},
		dataType : "json",
		headers : createAuthorizationTokenHeader(tokenKey),
		success : function(data) {
			renderRooms(data);
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

/* UTILITY */

function setUpMap(latitude, longitude, div, draggable) {
	var retval = L.map(div).setView([ latitude, longitude ], MAP_ZOOM);
	L.tileLayer(tileLayerURL, {
		maxZoom : MAX_MAP_ZOOM,
		id : MAP_ID
	}).addTo(retval);
	var marker = L.marker([ latitude, longitude ], {
		draggable : draggable
	}).addTo(retval);
	if (draggable) {
		marker.on('dragend', function(e) {
			$("#latitude").val(marker.getLatLng().lat);
			$("#longitude").val(marker.getLatLng().lng);
		});
	}
	return retval
}

function setUpTableFilter(tableID){
	$(tableID + ' thead tr').clone(true).appendTo(tableID + ' thead');
	$(tableID + ' thead tr:eq(1) th').each(
			function(i) {
				var title = $(this).text();
				$(this).html(
						'<input type="text" placeholder="Filter by ' + title
								+ '" />');

				$('input', this).on('keyup change', function() {
					table = $(tableID).DataTable();
					if (table.column(i).search() !== this.value) {
						table.column(i).search(this.value).draw();
					}
				});
			});
}

function showFriendsStep(e) {
	e.preventDefault();
	var userPass = $("#userPassNumber").val();
	if (userPass == "" || userPass == undefined) {
		toastr["error"]("Invalid passport number.");
		return;
	}
	var numOfBags = $("#numOfBags").val();
	if (isNaN(numOfBags) || numOfBags < 0) {
		toastr["error"]("Invalid number of bags.");
		return;
	}
	var numberOfPassengers = seatsToReserve.length;
	if (numberOfPassengers == 0) {
		toastr["error"]("You did not choose any seat.");
		return;
	}
	var flightReservation = { "flightCode" : localStorage.getItem("flightCode"), 
							  "numberOfPassengers" : numberOfPassengers,
							  "seatsLeft" : numberOfPassengers - 1,
							  "invitedFriends" : [],
							  "passengers" : [{ "firstName" : "", "lastName": "", "passport" : userPass, "numberOfBags" : numOfBags }],
							  "seats": seatsToReserve };
	localStorage.setItem("flightReservation", JSON.stringify(flightReservation));
	if (flightReservation["seatsLeft"] == 0) {
		toastr["success"]("Successfully added flight reservation to cart.");
		$('#showFlightModal').modal('toggle');
		localStorage.setItem("flightReservation", JSON.stringify(flightReservation));
		var startDest = localStorage.getItem("startDest");
		var endDest = localStorage.getItem("endDest");
		var flightDate = localStorage.getItem("flightDate");
		$("#flightRes").html(startDest + "-" + endDest + " " + flightDate);
		localStorage.setItem("flightRes", "true");
	}
	$.ajax({
		type : 'GET',
		url : getFriendsURL,
		headers : createAuthorizationTokenHeader(tokenKey),
		success : function(data) {
			if (data != null) {
				var table = $('#inviteFriendsTable').DataTable();
				table.clear().draw();
				$.each(data, function(i, val) {
					table.row.add([val.email, val.firstName, val.lastName, 
						"<input type='checkbox' value='" + val.email + "'>"]).draw(false);
				});
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
	$("#reserveDiv").hide();
	$("#reserveDivFriends").show();
}

function showLastStep(e) {
	e.preventDefault();
	var numOfInvited = $('#reserveDivFriends').find('input[type=checkbox]:checked').length;
	var flightReservation = JSON.parse(localStorage.getItem("flightReservation"));
	if (numOfInvited > flightReservation["seatsLeft"]) {
		toastr["error"]("You want to invite more friends than number of seats reserved.");
		return;
	}
	var invitedFriends = $("#reserveDivFriends").find('input[type=checkbox]:checked');
	$.each(invitedFriends, function(i, val) {
		flightReservation["invitedFriends"].push(val.val());
		flightReservation["seatsLeft"]--;
	});
	if (flightReservation["seatsLeft"] == 0) {
		toastr["success"]("Successfully added flight reservation to cart.");
		$('#showFlightModal').modal('toggle');
		localStorage.setItem("flightReservation", JSON.stringify(flightReservation));
		var startDest = localStorage.getItem("startDest");
		var endDest = localStorage.getItem("endDest");
		var flightDate = localStorage.getItem("flightDate");
		$("#flightRes").html(startDest + "-" + endDest + " " + flightDate);
		localStorage.setItem("flightRes", "true");
	}
	else {
		var lastStepTable = $("#passengersTable");
		for (var i = 0; i < flightReservation["seatsLeft"]; i++) {
			var passengerNumber = flightReservation["numberOfPassengers"] - flightReservation["seatsLeft"] + i + 1;
			var tableRow = "<tr><td>Passenger</td><td>" + passengerNumber + "</td></tr>";
			tableRow += "<tr><td>First name</td><td><input type='text' name='passFirstName'/></td><tr>";
			tableRow += "<tr><td>Last name</td><td><input type='text' name='passLastName'/></td><tr>";
			tableRow += "<tr><td>Passport number</td><td><input type='text' name='passportNumber'/></td><tr>";
			tableRow += "<tr><td>Number of bags</td><td><input type='number' name='bags'/></td><tr>";
			tableRow += "<tr><td></td><td></td></tr>";
			lastStepTable.append(tableRow);
		}
		$("#reserveDivFriends").hide();
		$("#reserveDivPassengers").show();
		localStorage.setItem("flightReservation", JSON.stringify(flightReservation));
	}	
}

function endReservation(e) {
	e.preventDefault();
	var flightReservation = JSON.parse(localStorage.getItem("flightReservation"));
	var firstNames = $("input[name='passFirstName']").map(function(){return $(this).val();}).get();
	var lastNames = $("input[name='passLastName']").map(function(){return $(this).val();}).get();
	var passports = $("input[name='passportNumber']").map(function(){return $(this).val();}).get();
	var bags = $("input[name='bags']").map(function(){return $(this).val();}).get();
	for (var i = 0; i < firstNames.length; i++) {
		if ((!firstNames[i].trim()) || (!lastNames[i].trim()) || (!passports[i].trim())) {
			toastr["error"]("Invalid data for passengers.");
			return;
		}
		if (isNaN(bags[i]) || bags[i] <= 0) {
			toastr["error"]("Invalid number of bags.");
			return;
		}
	}
	for (var i = 0; i < firstNames.length; i++) {
		flightReservation["passengers"].push({"firstName" : firstNames[i], "lastName" : lastNames[i], "passport" : passports[i], "numberOfBags" : bags[i] });
		flightReservation["seatsLeft"]--;
	}
	toastr["success"]("Successfully added flight reservation to cart.");
	$('#showFlightModal').modal('toggle');
	localStorage.setItem("flightReservation", JSON.stringify(flightReservation));
	var startDest = localStorage.getItem("startDest");
	var endDest = localStorage.getItem("endDest");
	var flightDate = localStorage.getItem("flightDate");
	$("#flightRes").html(startDest + "-" + endDest + " " + flightDate);
	localStorage.setItem("flightRes", "true");
}

function confirmReservation(e) {
	e.preventDefault();
	var flightRes = localStorage.getItem("flightRes");
	var hotelRes = localStorage.getItem("hotelRes");
	var carRes = localStorage.getItem("carRes");
	if (flightRes === null) {
		toastr["error"]("You have to reserve flight first.");
		return;
	}
	if (hotelRes === null && carRes === null) {
		var flightReservation = JSON.parse(localStorage.getItem("flightReservation"));
		$.ajax({
			type : 'POST',
			url : reserveFlightURL,
			headers : createAuthorizationTokenHeader(tokenKey),
			data : JSON.stringify({"flightCode" : flightReservation["flightCode"], 
									"invitedFriends" : flightReservation["invitedFriends"], 
									"numberOfPassengers" : flightReservation["numberOfPassengers"],
									"passengers" : flightReservation["passengers"],
									"seats" : flightReservation["seats"]}),
			success : function(data) {
				if (data != null) {
					toastr[data.toastType](data.message);
					localStorage.removeItem("flightReservation");
					localStorage.removeItem("flightRes");
					$("#flightRes").html("No flight reserved");
					getReservations();
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert("AJAX ERROR: " + textStatus);
			}
		});
	}
	else if (hotelRes != null && carRes === null) {
		// FLIGHT + HOTEL
	}
	else if (hotelRes === null && carRes != null) {
		// FLIGHT + CAR
	}
	else {
		// FLIGHT + HOTEL + CAR
	}
}

function getReservations() {
	$.ajax({
		type : 'GET',
		url : getReservationsURL,
		headers : createAuthorizationTokenHeader(tokenKey),
		success : function(data) {
			if (data != null) {
				var table = $("#reservationsTable").DataTable();
				$.each(data, function(i, val) {
					table.row.add([ val.reservationInf, val.dateOfReservation, val.price, val.grade ]).draw(false);
				});
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}