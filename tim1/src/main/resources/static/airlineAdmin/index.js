const TOKEN_KEY = 'jwtToken';

const tileLayerURL = "https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw";
const MAP_ZOOM = 12;
const MAX_MAP_ZOOM = 19;
const MAP_ID = 'mapbox.streets';

const editAirlineURL = "/api/editAirline";
const getDestinationsURL = "/api/getDestinations";
const getFlightsURL = "/api/getFlights";
const getAirlineOfAdminURL = "/api/getAirlineOfAdmin";
const saveSeatsChangesURL = "/api/saveSeats";
const addFlightURL = "/api/addFlight";
const addDestinationURL = "/api/addDestination";

const logoutURL = "../logout";
const loadUserInfoURL = "../api/getUserInfo";
const editUserInfoURL = "../api/editUser";
const changePasswordURL = "../changePassword";
var airlineName = "";

$(document).ready(function() {
	setUpToastr();
	loadAirline();
	loadProfileData();
	setUpTables();
	
	userEditFormSetUp();
	$('a[data-toggle="tab"]').on('shown.bs.tab', function(e){
		$($.fn.dataTable.tables(true)).DataTable().columns.adjust();
	});
	
	$("#logout").click(function(){
		document.location.href = logoutURL;
	});
	
	$('.edit').click(function() {
		if ($(this).siblings().first().is('[readonly]')) {
			$(this).siblings().first().removeAttr('readonly');
		} else {
			$(this).siblings().first().prop('readonly', 'true');
		}
	});
})

function setUpTables() {
	$('#destinationsTable').DataTable({
		"paging" : false,
		"info" : false,
		"scrollY" : "17vw",
		"scrollCollapse" : true,
		"retrieve" : true,
	});
	$('#flightsTable').DataTable({
		"scrollX": true,
		"paging" : false,
		"info" : false,
		"scrollY" : "17vw",
		"scrollCollapse" : true,
		"retrieve" : true,
	});
	$('#quickReservationsTable').DataTable({
		"paging" : false,
		"info" : false,
		"scrollY" : "17vw",
		"scrollCollapse" : true,
		"retrieve" : true,
	});
}

