const TOKEN_KEY = 'jwtToken';

const tileLayerURL = "https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw";
const MAP_ZOOM = 12;
const MAX_MAP_ZOOM = 19;
const MAP_ID = 'mapbox.streets';

const editAirlineURL = "/api/editAirline";
const getDestinationsOfAirlineURL = "/api/getDestinationsOfAirline";
const editDestinationURL = "/api/editDestination";
const getFlightsURL = "/api/getFlights";
const getAirlineOfAdminURL = "/api/getAirlineOfAdmin";
const saveSeatsChangesURL = "/api/saveSeats";
const addFlightURL = "/api/addFlight";
const addDestinationURL = "/api/addDestination";
const getIncomeOfAirlineURL = "/api/getIncomeOfAirline";
const loadDailyChartDataURL = "/api/getAirlineDailyGraphData";
const loadWeeklyChartDataURL = "/api/getAirlineWeeklyGraphData";
const loadMonthlyChartDataURL = "/api/getAirlineMonthlyGraphData";
const getPlaneSeatsURL = "/api/getPlaneSeats";
const getDetailedFlightURL = "/api/getDetailedFlight";
const createQuickFlightReservationURL = "/api/createQuickFlightReservation";
const getQuickFlightReservationsURL = "/api/getQuickFlightReservations";
const editFlightURL = "/api/editFlight";
const logoutURL = "../logout";
const loadUserInfoURL = "../api/getUserInfo";
const editUserInfoURL = "../api/editUser";
const changePasswordURL = "../changePassword";
const loadDestinationURL = "/api/loadDestination";
var airlineName = "";
var timeFormat = 'DD/MM/YYYY';

var airlineMap = null;
var destMap = null;

$(document).ready(function() {
	setUpToastr();
	loadAirline();
	loadProfileData();
	setUpTables();
	getQuickReservations();
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
	
	$("#roundTrip").change(function() {
	    if(this.checked) {
	        $(".retTrip").show();
	    }
	    else {
	    	$(".retTrip").hide();
	    }
	});
	
	$('#showIncomeDateRange').daterangepicker({
		locale : {
			format : 'DD/MM/YYYY'
		}
	});
	
	$('#descriptionDiv').on( 'change keyup keydown paste cut', 'textarea', function (){
	    $(this).height(0).height(this.scrollHeight);
	}).find( 'textarea' ).change();
	
	$('a[href="#reportsTab"]').click(function(){
		getDailyChartData();
	});
	
	$('#graphicLevel').on('change', function() {
		changeGraphic(this.value);
	});
	
	$('#destinationModalDialog').on('shown.bs.modal', function() {
		setTimeout(function() {
			destMap = setUpMap(45, 0, 'destMapDiv', true, destMap, '#destMapDivLatitude', '#destMapDivLongitude', 2);
		}, 10);
	});
	
	$('#addQuickReservationModal').on('shown.bs.modal', function() {
		$('#quickReserveDiv').hide();
	});
	
	$('#flightsResTable tbody').on('click', 'tr', function() {
		if(this.textContent === "No data available in table") return;
		var flightsTable = $('#flightsResTable').DataTable();
		shownFlight = flightsTable.row(this).data()[0];
		loadFlight(shownFlight);
		$("#quickReserveDiv").show();
	});
	
	$('#destinationsTable tbody').on('click', 'tr', function() {
		if(this.textContent === "No data available in table") return;
		var destTable = $('#destinationsTable').DataTable();
		var destToShow = destTable.row(this).data()[0];
		loadDestination(destToShow);
		$("#modalDestButton").attr("onclick", "editDestination('" + destToShow + "')");
		$("#destinationModalDialog").modal('show');
	});
	
	$("#destinationModalDialog").on('hidden.bs.modal', function() {
		$("#destinationName").val("");
		$("#modalDestButton").attr("onclick", "addDestination(event)");
	});
	
	$('#flightModalDialog').on('hidden.bs.modal', function() {
		resetFlightsModal();
	});
	
	$('#flightsTable tbody').on('click', 'tr', function(event) {
		if(this.textContent === "No data available in table") return;
		var flightsTable = $('#flightsTable').DataTable();
		shownFlight = flightsTable.row(this).data()[0];
		loadFlightForEdit(shownFlight);
		$("#saveSeatButton").text("Edit flight");
		$("#saveSeatButton").attr("onclick", "editFlight('" + shownFlight + "')");
		$("#flightModalDialog").modal('show');
	});
})

