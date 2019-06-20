const logoutURL = "../logout";
const loadUserInfoURL = "../api/getUserInfo";
const saveChangesURL = "../api/editUser";
const getAirlinesURL = "../api/getAirlines";
const getHotelsURL = "../api/getHotels";
const getRentACarsURL = "../api/getRentACars";
const getAirlineURL = "../api/getAirline";
const getHotelURL = "../api/getHotel";
const getRentACarURL = "../api/getRentACar";
const registerAdminURL = "../auth/registerAdmin/";
const addAirlineURL = "../api/addAirline";
const addHotelURL = "../api/addHotel";
const addRentACarURL = "../api/addRentACar";
const getDiscountInfoURL = "../api/getDiscountInfo"
const editDiscountInfoURL = "../api/editDiscountInfo"

const tokenKey = "jwtToken";

const tileLayerURL = "https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw";
const MAP_ZOOM = 8;
const MAX_MAP_ZOOM = 19;
const MAP_ID = 'mapbox.streets';

const NEW_AIRLINE = 1;
const NEW_HOTEL = 2;
const NEW_RENTACAR = 3;
var CURRENT_ADD = NEW_AIRLINE;

var currentService = "";
var currentServiceURL = "";

var destMap = null;

$(document).ready(function() {
	setUpToastr();
	setUpTabView();
	setUpTables();
	loadData();
	
	
	$("#logout").click(function() {
		removeJwtToken(tokenKey);
		document.location.href = logoutURL;
	});
	
	$('.edit').click(function() {
		if ($(this).siblings().first().is('[readonly]')) {
			$(this).siblings().first().removeAttr('readonly');
		} else {
			$(this).siblings().first().prop('readonly', 'true');
		}
	});

	setUpEditForm();
	setUpAddServiceForm();
	setUpRegistrationForm();
	setUpEditDiscountInfoForm();

	$('#modalDialog').on('hidden.bs.modal', function() {
		airlinesTable.$('tr.selected').removeClass('selected');
		hotelsTable.$('tr.selected').removeClass('selected');
		rentACarsTable.$('tr.selected').removeClass('selected');
		destMap.off();
		destMap.remove();
		destMap = null;
		adminsTable.clear().draw();
	});

	$('#modalDialog').on('shown.bs.modal', function() {
		setTimeout(function() {
			destMap.invalidateSize()
		}, 10);
		setTimeout(function() {
			destMap.invalidateSize()
		}, 1000);
	});

	$('#addServiceModal').on('hidden.bs.modal', function(){
		$('#addServiceForm').trigger('reset');
	});

	newServiceMap = setUpMap(45, 0, 'newServiceMapDiv', true);
	$('#addServiceModal').on('shown.bs.modal', function() {
		setTimeout(function() {
			newServiceMap.invalidateSize()
		}, 10);
	});

});

function loadData() {
	loadProfile();
	loadServices(getAirlinesURL, "#airlinesTable");
	loadServices(getHotelsURL, "#hotelsTable");
	loadServices(getRentACarsURL, "#rentACarsTable");
	loadDiscountInfo();
}

