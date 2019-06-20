//TOKEN KEY
const TOKEN_KEY = 'jwtToken';

/* MAP CONSTANTS */
const tileLayerURL = "https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw";
const MAP_ZOOM = 12;
const MAX_MAP_ZOOM = 19;
const MAP_ID = 'mapbox.streets';

/* MAP VARIABLES */
var racMap = null;
var editBranchMap = null;
var addBranchMap = null;

/* URLs */
const basicInfoURL = "/api/getRentACarInfo";
const editRentacarURL = "/api/editRentACar";
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
const deleteVehicleURL = "/api/deleteVehicle/";
const editVehicleURL = "/api/editVehicle/";
const loadDailyChartDataURL = "/api/getDailyGraphData";
const loadWeeklyChartDataURL = "/api/getWeeklyGraphData";
const loadMonthlyChartDataURL = "/api/getMonthlyGraphData";
const getIncomeOfRentACarURL = "/api/getIncomeOfRentACar";
const loadQuickReservationsURL = "/api/getQuickVehicleReservations";
const addQuickReservationURL = "/api/createQuickVehicleReservation";
const changePasswordURL = "../changePassword";

var oldRentacarName = null;

$(document).ready(function() {
	/* INITIALIZING TOASTR, TABLES, MAPS AND LOADING BASIC RENT A CAR DATA */
	loadBasicData();
	loadProfileData();
	loadBranchOffices();
	loadVehicles();
	loadQuickReservations();
	
	getDailyChartData();
	setUpToastr();
	setUpTable("branchTable");
	setUpTable("vehicleTable");
	setUpTable("quickReservationsTable");
	setUpDatePicker("showIncomeDateRange");
	setUpDatePicker("quickPeriod");
	
	$("#saveChangesBasic").click(function(e){
		e.preventDefault();
		
		var name = $("#rentACarName").val();
		
		if(name == null || name === ""){
			toastr["error"]("Rent a car name must not be empty");
			return;
		}
		
		var description = $("#rentACarDescription").val();
		
		if(description == null || description === ""){
			toastr["error"]("Rent a car description must not be empty");
			return;
		}
		
		var latitude = $("#basicLatitude").val();
		
		if(latitude == null){
			toastr["error"]("Rent a car latitude must not be empty");
			return;
		}
		
		var longitude = $("#basicLongitude").val();
		
		if(longitude == null){
			toastr["error"]("Rent a car longitude must not be empty");
			return;
		}
		
		editRentacar(name, description, latitude, longitude);
	});

	/* ADJUSTING TABLES */
	$('a[data-toggle="tab"]').on('shown.bs.tab', function(e){
		$($.fn.dataTable.tables(true)).DataTable().columns.adjust();
	});
	
	/* LOGOUT */
	$("#logout").click(function(){
		removeJwtToken(TOKEN_KEY);
		document.location.href = logoutURL;
	});
	
	/* SWITCHING TAB EVENTS */
	$('a[href="#branch"]').click(function(){
		loadBranchOffices();
	});
	
	$('a[href="#profile"]').click(function(){
		loadProfileData();
	});
	
	$('a[href="#vehicle"]').click(function(){
		loadVehicles();
	});
	
	/* EDIT ENABLE/DISABLE EVENT */
	$('.edit').click(function(){
		if($(this).siblings().first().is('[readonly]')) {
			$(this).siblings().first().removeAttr('readonly');
		} else {
			$(this).siblings().first().prop('readonly', 'true');
		}
	});
	
	/* PROFILE EVENTS */
	$('#userEditForm').on('submit', function(e){
		e.preventDefault();
		let firstName = $('input[name="fname"]').val();
		
		if(firstName == null || firstName === ""){
			toastr["error"]("First name must not be empty");
			return;
		}
		
		let lastName = $('input[name="lname"]').val();
		
		if(lastName == null || lastName === ""){
			toastr["error"]("Last name must not be empty");
			return;
		}
		
		let phone = $('input[name="phone"]').val();
		
		if(phone == null || phone === ""){
			toastr["error"]("Phone must not be empty");
			return;
		}
		
		let address = $('input[name="address"]').val();
		
		if(address == null || address === ""){
			toastr["error"]("Address must not be empty");
			return;
		}
		
		let email = $('#email').text();

		if(email == null || email === ""){
			toastr["error"]("Email must not be empty");
			return;
		}
		
		editProfile(firstName, lastName, phone, address, email);
	});
	
	/* BRANCH OFFICE EVENTS */
	$('#addBranchModalDialog').on('shown.bs.modal', function() {
	    setTimeout(function() {
	      addBranchMap = setUpMap(45, 0, 'addBranchOfficeMap', true, addBranchMap, '#addBranchLatitude', '#addBranchLongitude');
	    }, 10);
	  });
	
	$(document).on('click', '#addBranch', function(e) {
		e.preventDefault();
		addBranchOffice();
	});
	
	var oldName;
	$(document).on('click', '#branchTable tbody tr', function(e) {
		oldName = e.target.parentNode.childNodes[1].innerText;
		$("#editBranchOfficeForm input[name='name']").val(oldName);
		lat = e.target.parentNode.childNodes[2].innerText;
		long = e.target.parentNode.childNodes[3].innerText;
		editBranchMap = setUpMap(lat, long, 'branchMapDiv', true, editBranchMap, '#branchLatitude', '#branchLongitude');
		$('#editBranchModalDialog').modal('show');
	});
	
	$(document).on('click', '#editBranch', function(e) {
		e.preventDefault();
		let newName = $("#editBranchOfficeForm input[name='name']").val();
		
		if(newName == null || newName === ""){
			toastr["error"]("Branch office name must not be null");
			return;
		}
		
		var latitude = $('#branchLatitude').val();
		
		if(latitude == null){
			toastr["error"]("Branch office latitude must not be null");
			return;
		}
		
		var longitude = $('#branchLongitude').val();
		
		if(longitude == null){
			toastr["error"]("Branch office longitude must not be null");
			return;
		}
		
		editBranchOffice(oldName, newName, latitude, longitude);
		oldName = newName;
	});
	
	$(document).on('click', '#deleteBranch', function(e) {
		e.preventDefault();
		deleteBranchOffice(oldName);
	});
	
	/* VEHICLE EVENTS */
	$(document).on('click', '#addVehicle', function(e) {
		loadVehicleTypes('#vehicleTypeAdd');
		loadFuelTypes('#fuelTypeAdd');
	});
	
	$(document).on('click', '#saveVehicle', function(e) {
		e.preventDefault();
		addVehicle();
	});
	
	var oldProducer;
	var oldModel;
	$(document).on('click', '#vehicleTable tbody tr', function(e) {
		oldProducer = e.target.parentNode.childNodes[0].innerText;
		oldModel = e.target.parentNode.childNodes[1].innerText;
		$('#editVehicleForm input[name="producer"]').val(oldProducer);
		$('#editVehicleForm input[name="model"]').val(oldModel);
		$('#editVehicleForm input[name="year"]').val(e.target.parentNode.childNodes[2].innerText);
		$('#editVehicleForm input[name="seats"]').val(e.target.parentNode.childNodes[3].innerText);
		$('#editVehicleForm input[name="price"]').val(e.target.parentNode.childNodes[6].innerText);
		
		loadVehicleTypes('#vehicleTypeEdit', selected=e.target.parentNode.childNodes[5].innerText);
		loadFuelTypes('#fuelTypeEdit', selected=e.target.parentNode.childNodes[4].innerText);
		
		$('#editVehicleModalDialog').modal('show');
	});
	
	$(document).on("click", "#changePasswordButton", function(e){
		e.preventDefault();
		document.location.href = changePasswordURL;
	});
	
	$(document).on('click', '#editVehicle', function(e) {
		e.preventDefault();
		
		let newProducer = $('#editVehicleForm input[name="producer"]').val();
		
		if(newProducer == null || newProducer === ""){
			toastr["error"]("Vehicle producer must not be null");
			return;
		}
		
		let newModel = $('#editVehicleForm input[name="model"]').val();
		
		if(newModel == null || newModel === ""){
			toastr["error"]("Vehicle model must not be null");
			return;
		}
		
		let newYear = $('#editVehicleForm input[name="year"]').val();
		
		if(newYear == null || newYear === ""){
			toastr["error"]("Vehicle year must not be null");
			return;
		}
		
		let newSeats = $('#editVehicleForm input[name="seats"]').val();
		
		if(newSeats == null || newSeats === ""){
			toastr["error"]("Vehicle seats must not be null");
			return;
		}
		
		let newPrice = $('#editVehicleForm input[name="price"]').val();
		
		if(newPrice == null || newPrice === ""){
			toastr["error"]("Vehicle price must not be null");
			return;
		}
		
		let newFuel = $('#fuelTypeEdit').val();
		
		if(newFuel == null || newFuel === ""){
			toastr["error"]("Vehicle fuel must not be null");
			return;
		}
		
		let newType = $('#vehicleTypeEdit').val();
		
		if(newType == null || newType === ""){
			toastr["error"]("Vehicle type must not be null");
			return;
		}
		
		editVehicle(oldModel, oldProducer, newProducer, newModel, newYear, newSeats, newPrice, newType, newFuel);
		
		oldProducer = newProducer;
		oldModel = newModel;
	});
	
	$(document).on('click', '#deleteVehicle', function(e) {
		e.preventDefault();
		deleteVehicle(oldProducer, oldModel);
	});
	
	/* GRAPHICS EVENTS */
	$('#graphicLevel').on('change', function() {
		changeGraphic(this.value);
	});
	
	/* INCOME EVENTS */
	$('#showIncomeButton').on('click', function(e) {
		e.preventDefault();
		let drp = $('#showIncomeDateRange').data('daterangepicker');
		showIncome(drp.startDate.toDate(), drp.endDate.toDate());

	});
	
	/* BRANCH MAP EVENTS */
	$('#editBranchModalDialog').on('shown.bs.modal', function() {
		setTimeout(function() {
			editBranchMap.invalidateSize()
		}, 10);
		setTimeout(function() {
			editBranchMap.invalidateSize()
		}, 1000);
	});
	
	/* QUICK RESERVATIONS EVENTS */
	$('#quickCreate').click(function(e) {
		e.preventDefault();
		
		let branch = $("#selectBranch").val();
		let vehicle = $("#selectVehicle").val();
		let producer = vehicle.split("_")[0];
		let model = vehicle.split("_")[1];
		let discount = emptyToZero($("#discount").val());
		let startDate = $('#showIncomeDateRange').data('daterangepicker').startDate.format("DD/MM/YYYY");
		let endDate = $('#showIncomeDateRange').data('daterangepicker').endDate.format("DD/MM/YYYY");
		
		addQuickReservation(branch, producer, model, discount, startDate, endDate);
	});
});


