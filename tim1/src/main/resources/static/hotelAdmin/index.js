const TOKEN_KEY = 'jwtToken';

const tileLayerURL = "https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw";
const MAP_ZOOM = 12;
const MAX_MAP_ZOOM = 19;
const MAP_ID = 'mapbox.streets';

const editHotelURL = "/api/editHotel";
const getAdditionalServicesURL = "/api/getAdditionalServicesURL";
const getRoomsURL = "/api/getRooms";
const getHotelOfAdminURL = "/api/getHotelOfAdmin";

const logoutURL = "../logout";
const loadUserInfoURL = "../api/getUserInfo";
const editUserInfoURL = "../api/editUser";
const changePasswordURL = "../changePassword";

$(document).ready(function() {
	loadHotel();
	loadProfileData();
	setUpTables();

	userEditFormSetUp();
	$('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
		$($.fn.dataTable.tables(true)).DataTable().columns.adjust();
	});

	$("#logout").click(function() {
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
	$('#roomsTable').DataTable({
		"paging" : false,
		"info" : false,
		"scrollY" : "17vw",
		"scrollCollapse" : true,
		"retrieve" : true,
	});
	$('#additionalServices').DataTable({
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
		$("#latitude").val(marker.getLatLng().lat);
		$("#longitude").val(marker.getLatLng().lng);
	});
}

function editHotel(e) {
	e.preventDefault();
	$.ajax({
		type : 'POST',
		url : editHotelURL,
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

function loadHotel() {
	$.ajax({
		dataType : "json",
		url : getHotelOfAdminURL,
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		success : function(data) {
			$("#hotelName").val(data["name"]);
			$("#hotelGrade").text(data["averageGrade"]);
			$("#hotelDescription").text(data["description"]);
			setUpMap(data["latitude"], data["longitude"], 'basicMapDiv');
			renderAdditionalServices(data["additionalServices"]);
			renderRooms(data["rooms"]);
			// renderQuickReservations[data["quickReservations"]];
		}
	});
}

function renderAdditionalServices(data) {
	var table = $('#destinationsTable').DataTable();
	$.each(data, function(i, val) {
		table.row.add([ val.name, val.price ]).draw(false);
	});
}

function renderFlights(data) {
	var table = $('#flightsTable').DataTable();
	$.each(data, function(i, val) {
		table.row.add(
				[ val.roomNumber, val.defaultPriceOneNight, val.numberOfPeople,
						val.averageGrade ]).draw(false);
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

$(document).on("click", "#changePasswordButton", function(e) {
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
					toastr["error"](data);
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert("AJAX ERROR: " + textStatus);
			}
		});
	});
}
