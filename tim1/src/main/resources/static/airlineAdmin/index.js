const TOKEN_KEY = 'jwtToken';

const tileLayerURL = "https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw";
const MAP_ZOOM = 12;
const MAX_MAP_ZOOM = 19;
const MAP_ID = 'mapbox.streets';

const editAirlineURL = "/api/editAirline";
const getDestinationsURL = "/api/getDestinations";
const getFlightsURL = "/api/getFlights";
const getAirlineOfAdminURL = "/api/getAirlineOfAdmin";

$(document).ready(function() {
	loadAirline();
	setUpMap(45, 45);
})

function setUpMap(latitude, longitude) {
	var destMap = L.map('mapDiv').setView([ latitude, longitude ], MAP_ZOOM);
	L.tileLayer(tileLayerURL, {
		maxZoom : MAX_MAP_ZOOM,
		id : MAP_ID
	}).addTo(destMap);
	var marker = L.marker([ latitude, longitude ], {
		draggable : true
	}).addTo(destMap);
	marker.on('dragend', function(e) {
		$("#latitude").val(marker.getLatLng().lat);
		$("#longitude").val(marker.getLatLng().lng);
	});
}

function hideModal() {
	$("#modalDialog").fadeOut(function() {
		$("#modalDialog").empty();
	});
	$("#overlayDiv").fadeOut();
}
function showModal(callback) {
	$("#modalDialog").fadeIn(function() {
		if (callback !== undefined)
			callback();
	});
	$("#overlayDiv").fadeIn();
}

/*function editAirline(e) {
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
}*/

function loadAirline() {
	$.ajax({
		dataType : "json",
		url : getAirlineOfAdminURL,
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		success : function(data) {
			$("#airlineName").val(data["name"]);
			$("#airlineGrade").text(data["averageGrade"]);
			$("#airlineDescription").text(data["description"]);
			/*setUpMap(data["latitude"],
					data["longitude"]);*/
			renderDestinations(data["destinations"]);
			renderFlights(data["flights"]);
			//renderQuickReservations[data["quickReservations"]];
			renderPlaneSeats(data["planeSegments"]);
		}
	});
}

function renderDestinations(list) {
	var table = $("#destinationsTable");
	$.each(list, function(i, val) {
		var tr = $("<tr class='tableData'></tr>");
		tr.append(`<td>${val.name}</td>`);
		table.append(tr);
	});
}

function renderFlights(list) {
	var table = $("#roomsTable");
	$.each(list, function(i, val) {
		var tr = $("<tr class='tableData'></tr>");
		tr.append(`<td>${val.startDestination}</td>`);
		tr.append(`<td>${val.endDestination}</td>`);
		tr.append(`<td>${val.departureTime}</td>`);
		tr.append(`<td>${val.landingTime}</td>`);
		tr.append(`<td>${val.flightDuration}</td>`);
		tr.append(`<td>${val.flightLength}</td>`);
		tr.append(`<td>${val.numberOfFlightConnections}</td>`);
		tr.append(`<td>${val.ticketPrice}</td>`);
		tr.append(`<td>${val.pricePerBag}</td>`);
		tr.append(`<td>${val.averageGrade}</td>`);
		table.append(tr);
	});
}

function changeContentToDestinations() {
	$(".contentTable").hide();
	$("#destinationsTable").show();
	$(".active").removeClass("active");
	$("#destinationsTab").addClass("active");
}

function changeContentToFlights() {
	$(".contentTable").hide();
	$("#flightsTable").show();
	$(".active").removeClass("active");
	$("#flightsTab").addClass("active");
}

function changeContentToQuickRes() {
	$(".contentTable").hide();
	$("#quickReservationsTable").show();
	$("#addQuickReservationButton").show();
	$(".active").removeClass("active");
	$("#quickReservationsTab").addClass("active");
}

function changeContentToPlaneSeats() {
	$(".contentTable").hide();
	$("#planeSeatsTable").show();
	$(".active").removeClass("active");
	$("#planeSeatsTab").addClass("active");
}


/* PLANE SEATS JS */
var firstSeatLabel = 1;
var firstClass = [];
var businessClass = [];
var economyClass = [];
var numberOfSeatsPerClass = [0, 0, 0];

function renderPlaneSeats(list) {
	showPlaneSeats([]);
}

