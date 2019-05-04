const logoutURL = "../logout";
const loadUserInfoURL = "../api/getUserInfo";
const saveChangesURL = "../api/editUser";
const getPlaneSeatsURL = "/api/getPlaneSeats";
const searchUsersURL = "/api/searchUsers";
const friendInvitationURL = "/api/sendInvitation";
const acceptInvitationURL = "/api/acceptInvitation";
const declineInvitationURL = "/api/declineInvitation";
const getFriendsURL = "/api/getFriends";
const getDestinationsURL = "/api/getDestinations";
const searchFlightsURL = "/api/searchFlights";
const searchHotelsURL = "/api/searchHotels";

const tokenKey = "jwtToken";

var userMail = "";

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
					// getPlaneSeats();

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

					$('#flightsTable').DataTable({
						"paging" : false,
						"info" : false,
						"scrollCollapse" : true,
						"retrieve" : true,
					});

					$('#usersTable').DataTable({
						"paging" : false,
						"info" : false,
						"scrollCollapse" : true,
						"retrieve" : true,
					});

					$('#flightsTable thead tr').clone(true).appendTo(
							'#flightsTable thead');
					$('#flightsTable thead tr:eq(1) th')
							.each(
									function(i) {
										var title = $(this).text();
										$(this).html(
												'<input type="text" placeholder="Filter by '
														+ title + '" />');

										$('input', this)
												.on(
														'keyup change',
														function() {
															if (table.column(i)
																	.search() !== this.value) {
																table
																		.column(
																				i)
																		.search(
																				this.value)
																		.draw();
															}
														});
									});

					var table = $('#flightsTable').DataTable({
						"paging" : false,
						"info" : false,
						"scrollY" : "17vw",
						"scrollX" : true,
						"scrollCollapse" : true,
						"retrieve" : true,
						"orderCellsTop" : true
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
				});

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
							[ val.departureTime, val.landingTime, val.airline,
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
				url : getFriendsURL,
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

function getPlaneSeats() {
	$.ajax({
		dataType : "json",
		url : getPlaneSeatsURL,
		success : function(data) {
			firstPrice = data["firstClassPrice"];
			businessPrice = data["businessClassPrice"];
			economyPrice = data["economyClassPrice"];
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
		reservedSeats = [];
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

	setUpTablesHotelsTab();

}

function setUpTablesHotelsTab() {
	$('#hotelsTable thead tr').clone(true).appendTo('#hotelsTable thead');
	$('#hotelsTable thead tr:eq(1) th').each(
			function(i) {
				var title = $(this).text();
				$(this).html(
						'<input type="text" placeholder="Search ' + title
								+ '" />');

				$('input', this).on('keyup change', function() {
					if (hotelsTable.column(i).search() !== this.value) {
						hotelsTable.column(i).search(this.value).draw();
					}
				});
			});
	var hotelsTable = $('#hotelsTable').DataTable({
		"paging" : false,
		"info" : false,
		"orderCellsTop" : true,
		"fixedHeader" : true
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