function setUpMap(latitude, longitude, div) {
	var destMap = L.map(div).setView([ latitude, longitude ], MAP_ZOOM);
	L.tileLayer(tileLayerURL, {
		maxZoom : MAX_MAP_ZOOM,
		id : MAP_ID
	}).addTo(destMap);
	var marker = L.marker([ latitude, longitude ], {
		draggable : true
	}).addTo(destMap);
	marker.on('dragend', function(e) {
		$("#" + div + "Latitude").val(marker.getLatLng().lat);
		$("#" + div + "Longitude").val(marker.getLatLng().lng);
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

function editAirline(e) {
	e.preventDefault();
	$.ajax({
		type : 'POST',
		url : editAirlineURL,
		contentType : "application/json",
		data : JSON.stringify({
		// TODO
		}),
		dataType : "json",
		success : function(data) {
			alert(data);
		}
	});
}

function loadAirline() {
	$.ajax({
		dataType : "json",
		url : getAirlineOfAdminURL,
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		success : function(data) {
			$("#airlineName").val(data["name"]);
			airlineName = data["name"];
			$("#airlineGrade").text(data["averageGrade"]);
			$("#airlineDescription").text(data["description"]);
			setUpMap(data["latitude"], data["longitude"], 'basicMapDiv');
			renderDestinations(data["destinations"]);
			renderFlights(data["flights"]);
			// renderQuickReservations[data["quickReservations"]];
			reservedSeats = data["reservedSeats"];
			renderPlaneSeats(data["planeSegments"], data["reservedSeats"]);
			console.log(data["planeSegments"]);
		}
	});
}

function renderDestinations(data) {
	var table = $('#destinationsTable').DataTable();
	$.each(data, function(i, val) {
		table.row.add([ val.name ]).draw(false);
	});
	var start = $("#startDestination");
	var end = $("#endDestination");
	var con = $("#connections");
	$.each(data, function(i, val) {
		start.append("<option value=" + val.name + ">" + val.name + "</option>");
		end.append("<option value=" + val.name + ">" + val.name + "</option>");
		con.append("<option value=" + val.name + ">" + val.name + "</option>");
	});
}

function renderFlights(data) {
	var table = $('#flightsTable').DataTable();
	$.each(data, function(i, val) {
		var date1 = moment(val.departureTime);
		var date2 = moment(val.landingTime);
		var diff = date2.diff(date1, 'minutes');
		var conn = "<select>";
		$.each(val.connections, function(i, val) {
			conn += "<option>" + val + "</option>";
		});
		conn += "</select>";
		table.row.add(
				[ val.startDestination, val.endDestination, val.departureTime,
					val.landingTime, diff + " min", val.flightDistance,
						val.connections.length, conn, val.firstClassPrice, val.businessClassPrice, val.economyClassPrice,
						val.pricePerBag]).draw(false);
	});
}

/* USER PROFILE JS */

function loadProfileData() {
	let token = getJwtToken("jwtToken");
	$.ajax({
		type : 'GET',
		url : loadUserInfoURL,
		dataType : "json",
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		success : function(data) {
			if (data != null) {
				$('input[name="fname"]').val(data.firstName);
				$('input[name="lname"]').val(data.lastName);
				$('input[name="phone"]').val(data.phone);
				$('input[name="address"]').val(data.address);
				$('#email').text(data.email);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

$(document).on("click", "#changePasswordButton", function(e){
	e.preventDefault();
	document.location.href = changePasswordURL;
});

function userFormToJSON(firstName, lastName, phone, address, email) {
	return JSON.stringify({
		"firstName" : firstName,
		"lastName" : lastName,
		"phoneNumber" : phone,
		"address" : address,
		"email" : email
	});
}

function userEditFormSetUp() {
	$('#userEditForm').on('submit', function(e) {
		e.preventDefault();
		let firstName = $('input[name="fname"]').val();
		let lastName = $('input[name="lname"]').val();
		let phone = $('input[name="phone"]').val();
		let address = $('input[name="address"]').val();
		let email = $('#email').text();

		$.ajax({
			type : 'PUT',
			url : editUserInfoURL,
			contentType : 'application/json',
			dataType : "html",
			data : userFormToJSON(firstName, lastName, phone, address, email),
			success : function(data) {
				if (data != "") {
					toastr["error"](data);
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert("AJAX ERROR: " + textStatus);
			}
		});
	});
}

/* PLANE SEATS JS */
var firstSeatLabel = 1;
var firstClass = [];
var businessClass = [];
var economyClass = [];
var numberOfSeatsPerClass = [ 0, 0, 0 ];
var reservedSeats = [];
var seatsForDelete = [];

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
		seatClass.push("ll_ll");
	$.each(planeSegment, function(i, val) {
		var row = seatClass[val["row"] - 1 - fixer].split("");
		if (reservedSeats.includes(val["row"] + "_" + val["column"]))
			row[val["column"] - 1 + Math.floor(val["column"] / 3)] = 'a';
		else
			row[val["column"] - 1 + Math.floor(val["column"] / 3)] = label;
		seatClass[val["row"] - 1 - fixer] = row.join("");
		numberOfSeatsPerClass[cat - 1] += 1;
	});
}

function add(e, cat) {
	e.preventDefault();
	var number, seatClass;
	if (cat == 1) {
		number = parseInt($("#first").val());
		if (isNaN(number) || number <= 0) {
			toastr["error"]("Invalid number.");
			return;
		}
		addSeats(number, firstClass, 'f', cat);
	} else if (cat == 2) {
		number = parseInt($("#business").val());
		if (isNaN(number) || number <= 0) {
			toastr["error"]("Invalid number.");
			return;
		}
		if (numberOfSeatsPerClass[0] == 0) {
			toastr["error"]("There are no seats in First class.");
		}
		addSeats(number, businessClass, 'b', cat);
	} else {
		number = parseInt($("#economy").val());
		if (isNaN(number) || number <= 0) {
			toastr["error"]("Invalid number.");
			return;
		}
		if (numberOfSeatsPerClass[0] == 0 && numberOfSeatsPerClass[1] == 0) {
			toastr["error"]("There are no seats in First and Business classes.");
			return;
		}
		addSeats(number, economyClass, 'e', cat);
	}
}

function addSeatsIndividually(e) {
	e.preventDefault();
	var invalid = false;
	if (seatsForDelete.length == 0) {
		toastr["error"]("No empty places chosen.");
		return;
	}
	$.each(seatsForDelete, function(i, val) {
		if (invalid)
			return;
		var idx = val.split('_');
		var row;
		if (idx[0] <= firstClass.length) {
			row = firstClass[idx[0] - 1].split("");
		} else if (idx[0] > firstClass.length
				&& idx[0] <= businessClass.length + firstClass.length) {
			row = businessClass[idx[0] - firstClass.length - 1].split("");
		} else if (idx[0] > businessClass.length
				&& idx[0] <= firstClass.length + businessClass.length
						+ economyClass.length) {
			row = economyClass[idx[0] - firstClass.length
					- businessClass.length - 1].split("");
		}
		if (row[idx[1] - 1] != 'l')
			invalid = true;
	});
	if (invalid) {
		toastr["error"]("Seats which already exist can not be added");
		return;
	}
	$.each(seatsForDelete,
			function(i, val) {
				var idx = val.split('_');
				if (idx[0] <= firstClass.length) {
					var row = firstClass[idx[0] - 1].split("");
					row[idx[1] - 1] = 'f';
					firstClass[idx[0] - 1] = row.join("");
					numberOfSeatsPerClass[0] = numberOfSeatsPerClass[0] + 1;
				} else if (idx[0] > firstClass.length
						&& idx[0] <= businessClass.length + firstClass.length) {
					var row = businessClass[idx[0] - firstClass.length - 1]
							.split("");
					row[idx[1] - 1] = 'b';
					businessClass[idx[0] - firstClass.length - 1] = row
							.join("");
					numberOfSeatsPerClass[1] = numberOfSeatsPerClass[1] + 1;
				} else if (idx[0] > businessClass.length
						&& idx[0] <= firstClass.length + businessClass.length
								+ economyClass.length) {
					var row = economyClass[idx[0] - firstClass.length
							- businessClass.length - 1].split("");
					row[idx[1] - 1] = 'e';
					economyClass[idx[0] - firstClass.length
							- businessClass.length - 1] = row.join("");
					numberOfSeatsPerClass[2] = numberOfSeatsPerClass[2] + 1;
				}
			});
	console.log(numberOfSeatsPerClass);
	seatsForDelete = [];
	var seats = firstClass.concat(businessClass).concat(economyClass);
	firstSeatLabel = 1;
	$('.seatCharts-row').remove();
	$('.seatCharts-legendItem').remove();
	$('#seat-map,#seat-map *').unbind().removeData();
	showPlaneSeats(seats);
}

function addSeats(number, seatClass, label, cat, initial) {
	var row = '';
	var seq = 1;
	var num = number;
	if (seatClass.length != 0) {
		var lastRow = seatClass[seatClass.length - 1].split("");
		var counter = 0;
		var idx = lastRow.lastIndexOf(label);
		var idx2 = lastRow.lastIndexOf('a');
		if (idx < idx2)
			idx = idx2;
		for (var i = idx; i < 5; i++) {
			if (lastRow[i] == 'l' && i != 2) {
				lastRow[i] = label;
				numberOfSeatsPerClass[cat - 1]++;
				counter++;
				if (counter == number) {
					break;
				}
			}
		}
		seatClass[seatClass.length - 1] = lastRow.join("");
		number -= counter;
	}
	if (number != 1)
		number += Math.ceil(number / 5);

	for (var i = 0; i < number; i++) {
		if (i == (2 + 5 * (seq - 1))) {
			row += '_';
		} else {
			row += label;
			numberOfSeatsPerClass[cat - 1]++;
		}
		if (i % (4 + 5 * (seq - 1)) == 0 && i != 0) {
			seatClass.push(row);
			row = '';
			seq++;
		}
		if ((number - i - 1 < 5) && row == '' && (number != i + 1)) {
			if (number - i - 1 == 3) {
				row = label.repeat(number - i - 2) + '_' + 'll';
				numberOfSeatsPerClass[cat - 1] += number - i - 2;
			} else if (number - i - 1 == 4) {
				row = label.repeat(number - i - 3) + '_' + label + 'l';
				numberOfSeatsPerClass[cat - 1] += number - i - 2;
			} else {
				if (3 - number + i != 0)
					row = label.repeat(number - i - 1)
							+ 'l'.repeat(3 - (number - i)) + '_'
							+ 'l'.repeat(5 - (number - i + 1));
				else
					row = label.repeat(number - i - 1) + '_'
							+ 'l'.repeat(5 - (number - i));
				numberOfSeatsPerClass[cat - 1] += number - i - 1;
			}
			seatClass.push(row);
			break;
		} else if ((i == number - 1) && (row.length != 5) && (row.length != 0)) {
			if (number == 1)
				row += 'l' + '_' + 'll';
			else {
				var len = 5 - row.length;
				row += 'l'.repeat(len);
			}
			seatClass.push(row);
		}
	}
	console.log(numberOfSeatsPerClass);
	if (!initial) {
		var seats = firstClass.concat(businessClass).concat(economyClass);
		$('.seatCharts-row').remove();
		$('.seatCharts-legendItem').remove();
		$('#seat-map,#seat-map *').unbind().removeData();
		firstSeatLabel = 1;
		showPlaneSeats(seats);
	}

}

function deleteSeatsIndividually(e) {
	e.preventDefault();
	var invalid = false;
	if (seatsForDelete.length == 0) {
		toastr["error"]("No seats chosen.");
		return;
	}
	$.each(seatsForDelete, function(i, val) {
		if (invalid)
			return;
		var idx = val.split('_');
		var row;
		if (idx[0] <= firstClass.length) {
			row = firstClass[idx[0] - 1].split("");
		} else if (idx[0] > firstClass.length
				&& idx[0] <= businessClass.length + firstClass.length) {
			row = businessClass[idx[0] - firstClass.length - 1].split("");
		} else if (idx[0] > businessClass.length
				&& idx[0] <= firstClass.length + businessClass.length
						+ economyClass.length) {
			row = economyClass[idx[0] - firstClass.length
					- businessClass.length - 1].split("");
		}
		if (row[idx[1] - 1] == 'l')
			invalid = true;
	});
	if (invalid) {
		toastr["error"]("Empty seats can not be deleted.");
		return;
	}
	$.each(seatsForDelete,
			function(i, val) {
				var idx = val.split('_');
				if (idx[0] <= firstClass.length) {
					var row = firstClass[idx[0] - 1].split("");
					row[idx[1] - 1] = 'l';
					firstClass[idx[0] - 1] = row.join("");
					numberOfSeatsPerClass[0] = numberOfSeatsPerClass[0] - 1;
				} else if (idx[0] > firstClass.length
						&& idx[0] <= businessClass.length + firstClass.length) {
					var row = businessClass[idx[0] - firstClass.length - 1]
							.split("");
					row[idx[1] - 1] = 'l';
					businessClass[idx[0] - firstClass.length - 1] = row
							.join("");
					numberOfSeatsPerClass[1] = numberOfSeatsPerClass[1] - 1;
				} else if (idx[0] > businessClass.length
						&& idx[0] <= firstClass.length + businessClass.length
								+ economyClass.length) {
					var row = economyClass[idx[0] - firstClass.length
							- businessClass.length - 1].split("");
					row[idx[1] - 1] = 'l';
					economyClass[idx[0] - firstClass.length
							- businessClass.length - 1] = row.join("");
					numberOfSeatsPerClass[2] = numberOfSeatsPerClass[2] - 1;
				}
			});
	while (firstClass.indexOf("ll_ll") != -1)
		firstClass.splice(firstClass.indexOf("ll_ll"), 1);
	while (businessClass.indexOf("ll_ll") != -1)
		businessClass.splice(businessClass.indexOf("ll_ll"), 1);
	while (economyClass.indexOf("ll_ll") != -1)
		economyClass.splice(economyClass.indexOf("ll_ll"), 1);
	console.log(numberOfSeatsPerClass);
	seatsForDelete = [];
	var seats = firstClass.concat(businessClass).concat(economyClass);
	firstSeatLabel = 1;
	$('.seatCharts-row').remove();
	$('.seatCharts-legendItem').remove();
	$('#seat-map,#seat-map *').unbind().removeData();
	showPlaneSeats(seats);
}

function deleteSeats(e, cat) {
	e.preventDefault();
	var number;
	if (cat == 1) {
		number = parseInt($("#first").val());
		if (isNaN(number) || number <= 0) {
			toastr["error"]("Invalid number.");
			return;
		}
		if (number > numberOfSeatsPerClass[cat - 1]) {
			toastr["error"]("Number is greather than number of seats.");
			return;
		}
		var tempNumber = number;
		var invalid = false;
		var valid = false;
		var tempClass = firstClass.slice().reverse();
		$.each(tempClass, function(i, val) {
			if (invalid || valid)
				return;
			var row = val.split("");
			$.each(row.reverse(), function(i, val) {
				if (invalid || valid)
					return;
				if (val == 'l' || val == '_')
					return;
				if (val == 'a') {
					invalid = true;
					return;
				}
				tempNumber--;
				if (tempNumber == 0)
					valid = true;
			});
		});
		if (invalid) {
			toastr["error"]("Reserved seat can not be deleted.");
			return;
		}
		$.each(tempClass, function(i, val) {
			if (number == 0)
				return;
			var row = val.split("");
			var tempRow = row.reverse();
			$.each(tempRow, function(i, val) {
				if (number == 0)
					return;
				if (tempRow[i] == 'l' || tempRow[i] == '_')
					return;
				tempRow[i] = 'l';
				numberOfSeatsPerClass[cat - 1]--;
				number--;
			});
			row = tempRow.reverse();
			firstClass[firstClass.length - 1 - i] = row.join("");
		});
		while (firstClass.indexOf("ll_ll") != -1)
			firstClass.splice(firstClass.indexOf("ll_ll"), 1);
	} else if (cat == 2) {
		number = parseInt($("#business").val());
		if (isNaN(number) || number <= 0) {
			toastr["error"]("Invalid number.");
			return;
		}
		if (number > numberOfSeatsPerClass[cat - 1]) {
			toastr["error"]("Number is greather than number of seats.");
			return;
		}
		var tempNumber = number;
		var invalid = false;
		var valid = false;
		var tempClass = businessClass.slice().reverse();
		$.each(tempClass, function(i, val) {
			if (invalid || valid)
				return;
			var row = val.split("");
			$.each(row.reverse(), function(i, val) {
				if (invalid || valid)
					return;
				if (val == 'l' || val == '_')
					return;
				if (val == 'a') {
					invalid = true;
					return;
				}
				tempNumber--;
				if (tempNumber == 0)
					valid = true;
			});
		});
		if (invalid) {
			toastr["error"]("Reserved seat can not be deleted.");
			return;
		}
		$.each(tempClass, function(i, val) {
			if (number == 0)
				return;
			var row = val.split("");
			var tempRow = row.reverse();
			$.each(tempRow, function(i, val) {
				if (number == 0)
					return;
				if (tempRow[i] == 'l' || tempRow[i] == '_')
					return;
				tempRow[i] = 'l';
				numberOfSeatsPerClass[cat - 1]--;
				number--;
			});
			row = tempRow.reverse();
			businessClass[businessClass.length - 1 - i] = row.join("");
		});
		while (businessClass.indexOf("ll_ll") != -1)
			businessClass.splice(businessClass.indexOf("ll_ll"), 1);
	} else {
		number = parseInt($("#economy").val());
		if (isNaN(number) || number <= 0) {
			toastr["error"]("Invalid number.");
			return;
		}
		if (number > numberOfSeatsPerClass[cat - 1]) {
			toastr["error"]("Number is greather than number of seats.");
			return;
		}
		var tempNumber = number;
		var invalid = false;
		var valid = false;
		var tempClass = economyClass.slice().reverse();
		$.each(tempClass, function(i, val) {
			if (invalid || valid)
				return;
			var row = val.split("");
			$.each(row.reverse(), function(i, val) {
				if (invalid || valid)
					return;
				if (val == 'l' || val == '_')
					return;
				if (val == 'a') {
					invalid = true;
					return;
				}
				tempNumber--;
				if (tempNumber == 0)
					valid = true;
			});
		});
		if (invalid) {
			toastr["error"]("Reserved seat can not be deleted.");
			return;
		}
		$.each(tempClass, function(i, val) {
			if (number == 0)
				return;
			var row = val.split("");
			var tempRow = row.reverse();
			$.each(tempRow, function(i, val) {
				if (number == 0)
					return;
				if (tempRow[i] == 'l' || tempRow[i] == '_')
					return;
				tempRow[i] = 'l';
				numberOfSeatsPerClass[cat - 1]--;
				number--;
			});
			row = tempRow.reverse();
			economyClass[economyClass.length - 1 - i] = row.join("");
		});
		while (economyClass.indexOf("ll_ll") != -1)
			economyClass.splice(economyClass.indexOf("ll_ll"), 1);
	}
	var seats = firstClass.concat(businessClass).concat(economyClass);
	firstSeatLabel = 1;
	$('.seatCharts-row').remove();
	$('.seatCharts-legendItem').remove();
	$('#seat-map,#seat-map *').unbind().removeData();
	showPlaneSeats(seats);
}

function showPlaneSeats(seats) {
	var $cart = $('#selected-seats'), $counter = $('#counter'), $total = $('#total'), sc = $(
			'#seat-map').seatCharts(
			{
				map : seats,
				seats : {
					f : {
						price : 100,
						classes : 'first-class', // your custom CSS class
						category : 'First Class'
					},
					e : {
						price : 40,
						classes : 'economy-class', // your custom CSS class
						category : 'Economy Class'
					},
					b : {
						price : 40,
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
						if (character == 'l')
							return;
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
						if (this.settings.character == 'a')
							return;
						seatsForDelete.push(this.settings.id);
						console.log(seatsForDelete);
						/*
						 * Lets update the counter and total
						 * 
						 * .find function will not find the current seat,
						 * because it will change its stauts only after return
						 * 'selected'. This is why we have to add 1 to the
						 * length and the current seat price to the total.
						 */
						$counter.text(sc.find('selected').length + 1);

						return 'selected';
					} else if (this.status() == 'selected') {
						let index = seatsForDelete.indexOf(this.settings.id);
						seatsForDelete.splice(index, 1);
						console.log(seatsForDelete);

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
}

function saveSeatsChanges(e) {
	e.preventDefault();
	var seats = firstClass.concat(businessClass).concat(economyClass);
	var savedSeats = [];
	for (var i = 0; i < seats.length; i++) {
		for (var j = 0; j < 5; j++) {
			if (seats[i][j] != 'l' && seats[i][j] != '_') {
				if (j == 3 || j == 4)
					savedSeats.push((i + 1) + "_" + j + "_" + seats[i][j]);
				else
					savedSeats
							.push((i + 1) + "_" + (j + 1) + "_" + seats[i][j]);
			}
		}
	}
	console.log(savedSeats);
	$.ajax({
		method : "PUT",
		url : saveSeatsChangesURL,
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		contentType : "application/json",
		data : JSON.stringify(savedSeats),
		success : function(data) {
			console.log(data);
		}
	});
}

function addFlight(e) {
	e.preventDefault();
	var startDestination = $( "#startDestination option:selected" ).text();
	var endDestination = $( "#endDestination  option:selected" ).text();
	if (startDestination == endDestination) {
		toastr["error"]("Start destination and end destination must not be the same.");
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
	if (moment(landingTime).isBefore(departureTime) || moment(landingTime).isSame(departureTime)) {
		toastr["error"]("Landing time must be after the departure time.");
		return;
	}
	var flightDistance = $("#flightDistance").val();
	if (isNaN(flightDistance) || flightDistance <= 0 || flightDistance == "") {
		toastr["error"]("Invalid flight distance.");
		return;
	}
	var connections = $("#connections").val();
	var pricePerBag = $("#pricePerBag").val();
	if (isNaN(pricePerBag) || pricePerBag < 0 || pricePerBag == "") {
		toastr["error"]("Invalid price per bag.");
		return;
	}
	var firstPrice = $("#firstPrice").val();
	if (isNaN(firstPrice) || firstPrice < 0 || firstPrice == "") {
		toastr["error"]("Invalid first class seat price.");
		return;
	}
	var businessPrice = $("#businessPrice").val();
	if (isNaN(businessPrice) || businessPrice < 0 || businessPrice == "") {
		toastr["error"]("Invalid business class seat price.");
		return;
	}
	var economyPrice = $("#economyPrice").val();
	if (isNaN(economyPrice) || economyPrice < 0 || economyPrice == "") {
		toastr["error"]("Invalid economy class seat price.");
		return;
	}
	$.ajax({
		method : "POST",
		url : addFlightURL,
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		contentType : "application/json",
		data : flightToJSON(startDestination, endDestination, departureTime, landingTime, flightDistance, connections, pricePerBag,
				firstPrice, businessPrice, economyPrice),
		success : function(data) {
			if (data.toastType == "success") {
				var table = $('#flightsTable').DataTable();
				var date1 = moment(departureTime);
				var date2 = moment(landingTime);
				var diff = date2.diff(date1, 'minutes');
				var conn = "<select>";
				$.each(connections, function(i, val) {
					conn += "<option>" + val + "</option>";
				});
				conn += "</select>";
				table.row.add(
						[ startDestination, endDestination, moment(new Date(departureTime)).format("DD.MM.YYYY HH:mm"),
							moment(new Date(landingTime)).format("DD.MM.YYYY HH:mm"), diff + " min", flightDistance,
								connections.length, conn, firstPrice, businessPrice, economyPrice,
								pricePerBag]).draw(false);
				toastr[data.toastType](data.message);
			}
		}
	});
}

function flightToJSON(startDestination, endDestination, departureTime, landingTime, flightDistance, connections, pricePerBag,
		firstPrice, businessPrice, economyPrice) {
	var a = JSON.stringify({
		"startDestination" : startDestination,
		"endDestination" : endDestination,
		"departureTime" : departureTime,
		"landingTime" : landingTime,
		"flightDistance" : flightDistance,
		"connections" : connections,
		"pricePerBag" : pricePerBag,
		"firstClassPrice" : firstPrice,
		"businessClassPrice": businessPrice,
		"economyClassPrice": economyPrice,
		"airlineName" : airlineName
	});
	return a;
}

function setDestMap(e) {
	e.preventDefault();
	setUpMap(45, 45, 'destMapDiv');
}

function addDestination(e) {
	e.preventDefault();
	var nameOfDest = $("#destinationName").val();
	if (nameOfDest == null || nameOfDest == "") {
		toastr["error"]("Invalid name of destination.");
		return;
	}
	var latitude = $("#destMapDivLatitude").val();
	var longitude = $("#destMapDivLongitude").val();
	$.ajax({
		method : "POST",
		url : addDestinationURL,
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		contentType : "application/json",
		data : JSON.stringify({
			"nameOfDest": nameOfDest,
			"latitude": latitude,
			"longitude": longitude,
			"airlineName": airlineName
		}),
		success : function(data) {
			if (data.toastType == "success") {
				var table = $('#destinationsTable').DataTable();
				table.row.add([ nameOfDest ]).draw(false);
				var start = $("#startDestination");
				var end = $("#endDestination");
				var con = $("#connections");
				start.append("<option value=" + nameOfDest + ">" + nameOfDest + "</option>");
				end.append("<option value=" + nameOfDest + ">" + nameOfDest + "</option>");
				con.append("<option value=" + nameOfDest + ">" + nameOfDest + "</option>");
				toastr[data.toastType](data.message);
			}
			else {
				toastr["error"](data.message);
			}
		}
	});
}