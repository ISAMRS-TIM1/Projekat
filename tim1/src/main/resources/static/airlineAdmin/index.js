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
			setUpMap(data["latitude"],
					data["longitude"]);
			renderDestinations(data["destinations"]);
			renderFlights(data["flights"]);
			//renderQuickReservations[data["quickReservations"]];
			reservedSeats = data["reservedSeats"];
			renderPlaneSeats(data["planeSegments"], data["reservedSeats"]);
			console.log(data["planeSegments"]);
			console.log("a");
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
var reservedSeats = [];
var seatsForDelete = [];

function renderPlaneSeats(planeSegments) {
	if (planeSegments.length == 0) {
		showPlaneSeats([]);
	}
	else {
		reservedSeats = [2];
		var first = planeSegments[0].seats.length;
		var business = planeSegments[1].seats.length;
		var economy = planeSegments[2].seats.length;
		addSeats(first, firstClass, 'f', 1, true);
		addSeats(business, businessClass, 'b', 2, true);
		addSeats(economy, economyClass, 'e', 3, true);
		addReservedSeats();
		var seats = firstClass.concat(businessClass).concat(economyClass);
		$('.seatCharts-row').remove();
		$('.seatCharts-legendItem').remove();
		$('#seat-map,#seat-map *').unbind().removeData();
		firstSeatLabel = 1;
		showPlaneSeats(seats);
	}
}