function loadDiscountInfo(){
	$.ajax({
		type : 'GET',
		url : getDiscountInfoURL,
		headers: createAuthorizationTokenHeader(tokenKey),
		contentType : 'application/json',
		dataType : "json",
		headers : createAuthorizationTokenHeader(tokenKey),
		success : function(data) {
			if (data != null) {
				$('#discountPercentagePerPoint').val(data.discountPercentagePerPoint);
				$('#kmsNeededForPoint').val(data.kmsNeededForPoint);
				$('#maxDiscountPoints').val(data.maxDiscountPoints);
				$('#discountPerExtraReservation').val(data.discountPerExtraReservation);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function loadProfile() {
	$.ajax({
		type : 'GET',
		url : loadUserInfoURL,
		contentType : 'application/json',
		headers: createAuthorizationTokenHeader(tokenKey),
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

function setUpTabView(){
	$(".nav li").click(function() {
		$(this).addClass("active");
		$(this).siblings().removeClass("active");
	});
	$('.nav-tabs a').click(function() {
		$(this).tab('show');
	});
	$('a[href="#airlines"]').click(function(){
		loadServices(getAirlinesURL, "#airlinesTable");
	});
	$('a[href="#hotels"]').click(function(){
		loadServices(getHotelsURL, "#hotelsTable");
	});
	$('a[href="#rentACars"]').click(function(){
		loadServices(getRentACarsURL, "#rentACarsTable");
	});
	$('a[href="#discounts"]').click(function(){
		loadDiscountInfo();
	});
	$('a[href="#profile"]').click(function(){
		loadProfile();
	});
}

function setUpTables(){
	airlinesTable = $('#airlinesTable').DataTable({
		"paging" : true,
		"info" : false,
	});

	hotelsTable = $('#hotelsTable').DataTable({
		"paging" : true,
		"info" : false,
	});

	rentACarsTable = $('#rentACarsTable').DataTable({
		"paging" : true,
		"info" : false,
	});
	
	
	adminsTable = $("#adminsTable").DataTable({
		"paging" : true,
		"info" : false
	});


	$('#airlinesTable tbody').on('click', 'tr', function() {
		airlinesTable.$('tr.selected').removeClass('selected');
		$(this).addClass('selected');
		currentService = airlinesTable.row(this).data()[0];
		currentServiceURL = getAirlineURL;
		loadService(currentService, currentServiceURL);
		$("#modalDialog").modal("show");
	});

	$('#hotelsTable tbody').on('click', 'tr', function() {
		hotelsTable.$('tr.selected').removeClass('selected');
		$(this).addClass('selected');
		currentService = hotelsTable.row(this).data()[0];
		currentServiceURL = getHotelURL;
		loadService(currentService, currentServiceURL);
		$("#modalDialog").modal();
	});

	$('#rentACarsTable tbody').on('click', 'tr', function() {
		rentACarsTable.$('tr.selected').removeClass('selected');
		$(this).addClass('selected');
		currentService = rentACarsTable.row(this).data()[0];
		currentServiceURL = getRentACarURL;
		loadService(currentService, currentServiceURL);
		$("#modalDialog").modal();
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
					url : saveChangesURL,
					contentType : 'application/json',
					headers: createAuthorizationTokenHeader(tokenKey),
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

function setUpAddServiceForm() {
	$('#addServiceForm').on(
			'submit',
			function(e) {
				e.preventDefault();
				let name = $('#newServiceName').val();
				let description = $('#newServiceDescription').val();
				let lat = $('#latitude').val();
				let long = $('#longitude').val();
				let url = "";
				switch (CURRENT_ADD) {
				case NEW_AIRLINE:
					url = addAirlineURL;
					break;
				case NEW_HOTEL:
					url = addHotelURL;
					break;
				case NEW_RENTACAR:
					url = addRentACarURL;
					break;
				}
				
				$.ajax({
					type : 'POST',
					url : url,
					contentType : 'application/json',
					headers: createAuthorizationTokenHeader(tokenKey),
					dataType : 'json',
					data : JSON.stringify({"name":name, "description":description, "latitude":lat, "longitude":long}),
					success : function(data) {
						loadData();
						if (data != null) {
							toastr[data.toastType](data.message);
						}
					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						alert("AJAX ERROR: " + textStatus);
					}
				});
			});
}

function setUpRegistrationForm(){
	$(document).on(
			'submit',
			'#registrationForm',
			function(e) {
				e.preventDefault();
				var email = $('#adminEmail').val();
				var password = $('#adminPassword').val();
				var firstName = $('#adminFirstName').val();
				var lastName = $('#adminLastName').val();
				var phone = $('#adminPhone').val();
				var address = $('#adminAddress').val();

				$.ajax({
					type : 'POST',
					url : registerAdminURL + $('#serviceName').text(),
					contentType : 'application/json',
					headers: createAuthorizationTokenHeader(tokenKey),
					dataType : "json",
					data : registerAdminFormToJSON(email, password, firstName,
							lastName, phone, address),
					success : function(data) {
						if (data != null) {
							loadData();
							loadService(currentService, currentServiceURL);
							toastr[data.toastType](data.message);
							if (data.toastType=="success") {
								$('#adminEmail').val("");
								$('#adminPassword').val("");
								$('#adminFirstName').val("");
								$('#adminLastName').val("");
								$('#adminPhone').val("");
								$('#adminAddress').val("");
							}
						}
					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						alert("AJAX ERROR: " + textStatus);
					}
				})
			})
}

function setUpEditDiscountInfoForm(){
	$('#editDiscountInfoForm').on(
			'submit',
			function(e) {
				e.preventDefault();
				let discountPercentagePerPoint = $('#discountPercentagePerPoint').val();
				let kmsNeededForPoint = $('#kmsNeededForPoint').val();
				let maxDiscountPoints = $('#maxDiscountPoints').val();
				let discountPerExtraReservation = $('#discountPerExtraReservation').val();
				
				$.ajax({
					type : 'PUT',
					headers: createAuthorizationTokenHeader(tokenKey),
					url : editDiscountInfoURL,
					contentType : 'application/json',
					dataType : 'json',
					data : JSON.stringify({"discountPercentagePerPoint":discountPercentagePerPoint, "kmsNeededForPoint":kmsNeededForPoint, 
						"maxDiscountPoints" : maxDiscountPoints, "discountPerExtraReservation" : discountPerExtraReservation}),
					success : function(data) {
						loadData();
						if (data != null) {
							toastr[data.toastType](data.message);
						}
					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						alert("AJAX ERROR: " + textStatus);
					}
				});
			});
	
}

function loadServices(url, tableID) {
	$.ajax({
		type : 'GET',
		url : url,
		dataType : "json",
		headers : createAuthorizationTokenHeader(tokenKey),
		success : function(data) {
			if (data != null) {
				var table = $(tableID).DataTable();
				table.clear().draw();
				$.each(data, function(i, val) {
					var grade = val.averageGrade;
		        	
		        	if(grade !== 0){
		        		grade = grade/5*100;
		        	}
		        	var roundedGrade = Math.round(val.averageGrade*10)/10;
		        	var rating = "<div class='star-ratings-sprite' title='" + roundedGrade + "/5.0'><span style='width:" + grade 
                	+ "%' class='star-ratings-sprite-rating'></span></div>";
					table.row.add(
							[ val.name, rating, val.numberOfAdmins ])
							.draw(false);
				});
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function loadService(name, url) {
	$.ajax({
		type : 'GET',
		url : url,
		contentType : "application/json",
		data : {
			'name' : name
		},
		dataType : "json",
		headers : createAuthorizationTokenHeader(tokenKey),
		success : function(data) {
			if (data != null) {
				$("#serviceName").html(data["name"]);
				var grade = data["averageGrade"];
            	
            	if(grade !== 0){
            		grade = grade/5*100;
            	}
            	var roundedGrade = Math.round(data["averageGrade"]*10)/10;
            	var rating = "<div class='star-ratings-sprite'><span style='width:" + grade 
            	+ "%' class='star-ratings-sprite-rating'></span></div><p>" + roundedGrade + "/5.0";
				$("#serviceGrade").html(rating);
				$("#serviceDescription").text(data["description"]);
				if(destMap != null){
					destMap.off();
					destMap.remove();
					destMap = null;
				}
				destMap = setUpMap(data["latitude"], data["longitude"],
						'mapDiv', false);
				adminsTable.clear().draw();
				$.each(data.admins, function(i, val) {
					adminsTable.row.add(
							[ val.email, val.firstName, val.lastName,
									val.address, val.phone ]).draw(false);
				});
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});

}

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

function registerAdminFormToJSON(email, password, firstName, lastName, phone,
		address) {
	return JSON.stringify({
		"email" : email,
		"password" : password,
		"firstName" : firstName,
		"lastName" : lastName,
		"phoneNumber" : phone,
		"address" : address
	})
}

function userEditFormToJSON(firstName, lastName, phone, address, email) {
	return JSON.stringify({
		"firstName" : firstName,
		"lastName" : lastName,
		"phoneNumber" : phone,
		"address" : address,
		"email" : email
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