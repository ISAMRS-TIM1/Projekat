const TOKEN_KEY = 'jwtToken';

const tileLayerURL = "https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw";
const MAP_ZOOM = 12;
const MAX_MAP_ZOOM = 19;
const MAP_ID = 'mapbox.streets';

const basicInfoURL = "/api/getRentACarInfo"
const loadUserInfoURL = "/api/getUserInfo";
const logoutURL = "../logout";
const loadBranchOfficesURL = "/api/getBranchOffices";
const addBranchOfficeURL = "/api/addBranchOffice";
const editBranchOfficeURL = "/api/editBranchOffice/";
const deleteBranchOfficeURL = "/api/deleteBranchOffice/";
const editUserInfoURL = "../api/editUser";
const loadVehiclesURL = "/api/getVehicles";
const addVehicleURL = "/api/addVehicle/";
const loadVehicleTypesURL = "/api/getVehicleTypes";
const loadFuelTypesURL = "/api/getFuelTypes";


$(document).ready(function() {
	setUpToastr();
	loadBasicData();
	loadProfileData();
	setUpTables();
	setUpMap(45.267136, 19.833549, 'basicMapDiv');
	setUpMap(45.267136, 19.833549, 'branchMapDiv');
	
	$('a[data-toggle="tab"]').on('shown.bs.tab', function(e){
		$($.fn.dataTable.tables(true)).DataTable().columns.adjust();
	});
	
	$("#logout").click(function(){
		document.location.href = logoutURL;
	});
	
	$('a[href="#branch"]').click(function(){
		loadBranchOffices();
	});
	
	$('a[href="#vehicle"]').click(function(){
		loadVehicles();
	});
	
	$('#userEditForm').on('submit', function(e){
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
			success: function(data){
				if(data != ""){
					toastr["error"](data);
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert("AJAX ERROR: " + textStatus);
			}
		});
	});
	
	$(document).on('click', '#addBranch', function(e) {
		e.preventDefault();
		addBranchOffice();
	});
	
	$('.edit').click(function(){
		if($(this).siblings().first().is('[readonly]')) {
			$(this).siblings().first().removeAttr('readonly');
		} else {
			$(this).siblings().first().prop('readonly', 'true');
		}
	});
	
	var oldName;
	$(document).on('click', '#branchTable tbody tr', function(e) {
		oldName = e.target.parentNode.childNodes[1].innerText;
		$("#editBranchOfficeForm input[name='name']").val(oldName);
		// add branch location to map
		$('#editBranchModalDialog').modal('show');
	});
	
	$(document).on('click', '#editBranch', function(e) {
		e.preventDefault();
		let newName = $("#editBranchOfficeForm input[name='name']").val();
		editBranchOffice(oldName, newName, 14, 14/* latitude, longitude*/);
	});
	
	$(document).on('click', '#deleteBranch', function(e) {
		e.preventDefault();
		deleteBranchOffice(oldName);
	});
	
	$(document).on('click', '#addVehicle', function(e) {
		loadVehicleTypes();
		loadFuelTypes();
	});
});

function setUpTables() {
	$('#branchTable').DataTable({
        "paging": false,
        "info": false,
        "scrollY": "17vw",
        "scrollCollapse": true,
        "retrieve": true,
    });
	
	$('#vehicleTable').DataTable({
        "paging": false,
        "info": false,
        "scrollY": "17vw",
        "scrollCollapse": true,
        "retrieve": true,
    });
}

function setUpMap(latitude, longitude, div) {
	var branchOfficeMap = L.map(div).setView([ latitude, longitude ], MAP_ZOOM);
	L.tileLayer(tileLayerURL, {
		maxZoom : MAX_MAP_ZOOM,
		id : MAP_ID
	}).addTo(branchOfficeMap);
	var marker = L.marker([ latitude, longitude ], {
		draggable : true
	}).addTo(branchOfficeMap);
}

