const tokenKey = "jwtToken";

const tileLayerURL = "https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw";
const MAP_ZOOM = 12;
const MAX_MAP_ZOOM = 19;
const MAP_ID = 'mapbox.streets';

const editHotelURL = "/api/editHotel";
const getAdditionalServicesURL = "/api/getAdditionalServicesURL";
const getRoomsURL = "/api/getRooms";
const getHotelOfAdminURL = "/api/getHotelOfAdmin";
const getHotelRoomURL = "/api/getHotelRoom";
const addHotelRoomURL = "/api/addHotelRoom";
const deleteHotelRoomURL = "/api/deleteHotelRoom/";
const addSeasonalPriceURL = "/api/addSeasonalPrice/"
const deleteSeasonalPriceURL = "/api/deleteSeasonalPrice/"
	
const logoutURL = "../logout";
const loadUserInfoURL = "../api/getUserInfo";
const editUserInfoURL = "../api/editUser";
const changePasswordURL = "../changePassword";

var destMap = null;
var shownRoom = null;

$(document).ready(function() {
	setUpToastr();
	setUpTabView();
	setUpTables();
	loadData();

	setUpNewHotelRoomForm();
	setUpNewSeasonalPriceForm();
	setUpEditForm();

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

	$('#showHotelRoomModal').on('hidden.bs.modal', function() {
		roomsTable.$('tr.selected').removeClass('selected');
		shownRoom = null;
	});
})

function loadData() {
	loadHotel();
	loadProfileData();
}

function setUpTables() {
	roomsTable = $('#roomsTable').DataTable({
		"paging" : false,
		"info" : false,
	});
	$('#additionalServicesTable').DataTable({
		"paging" : false,
		"info" : false,
	});
	$('#quickReservationsTable').DataTable({
		"paging" : false,
		"info" : false,
	});
	seasonalPricesTable = $('#seasonalPricesTable').DataTable({
		"paging" : false,
		"info" : false,
		"columnDefs": [
			{ "orderable": false, "targets": 3 }
		]
	});

	$('#roomsTable tbody').on('click', 'tr', function() {
		roomsTable.$('tr.selected').removeClass('selected');
		$(this).addClass('selected');
		loadHotelRoom(roomsTable.row(this).data()[0]);
		shownRoom = roomsTable.row(this).data()[0];
		$("#showHotelRoomModal").modal();
	});

}

function setUpTabView() {
	$(".nav li").click(function() {
		$(this).addClass("active");
		$(this).siblings().removeClass("active");
	});
	$('.nav-tabs a').click(function() {
		$(this).tab('show');
	});
	$(
			'a[href="#basicTab"], a[href="#roomsTab"], a[href="#additionalServicesTab"], a[href="#quickResevationsTab"]')
			.click(function() {
				loadHotel();
			});
	$('a[href="#profile"]').click(function() {
		loadProfileData();
	});
	$('a[href="#basicTab"]').on('shown.bs.tab', function() {
		if (destMap != null)
			destMap.invalidateSize();
	})
}

