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
var seatsForDelete = [];

function renderPlaneSeats(list) {
	showPlaneSeats([]);
}

function addFirstClass(e) {
	e.preventDefault();
	var number = parseInt($("#first").val());
	console.log(number);
	var row = '';
	var seq = 1;
	if (firstClass.length != 0) {
		firstClass.pop();
		var lastRow = firstClass[firstClass.length - 1].split("");
		var counter = 0;
		for (var i = 0; i < 5; i++) {
			if (lastRow[i] == '_' && i != 2) {
				lastRow[i] = 'f';
				counter++;
			}
		}
		firstClass[firstClass.length - 1] = lastRow.join("");
		number -= counter;
	}
	number += Math.ceil(number / 5);
	console.log(number);
	for(var i = 0; i < number; i++) {
		if (i == (2 + 5 * (seq - 1))) {
			row += '_';
		}
		else
			row += 'f';
		if (i % (4 + 5 * (seq - 1)) == 0 && i != 0) {
			firstClass.push(row);
			row = '';
			seq++;
		}
		if ((number - i - 1 < 5) && row == '' && (number != i + 1)) {
			row = 'f'.repeat(number - i - 1) + '_'.repeat(5 - (number - i - 1));
			firstClass.push(row);
			break;
		}
	}
	console.log(firstClass);
	firstClass.push("_____");
	var seats = firstClass.concat(businessClass).concat(economyClass);
	$('.seatCharts-row').remove();
	$('.seatCharts-legendItem').remove();
	$('#seat-map,#seat-map *').unbind().removeData();
	firstSeatLabel = 1;
	showPlaneSeats(seats);
}

function addBusinessClass(e) {
	e.preventDefault();
	var number = parseInt($("#business").val());
	console.log(number);
	var row = '';
	var seq = 1;
	if (businessClass.length != 0) {
		businessClass.pop();
		var lastRow = businessClass[businessClass.length - 1].split("");
		var counter = 0;
		for (var i = 0; i < 5; i++) {
			if (lastRow[i] == '_' && i != 2) {
				lastRow[i] = 'b';
				counter++;
			}
		}
		businessClass[businessClass.length - 1] = lastRow.join("");
		number -= counter;
	}
	number += Math.ceil(number / 5);
	console.log(number);
	for(var i = 0; i < number; i++) {
		if (i == (2 + 5 * (seq - 1))) {
			row += '_';
		}
		else
			row += 'b';
		if (i % (4 + 5 * (seq - 1)) == 0 && i != 0) {
			businessClass.push(row);
			row = '';
			seq++;
		}
		if ((number - i - 1 < 5) && row == '' && (number != i + 1)) {
			row = 'b'.repeat(number - i - 1) + '_'.repeat(5 - (number - i - 1));
			businessClass.push(row);
			break;
		}
	}
	console.log(businessClass);
	businessClass.push("_____");
	var seats = firstClass.concat(businessClass).concat(economyClass);
	$('.seatCharts-row').remove();
	$('.seatCharts-legendItem').remove();
	$('#seat-map,#seat-map *').unbind().removeData();
	firstSeatLabel = 1;
	showPlaneSeats(seats);
}

function addEconomyClass(e) {
	e.preventDefault();
	var number = parseInt($("#economy").val());
	console.log(number);
	var row = '';
	var seq = 1;
	if (economyClass.length != 0) {
		economyClass.pop();
		var lastRow = economyClass[economyClass.length - 1].split("");
		var counter = 0;
		for (var i = 0; i < 5; i++) {
			if (lastRow[i] == '_' && i != 2) {
				lastRow[i] = 'e';
				counter++;
			}
		}
		economyClass[economyClass.length - 1] = lastRow.join("");
		number -= counter;
	}
	number += Math.ceil(number / 5);
	console.log(number);
	for(var i = 0; i < number; i++) {
		if (i == (2 + 5 * (seq - 1))) {
			row += '_';
		}
		else
			row += 'e';
		if (i % (4 + 5 * (seq - 1)) == 0 && i != 0) {
			economyClass.push(row);
			row = '';
			seq++;
		}
		if ((number - i - 1 < 5) && row == '' && (number != i + 1)) {
			row = 'e'.repeat(number - i - 1) + '_'.repeat(5 - (number - i - 1));
			economyClass.push(row);
			break;
		}
	}
	console.log(economyClass);
	economyClass.push("_____");
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
		if (number > firstClass.length) {
			return;
		}
		while (number - 4 >= 0) {
			firstClass.pop();
			number -= 4;
		}
		if (number > 0) {
			if (number > 2) {
				number -= 2;
			}
			var lastRow = firstClass[firstClass.length - 1].split("");
			for (var i = 4; i >= number; i--) {
				if (lastRow[i] == '_') {
					continue;
				}
				lastRow[i] = '_';
			}
			firstClass[firstClass.length - 1] = lastRow.join("");
		}
		firstClass.push('_____');
		var seats = firstClass.concat(businessClass).concat(economyClass);
		$('.seatCharts-row').remove();
		$('.seatCharts-legendItem').remove();
		$('#seat-map,#seat-map *').unbind().removeData();
		firstSeatLabel = 1;
		showPlaneSeats(seats);
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
			[ 'e', 'available',   'Economy Class'],
			[ 'f', 'unavailable', 'Already Booked']
	    ]					
	},
	click: function () {
		if (this.status() == 'available') {
			//let's create a new <li> which we'll add to the cart items
			seatsForDelete.push(this.settings.label);
			/*
			 * Lets update the counter and total
			 *
			 * .find function will not find the current seat, because it will change its stauts only after return
			 * 'selected'. This is why we have to add 1 to the length and the current seat price to the total.
			 */
			$counter.text(sc.find('selected').length+1);
			$total.text(recalculateTotal(sc)+this.data().price);
			
			return 'selected';
		} else if (this.status() == 'selected') {
			var index = seatsForDelete.indexOf(this.settings.label);
			seatsForDelete.splice(index, 1);
			//update the counter
			$counter.text(sc.find('selected').length-1);
			//and total
			$total.text(recalculateTotal(sc)-this.data().price);
		
			//remove the item from our cart
			$('#cart-item-'+this.settings.id).remove();
		
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
//this will handle "[cancel]" link clicks
$('#selected-seats').on('click', '.cancel-cart-item', function () {
	//let's just trigger Click event on the appropriate seat, so we don't have to repeat the logic here
	sc.get($(this).parents('li:first').data('seatId')).click();
});

function recalculateTotal(sc) {
	var total = 0;
	
	//basically find every selected seat and sum its price
	sc.find('selected').each(function () {
		total += this.data().price;
	});

	return total;
}
