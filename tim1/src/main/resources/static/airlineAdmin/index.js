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
			$("#airlineGrade").text(data["averageGrade"]);
			$("#airlineDescription").text(data["description"]);
			/*setUpMap(data["latitude"],
					data["longitude"]);*/
			renderDestinations(data["destinations"]);
			renderFlights(data["flights"]);
			//renderQuickReservations[data["quickReservations"]];
			//renderPlaneSeats(data["planeSegments"]);
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