function setUpMap(latitude, longitude, div) {
	if (destMap != null) {
		destMap.off();
		destMap.remove();
	}
	destMap = L.map(div).setView([ latitude, longitude ], MAP_ZOOM);
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
		type : "GET",
		url : getHotelOfAdminURL,
		headers : createAuthorizationTokenHeader(tokenKey),
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

function loadHotelRoom(roomNumber) {
	$.ajax({
		type : 'GET',
		contentType : "application/json",
		data : {
			'roomNumber' : roomNumber
		},
		dataType : "json",
		url : getHotelRoomURL,
		headers : createAuthorizationTokenHeader(tokenKey),
		success : function(data) {
			$("#shownRoomNumber").val(data["roomNumber"]);
			$("#shownRoomAverageGrade").val(data["averageGrade"]);
			$("#shownRoomDefaultPrice").val(data["defaultPrice"]);
			$("#shownRoomNumberOfPeople").val(data["numberOfPeople"]);
			renderSeasonalPrices(data["seasonalPrices"]);
		}
	});

}

function renderAdditionalServices(data) {
	var table = $('#additionalServicesTable').DataTable();
	table.clear().draw();
	$.each(data, function(i, val) {
		table.row.add([ val.name, val.price ]).draw(false);
	});
}

function renderRooms(data) {
	roomsTable.clear().draw();
	$.each(data, function(i, val) {
		roomsTable.row.add(
				[ val.roomNumber, val.price, val.numberOfPeople,
						val.averageGrade ]).draw(false);
	});
}

function renderSeasonalPrices(data) {
	seasonalPricesTable.clear().draw();
	$.each(data, function(i, val) {
		seasonalPricesTable.row.add([ val.price, val.fromDate, val.toDate,
			`<a href="javascript:deleteSeasonalPrice('${val.fromDate}', '${val.toDate}')">Delete</a>` ])
				.draw(false);
	});

}

function deleteSeasonalPrice(fromDate, toDate){
	fromDate = new Date(fromDate);
	toDate = new Date(toDate);
	$.ajax({
		type : 'DELETE',
		url : deleteSeasonalPriceURL + shownRoom,
		contentType : 'application/json',
		headers : createAuthorizationTokenHeader(tokenKey),
		dataType : "json",
		data : JSON.stringify({
			"fromDate":fromDate,
			"toDate":toDate
		}),
		success : function(data) {
			loadHotel();
			loadHotelRoom(shownRoom);
			if (data != "") {
				toastr[data.toastType](data.message);
			}
			
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

/* USER PROFILE JS */

function loadProfileData() {
	let token = getJwtToken("jwtToken");
	$.ajax({
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

function userEditFormToJSON(firstName, lastName, phone, address, email) {
	return JSON.stringify({
		"firstName" : firstName,
		"lastName" : lastName,
		"phoneNumber" : phone,
		"address" : address,
		"email" : email
	});
}

function setUpNewHotelRoomForm() {
	$('#newHotelRoomForm').on('submit', function(e) {
		e.preventDefault();
		$.ajax({
			type : 'POST',
			url : addHotelRoomURL,
			contentType : 'application/json',
			headers : createAuthorizationTokenHeader(tokenKey),
			dataType : "json",
			data : JSON.stringify({
				"roomNumber" : $("#newRoomNumber").val(),
				"price" : $("#newRoomDefaultPrice").val(),
				"numberOfPeople" : $("#newRoomNumberOfPeople").val(),
			}),
			success : function(data) {
				loadHotel();
				if (data != "") {
					toastr[data.toastType](data.message);
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert("AJAX ERROR: " + textStatus);
			}
		});
	});

}

function setUpNewSeasonalPriceForm(){
	$('#newSeasonalPriceForm').on('submit', function(e) {
		e.preventDefault();
		$.ajax({
			type : 'POST',
			url : addSeasonalPriceURL + shownRoom,
			contentType : 'application/json',
			headers : createAuthorizationTokenHeader(tokenKey),
			dataType : "json",
			data : JSON.stringify({
				"price" : $("#newSeasonalPricePrice").val(),
				"fromDate" : $("#newSeasonalPriceFrom").val(),
				"toDate" : $("#newSeasonalPriceTo").val(),
			}),
			success : function(data) {
				loadHotel();
				loadHotelRoom(shownRoom);
				if (data != "") {
					toastr[data.toastType](data.message);
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert("AJAX ERROR: " + textStatus);
			}
		});
	});

}

function setUpEditForm() {
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
					url : editUserInfoURL,
					contentType : 'application/json',
					headers : createAuthorizationTokenHeader(tokenKey),
					dataType : "json",
					data : userEditFormToJSON(firstName, lastName, phone,
							address, email),
					success : function(data) {
						if (data != "") {
							toastr[data.toastType](data.message);
						}
					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						alert("AJAX ERROR: " + textStatus);
					}
				});
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

function deleteRoom() {
	$.ajax({
		type : 'DELETE',
		dataType : "json",
		url : deleteHotelRoomURL + shownRoom,
		headers : createAuthorizationTokenHeader(tokenKey),
		success : function(data) {
			loadHotel();
			if (data != "") {
				toastr[data.toastType](data.message);
			}
		}
	});

}