function add(e, cat) {
	e.preventDefault();
	var number, seatClass;
	if (cat == 1) {
		number = parseInt($("#first").val());
		addSeats(number, firstClass, 'f', cat);
	}
	else if (cat == 2) {
		number = parseInt($("#business").val());
		addSeats(number, businessClass, 'b', cat);
	}
	else {
		number = parseInt($("#economy").val());
		addSeats(number, economyClass, 'e', cat);
	}
}

function addSeats(number, seatClass, label, cat) {
	var row = '';
	var seq = 1;
	var num = number;
	if (seatClass[0] == '_____') {
		seatClass.pop();
	}
	if (seatClass.length != 0) {
		seatClass.pop();
		var lastRow = seatClass[seatClass.length - 1].split("");
		var counter = 0;
		for (var i = 0; i < 5; i++) {
			if (lastRow[i] == '_' && i != 2) {
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
	
	for(var i = 0; i < number; i++) {
		if (i == (2 + 5 * (seq - 1))) {
			row += '_';
		}
		else {
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
				row = label.repeat(number - i - 2) + '_' + label + '_';
				numberOfSeatsPerClass[cat - 1] += number - i - 1;
			}
			else if (number - i - 1 == 4) {
				row = label.repeat(number - i - 3) + '_' + label + '_';
				numberOfSeatsPerClass[cat - 1] += number - i - 2;
			}
			else {
				row = label.repeat(number - i - 1) + '_'.repeat(5 - (number - i - 1));
				numberOfSeatsPerClass[cat - 1] += number - i - 1;
			}
			seatClass.push(row);
			break;
		}
		else if ((i == number - 1) && (row.length != 5) && (num % 4 != 0)) {
			var len = 5 - row.length;
			row += '_'.repeat(len);
			seatClass.push(row);
		}
	}
	seatClass.push("_____");
	var seats = firstClass.concat(businessClass).concat(economyClass);
	$('.seatCharts-row').remove();
	$('.seatCharts-legendItem').remove();
	$('#seat-map,#seat-map *').unbind().removeData();
	firstSeatLabel = 1;
	showPlaneSeats(seats);
}

function deleteSeat(e, cat) {
	e.preventDefault();
	if (cat == 1) {
		var number = parseInt($("#first").val());
		if (number > numberOfSeatsPerClass[cat - 1]) {
			return;
		}
		var newNumber = numberOfSeatsPerClass[cat - 1] - number;
		numberOfSeatsPerClass[cat - 1] = 0;
		firstClass = [];
		addSeats(newNumber, firstClass, 'f', cat);
	}
	else if (cat == 2) {
		var number = parseInt($("#business").val());
		if (number > numberOfSeatsPerClass[cat - 1]) {
			return;
		}
		var newNumber = numberOfSeatsPerClass[cat - 1] - number;
		numberOfSeatsPerClass[cat - 1] = 0;
		businessClass = [];
		addSeats(newNumber, businessClass, 'b', cat);
	}
	else {
		var number = parseInt($("#economy").val());
		if (number > numberOfSeatsPerClass[cat - 1]) {
			return;
		}
		var newNumber = numberOfSeatsPerClass[cat - 1] - number;
		numberOfSeatsPerClass[cat - 1] = 0;
		economyClass = [];
		addSeats(newNumber, economyClass, 'e', cat);
	}
}

function showPlaneSeats(seats) {
	var $cart = $('#selected-seats'),
	$counter = $('#counter'),
	$total = $('#total'),
	sc = $('#seat-map').seatCharts({
	map: seats,
	seats: {
		f: {
			price   : 100,
			classes : 'first-class', //your custom CSS class
			category: 'First Class'
		},
		e: {
			price   : 40,
			classes : 'economy-class', //your custom CSS class
			category: 'Economy Class'
		}					
	
	},
	naming : {
		top : false,
		left: false,
		getLabel : function (character, row, column) {
			return firstSeatLabel++;
		},
	},
	legend : {
		node : $('#legend'),
	    items : [
			[ 'f', 'available',   'First Class' ],
			[ 'b', 'available',   'Business Class'],
			[ 'e', 'available',   'Economy Class']
	    ]					
	},
	click: function () {
			return this.style();
		}
	});
}