function setUpTables() {
	$('#destinationsTable').DataTable({
		"paging" : true,
		"info" : false,
		"scrollY" : "17vw",
		"scrollCollapse" : true,
		"retrieve" : true,
	});
	$('#flightsTable').DataTable({
		"paging" : true,
		"info" : false,
		"scrollY" : "17vw",
		"scrollCollapse" : true,
		"retrieve" : true,
	});
	$('#flightsResTable').DataTable({
		"paging" : true,
		"info" : false,
		"scrollY" : "17vw",
		"scrollCollapse" : true,
		"scrollX": true,
		"retrieve" : true,
	});
	$('#quickReservationsTable').DataTable({
		"paging" : true,
		"info" : false,
		"scrollY" : "17vw",
		"scrollCollapse" : true,
		"retrieve" : true,
	});
}

function setUpMap(latitude, longitude, div, draggable, destMap, latInput, longInput, zoom=MAP_ZOOM) {
	if (destMap != null) {
		destMap.off();
		destMap.remove();
	}
	destMap = L.map(div).setView([ latitude, longitude ], zoom);
	L.tileLayer(tileLayerURL, {
		maxZoom : MAX_MAP_ZOOM,
		id : MAP_ID
	}).addTo(destMap);
	var marker = L.marker([ latitude, longitude ], {
		draggable : draggable
	}).addTo(destMap);
	if (draggable) {
		marker.on('dragend', function(e) {
			$(latInput).val(marker.getLatLng().lat);
			$(longInput).val(marker.getLatLng().lng);
		});
	}
	return destMap
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
		type : 'PUT',
		url : editAirlineURL,
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		contentType : "application/json",
		data : JSON.stringify({
			"name" : $("#airlineName").val(),
			"description" : $("#airlineDescription").val(),
			"latitude" : $("#basicMapDivLatitude").val(),
			"longitude" : $("#basicMapDivLongitude").val(),
		}),
		dataType : "json",
		success : function(data) {
			loadAirline();
			if (data != "") {
				toastr[data.toastType](data.message);
			}
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
			var grade = data["averageGrade"];
        	
        	if(grade !== 0){
        		grade = grade/5*100;
        	}
        	var roundedGrade = Math.round(data["averageGrade"]*10)/10;
        	var rating = "<div class='star-ratings-sprite'><span style='width:" + grade 
        	+ "%' class='star-ratings-sprite-rating'></span></div><p style='color:black'>" + roundedGrade + "/5.0";
			$("#airlineGrade").html(rating);
			$("#airlineDescription").text(data["description"]);
			airlineMap = setUpMap(data["latitude"], data["longitude"], 'basicMapDiv', true, airlineMap, '#basicMapDivLatitude', '#basicMapDivLongitude');
			renderDestinations(data["destinations"]);
			renderFlights(data["flights"]);
		}
	});
}

function renderDestinations(data) {
	var table = $('#destinationsTable').DataTable();
	$.each(data, function(i, val) {
		table.row.add([ val.nameOfDest ]).draw(false);
	});
	var start = $("#startDestination");
	var end = $("#endDestination");
	var con = $("#connections");
	$.each(data, function(i, val) {
		start.append("<option value=" + val.nameOfDest + ">" + val.nameOfDest + "</option>");
		end.append("<option value=" + val.nameOfDest + ">" + val.nameOfDest + "</option>");
		con.append("<option value=" + val.nameOfDest + ">" + val.nameOfDest + "</option>");
	});
}

function renderFlights(data) {
	var table = $('#flightsTable').DataTable();
	var tableRes = $('#flightsResTable').DataTable();
	$.each(data, function(i, val) {
		var conn = "<select>";
		$.each(val.connections, function(i, val) {
			conn += "<option>" + val + "</option>";
		});
		conn += "</select>";
		var isRoundTrip = "No";
		if (val.roundTrip) {
			isRoundTrip = "Yes";
		}
		table.row.add(
				[ val.flightCode, val.startDestination, val.endDestination, val.departureTime,
					val.landingTime, isRoundTrip ]).draw(false);
		tableRes.row.add(
				[ val.flightCode, val.startDestination, val.endDestination, val.departureTime,
					val.landingTime, isRoundTrip ]).draw(false);
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
			dataType : "json",
			data : userFormToJSON(firstName, lastName, phone, address, email),
			success : function(data) {
				toastr[data.toastType](data.message);
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert("AJAX ERROR: " + textStatus);
			}
		});
	});
}