function loadVehicleTypes() {
	$.ajax({
		type : 'GET',
		url : loadVehicleTypesURL,
		dataType : "json",
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			if(data != null){
				let types = $('#vehicleType');
				for(let vehicleType of data) {
					types.append('<option value="' + vehicleType + '">' + vehicleType + '</option');
				}
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function loadFuelTypes() {
	$.ajax({
		type : 'GET',
		url : loadFuelTypesURL,
		dataType : "json",
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			if(data != null){
				let types = $('#fuelType');
				for(let fuelType of data) {
					types.append('<option value="' + fuelType + '">' + fuelType + '</option');
				}
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function loadBasicData() {
	$.ajax({
		type : 'GET',
		url : basicInfoURL,
		dataType : "json",
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			if(data != null){
				$("#basicInfo h2").text(data.name);
				$("#basicInfo h4").text(data.description);
				// average grade for reports
				// latitude and longitude for basic info map
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function loadProfileData(){
	let token = getJwtToken("jwtToken");
	$.ajax({
		type : 'GET',
		url : loadUserInfoURL,
		dataType : "json",
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			if(data != null){
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

function loadBranchOffices() {
	let token = getJwtToken("jwtToken");
	$.ajax({
		type : 'GET',
		url : loadBranchOfficesURL,
		dataType : "json",
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			if(data != null){
				let table = $("#branchTable").DataTable();
				table.clear();
				for(let branchOffice of data) {
					table.row.add([
					               branchOffice.id,
					               branchOffice.name,
					               branchOffice.location.latitude,
					               branchOffice.location.longitude
					               ]).draw(false);
					// make map marker based on location
				}
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function addBranchOffice() {
	let token = getJwtToken("jwtToken");
	let name = $("#addBranchOfficeForm input[name='name']").val();
	// extract latitude and longitude from map marker
	$.ajax({
		type : 'POST',
		url : addBranchOfficeURL,
		contentType: "application/json",
		dataType : "json",
		data: branchOfficeFormToJSON(name, 14, 14/*latitude, longitude*/),
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			toastr[data.toastType](data.message);
			loadBranchOffices();
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function editBranchOffice(oldName, name, latitude, longitude) {
	let token = getJwtToken("jwtToken");
	$.ajax({
		type : 'PUT',
		url : editBranchOfficeURL + oldName,
		contentType: "application/json",
		dataType : "json",
		data: branchOfficeFormToJSON(name, latitude, longitude),
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			toastr[data.toastType](data.message);
			loadBranchOffices();
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function deleteBranchOffice(name) {
	let token = getJwtToken("jwtToken");
	$.ajax({
		type : 'DELETE',
		url : deleteBranchOfficeURL + name,
		contentType: "application/json",
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			toastr[data.toastType](data.message);
			loadBranchOffices();
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function loadVehicles() {
	let token = getJwtToken("jwtToken");
	$.ajax({
		type : 'GET',
		url : loadVehiclesURL,
		dataType : "json",
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			if(data != null){
				let table = $("#vehicleTable").DataTable();
				table.clear();
				for(let vehicle of data) {
					table.row.add([
					               vehicle.producer,
					               vehicle.model,
					               vehicle.yearOfProduction,
					               vehicle.numberOfSeats,
					               vehicle.fuelType,
					               vehicle.vehicleType,
					               vehicle.pricePerDay
					               ]).draw(false);
				}
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function userFormToJSON(firstName, lastName, phone, address, email){
	return JSON.stringify({
		"firstName": firstName,
		"lastName": lastName,
		"phoneNumber": phone,
		"address": address,
		"email": email
	});
}

function branchOfficeFormToJSON(name, latitude, longitude){
	return JSON.stringify({
		"name": name,
		"location": {
			"latitude": latitude,
			"longitude": longitude
		}
	});
}

function setUpToastr() {
	toastr.options = {
			  "closeButton": true,
			  "debug": false,
			  "newestOnTop": false,
			  "progressBar": false,
			  "positionClass": "toast-top-center",
			  "preventDuplicates": false,
			  "onclick": null,
			  "showDuration": "300",
			  "hideDuration": "1000",
			  "timeOut": "3000",
			  "extendedTimeOut": "1000",
			  "showEasing": "swing",
			  "hideEasing": "linear",
			  "showMethod": "fadeIn",
			  "hideMethod": "fadeOut"
			}
}

