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

const tokenKey = "jwtToken";

const tileLayerURL = "https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw";
const MAP_ZOOM = 8;
const MAX_MAP_ZOOM = 19;
const MAP_ID = 'mapbox.streets';

$(document).ready(
		function() {
			loadData();

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

			$('.edit').click(function() {
				if ($(this).siblings().first().is('[readonly]')) {
					$(this).siblings().first().removeAttr('readonly');
				} else {
					$(this).siblings().first().prop('readonly', 'true');
				}
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
							data : userEditFormToJSON(firstName, lastName,
									phone, address, email),
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
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
								alert("AJAX ERROR: " + textStatus);
							}
						});
					});

			var airlinesTable = $('#airlinesTable').DataTable({
				"paging" : false,
				"info" : false,
			});

			var hotelsTable = $('#hotelsTable').DataTable({
				"paging" : false,
				"info" : false,
			});

			var rentACarsTable = $('#rentACarsTable').DataTable({
				"paging" : false,
				"info" : false,
			});

			$('#airlinesTable tbody').on('click', 'tr', function() {
				airlinesTable.$('tr.selected').removeClass('selected');
				$(this).addClass('selected');
				loadService(airlinesTable.row(this).data()[0], getAirlineURL);
				$("#modalDialog").modal();
			});

			$('#hotelsTable tbody').on('click', 'tr', function() {
				hotelsTable.$('tr.selected').removeClass('selected');
				$(this).addClass('selected');
				loadService(hotelsTable.row(this).data()[0], getHotelURL);
				$("#modalDialog").modal();
			});

			$('#rentACarsTable tbody')
					.on(
							'click',
							'tr',
							function() {
								rentACarsTable.$('tr.selected').removeClass(
										'selected');
								$(this).addClass('selected');
								loadService(rentACarsTable.row(this).data()[0],
										getRentACarURL);
								$("#modalDialog").modal();
							});

			$('#modalDialog').on('hidden.bs.modal', function() {
				airlinesTable.$('tr.selected').removeClass('selected');
				hotelsTable.$('tr.selected').removeClass('selected');
				rentACarsTable.$('tr.selected').removeClass('selected');
				destMap.off();
				destMap.remove();
				adminsTable.clear().draw();
			})

			$('#modalDialog').on('shown.bs.modal', function() {
				setTimeout(function() {
					destMap.invalidateSize()
				}, 10);
			});

			adminsTable = $("#adminsTable").DataTable({
				"paging" : false,
				"info" : false
			});
		});

function loadData() {
	loadProfile();
	loadServices(getAirlinesURL, "#airlinesTable");
	loadServices(getHotelsURL, "#hotelsTable");
	loadServices(getRentACarsURL, "#rentACarsTable");
}

function loadProfile() {
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

function loadServices(url, tableID) {
	let token = getJwtToken("jwtToken");
	$.ajax({
		type : 'GET',
		url : url,
		dataType : "json",
		headers : createAuthorizationTokenHeader(tokenKey),
		success : function(data) {
			if (data != null) {
				var table = $(tableID).DataTable();
				$.each(data, function(i, val) {
					table.row.add(
							[ val.name, val.averageGrade, val.numberOfAdmins ])
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
	let token = getJwtToken("jwtToken");
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
				$("#serviceName").val(data["name"]);
				$("#serviceGrade").text(data["averageGrade"]);
				$("#serviceDescription").text(data["description"]);
				setUpMap(data["latitude"], data["longitude"]);
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
				url : registerAdminURL + $('#serviceName').val(),
				contentType : 'application/json',
				dataType : "json",
				data : registerAdminFormToJSON(email, password, firstName,
						lastName, phone, address),
				success : function(data) {
					if (data) {
						$('#adminEmail').val("");
						$('#adminPassword').val("");
						$('#adminFirstName').val("");
						$('#adminLastName').val("");
						$('#adminPhone').val("");
						$('#adminAddress').val("");
					} else {
						alert("Admin cannot be added!");
					}
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("AJAX ERROR: " + textStatus);
				}
			})
		})

function setUpMap(latitude, longitude) {
	destMap = L.map('mapDiv').setView([ latitude, longitude ], MAP_ZOOM);
	L.tileLayer(tileLayerURL, {
		maxZoom : MAX_MAP_ZOOM,
		id : MAP_ID
	}).addTo(destMap);
	var marker = L.marker([ latitude, longitude ], {
		draggable : false
	}).addTo(destMap);
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