function showIncome(e) {
	e.preventDefault();
	var drp = $('#showIncomeDateRange').data('daterangepicker');
	$.ajax({
		type : 'GET',
		url : getIncomeOfAirlineURL,
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		contentType : 'application/json',
		data : {"fromDate" : drp.startDate.toDate(), "toDate" : drp.endDate.toDate()},
		success : function(data) {
			if (data != null) {
				$("#income").html("Income of airline: " + data + "EUR");
				$("#income").show();
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
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
var quickSeat = null;

function renderPlaneSeats(planeSegments, reserved, mapNum) {
	if (planeSegments[0].length == 0 && planeSegments[1].length == 0
			&& planeSegments[2].length == 0) {
		if (mapNum == 0) {
			showPlaneSeatsFirstMap([]);
		}
		else {
			showPlaneSeatsSecondMap([]);
		}
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
		firstSeatLabel = 1;
		if (mapNum == 0) {
			$('.seat-map,.seat-map *').unbind().removeData();
			showPlaneSeatsFirstMap(seats);
		}
		else {
			$('#seat-map-second,#seat-map-second *').unbind().removeData();
			showPlaneSeatsSecondMap(seats);
		}
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
	$('.seat-map,.seat-map *').unbind().removeData();
	showPlaneSeatsFirstMap(seats);
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
		$('.seat-map,.seat-map *').unbind().removeData();
		firstSeatLabel = 1;
		showPlaneSeatsFirstMap(seats);
	}

}

function deleteSeatsIndividually(e) {
	e.preventDefault();
	var invalid = false;
	if (seatsForDelete.length == 0) {
		toastr["error"]("No seats chosen.");
		return;
	}
	var numOfFirst = 0;
	var numOfBusiness = 0;
	var numOfEconomy = 0;
	$.each(seatsForDelete, function(i, val) {
		if (invalid)
			return;
		var idx = val.split('_');
		var row;
		if (idx[0] <= firstClass.length) {
			row = firstClass[idx[0] - 1].split("");
			numOfFirst++;
		} else if (idx[0] > firstClass.length
				&& idx[0] <= businessClass.length + firstClass.length) {
			row = businessClass[idx[0] - firstClass.length - 1].split("");
			numOfBusiness++;
		} else if (idx[0] > businessClass.length
				&& idx[0] <= firstClass.length + businessClass.length
						+ economyClass.length) {
			row = economyClass[idx[0] - firstClass.length
					- businessClass.length - 1].split("");
			numOfEconomy++;
		}
		if (row[idx[1] - 1] == 'l')
			invalid = true;
	});
	if (invalid) {
		toastr["error"]("Empty seats can not be deleted.");
		return;
	}
	if (numOfFirst >= numberOfSeatsPerClass[0] || numOfBusiness >= numberOfSeatsPerClass[1]
				|| numOfEconomy >= numberOfSeatsPerClass[2]) {
		toastr["error"]("You can not delete all seats in the class.");
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
	$('.seat-map,.seat-map *').unbind().removeData();
	showPlaneSeatsFirstMap(seats);
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
		if (number >= numberOfSeatsPerClass[cat - 1]) {
			toastr["error"]("Number is greather than or equals to the number of seats in class.");
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
		if (number >= numberOfSeatsPerClass[cat - 1]) {
			toastr["error"]("Number is greather than or equals to the number of seats in class.");
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
		if (number >= numberOfSeatsPerClass[cat - 1]) {
			toastr["error"]("Number is greather than or equals to the number of seats in class.");
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
	$('.seat-map,.seat-map *').unbind().removeData();
	showPlaneSeatsFirstMap(seats);
}

function showPlaneSeatsFirstMap(seats) {
	var sc = $('.seat-map').seatCharts(
			{
				map : seats,
				seats : {
					f : {
						price : 100,
						classes : 'first-class',
						category : 'First Class'
					},
					e : {
						price : 40,
						classes : 'economy-class',
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
						return 'selected';
					} else if (this.status() == 'selected') {
						let index = seatsForDelete.indexOf(this.settings.id);
						seatsForDelete.splice(index, 1);
						console.log(seatsForDelete);
						return 'available';
					} else if (this.status() == 'unavailable') {
						return 'unavailable';
					} else {
						return this.style();
					}
				}
			});
}

function showPlaneSeatsSecondMap(seats) {
	var sc = $('#seat-map-second').seatCharts(
			{
				map : seats,
				seats : {
					f : {
						price : 100,
						classes : 'first-class',
						category : 'First Class'
					},
					e : {
						price : 40,
						classes : 'economy-class',
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
					node : $('#legend-second'),
					items : [ [ 'f', 'available', 'First Class' ],
							[ 'b', 'available', 'Business Class' ],
							[ 'e', 'available', 'Economy Class' ],
							[ 'a', 'unavailable', 'Already Booked' ],
							[ 'l', 'available', 'Blank seat' ] ]
				},
				click : function() {
					if (this.settings.character == 'l')
						return;
					if (this.status() == 'available') {
						if (this.settings.character == 'a')
							return;
						if (quickSeat != null) {
							toastr["error"]("You can not choose more than one seat for quick reservation.");
							return 'available';
						}
						var seat = this.settings.id.split("_");
						if (seat[1] == 4 || seat[1] == 5) {
							quickSeat = seat[0] + "_" +  (seat[1] - 1) + "_" + this.settings.character;
						}
						else {
							quickSeat = this.settings.id + "_" + this.settings.character;
						}
						console.log(quickSeat);
						return 'selected';
					} else if (this.status() == 'selected') {
						let index = seatsForDelete.indexOf(this.settings.id);
						quickSeat = null;
						console.log(quickSeat);
						return 'available';
					} else if (this.status() == 'unavailable') {
						return 'unavailable';
					} else {
						return this.style();
					}
				}
			});
}

function saveSeatsChanges(flightCode) {
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
		data : JSON.stringify({ "savedSeats" : savedSeats, "flightCode" : flightCode }),
		success : function(data) {
			toastr[data.toastType](data.message);
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
	if (moment(departureTime).isBefore(moment(), 'day')) {
    	toastr["error"]("Departure time can not be before today's date.");
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
	if (connections.includes(startDestination) || connections.includes(endDestination)) {
		toastr["error"]("Start and end destination can not be in locations of connections");
		return;
	}
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
	var roundTrip = $("#roundTrip:checked").length > 0;
	var retDepTime;
	var retLandTime;
	if (roundTrip) {
		retDepTime = $("#retDepartureTime").val();
		if (retDepTime == null || retDepTime == "") {
			toastr["error"]("Returning departure time is not valid.")
			return;
		}
		retLandTime = $("#retLandingTime").val();
		if (retLandTime == null || retLandTime == "") {
			toastr["error"]("Returning landing time is not valid.")
			return;
		}
		if (moment(retLandTime).isBefore(retDepTime) || moment(retLandTime).isSame(retDepTime)) {
			toastr["error"]("Returning landing time must be after the returning departure time.");
			return;
		}
		if (moment(retDepTime).isBefore(landingTime) || moment(retDepTime).isSame(landingTime)) {
			toastr["error"]("Returning departure time must be after the landing time.");
			return;
		}
	}
	$.ajax({
		method : "POST",
		url : addFlightURL,
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		contentType : "application/json",
		data : flightToJSON("", startDestination, endDestination, departureTime, landingTime, flightDistance, connections, pricePerBag,
				firstPrice, businessPrice, economyPrice, roundTrip, retDepTime, retLandTime),
		success : function(data) {
				var table = $('#flightsTable').DataTable();
				var tableRes = $('#flightsResTable').DataTable();
				var conn = "<select>";
				$.each(connections, function(i, val) {
					conn += "<option>" + val + "</option>";
				});
				conn += "</select>";
				var isRoundTrip = "No";
				if (roundTrip) {
					isRoundTrip = "Yes";
				}
				table.row.add(
						[ data, startDestination, endDestination, moment(new Date(departureTime)).format("DD.MM.YYYY HH:mm"),
							moment(new Date(landingTime)).format("DD.MM.YYYY HH:mm"), isRoundTrip ]).draw(false);
				tableRes.row.add(
						[ data, startDestination, endDestination, moment(new Date(departureTime)).format("DD.MM.YYYY HH:mm"),
							moment(new Date(landingTime)).format("DD.MM.YYYY HH:mm"), isRoundTrip ]).draw(false);
				saveSeatsChanges(data);
				$("#flightModalDialog").modal("hide");
		}
	});
}

function loadDestination(dest) {
	$.ajax({
		type : 'GET',
		url : loadDestinationURL,
		contentType : "application/json",
		data : {
			'dest' : dest
		},
		success : function(data) {
			if (data != null) {
				$("#destinationName").val(data["nameOfDest"]);
				setTimeout(function() {
					destMap = setUpMap(data["latitude"], data["longitude"] , 'destMapDiv', true, destMap, '#destMapDivLatitude', '#destMapDivLongitude', 2);
				}, 500);
			}
		}
	});
}

function editFlight(code) {
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
	var roundTrip = $("#roundTrip:checked").length > 0;
	var retDepTime;
	var retLandTime;
	if (roundTrip) {
		retDepTime = $("#retDepartureTime").val();
		if (retDepTime == null || retDepTime == "") {
			toastr["error"]("Returning departure time is not valid.")
			return;
		}
		retLandTime = $("#retLandingTime").val();
		if (retLandTime == null || retLandTime == "") {
			toastr["error"]("Returning landing time is not valid.")
			return;
		}
		if (moment(retLandTime).isBefore(retDepTime) || moment(retLandTime).isSame(retDepTime)) {
			toastr["error"]("Returning landing time must be after the returning departure time.");
			return;
		}
		if (moment(retDepTime).isBefore(landingTime) || moment(retDepTime).isSame(landingTime)) {
			toastr["error"]("Returning departure time must be after the landing time.");
			return;
		}
	}
	$.ajax({
		method : "PUT",
		url : editFlightURL,
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		contentType : "application/json",
		data : flightToJSON(code, startDestination, endDestination, departureTime, landingTime, flightDistance, connections, pricePerBag,
				firstPrice, businessPrice, economyPrice, roundTrip, retDepTime, retLandTime),
		success : function(data) {
			if (data.toastType == "success") {
				saveSeatsChanges(code);
				$("#flightModalDialog").modal("hide");
				getFlights();
			}
			toastr[data.toastType](data.message);
		}
	});
}

function getFlights() {
	$.ajax({
		method : "GET",
		url : getFlightsURL,
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		success : function(data) {
			var table = $('#flightsTable').DataTable();
			var tableRes = $('#flightsResTable').DataTable();
			table.clear().draw();
			tableRes.clear().draw();
			renderFlights(data);
		}
	});
}

function flightToJSON(code, startDestination, endDestination, departureTime, landingTime, flightDistance, connections, pricePerBag,
		firstPrice, businessPrice, economyPrice, roundTrip, retDepTime, retLandTime) {
	var a = JSON.stringify({
		"flightCode" : code,
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
		"airlineName" : airlineName,
		"roundTrip" : roundTrip,
		"returningDepartureTime" : retDepTime,
		"returningLandingTime" : retLandTime
	});
	return a;
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
				$("#destinationModalDialog").modal("hide");
			}
			else {
				toastr["error"](data.message);
			}
		}
	});
}

function editDestination(dest) {
	var nameOfDest = $("#destinationName").val();
	if (nameOfDest == null || nameOfDest == "") {
		toastr["error"]("Invalid name of destination.");
		return;
	}
	var latitude = $("#destMapDivLatitude").val();
	var longitude = $("#destMapDivLongitude").val();
	$.ajax({
		method : "PUT",
		url : editDestinationURL,
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		contentType : "application/json",
		data : JSON.stringify({
			"nameOfDest": nameOfDest,
			"latitude": latitude,
			"longitude": longitude,
			"airlineName": airlineName,
			"oldName": dest
		}),
		success : function(data) {
			if (data.toastType == "success") {
				getDestinationsOfAirline();
				toastr[data.toastType](data.message);
				$("#destinationModalDialog").modal("hide");
			}
			else {
				toastr[data.toastType](data.message);
			}
		}
	});
}

function getDestinationsOfAirline() {
	$.ajax({
		method : "GET",
		url : getDestinationsOfAirlineURL,
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		contentType : "application/json",
		success : function(data) {
			if (data != null) {
				$("#startDestination").find("option").remove();
				$("#endDestination").find("option").remove();
				$("#connections").find("option").remove();
				var table = $("#destinationsTable").DataTable();
				table.clear().draw();
				$.each(data, function(i, val) {
					table.row.add([ val.nameOfDest ]).draw(false);
				});
				var start = $("#startDestination");
				var end = $("#endDestination");
				var con = $("#connections");
				$.each(data, function(i, val) {
					start.append("<option value=" + val.nameOfDest + ">" + val.nameOfDest + "</option>");
					end.append("<option value=" + val.nameOfDest + ">" + val.nameOfDest + "</option>");
					con.append("<option value=" + val.nameOfDest + ">" + val.nameOfDest + "</option>");
				});
			}
		}
	});
}

function dayComparator(a, b) {
	let aTokens = a.split("/");
	let bTokens = b.split("/");
	
	let aYear = parseInt(aTokens[2]);
	let bYear = parseInt(bTokens[2]);
	let aMonth = parseInt(aTokens[1]);
	let bMonth = parseInt(bTokens[1]);
	let aDay = parseInt(aTokens[0]);
	let bDay = parseInt(bTokens[0]);
	
	if(aYear > bYear) {
		return 1;
	} else if(aYear < bYear) {
		return -1;
	} else {
		if(aMonth > bMonth) {
			return 1;
		} else if(aMonth < bMonth) {
			return -1;
		} else {
			if(aDay > bDay) {
				return 1;
			} else if(aDay < bDay) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}

function weekComparator(a, b) {
	// date week: num
	let aTokens = a.split(" ");
	let aDate = aTokens[0].split("/");
	
	let aWeek = aTokens[2];
	let aMonth = parseInt(aDate[0]);
	let aYear = parseInt(aDate[1]);
	
	let bTokens = b.split(" ");
	let bDate = bTokens[0].split("/");
	
	let bWeek = bTokens[2];
	let bMonth = parseInt(bDate[0]);
	let bYear = parseInt(bDate[1]);
	
	if(aYear > bYear) {
		return 1;
	} else if(aYear < bYear) {
		return -1;
	} else {
		if(aMonth > bMonth) {
			return 1;
		} else if(aMonth < bMonth) {
			return -1;
		} else {
			if(aWeek > bWeek) {
				return 1;
			} else if(aWeek < bWeek) {
				return -1;
			} else {
				return 0;
			}
		}
	}
}

function monthComparator(a, b) {
	let aDate = a.split("/");
	let aMonth = parseInt(aDate[0]);
	let aYear = parseInt(aDate[1]);
	
	let bDate = b.split("/");
	let bMonth = parseInt(bDate[0]);
	let bYear = parseInt(bDate[1]);
	
	if(aYear > bYear) {
		return 1;
	} else if(aYear < bYear) {
		return -1;
	} else {
		if(aMonth > bMonth) {
			return 1;
		} else if(aMonth < bMonth) {
			return -1
		} else {
			return 0;
		}
	}
}

function getDailyChartData() {
	$.ajax({
		type : 'GET',
		url : loadDailyChartDataURL,
		dataType : "json",
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			makeDailyChart(data, dayComparator);
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function getWeeklyChartData() {
	$.ajax({
		type : 'GET',
		url : loadWeeklyChartDataURL,
		dataType : "json",
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			makeWeeklyChart(data, weekComparator);
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function getMonthlyChartData() {
	$.ajax({
		type : 'GET',
		url : loadMonthlyChartDataURL,
		dataType : "json",
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			makeMonthlyChart(data, monthComparator);
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function makeDailyChart(data, comparator) {
	let labels = (Object.keys(data)).sort(comparator);
	let values = [];
	
	for(let label of labels) {
		values.push(data[label]);
	}
	
	var timeFormat = "DD/MM/YYYY";
	var startDate = moment(moment(labels[0], timeFormat).subtract('days', 3)).format(timeFormat);
	var endDate = moment(moment(labels[labels.length - 1], timeFormat).add('days', 3)).format(timeFormat);
	
	let chart = new Chart($('#chart'), {
	    type: 'bar',
	    data: {
	        labels: labels,
	        datasets: [{
	            label: 'Number of reservations',
	            backgroundColor: 'rgb(255, 99, 132)',
	            borderColor: 'rgb(255, 99, 132)',
	            data: values
	        }]
	    },
	    options: {
	    	responsive:true,
	        scales: {
	        	xAxes: [{
                    type: "time",
                    time: {
                    	format: timeFormat,
                        unit: 'day',                    
                        unitStepSize: 1,
                        minUnit: 'day',
                        min: startDate,
                        max: endDate,
                        tooltipFormat: 'll'
                    },
                    scaleLabel: {
                        display:     true,
                        labelString: 'Date'
                    },
                    ticks: {
                    	autoSkip: true,
                        maxTicksLimit: 30
                    }
                }],
                yAxes: [{
                    scaleLabel: {
                        display:     true,
                        labelString: 'value'
                    },
                    ticks: {
	                	beginAtZero: true
	                }
                }]
	        },
	        pan: {
	            enabled: true,
	            mode: 'x'
	        },
	        zoom: {
	        	enabled: true,
	        	mode: 'x'
	        }
	    }
	});
}

function makeWeeklyChart(data, comparator) {
	let labels = (Object.keys(data)).sort(comparator);
	let values = [];
	
	for(let label of labels) {
		values.push(data[label]);
	}
	
	let chart = new Chart($('#chart'), {
	    type: 'bar',
	    data: {
	        labels: labels,
	        datasets: [{
	            label: 'Number of reservations',
	            backgroundColor: 'rgb(255, 99, 132)',
	            borderColor: 'rgb(255, 99, 132)',
	            data: values
	        }]
	    },
	    options: {
	        scales: {
	            yAxes: [{
	                ticks: {
	                	beginAtZero: true
	                }
	            }]
	        },
	        pan: {
	            enabled: true,
	            mode: 'x'
	        },
	        zoom: {
	        	enabled: true,
	        	mode: 'x'
	        }
	    }
	});
}

function makeMonthlyChart(data, comparator) {
	let labels = (Object.keys(data)).sort(comparator);
	let values = [];
	
	for(let label of labels) {
		values.push(data[label]);
	}
	
	var timeFormat = "MM/YYYY";
	var startDate = moment(moment(labels[0], timeFormat).subtract(1, 'months')).format(timeFormat);
	var endDate = moment(moment(labels[labels.length - 1], timeFormat).add(1, 'months')).format(timeFormat);
	
	let chart = new Chart($('#chart'), {
	    type: 'bar',
	    data: {
	        labels: labels,
	        datasets: [{
	            label: 'Number of reservations',
	            backgroundColor: 'rgb(255, 99, 132)',
	            borderColor: 'rgb(255, 99, 132)',
	            data: values
	        }]
	    },
	    options: {
	    	responsive:true,
	        scales: {
	        	xAxes: [{
                    type: "time",
                    time: {
                        format: timeFormat,
                        unit: 'month',                    
                        minUnit: 'month',
                        min: startDate,
                        max: endDate,
                        tooltipFormat: 'll'
                    },
                    scaleLabel: {
                        display:     true,
                        labelString: 'Date'
                    },
                    ticks: {
                    	autoSkip: true,
                        maxTicksLimit: 30
                    }
                }],
                yAxes: [{
                    scaleLabel: {
                        display:     true,
                        labelString: 'value'
                    },
                    ticks: {
	                	beginAtZero: true
	                }
                }]
	        },
	        pan: {
	            enabled: true,
	            mode: 'x'
	        },
	        zoom: {
	        	enabled: true,
	        	mode: 'x'
	        }
	    }
	});
}

function changeGraphic(level) {
	$('#chart').remove();
	$('#chartDiv').append('<canvas id="chart"><canvas>');
	if (level == "daily") {
		getDailyChartData();
	}
	else if (level == "weekly") {
		getWeeklyChartData();
	}
	else {
		getMonthlyChartData();
	}
}

function loadFlightForEdit(code) {
	$.ajax({
		type : 'GET',
		url : getDetailedFlightURL,
		contentType : "application/json",
		data : {
			'flightCode' : code
		},
		dataType : "json",
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		success : function(data) {
			if (data != null) {
				$("#startDestination").val(data["startDestination"]);
				$("#endDestination").val(data["endDestination"]);
				var d = data["departureTime"].split(" ");
				$("#departureTime").val(d[0].substring(6, 10) + "-" + d[0].substring(3, 5) + "-" +
							d[0].substring(0, 2) + "T" + d[1]);
				d = data["landingTime"].split(" ");
				$("#landingTime").val(d[0].substring(6, 10) + "-" + d[0].substring(3, 5) + "-" +
							d[0].substring(0, 2) + "T" + d[1]);
				$("#flightDistance").val(data["flightDistance"]);
				$("#pricePerBag").val(data["pricePerBag"]);
				$.each(data["connections"], function(i, val){
				    $("#connections option[value='" + val + "']").prop("selected", true);
				});
				$("#firstPrice").val(data["firstClassPrice"]);
				$("#businessPrice").val(data["businessClassPrice"]);
				$("#economyPrice").val(data["economyClassPrice"]);
				if (data["roundTrip"]) {
					$("#roundTrip").prop('checked', true);
					d = data["returningDepartureTime"].split(" ");
					$("#retDepartureTime").val(d[0].substring(6, 10) + "-" + d[0].substring(3, 5) + "-" +
								d[0].substring(0, 2) + "T" + d[1]);
					d = data["returningLandingTime"].split(" ");
					$("#retLandingTime").val(d[0].substring(6, 10) + "-" + d[0].substring(3, 5) + "-" +
								d[0].substring(0, 2) + "T" + d[1]);
					$(".retTrip").show();
				}
				getPlaneSeats(code, 0);
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
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		success : function(data) {
			if (data != null) {
				localStorage.setItem("flightCode", code);
				$("#startDest").text(data["startDestination"]);
				$("#endDest").text(data["endDestination"]);
				$("#depTime").text(data["departureTime"]);
				$("#landTime").text(data["landingTime"]);
				$("#flightAirline").text(data["airlineName"]);
				var date1 = moment(data["departureTime"], 'DD.MM.YYYY hh:mm');
				var date2 = moment(data["landingTime"], 'DD.MM.YYYY hh:mm');
				var diff = date2.diff(date1, 'minutes');
				$("#flightDuration").text(diff);
				$("#flightResDistance").text(data["flightDistance"]);
				$("#flightResConnections").find("option").remove();
				var conn = $("#flightResConnections");
				if (data["connections"].length == 0) {
					conn.append("<option value=''></option>");
				}
				else {
					$.each(data["connections"], function(i, val) {
						conn.append("<option value=" + val + ">" + val
								+ "</option>");
					});
				}
				$("#resPricePerBag").text(data["pricePerBag"]);
				var grade = data["averageGrade"];
	        	
	        	if(grade !== 0){
	        		grade = grade/5*100;
	        	}
	        	var roundedGrade = Math.round(data["averageGrade"]*10)/10;
	        	var rating = "<div class='star-ratings-sprite'><span style='width:" + grade 
	        	+ "%' class='star-ratings-sprite-rating'></span></div><p>" + roundedGrade + "/5.0";
				$("#resAverageGrade").html(rating);
				
				getPlaneSeats(code, 1);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function getPlaneSeats(code, mapNum) {
	$.ajax({
		url : getPlaneSeatsURL,
		contentType : "application/json",
		data : {
			'flightCode' : code
		},
		dataType : "json",
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		success : function(data) {
			firstPrice = data["firstClassPrice"];
			businessPrice = data["businessClassPrice"];
			economyPrice = data["economyClassPrice"];
			resetReservationModal();
			renderPlaneSeats(data["planeSegments"], data["reservedSeats"], mapNum);
		}
	});
}

function resetReservationModal() {
	firstClass = [];
	businessClass = [];
	economyClass = [];
	firstSeatLabel = 1;
	reservedSeats = [];
}

function resetFlightsModal() {
	$("#startDestination option:selected").prop("selected", false);
	$("#endDestination option:selected").prop("selected", false);
	$("#departureTime").val("");
	$("#landingTime").val("");
	$("#retDepartureTime").val("");
	$("#retLandingTime").val("");
	$(".retTrip").hide();
	$("#roundTrip").prop('checked', false);
	$("#flightDistance").val(0);
	$("#pricePerBag").val(0);
	$("#firstPrice").val(0);
	$("#businessPrice").val(0);
	$("#economyPrice").val(0);
	$("#connections option:selected").prop("selected", false);
	$("#first").val(0);
	$("#business").val(0);
	$("#economy").val(0);
	$('.seatCharts-row').remove();
	$('.seatCharts-legendItem').remove();
	$('.seat-map,.seat-map *').unbind().removeData();
	$("#saveSeatButton").text("Add flight");
	$("#saveSeatButton").attr("onclick", "addFlight(event)");
	firstClass = [];
	businessClass = [];
	economyClass = [];
	firstSeatLabel = 1;
	reservedSeats = [];
}

function createQuickReservation(e) {
	e.preventDefault();
	var discount = $("#discount").val();
	if (isNaN(discount) || discount == 0) {
		toastr["error"]("Invalid discount.");
		return;
	}
	if (quickSeat == null) {
		toastr["error"]("Seat is not chosen.");
		return;
	}
	var flightCode = localStorage.getItem("flightCode");
	if (flightCode === null) {
		toastr["error"]("Flight is not chosen.");
		return;
	}
	$.ajax({
		method : 'POST',
		url : createQuickFlightReservationURL,
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		contentType : "application/json",
		data : JSON.stringify ({
			"flightCode" : flightCode,
			"seat" : quickSeat,
			"discount" : discount
		}),
		success : function(data) {
			if (data != null) {
				if (data.toastType == "success") {
					localStorage.removeItem("flightCode");
					quickSeat = null;
					getQuickReservations();
					$("#addQuickReservationModal").modal("hide");
				}
				toastr[data.toastType](data.message);
			}
		}
	});
}

function getQuickReservations() {
	$.ajax({
		method : 'GET',
		url : getQuickFlightReservationsURL,
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		success : function(data) {
			if (data != null) {
				var table = $("#quickReservationsTable").DataTable();
				table.clear().draw();
				$.each(data, function(i, val) {
					table.row.add([ val.flightCode, val.seat, val.seatClass, val.discount, val.discountedPrice ]).draw(false);
				});
			}
		}
	});
}