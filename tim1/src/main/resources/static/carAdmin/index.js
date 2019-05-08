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
const deleteVehicleURL = "/api/deleteVehicle/";
const editVehicleURL = "/api/editVehicle/";
const loadDailyChartDataURL = "/api/getDailyGraphData";
const loadWeeklyChartDataURL = "/api/getWeeklyGraphData";
const loadMonthlyChartDataURL = "/api/getMonthlyGraphData";
const getIncomeOfRentACarURL = "/api/getIncomeOfRentACar";

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
		editBranchOffice(oldName, newName, 14, 14/* latitude, longitude */);
		oldName = newName;
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
	
	$(document).on('click', '#editVehicle', function(e) {
		e.preventDefault();
		let newProducer = $('#editVehicleForm input[name="producer"]').val();
		let newModel = $('#editVehicleForm input[name="model"]').val();
		let newYear = $('#editVehicleForm input[name="year"]').val();
		let newSeats = $('#editVehicleForm input[name="seats"]').val();
		let newPrice = $('#editVehicleForm input[name="price"]').val();
		let newFuel = $('#fuelTypeEdit').val();
		let newType = $('#vehicleTypeEdit').val();
		
		editVehicle(oldModel, oldProducer, newProducer, newModel, newYear, newSeats, newPrice, newType, newFuel);
		
		oldProducer = newProducer;
		oldModel = newModel;
	});
	
	$(document).on('click', '#deleteBranch', function(e) {
		e.preventDefault();
		deleteBranchOffice(oldName);
	});
	
	$(document).on('click', '#deleteVehicle', function(e) {
		e.preventDefault();
		deleteVehicle(oldProducer, oldModel);
	});
	
	$(document).on('click', '#addVehicle', function(e) {
		loadVehicleTypes('#vehicleTypeAdd');
		loadFuelTypes('#fuelTypeAdd');
	});
	
	$(document).on('click', '#saveVehicle', function(e) {
		e.preventDefault();
		addVehicle();
	});
	
	$('a[href="#report"]').click(function(){
		getDailyChartData();
	});
	
	$('#graphicLevel').on('change', function() {
		changeGraphic(this.value);
	});
	
	$('#showIncomeDateRange').daterangepicker({
		locale : {
			format : 'DD/MM/YYYY'
		}
	});
	
	$('#showIncomeButton').on('click', function(e) {
		e.preventDefault();
		let drp = $('#showIncomeDateRange').data('daterangepicker');
		showIncome(drp.startDate.toDate(), drp.endDate.toDate());
	})
});

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
	//date week: num
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

function loadVehicleTypes(id, selected=undefined) {
	$.ajax({
		type : 'GET',
		url : loadVehicleTypesURL,
		dataType : "json",
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			if(data != null){
				let types = $(id);
				types.empty();
				for(let vehicleType of data) {
					if (selected != undefined && selected === vehicleType) {
						types.append('<option value="' + vehicleType + '"selected>' + vehicleType + '</option');
					} else {
						types.append('<option value="' + vehicleType + '">' + vehicleType + '</option');
					}
				}
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function loadFuelTypes(id, selected=undefined) {
	$.ajax({
		type : 'GET',
		url : loadFuelTypesURL,
		dataType : "json",
		headers: createAuthorizationTokenHeader(TOKEN_KEY),
		success: function(data){
			if(data != null){
				let types = $(id);
				types.empty();
				for(let fuelType of data) {
					if (selected != undefined && selected === fuelType) {
						types.append('<option value="' + fuelType + '" selected>' + fuelType + '</option>');
					} else {						
						types.append('<option value="' + fuelType + '">' + fuelType + '</option>');
					}
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
				$("#rentACarName").val(data.name);
				$("#rentACarDescription").text(data.description);
				$("#rentACarGrade").text(data.averageGrade);
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
					               branchOffice.location.longitude,
					               branchOffice.deleted
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
		data: branchOfficeFormToJSON(name, 14, 14/* latitude, longitude */),
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
					               vehicle.pricePerDay,
					               vehicle.averageGrade,
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
	let model = $('input[name="model"]').val();
	let year = $('input[name="year"]').val();
	let seats = $('input[name="seats"]').val();
	let price = $('input[name="price"]').val();
	let vehicleType = $('#vehicleTypeAdd').val();
	let fuelType = $('#fuelTypeAdd').val();
	let quantity = $('input[name="quantity"]').val();
	
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