/* RENT A CAR FUNCTIONS */
function loadBasicData() {
	$.ajax({
		type : 'GET',
		url : basicInfoURL,
		dataType : "json",
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			if(data != null){
				$("#rentACarName").val(data.name);
				oldRentacarName = data.name;
				$("#rentACarDescription").text(data.description);
				var grade = data["averageGrade"];
	        	
	        	if(grade !== 0){
	        		grade = grade/5*100;
	        	}
	        	var roundedGrade = Math.round(data["averageGrade"]*10)/10;
	        	var rating = "<div class='star-ratings-sprite'><span style='width:" + grade 
	        	+ "%' class='star-ratings-sprite-rating'></span></div><p>" + roundedGrade + "/5.0";
				$("#averageGrade").html(rating);
				racMap = setUpMap(data["latitude"], data["longitude"], 'basicMapDiv', true, racMap, '#basicLatitude', '#basicLongitude');
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function editRentacar(name, description, latitude, longitude) {
	$.ajax({
		type : 'PUT',
		url : editRentacarURL,
		contentType : 'application/json',
		dataType : "json",
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		data : rentacarFormToJSON(oldRentacarName, name, description, latitude, longitude),
		success: function(data){
			toastr[data.toastType](data.message);
			
			if(data.toastType === "success")
				loadBasicData();
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function rentacarFormToJSON(oldName, name, description, latitude, longitude){
	return JSON.stringify({
		"name": name,
		"oldName": oldName,
		"description": description,
		"latitude": latitude,
		"longitude": longitude,
	});
}

/* PROFILE FUNCTIONS */
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

function editProfile(firstName, lastName, phone, address, email) {
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
}

/* BRANCH OFFICE FUNCTIONS */
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
				$('#selectBranch').empty();
				for(let branchOffice of data) {
					$('#selectBranch').append(new Option(branchOffice.name, branchOffice.name));
					table.row.add([
					               branchOffice.id,
					               branchOffice.name,
					               Math.round(branchOffice.location.latitude*1000)/1000,
					               Math.round(branchOffice.location.longitude*1000)/1000,
					               branchOffice.deleted
					               ]).draw(false);
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
	
	if(name == null || name === ""){
		toastr["error"]("Branch office name must not be empty");
		return;
	}
	
	var latitude = $('#addBranchLatitude').val();
	
	if(latitude == null){
		toastr["error"]("Branch office latitude must not be empty");
		return;
	}
	
	var longitude = $('#addBranchLongitude').val();
	
	if(longitude == null){
		toastr["error"]("Branch office longitude must not be empty");
		return;
	}
	
	$.ajax({
		type : 'POST',
		url : addBranchOfficeURL,
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

/* VEHICLE FUNCTIONS */
function loadVehicleTypes(id, selected=undefined) {
	$.ajax({
		type : 'GET',
		url : loadVehicleTypesURL,
		dataType : "json",
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			let types = $(id);
			types.empty();
			
			for (let vehicleType of data) {
				var sel = "";
				if (selected === vehicleType)
					sel = "selected";
				types.append(`<option value="${vehicleType}" ${sel}>${capitalize(vehicleType)}</option>`);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function capitalize(word){
	return word.charAt(0).toUpperCase() + (word.toLowerCase()).slice(1);
}

function loadFuelTypes(id, selected=undefined) {
	$.ajax({
		type : 'GET',
		url : loadFuelTypesURL,
		dataType : "json",
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			let types = $(id);
			types.empty();
			for (let fuelType of data) {
				var sel = "";
				if (selected === fuelType)
					sel = "selected";
				types.append(`<option value="${fuelType}" ${sel}>${capitalize(fuelType)}</option>`);
			}
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
				$('#selectVehicle').empty();
				for(let vehicle of data) {
					$('#selectVehicle').append(new Option(vehicle.producer + " " + vehicle.model, vehicle.producer + "_" + vehicle.model));
					var grade = vehicle.averageGrade;
                	
                	if(grade !== 0){
                		grade = grade/5*100;
                	}
                	var roundedGrade = Math.round(vehicle.averageGrade * 10)/10;
                	var rating = "<div class='star-ratings-sprite' title='" + roundedGrade + "/5.0'><span style='width:" + grade 
                	+ "%' class='star-ratings-sprite-rating'></span></div>";
					table.row.add([
					               vehicle.producer,
					               vehicle.model,
					               vehicle.yearOfProduction,
					               vehicle.numberOfSeats,
					               vehicle.fuelType,
					               vehicle.vehicleType,
					               vehicle.pricePerDay,
					               rating,
					               vehicle.deleted
					               ]).draw(false);
				}
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function addVehicle() {
	let token = getJwtToken("jwtToken");
	
	let producer = $('input[name="producer"]').val();
	
	if(producer == null || producer === ""){
		toastr["error"]("Vehicle producer must not be null");
		return;
	}
	
	let model = $('input[name="model"]').val();
	
	if(model == null || model === ""){
		toastr["error"]("Vehicle model must not be null");
		return;
	}
	
	let year = $('input[name="year"]').val();
	
	if(year == undefined || year === ""){
		toastr["error"]("Vehicle production year must not be null");
		return;
	}
	
	let seats = $('input[name="seats"]').val();
	
	if(seats == undefined || seats === ""){
		toastr["error"]("Vehicle seats must not be null");
		return;
	}
	
	let price = $('input[name="price"]').val();
	
	if(price == undefined || price === ""){
		toastr["error"]("Vehicle price must not be null");
		return;
	}
	
	let vehicleType = $('#vehicleTypeAdd').val();
	
	if(vehicleType == undefined || vehicleType === ""){
		toastr["error"]("Vehicle type must not be null");
		return;
	}
	
	let fuelType = $('#fuelTypeAdd').val();
	
	if(fuelType == undefined || fuelType === ""){
		toastr["error"]("Vehicle fuel type must not be null");
		return;
	}
	
	let quantity = $('input[name="quantity"]').val();
	
	if(quantity == undefined || quantity === ""){
		toastr["error"]("Vehicle quantity must not be null");
		return;
	}
	
	$.ajax({
		type : 'POST',
		url : addVehicleURL + quantity,
		contentType: "application/json",
		dataType : "json",
		data: vehicleFormToJSON(producer, model, year, seats,fuelType, vehicleType, price),
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			toastr[data.toastType](data.message);
			loadVehicles();
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function editVehicle(oldModel, oldProducer, producer, model, year, seats, price, vehicleType, fuelType) {
	let token = getJwtToken("jwtToken");
	
	$.ajax({
		type : 'PUT',
		url : editVehicleURL + oldProducer + "/" + oldModel,
		contentType: "application/json",
		dataType : "json",
		data: vehicleFormToJSON(producer, model, year, seats, fuelType, vehicleType, price),
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			toastr[data.toastType](data.message);
			loadVehicles();
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function deleteVehicle(producer, model) {
	let token = getJwtToken("jwtToken");
	
	$.ajax({
		type : 'DELETE',
		url : deleteVehicleURL + producer + "/" + model,
		contentType: "application/json",
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			toastr[data.toastType](data.message);
			loadVehicles();
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

/* GRAPHICS FUNCTIONS */
function showIncome(startDate, endDate) {
	$.ajax({
		type : 'GET',
		url : getIncomeOfRentACarURL,
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		contentType : 'application/json',
		data : {"fromDate" : startDate, "toDate" : endDate},
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

	let timeFormat = "DD/MM/YYYY";
	let startDate = moment(moment(labels[0], timeFormat).subtract(3, 'days')).format(timeFormat);
	let endDate = moment(moment(labels[labels.length - 1], timeFormat).add(3, 'days')).format(timeFormat);

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
	            mode: 'x',
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
	            mode: 'x',
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

	let timeFormat = "MM/YYYY";
	let startDate = moment(moment(labels[0], timeFormat).subtract(1, 'months')).format(timeFormat);
	let endDate = moment(moment(labels[labels.length - 1], timeFormat).add(1, 'months')).format(timeFormat);

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
	            mode: 'x',
	        },
	        zoom: {
	        	enabled: true,
	        	mode: 'x'
	        }
	    }
	});
}

/* QUICK RESERVATIONS FUNCTIONS */
function loadQuickReservations() {
	$.ajax({
		type : 'GET',
		url : loadQuickReservationsURL,
		dataType : "json",
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			if (data != null) {
				let table = $('#quickReservationsTable').DataTable();
				table.clear().draw();
				
				for(let reservation of data) {
					table.row.add([
						reservation.branchOfficeName,
						reservation.vehicleProducer,
						reservation.vehicleModel,
						reservation.fromDate,
						reservation.toDate,
						reservation.discount + "%"
					]).draw(false);
				}
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function addQuickReservation(branch, producer, model, discount, fromDate, toDate) {
	$.ajax({
		type : 'POST',
		url : addQuickReservationURL,
		contentType: "application/json",
		dataType : "json",
		data: quickReservationFormToJSON(branch, producer, model, discount, fromDate, toDate),
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			toastr[data.toastType](data.message);
			loadQuickReservations();
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

/* QUICK RESERVATIONS UTILITY FUNCTIONS */
function emptyToZero(value) {
	if(value == "") {
		return 0;
	} else {
		return value;
	}
}

function quickReservationFormToJSON(branch, producer, model, discount, fromDate, toDate) {
	return JSON.stringify({
		"fromDate": fromDate,
		"toDate": toDate,
		"vehicleProducer": producer,
		"vehicleModel": model,
		"branchOfficeName": branch,
		"discount": discount
	});
}

/* GRAPHICS UTILITY FUNCTIONS */
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

/* PROFILE UTILITY FUNCTIONS */
function userFormToJSON(firstName, lastName, phone, address, email){
	return JSON.stringify({
		"firstName": firstName,
		"lastName": lastName,
		"phoneNumber": phone,
		"address": address,
		"email": email
	});
}

/* BRANCH OFFICE UTILITY FUNCTIONS */
function branchOfficeFormToJSON(name, latitude, longitude){
	return JSON.stringify({
		"name": name,
		"location": {
			"latitude": latitude,
			"longitude": longitude
		}
	});
}

/* VEHICLE UTILITY FUNCTIONS */
function vehicleFormToJSON(producer, model, yearOfProduction, numberOfSeats, fuelType, vehicleType, pricePerDay) {
	return JSON.stringify({
		"producer": producer,
		"model": model,
		"yearOfProduction": yearOfProduction,
		"numberOfSeats": numberOfSeats,
		"fuelType": fuelType,
		"vehicleType": vehicleType,
		"pricePerDay": pricePerDay
	});
}

/* COMMON UTILITY FUNCTIONS */
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

function setUpDatePicker(id) {
	$('#' + id).daterangepicker({
		locale : {
			format : 'DD/MM/YYYY'
		}
	});
}

function setUpTable(tableID) {
	$('#' + tableID).DataTable({
        "paging": false,
        "info": false,
        "scrollY": "17vw",
        "scrollCollapse": true,
        "retrieve": true,
    });
}

function setUpMap(latitude, longitude, div, draggable, destMap, latInput, longInput) {
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