function addReservedSeats() {
	$.each(reservedSeats, function(i, val) {
		if (val <= numberOfSeatsPerClass[0]) {
			var idxRow;
			if (val <= 4)
				idxRow = 0;
			else
				idxRow = val % 4;
			var row = firstClass[idxRow];
			var stringRow = row.split("");
			stringRow[val - 1] = 'a';
			firstClass[idxRow] = stringRow.join("");
		}
		else if (val > numberOfSeatsPerClass[0] && val <= numberOfSeatsPerClass[1] + numberOfSeatsPerClass[0]) {
			var newVal = val - numberOfSeatsPerClass[0];
			var idxRow;
			if (newVal <= 4)
				idxRow = 0;
			else
				idxRow = newVal % 4;
			var row = businessClass[idxRow];
			var stringRow = row.split("");
			stringRow[newVal - 1] = 'a';
			businessClass[idxRow] = stringRow.join("");
		}
		else if (val > numberOfSeatsPerClass[1] + numberOfSeatsPerClass[0] && val <= numberOfSeatsPerClass[0] + numberOfSeatsPerClass[1] + numberOfSeatsPerClass[2]) {
			var newVal = val - numberOfSeatsPerClass[0] - numberOfSeatsPerClass[1];
			var idxRow;
			if (newVal <= 4)
				idxRow = 0;
			else
				idxRow = newVal % 4;
			var row = economyClass[idxRow];
			var stringRow = row.split("");
			stringRow[newVal - 1] = 'a';
			economyClass[idxRow] = stringRow.join("");
		}
	});
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

function addSeatsIndividually(e) {
	e.preventDefault();
	var invalid = false;
	$.each(seatsForDelete, function(i, val) {
		if (invalid)
			return;
		var idx = val.split('_');
		var row;
		if (idx[0] <= firstClass.length) {
			row = firstClass[idx[0] - 1].split("");
		}
		else if (idx[0] > firstClass.length && idx[0] <= businessClass.length + firstClass.length) {
			row = businessClass[idx[0] - firstClass.length - 1].split("");
		}
		else if (idx[0] > businessClass.length && idx[0] <= firstClass.length + businessClass.length + economyClass.length) {
			row = economyClass[idx[0] - firstClass.length - businessClass.length - 1].split("");
		}
		if (row[idx[1] - 1] != 'l')
			invalid = true;
	});
	if (invalid)
		return;
	$.each(seatsForDelete, function(i, val) {
		var idx = val.split('_');
		if (idx[0] <= firstClass.length) {
			var row = firstClass[idx[0] - 1].split("");
			row[idx[1] - 1] = 'f';
			firstClass[idx[0] - 1] = row.join("");
			numberOfSeatsPerClass[0] = numberOfSeatsPerClass[0] + 1;
		}
		else if (idx[0] > firstClass.length && idx[0] <= businessClass.length + firstClass.length) {
			var row = businessClass[idx[0] - firstClass.length - 1].split("");
			row[idx[1] - 1] = 'b';
			businessClass[idx[0] - firstClass.length - 1] = row.join("");
			numberOfSeatsPerClass[1] = numberOfSeatsPerClass[1] + 1;
		}
		else if (idx[0] > businessClass.length && idx[0] <= firstClass.length + businessClass.length + economyClass.length) {
			var row = economyClass[idx[0] - firstClass.length - businessClass.length - 1].split("");
			row[idx[1] - 1] = 'e';
			economyClass[idx[0] - firstClass.length - businessClass.length - 1] = row.join("");
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
		var idx = lastRow.lastIndexOf('f');
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
				row = label.repeat(number - i - 2) + '_' + 'll';
				numberOfSeatsPerClass[cat - 1] += number - i - 2;
			}
			else if (number - i - 1 == 4) {
				row = label.repeat(number - i - 3) + '_' + label + 'l';
				numberOfSeatsPerClass[cat - 1] += number - i - 2;
			}
			else {
				if (3 - number + i != 0)
					row = label.repeat(number - i - 1) + 'l'.repeat(3 - (number - i)) + '_' + 'l'.repeat(5 - (number - i + 1));
				else
					row = label.repeat(number - i - 1) + '_' + 'l'.repeat(5 - (number - i));
				numberOfSeatsPerClass[cat - 1] += number - i - 1;
			}
			seatClass.push(row);
			break;
		}
		else if ((i == number - 1) && (row.length != 5) && (row.length != 0)) {
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
		addReservedSeats();
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
	$.each(seatsForDelete, function(i, val) {
		if (invalid)
			return;
		var idx = val.split('_');
		var row;
		if (idx[0] <= firstClass.length) {
			row = firstClass[idx[0] - 1].split("");
		}
		else if (idx[0] > firstClass.length && idx[0] <= businessClass.length + firstClass.length) {
			row = businessClass[idx[0] - firstClass.length - 1].split("");
		}
		else if (idx[0] > businessClass.length && idx[0] <= firstClass.length + businessClass.length + economyClass.length) {
			row = economyClass[idx[0] - firstClass.length - businessClass.length - 1].split("");
		}
		if (row[idx[1] - 1] == 'l')
			invalid = true;
	});
	if (invalid)
		return;
	$.each(seatsForDelete, function(i, val) {
		var idx = val.split('_');
		if (idx[0] <= firstClass.length) {
			var row = firstClass[idx[0] - 1].split("");
			row[idx[1] - 1] = 'l';
			firstClass[idx[0] - 1] = row.join("");
			numberOfSeatsPerClass[0] = numberOfSeatsPerClass[0] - 1;
		}
		else if (idx[0] > firstClass.length && idx[0] <= businessClass.length + firstClass.length) {
			var row = businessClass[idx[0] - firstClass.length - 1].split("");
			row[idx[1] - 1] = 'l';
			businessClass[idx[0] - firstClass.length - 1] = row.join("");
			numberOfSeatsPerClass[1] = numberOfSeatsPerClass[1] - 1;
		}
		else if (idx[0] > businessClass.length && idx[0] <= firstClass.length + businessClass.length + economyClass.length) {
			var row = economyClass[idx[0] - firstClass.length - businessClass.length - 1].split("");
			row[idx[1] - 1] = 'l';
			economyClass[idx[0] - firstClass.length - businessClass.length - 1] = row.join("");
			numberOfSeatsPerClass[2] = numberOfSeatsPerClass[2] - 1;
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

function deleteSeats(e, cat) {
	e.preventDefault();
	var number;
	if (cat == 1) {
		number = parseInt($("#first").val());
		if (number > numberOfSeatsPerClass[cat - 1]) {
			return;
		}
		var newNumber = numberOfSeatsPerClass[cat - 1] - number;
		var tempNumber = number;
		var invalid = false;
		var valid = false;
		$.each(firstClass.reverse(), function(i, val) {
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
		if (invalid)
			return;
		console.log(newNumber);
		numberOfSeatsPerClass[cat - 1] = 0;
		firstClass = [];
		addSeats(newNumber, firstClass, 'f', cat);
	}
	else if (cat == 2) {
		number = parseInt($("#business").val());
		if (number > numberOfSeatsPerClass[cat - 1]) {
			return;
		}
		var newNumber = numberOfSeatsPerClass[cat - 1] - number;
		var tempNumber = number;
		var invalid = false;
		var valid = false;
		$.each(businessClass.reverse(), function(i, val) {
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
		if (invalid)
			return;
		console.log(newNumber);
		numberOfSeatsPerClass[cat - 1] = 0;
		businessClass = [];
		addSeats(newNumber, businessClass, 'b', cat);
	}
	else {
		number = parseInt($("#economy").val());
		if (number > numberOfSeatsPerClass[cat - 1]) {
			return;
		}
		var newNumber = numberOfSeatsPerClass[cat - 1] - number;
		var tempNumber = number;
		var invalid = false;
		var valid = false;
		$.each(economyClass.reverse(), function(i, val) {
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
		if (invalid)
			return;
		console.log(newNumber);
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
		},
		b: {
			price: 40,
			classes : 'business-class',
			category : 'Business Class'
		},
		l: {
			classes : 'blank-class',
			category : 'Blank seat'
		},
		a: {
			classes: 'unavailable',
			category: 'Already booked'
		}
	
	},
	naming : {
		top : false,
		left: false,
		getLabel : function (character, row, column) {
			if (character == 'l')
				return;
			return firstSeatLabel++;
		},
	},
	legend : {
		node : $('#legend'),
	    items : [
			[ 'f', 'available',   'First Class' ],
			[ 'b', 'available',   'Business Class'],
			[ 'e', 'available',   'Economy Class'],
			[ 'a', 'unavailable', 'Already Booked'],
			[ 'l', 'available', 'Blank seat']
	    ]
	},
	click: function () {
		if (this.status() == 'available') {
			if (this.settings.character == 'a')
				return;
			seatsForDelete.push(this.settings.id);
			console.log(seatsForDelete);
			/*
			 * Lets update the counter and total
			 *
			 * .find function will not find the current seat, because it will change its stauts only after return
			 * 'selected'. This is why we have to add 1 to the length and the current seat price to the total.
			 */
			$counter.text(sc.find('selected').length+1);
			
			return 'selected';
			} else if (this.status() == 'selected') {
				let index = seatsForDelete.indexOf(this.settings.id);
				seatsForDelete.splice(index, 1);
				console.log(seatsForDelete);
			
				//seat has been vacated
				return 'available';
			} else if (this.status() == 'unavailable') {
				//seat has been already booked
				return 'unavailable';
			} else {
				return this.style();
			}
		}
	});
}
