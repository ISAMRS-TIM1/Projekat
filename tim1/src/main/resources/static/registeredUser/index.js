const tokenKey = "jwtToken";
var isVisitor = false;
const loginPageURL = "../login";
const checkIfRegisteredUserURL = "../auth/checkIfRegisteredUser";

const tileLayerURL = "https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw";
const MAP_ZOOM = 8;
const MAX_MAP_ZOOM = 19;
const MAP_ID = 'mapbox.streets';

const logoutURL = "../logout";
const loadUserInfoURL = "../api/getUserInfo";
const saveChangesURL = "../api/editUser";
const getPlaneSeatsURL = "/api/getPlaneSeats";
const searchUsersURL = "/api/searchUsers";
const friendInvitationURL = "/api/sendInvitation";
const acceptInvitationURL = "/api/acceptInvitation";
const declineInvitationURL = "/api/declineInvitation";
const getFriendInvitationsURL = "/api/getFriendInvitations";
const getDestinationsURL = "/api/getDestinations";
const searchFlightsURL = "/api/searchFlights";
const getFriendsURL = "/api/getFriends";
const getReservationsURL = "/api/getReservations";
const cancelReservationURL = "/api/cancelReservation";
const getAirlinesURL = "/api/getAirlines";
const getDetailedAirlineURL = "../api/getDetailedAirline";
const reserveQuickFlightReservationURL = "/api/reserveQuickFlightReservation/";
const getDetailedReservationURL = "/api/getDetailedReservation";

const searchHotelsURL = "/api/searchHotels";
const getHotelURL = "../api/getHotel";
const getDetailedHotelURL = "../api/getDetailedHotel";
const getDetailedFlightURL = "/api/getDetailedFlight";
const searchRoomsURL = '/api/searchRooms/';

const getVehicleProducersURL = "/api/getVehicleProducers";
const getModelsForProducerURL = "/api/getModels/";
const getAllVehicleTypesURL = "/api/getVehicleTypes";	
const getAllFuelTypesURL = "/api/getFuelTypes";
const searchVehiclesURL = "/api/searchVehicles";
const getQuickReservationsForVehicleURL = "/api/getQuickReservationsForVehicle/";
const checkVehicleForPeriodURL = "/api/checkVehicleForPeriod";
const getBranchOfficesForVehicleURL = "/api/getBranchOfficesForVehicle/";
const checkCountryURL = "/api/checkCountry/";

const reserveFlightURL = "/api/reserveFlight/";
const reserveFlightHotelURL = "/api/reserveFlightHotel/";
const reserveFlightVehicleURL = "/api/reserveFlightVehicle/";
const reserveFlightHotelVehicleURL = "/api/reserveFlightHotelVehicle/";

const getDiscountInfoURL = "/api/getDiscountInfo";

var userMail = "";
var hotelMap = null;
var airlineMap = null;
var destinationsMap = null;

var shownHotel = null;
var shownReservation = null;
var hotelReservation = null;

var searchHotelCountry = null;

var error = false;

$(document)
    .ready(
        function() {
        	$.ajax({
        		type: "GET",
        		url: checkIfRegisteredUserURL,
        		dataType: "json",
        		headers: createAuthorizationTokenHeader(tokenKey),
        		success: function(data) {
        			isVisitor = !data;
        			if(!isVisitor){
        	            var socket = new SockJS('/friendsEndpoint');
        	            var stompClient = Stomp.over(socket);
        	            stompClient.connect({}, function(frame) {
        	                stompClient.subscribe("/friendsInvitation/" + userMail,
        	                    function(data) {
        	                        getFriends();
        	                    });
        	            });
        	            loadProfileData();
        	        }

                    getAirlines();
                    setUpToastr();
                    getDestinations();
                    
                    localStorage.removeItem("flightReservation");
                    localStorage.removeItem("flightRes");
                    localStorage.removeItem("quickFlightReservation");
                    
                    $('#friendsTable').DataTable({
                        "paging": false,
                        "info": false,
                        "scrollY": "17vw",
                        "scrollCollapse": true,
                        "retrieve": true,
                    });

                    var reservationsTable = $('#reservationsTable').DataTable({
                        "paging": false,
                        "info": false,
                        "scrollY": "17vw",
                        "scrollCollapse": true,
                        "retrieve": true,
                    });

                    $('#usersTable').DataTable({
                        "paging": false,
                        "info": false,
                        "scrollCollapse": true,
                        "retrieve": true,
                    });

                    $('#inviteFriendsTable').DataTable({
                        "paging": false,
                        "info": false,
                        "scrollCollapse": true,
                        "retrieve": true,
                    });
                    
                    var airlinesTable = $('#airlinesTable').DataTable({
                        "paging": false,
                        "info": false,
                        "scrollCollapse": true,
                        "retrieve": true,
                    });
            
                    var destinationsTable = $('#airlineDestinationsTable').DataTable({
                        "paging": false,
                        "info": false,
                        "scrollY": "200px",
                        "scrollCollapse": true,
                        "retrieve": true,
                        "columnDefs": [
                            {
                                "targets": [ 1 ],
                                "visible": false
                            },
                            {
                                "targets": [ 2 ],
                                "visible": false
                            }
                        ]
                    });
                    
                    $('#airlineDestinationsTable tbody').on('click', 'tr', function() {
                        destinationsTable.$('tr.selected').removeClass('selected');
                        $(this).addClass('selected');
                        var longitude = destinationsTable.row(this).data()[1];
                        var latitude = destinationsTable.row(this).data()[2];
                        destinationsMap = setUpMap(destinationsMap, latitude, longitude, 
        						'destinationsMapDiv', false, 2, true);
                        destinationsMap.invalidateSize();
                    });
                    
                    setUpTableFilter("#flightsTable");
                    
                    var flightsTable = $('#flightsTable').DataTable({
                        "paging": false,
                        "info": false,
                        "scrollY": "17vw",
                        "scrollX": true,
                        "scrollCollapse": true,
                        "retrieve": true,
                        "orderCellsTop": true
                    });
                    
                    $('#quickAirlineReservationsTable').DataTable({
                		"paging" : false,
                		"info" : false,
                		"orderCellsTop" : true,
                		"fixedHeader" : true
                	});

                    $('#showFlightModal').on('hidden.bs.modal', function() {
                        flightsTable.$('tr.selected').removeClass('selected');
                        $("#reserveDivPassengers").hide();
                        $("#reserveDivFriends").hide();
                        $("#reserveDiv").show();
                        seatsToReserve = [];
                        $("#flightConnections").find('option').remove();
                    });
                    
                    $('#showAirlineModal').on('shown.bs.modal', function() {
                    	destinationsMap = setUpMap(destinationsMap, 0, 0, 
        						'destinationsMapDiv', false, 2, false);
                        setTimeout(function() {
                            airlineMap.invalidateSize();
                            destinationsMap.invalidateSize();
                        }, 100);
                        setTimeout(function() {
                            airlineMap.invalidateSize();
                            destinationsMap.invalidateSize();
                        }, 1000);
                    });

                    setUpTableFilter("#flightsTable");

                    $('#showFlightModal').on('hidden.bs.modal', function() {
                        flightsTable.$('tr.selected').removeClass('selected');
                        $("#reserveDivPassengers").hide();
                        $("#reserveDivFriends").hide();
                        $("#reserveDiv").show();
                        seatsToReserve = [];
                        $("#flightConnections").find('option').remove();
                    });
                    
                    $('#showAirlineModal').on('shown.bs.modal', function() {
                        setTimeout(function() {
                            airlineMap.invalidateSize()
                        }, 100);
                        setTimeout(function() {
                            airlineMap.invalidateSize()
                        }, 1000);
                    });

                    $('#showAirlineModal').on('hidden.bs.modal', function() {
                        airlinesTable.$('tr.selected').removeClass('selected');
                        airlineMap.off();
                        airlineMap.remove();
                        airlineMap = null;
                    });

                    $('#flightsTable tbody').on('click', 'tr', function() {
                        flightsTable.$('tr.selected').removeClass('selected');
                        $(this).addClass('selected');
                        shownFlight = flightsTable.row(this).data()[0];
                        loadFlight(shownFlight);
                        $("#showFlightModal").modal();
                    });
                    
                    $('#airlinesTable tbody').on('click', 'tr', function() {
                    	airlinesTable.$('tr.selected').removeClass('selected');
                        $(this).addClass('selected');
                        shownAirline = airlinesTable.row(this).data()[0];
                        loadAirline(shownAirline);
                        $("#showAirlineModal").modal();
                    });
                    
                    $('#reservationsTable tbody').on('click', 'tr', function() {
                    	var tgt = $(event.target);
                    	var resTable = $("#reservationsTable").DataTable();
                    	if (tgt[0].id == "cancelResButton") {
                    		var res = resTable.row(this).data()[0];
                    		cancelReservation(res);
                    	}
                    	else {
                    		reservationsTable.$('tr.selected').removeClass('selected');
                            $(this).addClass('selected');
                            shownReservation = reservationsTable.row(this).data()[0];
                            loadReservation(shownReservation);
                            $("#showReservationModal").modal();
                    	}
                    });
                    
                    $('#showReservationModal').on('hidden.bs.modal', function() {
                    	reservationsTable.$('tr.selected').removeClass('selected');
                    });
                    
                    $("#srcRoundTrip").change(function() {
                	    if(this.checked) {
                	        $(".retTripSrc").show();
                	    }
                	    else {
                	    	$(".retTripSrc").hide();
                	    	$("#srcRetDepTime").val("");
                	    }
                	});

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

                    $('.edit')
                        .click(
                            function() {
                                if ($(this).siblings().first().is(
                                        '[readonly]')) {
                                    $(this).siblings().first()
                                        .removeAttr('readonly');
                                } else {
                                    $(this).siblings().first().prop(
                                        'readonly', 'true');
                                }
                            });
                    
                    $('#friendsTable tbody').on('click', 'tr', function(event) {
                    	var tgt = $(event.target);
                    	var table = $("#friendsTable").DataTable();
                    	if (tgt[0].innerHTML == "Accept") {
                            acceptInvitation(table.row(this).data()[0], table.row(this).index());
                        } else if (tgt[0].innerHTML == "Decline") {
                        	declineInvitation(table.row(this).data()[0], table.row(this).index());
                        }
                    });

                    $('#usersTable tbody').on('click', 'tr', function(event) {
                    	var tgt = $(event.target);
                        if (tgt[0].id == "sendInvButton") {
                            var table = $("#usersTable").DataTable();
                            friendInvitation(table.row(this).data()[0], tgt[0].parentElement.id);
                        }
                    });
                    
                    setUpSearchHotelsForm();
                    
                    if(!isVisitor){
        	            $("#searchUserForm")
        	                .submit(
        	                    function(e) {
        	                        e.preventDefault();
        	                        let firstName = $("#userFirstName")
        	                            .val();
        	                        let lastName = $("#userLastName").val();
        	                        $
        	                            .ajax({
        	                                type: 'GET',
        	                                url: searchUsersURL,
        	                                headers: createAuthorizationTokenHeader(tokenKey),
        	                                contentType: 'application/json',
        	                                data: {
        	                                    "firstName": firstName,
        	                                    "lastName": lastName
        	                                },
        	                                success: function(data) {
        	                                    var table = $(
        	                                            '#usersTable')
        	                                        .DataTable();
        	                                    table.clear().draw();
        	                                    $
        	                                        .each(
        	                                            data,
        	                                            function(
        	                                                i,
        	                                                val) {
        	                                                var sendInv = "<div id='status" +
        	                                                    i +
        	                                                    "'><button id='sendInvButton'" +
        	                                                    " class='btn btn-default'>Send invitation</button></div>";
        	                                                table.row
        	                                                    .add(
        	                                                        [
        	                                                            val.email,
        	                                                            val.firstName,
        	                                                            val.lastName,
        	                                                            sendInv
        	                                                        ])
        	                                                    .draw(
        	                                                        false);
        	                                            });
        	                                },
        	                                error: function(
        	                                    XMLHttpRequest,
        	                                    textStatus,
        	                                    errorThrown) {
        	                                    alert("AJAX ERROR: " +
        	                                        textStatus);
        	                                }
        	                            });
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
        	                        type: 'PUT',
        	                        url: saveChangesURL,
        	                        contentType: 'application/json',
        	                        dataType: "html",
        	                        data: formToJSON(firstName, lastName,
        	                            phone, address, email),
        	                        success: function(data) {
        	                            if (data != "") {
        	                                toastr["error"](data);
        	                            }
        	                        },
        	                        error: function(XMLHttpRequest,
        	                            textStatus, errorThrown) {
        	                            alert("AJAX ERROR: " + textStatus);
        	                        }
        	                    });
        	                });
                    }
                    
                    $('a[href="#profile"]').click(function() {
                    	loadProfileData();
                    });
                    
                    $('a[href="#reservations"]').click(function() {
                        getReservations();
                        loadProfileData();
                    });

                    $('a[data-toggle="tab"]')
                        .on(
                            'shown.bs.tab',
                            function(e) {
                                $($.fn.dataTable.tables(true))
                                    .DataTable().columns.adjust();
                            });

                    setUpHotelsTab();

                    $("#reserveDivFriends").hide();
                    $("#reserveDivPassengers").hide();

                    $('#startYear').datepicker({
                        format: 'yyyy',
                        minViewMode: 'years',
                        autoclose: true
                    }).on('changeDate', function(selected) {
                        startDate = $("#startYear").val();
                        $('#endYear').datepicker('setStartDate', startDate);
                    });

                    $('#endYear').datepicker({
                        format: 'yyyy',
                        minViewMode: 'years',
                        autoclose: true
                    });

                    $('#selectModel').multiselect({
                        includeSelectAllOption: true,
                        nonSelectedText: 'Select model'
                    });

                    $("#vehicleGrade").slider({});

                    $('#vehicleType').multiselect({
                        includeSelectAllOption: true,
                        nonSelectedText: 'Select car body type'
                    });

                    $('#fuelType').multiselect({
                        includeSelectAllOption: true,
                        nonSelectedText: 'Select fuel type'
                    });

                    $('a[href="#cars"]').click(function() {
                        getVehicleProducers();
                        getAllVehicleTypes();
                        getAllFuelTypes();
                    });

                    $('#selectProducer').change(function() {
                        let value = $('#selectProducer').val();

                        if (value == "all") {
                            $('#selectModel').prop('disabled', 'disabled');
                            $('#selectModel').multiselect('dataprovider', []);
                        } else {
                            getModelsForProducer(value);
                            $('#selectModel').prop('disabled', 'false');
                        }
                    });

                    $('#searchVehiclesForm').submit(function(e) {
                        e.preventDefault();

                        let producer = $('#selectProducer').val();
                        let models = $('#selectModel').val();
                        let vehicleTypes = $('#vehicleType').val();
                        let fuelTypes = $('#fuelType').val();
                        let priceTo = emptyToNull($('#priceTo').val());
                        let numberOfSeats = emptyToNull($('#numberOfSeats').val());
                        let startDate = emptyToNull($('#startYear').val());
                        let endDate = emptyToNull($('#endYear').val());
                        let minGrade = $("#vehicleGrade").slider('getValue')[0];
                        let maxGrade = $("#vehicleGrade").slider('getValue')[1];
                        let country = $("#vehicleCountry").val();
                        
                        searchVehicles(producer, models, vehicleTypes, fuelTypes, priceTo, numberOfSeats, startDate, endDate, minGrade, maxGrade, country);
                    });

                    $('#vehiclesTable').DataTable({
                        "paging": false,
                        "info": false,
                        "orderCellsTop": true,
                        "fixedHeader": true,
                        "scrollY": "200px",
                        "scrollCollapse": true,
                        "columnDefs": [
                        	{
                        		"targets": [ 0 ],
                        		"visible": false,
                        		"searchable": true
                        	}
                        ]
                    });
                    
                    $('#quickReservationsTable').DataTable({
                        "paging": false,
                        "info": false,
                        "orderCellsTop": true,
                        "fixedHeader": true,
                        "scrollY": "200px",
                        "scrollCollapse": true,
                    });
                    
                    var currentVehicleID = null;
                    var currentVehicleProducer = null;
                    var currentVehicleModel = null;
                    var currentVehiclePrice = null;
                    $(document).on('click', '#vehiclesTable tbody tr', function() {
                    	let table = $("#vehiclesTable").DataTable();
                    	let rowData = table.row(this).data();
                		let title = rowData[1] + " " + rowData[2];
                		currentVehicleID = rowData[0];
                		currentVehicleProducer = rowData[1];
                		currentVehicleModel = rowData[2];
                		currentVehiclePrice = rowData[6];
                		$("#quickReservationsModalTitle").text(title);
                		getQuickReservationsForVehicle(rowData[0]);
                		getBranchOfficesForVehicle(rowData[0]);
                		$('#quickVehicleReservationsModal').modal('show');
                	});
                    
                    $('#vrstartDate').datepicker({
                        format: "dd/mm/yyyy",
                        minViewMode: 'days',
                        autoclose: true,
                        startDate: new Date()
                    });
                    
                    $('#vrendDate').datepicker({
                    	format: "dd/mm/yyyy",
                        minViewMode: 'days',
                        autoclose: true,
                        startDate: new Date()
                    });
                    
                    $("#vrstartDate").change(function() {
                    	let date = $("#vrstartDate").datepicker('getDate', '+1d');
                    	date.setDate(date.getDate()+1);
                    	$("#vrendDate").datepicker('setStartDate', date);
                    });
                    
                    $("#rvButton").click(function(e) {
                    	if(isVisitor){
                    		warnVisitorToLogIn();
                    		return;
                    	}
                    	e.preventDefault();
                    	
                    	let branch = $("#vehicleBranchOffices").val();
                    	
                    	var quickFlightReservation = JSON.parse(localStorage.getItem("quickFlightReservation"));
                    	if(quickFlightReservation != null){
                    		toastr["error"]("Cannot reserve vehicle if quick flight is reserved");
                    		return;
                    	}
                    	
                    	checkCountry(branch, currentVehicleID);
                    	
                    	if(error){
                    		error = false;
                    		return;
                    	}
                    	
                    	let start = $("#vrstartDate").datepicker('getDate');
                    	
                    	if(start === null || start === "") {
                    		toastr["error"]("Start date must have a value");
                    		return;
                    	} else if(!moment(JSON.parse(localStorage.getItem("flightReservation"))["other"]["landingTime"], 'DD.MM.YYYY hh:mm')
                    			.isSame(start, 'day')){
                    		toastr["error"]("Vehicle reservation start date must be same as flight landing date");
                    		return;
                    	}
                    	
                    	let end = $("#vrendDate").datepicker('getDate');
                    	
                    	if(end === null || end === "") {
                    		toastr["error"]("End date must have a value");
                    		return;
                    	}
                    	
                    	checkVehicleForPeriod(currentVehicleID, currentVehiclePrice, start, end, currentVehicleProducer, currentVehicleModel, branch);
                    });
                    $('#showReservationModal').on('hidden.bs.modal', function () {
                    	$("#cartDiv").hide();
                    	});
                    
                    
                    
                    if(isVisitor) // after whole setup, we adjust the page for
									// visitor
                    	adjustPageForVisitor(); 
        		},
        		error: function(XMLHttpRequest, textStatus, errorThrown) {
        			alert("AJAX ERROR: " + textStatus);
        		}
        	});        	
        });

function adjustPageForVisitor(){
	$('[href="#friends"]').closest('li').hide();
	$('[href="#reservations"]').closest('li').hide();
	$('[href="#profile"]').closest('li').hide();
	$("#reservationCart").hide();
	
	$("#logout").text("Log in");
	$("#logout").click(function(){
		document.location.href = loginPageURL;
	});
}

function warnVisitorToLogIn(){
	toastr["error"](`You must <u><a href=${loginPageURL}>log in</a></u> to be able to do this`);
}


function checkCountry(branchOffice, vehicle){
	var flightReservation = JSON.parse(localStorage.getItem("flightReservation"));
	if(flightReservation === null){
		toastr["error"]("Flight must be reserved first");
		error = true;
		return;
	}
	$.ajax({
        type: 'GET',
        url: checkCountryURL + branchOffice + "/" + vehicle,
        headers: createAuthorizationTokenHeader(tokenKey),
        contentType: "application/json",
        dataType: "text",
        async: false,
        success: function(data) {
        	var country = flightReservation["other"]["countryName"];
        	
        	if(data !== country){
        		toastr["error"]("Branch office country must be same as flight destination country");
        		error = true;
        	}
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });
}

function getBranchOfficesForVehicle(id){
	$.ajax({
        type: 'GET',
        url: getBranchOfficesForVehicleURL + id,
        headers: createAuthorizationTokenHeader(tokenKey),
        contentType: "application/json",
        dataType: "json",
        success: function(data) {
        	var select = $("#vehicleBranchOffices");
        	select.empty();
        	for(var branch of data){
        		select.append(new Option(branch, branch));
        	}
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });
}

function getAirlines() {
	$.ajax({
        type: 'GET',
        url: getAirlinesURL,
        headers: createAuthorizationTokenHeader(tokenKey),
        contentType: "application/json",
        success: function(data) {
            if (data != null) {
                var table = $('#airlinesTable').DataTable();
                table.clear().draw();
                $.each(data, function(i, val) {
                	var grade = val.averageGrade;
                	
                	if(grade !== 0){
                		grade = grade/5*100;
                	}
                	var roundedGrade = Math.round(val.averageGrade * 10)/10;
                	var rating = "<div class='star-ratings-sprite' title='" + roundedGrade + "/5.0'><span style='width:" + grade 
                	+ "%' class='star-ratings-sprite-rating'></span></div>";
                    table.row.add(
                        [ val.name, rating ]).draw(false);
                });
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });
}

function checkVehicleForPeriod(vehicleID, price, start, end, vehicleProducer, vehicleModel, branch) {
	$.ajax({
        type: 'POST',
        url: checkVehicleForPeriodURL,
        headers : createAuthorizationTokenHeader(tokenKey),
        contentType: "application/json",
        data: checkVehicleToJSON(vehicleID, start, end),
        success: function(data) {
        	if(data) {
        		var carRes = {
        				'fromDate' : start,
        				'toDate' : end,
        				'vehicleProducer' : vehicleProducer,
        				'vehicleModel' : vehicleModel,
        				'branchOfficeName' : branch,
        				'price': price*Math.round((end-start)/(1000*60*60*24)),
        				'quickVehicleReservationID': null
        				};
        		localStorage.setItem("carRes", JSON.stringify(carRes));
        		$('#carRes').text($('#quickReservationsModalTitle').text());
        		toastr["success"]("Vehicle reservation successfully added to cart");
        		$('#quickVehicleReservationsModal').modal('hide');
        	} else{
        		toastr["error"]("Vehicle is taken in given period");
        	}
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });
}

function checkVehicleToJSON(vehicleID, start, end) {
	return JSON.stringify({
        "vehicleID": vehicleID,
        "start": moment(start, "YYYY-MM-DD"),
        "end": moment(end, "YYYY-MM-DD")
    });
}

function getQuickReservationsForVehicle(id) {
	$.ajax({
        type: 'GET',
        url: getQuickReservationsForVehicleURL + id,
        dataType: "json",
        headers: createAuthorizationTokenHeader(tokenKey),
        success: function(data) {
        		let table = $('#quickReservationsTable').DataTable();
        		table.clear().draw();
        		for(let quickReservation of data) {
        			table.row.add([
        				quickReservation.quickVehicleReservationID,
                    	quickReservation.branchOfficeName,
                    	quickReservation.vehicleProducer,
                    	quickReservation.vehicleModel,
                    	moment(quickReservation.fromDate).format("DD/MM/YYYY"),
                    	moment(quickReservation.toDate).format("DD/MM/YYYY"),
                    	quickReservation.discount,
                    	quickReservation.price,
                    	"<button onclick='reserveQuickVehicleReservation(" + quickReservation.quickVehicleReservationID + "," + quickReservation.branchOfficeName + ")' class='btn btn-default reserve' type='button'>Reserve</a>"
                    ]).draw(false);
        		}
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });
}

function reserveQuickVehicleReservation(reservationID, branchOffice) {
	if(isVisitor){
		warnVisitorToLogIn();
		return;
	}
	var quickFlightReservation = JSON.parse(localStorage.getItem("quickFlightReservation"));
	if(quickFlightReservation != null){
		toastr["error"]("Cannot reserve hotel if quick flight is reserved");
		return;
	}
	
	checkCountry(branch, currentVehicleID);
	
	if(error){
		error = false;
		return;
	}
	let table = $('#quickReservationsTable').DataTable();
	var val = null;
	table.rows().every(function ( rowIdx, tableLoop, rowLoop ) {
	    if(this.data()[0] == reservationID){
	    	val = this.data(); 
	    	return;
	    }
	} );
	
	

	var start = moment(val[4], "DD.MM.YYYY HH:mm").toDate();
	if(start === null || start === "") {
		toastr["error"]("Start date must have a value");
		return;
	} else if(!moment(JSON.parse(localStorage.getItem("flightReservation"))["other"]["landingTime"], 'DD.MM.YYYY hh:mm')
			.isSame(start, 'date')){
		toastr["error"]("Quick vehicle reservation start date must be same as flight landing date");
		return;
	}
	
	var carRes = {
			'fromDate' : moment(val[4], "DD.MM.YYYY HH:mm").toDate(),
			'toDate' : moment(val[5], "DD.MM.YYYY HH:mm").toDate(),
			'vehicleProducer' : val[2],
			'vehicleModel' : val[3],
			'branchOfficeName' : val[1],
			'price' : val[7],
			'quickVehicleReservationID' :parseInt(reservationID)
			};
	console.log(carRes);
	localStorage.setItem("carRes", JSON.stringify(carRes));
	$('#carRes').text($('#quickReservationsModalTitle').text());
	toastr["success"]("Vehicle reservation successfully added to cart");
	$('#quickVehicleReservationsModal').modal('hide');
}

function emptyToNull(value) {
    if (value == "") {
        return null;
    } else {
        return value;
    }
}

function vehicleSearchFormToJSON(producer, models, vehicleTypes, fuelTypes, priceMax, numberOfSeats, startDate, endDate, minGrade, maxGrade, country) {
    return JSON.stringify({
        "producer": producer,
        "models": models,
        "vehicleTypes": vehicleTypes,
        "fuelTypes": fuelTypes,
        "price": priceMax,
        "seats": numberOfSeats,
        "startDate": startDate,
        "endDate": endDate,
        "minGrade": minGrade,
        "maxGrade": maxGrade,
        "country": country
    });
}

function searchVehicles(producer, models, vehicleTypes, fuelTypes, priceMax, numberOfSeats, startDate, endDate, minGrade, maxGrade, country) {
    $.ajax({
        type: 'POST',
        url: searchVehiclesURL,
        headers : createAuthorizationTokenHeader(tokenKey),
        contentType: "application/json",
        data: vehicleSearchFormToJSON(producer, models, vehicleTypes, fuelTypes, priceMax, numberOfSeats, startDate, endDate, minGrade, maxGrade, country),
        success: function(data) {
            if (data != null) {
                let table = $('#vehiclesTable').DataTable();
                table.clear().draw();

                for (let vehicle of data) {
                    table.row.add([
                    	vehicle.id,
                        vehicle.producer,
                        vehicle.model,
                        vehicle.vehicleType,
                        vehicle.fuelType,
                        vehicle.numberOfSeats,
                        vehicle.pricePerDay,
                        vehicle.yearOfProduction,
                        vehicle.averageGrade
                    ]).draw(false);
                }
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });
}

function getAllVehicleTypes() {
    $.ajax({
        type: 'GET',
        url: getAllVehicleTypesURL,
        dataType: "json",
        headers: createAuthorizationTokenHeader(tokenKey),
        success: function(data) {
            let options = [];
            for (let type of data) {
                options.push({
                    label: type,
                    value: type
                });
            }
            $('#vehicleType').multiselect('dataprovider', options);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });
}

function getAllFuelTypes() {
    $.ajax({
        type: 'GET',
        url: getAllFuelTypesURL,
        dataType: "json",
        headers: createAuthorizationTokenHeader(tokenKey),
        success: function(data) {
            let options = [];
            for (let type of data) {
                options.push({
                    label: type,
                    value: type
                });
            }
            $('#fuelType').multiselect('dataprovider', options);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });
}

function getVehicleProducers() {
    $.ajax({
        type: 'GET',
        url: getVehicleProducersURL,
        dataType: "json",
        headers: createAuthorizationTokenHeader(tokenKey),
        success: function(data) {
            let producers = $("#selectProducer");
            producers.empty();
            producers.append('<option value="all" selected>All producers</option>');
            for (let producer of data) {
                producers.append('<option value="' + producer + '">' + producer + '</option');
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });
}

function getModelsForProducer(producer) {
    $.ajax({
        type: 'GET',
        url: getModelsForProducerURL + producer,
        dataType: "json",
        headers: createAuthorizationTokenHeader(tokenKey),
        success: function(data) {
            let options = [];
            for (let model of data) {
                options.push({
                    label: model,
                    value: model
                });
            }
            $('#selectModel').multiselect('dataprovider', options);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
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

function getDestinations() {
    $.ajax({
        type: 'GET',
        url: getDestinationsURL,
        headers: createAuthorizationTokenHeader(tokenKey),
        success: function(data) {
            if (data != null) {
                var start = $("#startDestination");
                var end = $("#endDestination");
                $.each(data, function(i, val) {
                    start.append("<option value=" + val + ">" + val +
                        "</option>");
                    end
                        .append("<option value=" + val + ">" + val +
                            "</option>");
                });
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });
}

function searchFlights(e) {
    e.preventDefault();
    var startDestination = $("#startDestination option:selected").text();
    var endDestination = $("#endDestination  option:selected").text();
    if (startDestination == endDestination) {
        toastr["error"]
            ("Start destination and end destination must not be the same.");
        return;
    }
    var departureTime = $("#departureTime").val();
    if (departureTime == null || departureTime == "") {
        toastr["error"]("Departure time is not valid.");
        return;
    }
    if (moment(departureTime).isBefore(moment(), 'day')) {
    	toastr["error"]("Departure time can not be before today's date.");
    	return;
    }
    var multiCity = $("#multicity:checked").length > 0;
    var roundTrip = $("#srcRoundTrip:checked").length > 0;
    var retDepTime;
    if (roundTrip) {
		retDepTime = $("#srcRetDepTime").val();
		if (retDepTime == null || retDepTime == "") {
			toastr["error"]("Returning departure time is not valid.");
			return;
		}
		/*
		 * if (moment(retDepTime).isBefore(departureTime)) {
		 * toastr["error"]("Returning departure time must be after the departure
		 * time."); return; }
		 */
	}
    $.ajax({
        type: 'POST',
        url: searchFlightsURL,
        headers: createAuthorizationTokenHeader(tokenKey),
        contentType: "application/json",
        data: JSON.stringify({
            "startDestination": startDestination,
            "endDestination": endDestination,
            "departureTime": departureTime,
            "multiCity": multiCity,
            "roundTrip": roundTrip,
            "returningDepartureTime": retDepTime
        }),
        success: function(data) {
            if (data !== "") {
                var table = $('#flightsTable').DataTable();
                table.clear().draw();
                $.each(data, function(i, val) {
                    table.row.add(
                        [val.flightCode, val.departureTime, val.landingTime, val.airline ]).draw(false);
                });
            }
            else {
            	toastr["error"]("Departure time can not be before today's date.");
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });
}

function getFriends() {
    $
        .ajax({
            type: 'GET',
            url: getFriendInvitationsURL,
            headers: createAuthorizationTokenHeader(tokenKey),
            success: function(data) {
                if (data != null) {
                    var table = $('#friendsTable').DataTable();
                    table.clear().draw();
                    $
                        .each(
                            data,
                            function(i, val) {
                                if (val.status == "Invitation pending") {
                                    var sendInv = "<div id='invFriendStatus'><button id='sendInvButton' class='btn btn-default'>Accept</button>";
                                    sendInv += "<button id='sendInvButton' class='btn btn-default'>Decline</button></div>";
                                    table.row.add(
                                            [val.email,
                                                val.firstName,
                                                val.lastName,
                                                sendInv
                                            ])
                                        .draw(false);
                                } else {
                                    table.row
                                        .add(
                                            [
                                                val.email,
                                                val.firstName,
                                                val.lastName,
                                                "<b>" +
                                                val.status +
                                                "</b>"
                                            ])
                                        .draw(false);
                                }
                            });
                }
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                alert("AJAX ERROR: " + textStatus);
            }
        });
}

function friendInvitation(invitedUser, idx) {
    idx = idx.substring(6);
    $.ajax({
        type: 'POST',
        url: friendInvitationURL,
        headers: createAuthorizationTokenHeader(tokenKey),
        data: invitedUser,
        success: function(data) {
            if (data.toastType == "success") {
                toastr[data.toastType](data.message);
                $("#status" + idx).html("<b>Invitation sent</b>");
                getFriends();
            } else {
                toastr[data.toastType](data.message);
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });
}

function acceptInvitation(acceptedUser, idx) {
    $.ajax({
        type: 'POST',
        url: acceptInvitationURL,
        headers: createAuthorizationTokenHeader(tokenKey),
        data: acceptedUser,
        success: function(data) {
            if (data.toastType == "success") {
                toastr[data.toastType](data.message);
                getFriends();
            } else {
                toastr[data.toastType](data.message);
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });
}

function declineInvitation(declinedUser, idx) {
    $.ajax({
        type: 'POST',
        url: declineInvitationURL,
        headers: createAuthorizationTokenHeader(tokenKey),
        contentType: 'application/json',
        data: declinedUser,
        success: function(data) {
            if (data.toastType == "success") {
                toastr[data.toastType](data.message);
                getFriends();
            } else {
                toastr[data.toastType](data.message);
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });
}

var firstPrice = 0;
var businessPrice = 0;
var economyPrice = 0;

function resetReservationModal() {
    firstClass = [];
    businessClass = [];
    economyClass = [];
    firstSeatLabel = 1;
    reservedSeats = [];
    $("#total").html(0);
    $("#counter").html(0);
    $("#selected-seats").empty();
    $("#userPassNumber").val("");
    $("#numOfBags").val(0);
    $("#passengersTable tr").remove();
}

function getPlaneSeats(code) {
    $.ajax({
        url: getPlaneSeatsURL,
        contentType: "application/json",
        data: {
            'flightCode': code
        },
        dataType: "json",
        headers: createAuthorizationTokenHeader(tokenKey),
        success: function(data) {
            firstPrice = data["firstClassPrice"];
            businessPrice = data["businessClassPrice"];
            economyPrice = data["economyClassPrice"];
            resetReservationModal();
            renderPlaneSeats(data["planeSegments"], data["reservedSeats"]);
        }
    });
}

function loadProfileData() {
    $
        .ajax({
            type: 'GET',
            url: loadUserInfoURL,
            dataType: "json",
            headers: createAuthorizationTokenHeader(tokenKey),
            success: function(data) {
                if (data != null) {
                    $('input[name="fname"]').val(data.firstName);
                    $('input[name="lname"]').val(data.lastName);
                    $('input[name="phone"]').val(data.phone);
                    $('input[name="address"]').val(data.address);
                    $('#email').text(data.email);
                    $('#availableDiscountPoints').val(data.availablePoints);
                    userMail = data.email;
                    var table = $('#friendsTable').DataTable();
                    table.clear().draw();
                    $
                        .each(
                            data.friends,
                            function(i, val) {
                                if (val.status == "Invitation pending") {
                                    var sendInv = "<div id='invFriendStatus'><button id='sendInvButton' class='btn btn-default'>Accept</button>";
                                    sendInv += "<button id='sendInvButton' class='btn btn-default'>Decline</button></div>";
                                    table.row.add(
                                            [val.email,
                                                val.firstName,
                                                val.lastName,
                                                sendInv
                                            ])
                                        .draw(false);
                                } else {
                                    table.row
                                        .add(
                                            [
                                                val.email,
                                                val.firstName,
                                                val.lastName,
                                                "<b>" +
                                                val.status +
                                                "</b>"
                                            ])
                                        .draw(false);
                                }
                            });
                }
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                alert("AJAX ERROR: " + textStatus);
            }
        });
}

function formToJSON(firstName, lastName, phone, address, email) {
    return JSON.stringify({
        "firstName": firstName,
        "lastName": lastName,
        "phoneNumber": phone,
        "address": address,
        "email": email
    });
}

/* SEAT CHART */
var firstSeatLabel = 1;
var firstClass = [];
var businessClass = [];
var economyClass = [];
var reservedSeats = [];
var seatsToReserve = [];

function showPlaneSeats(seats) {
	var $cart = $('#selected-seats'), $counter = $('#counter'), $total = $('#total'), sc = $(
			'#seat-map')
			.seatCharts(
					{
						map : seats,
						seats : {
							f : {
								price : firstPrice,
								classes : 'first-class',
								category : 'First Class'
							},
							e : {
								price : economyPrice,
								classes : 'economy-class',
								category : 'Economy Class'
							},
							b : {
								price : businessPrice,
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
								var seat = this.settings.id.split("_");
								if (seat[1] == 4 || seat[1] == 5) {
									seatsToReserve.push((seat[0] + "_" +  (seat[1] - 1)) + "_" + this.settings.character);
								}
								else {
									seatsToReserve.push(this.settings.id + "_" + this.settings.character);
								}
								console.log(seatsToReserve);
								$(
										'<li>'
												+ this.data().category
												+ ' Seat # '
												+ this.settings.label
												+ ': <b>$'
												+ this.data().price
												+ '</b> <a href="#" class="cancel-cart-item">[cancel]</a></li>')
										.attr('id',
												'cart-item-' + this.settings.id)
										.data('seatId', this.settings.id)
										.appendTo($cart);
								$counter.text(sc.find('selected').length + 1);
								$total.text(recalculateTotal(sc)
										+ this.data().price);

								return 'selected';
							} else if (this.status() == 'selected') {
								$counter.text(sc.find('selected').length - 1);
								$total.text(recalculateTotal(sc)
										- this.data().price);

								$('#cart-item-' + this.settings.id).remove();
								let index = seatsToReserve.indexOf(this.settings.id);
								seatsToReserve.splice(index, 1);
								console.log(seatsToReserve);
								return 'available';
							} else if (this.status() == 'unavailable') {
								return 'unavailable';
							} else {
								return this.style();
							}
						}
					});

	$('#selected-seats').on('click', '.cancel-cart-item', function() {
		sc.get($(this).parents('li:first').data('seatId')).click();
	});
}

function recalculateTotal(sc) {
    var total = 0;

    // basically find every selected seat and sum its price
    sc.find('selected').each(function() {
        total += this.data().price;
    });

    return total;
}

function renderPlaneSeats(planeSegments, reserved) {
    if (planeSegments[0].length == 0 && planeSegments[1].length == 0 &&
        planeSegments[2].length == 0) {
        showPlaneSeats([]);
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
        $('#seat-map,#seat-map *').unbind().removeData();
        firstSeatLabel = 1;
        showPlaneSeats(seats);
    }
}

function initializeSeats(number, seatClass, planeSegment, label, cat) {
    var fixer = 0;
    if (cat == 2)
        fixer = firstClass.length;
    else if (cat == 3)
        fixer = firstClass.length + businessClass.length;
    for (var i = 0; i < number; i++)
        seatClass.push("_____");
    $.each(planeSegment, function(i, val) {
        var row = seatClass[val["row"] - 1 - fixer].split("");
        if (reservedSeats.includes(val["row"] + "_" + val["column"]))
            row[val["column"] - 1 + Math.floor(val["column"] / 3)] = 'a';
        else
            row[val["column"] - 1 + Math.floor(val["column"] / 3)] = label;
        seatClass[val["row"] - 1 - fixer] = row.join("");
    });
}

/* HOTELS TAB */

function setUpHotelsTab() {
    $("#searchHotelGrade").slider({});
    $('#searchRoomsDateRange').daterangepicker({
        minDate: new Date(),
        locale: {
            format: 'DD/MM/YYYY'
        },
    });
    $("#searchRoomsPrice").slider({});
    $("#searchRoomsGrade").slider({});

    setUpTablesHotelsTab();
    setUpShowHotelModal();

}

function setUpShowHotelModal() {
    $('#showHotelModal').on('shown.bs.modal', function() {
        setTimeout(function() {
            hotelMap.invalidateSize()
        }, 100);
        setTimeout(function() {
            hotelMap.invalidateSize()
        }, 1000);
    });

    $('#showHotelModal').on('hidden.bs.modal', function() {
        hotelsTable.$('tr.selected').removeClass('selected');
        hotelMap.off();
        hotelMap.remove();
        hotelMap = null;
    });
}

function setUpTablesHotelsTab() {
	hotelsTable = $('#hotelsTable').DataTable({
		"paging" : false,
		"info" : false,
		"orderCellsTop" : true,
		"fixedHeader" : true
	});
	setUpTableFilter("#hotelsTable");

	roomsTable = $('#roomsTable').DataTable({
		"paging" : false,
		"info" : false,
		"orderCellsTop" : true,
		"fixedHeader" : true
	});
	setUpTableFilter("#roomsTable");

	additionalServicesTable = $('#additionalServicesTable').DataTable({
		"paging" : false,
		"info" : false,
		"orderCellsTop" : true,
		"fixedHeader" : true
	});
	setUpTableFilter("#additionalServicesTable", "Add to reservation");
	
	quickHotelReservationsTable = $('#quickHotelReservationsTable').DataTable({
		"paging" : false,
		"info" : false,
		"orderCellsTop" : true,
		"fixedHeader" : true
	});

	$('#hotelsTable tbody').on('click', 'tr', function() {
		hotelsTable.$('tr.selected').removeClass('selected');
		$(this).addClass('selected');
		shownHotel = hotelsTable.row(this).data()[0];
		loadHotel(shownHotel);
		$("#showHotelModal").modal();
	});
}

function setUpSearchHotelsForm(e) {
	$("#searchHotelsForm")
    .submit(
        function(e) {
            e.preventDefault();
            searchHotelCountry = $("#searchHotelCountry").val();
		    $.ajax({
		        type: "GET",
		        url: searchHotelsURL,
		        contentType: "application/json",
		        data: {
		            'name': $("#searchHotelName").val(),
		            'fromGrade': $("#searchHotelGrade").slider('getValue')[0],
		            'toGrade': $("#searchHotelGrade").slider('getValue')[1],
		            'country': searchHotelCountry
		        },
		        dataType: "json",
		        headers: createAuthorizationTokenHeader(tokenKey),
		        success: function(data) {
		            renderHotels(data);
		        },
		        error: function(XMLHttpRequest, textStatus, errorThrown) {
		            alert("AJAX ERROR: " + textStatus);
		        }
		    });
    });
}

function renderHotels(data) {
    if (data != null) {
        var table = $("#hotelsTable").DataTable();
        table.clear().draw();
        $.each(data, function(i, val) {
            table.row.add([val.name, val.averageGrade]).draw(false);
        });
    }
}

function loadHotel(name) {
	$.ajax({
		type : 'GET',
		url : getDetailedHotelURL,
		contentType : "application/json",
		data : {
			'name' : name
		},
		dataType : "json",
		headers : createAuthorizationTokenHeader(tokenKey),
		success : function(data) {
			if (data != null) {
				$("#hotelName").val(data["name"]);
				$("#hotelGrade").text(data["averageGrade"]);
				$("#hotelDescription").text(data["description"]);
				if (hotelMap != null) {
					hotelMap.off();
					hotelMap.remove();
					hotelMap = null;
				}
				hotelMap = setUpMap(hotelMap, data["latitude"], data["longitude"],
						'hotelMapDiv', false);
				renderAdditionalServices(data["additionalServices"]);
				renderQuickHotelReservations(data["quickReservations"]);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function loadAirline(name) {
	$.ajax({
		type : 'GET',
		url : getDetailedAirlineURL,
		contentType : "application/json",
		data : {
			'name' : name
		},
		dataType : "json",
		headers : createAuthorizationTokenHeader(tokenKey),
		success : function(data) {
			if (data != null) {
				$("#airlineName").val(data["name"]);
				var grade = data["averageGrade"];
            	
            	if(grade !== 0){
            		grade = grade/5*100;
            	}
            	var roundedGrade = Math.round(data["averageGrade"]*10)/10;
            	var rating = "<div class='star-ratings-sprite'><span style='width:" + grade 
            	+ "%' class='star-ratings-sprite-rating'></span></div><p>" + roundedGrade + "/5.0";
				$("#airlineGrade").html(rating);
				$("#airlineDescription").text(data["description"]);
				if (airlineMap != null) {
					airlineMap.off();
					airlineMap.remove();
					airlineMap = null;
				}
				airlineMap = setUpMap(airlineMap, data["latitude"], data["longitude"],
						'airlineMapDiv', false);
				renderDestinations(data["destinations"]);
				renderQuickFlightReservations(data["quickReservations"]);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function loadFlight(code) {
    $.ajax({
        type: 'GET',
        url: getDetailedFlightURL,
        contentType: "application/json",
        data: {
            'flightCode': code
        },
        dataType: "json",
        headers: createAuthorizationTokenHeader(tokenKey),
        success: function(data) {
            if (data != null) {
            	localStorage.setItem("shownFlight", JSON.stringify(data));
                localStorage.setItem("flightCode", code);

                $("#startDest").text(data["startDestination"]);
                $("#endDest").text(data["endDestination"]);
                $("#depTime").text(data["departureTime"]);
                $("#landTime").text(data["landingTime"]);
                $("#flightAirline").text(data["airlineName"]);
                if (data["roundTrip"]) {
                	$("#retDepartureTime").text(data["returningDepartureTime"]);
                    $("#retLandingTime").text(data["returningLandingTime"]);
					$(".retTrip").show();
				}
                else {
                	$(".retTrip").hide();
                }
                var date1 = moment(data["departureTime"], 'DD.MM.YYYY hh:mm');
                var date2 = moment(data["landingTime"], 'DD.MM.YYYY hh:mm');
                var diff = date2.diff(date1, 'minutes');
                $("#flightDuration").text(diff);
                $("#flightDistance").text(data["flightDistance"]);
                var conn = $("#flightConnections");
                if (data["connections"].length == 0) {
                    conn.append("<option value=''></option>");
                } else {
                    $.each(data["connections"], function(i, val) {
                        conn.append("<option value=" + val + ">" + val +
                            "</option>");
                    });
                }
                $("#flightConnections").text(data["description"]);
                $("#pricePerBag").text(data["pricePerBag"]);
                $("#averageGrade").text(data["averageGrade"]);
                getPlaneSeats(code);
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });
}

function renderRooms(data) {
	roomsTable.clear().draw();
	$.each(data, function(i, val) {
		roomsTable.row.add(
				[ val.roomNumber, val.price, val.numberOfPeople, val.averageGrade,
					`<button onclick="reserveRoomNumber('${val.roomNumber}', ${val.price})" class="btn btn-default">Reserve</a>` ]).draw(false);
	});
}

function renderAdditionalServices(data) {
    additionalServicesTable.clear().draw();
    $.each(data, function(i, val) {
        additionalServicesTable.row.add([val.name, val.price,
            `<button onclick="reserveAdditionalService('${val.name}')" class="btn btn-default"><i class="fa fa-plus"></i></button>`
        ]).draw(false);
    });
}

function reserveAdditionalService(name) {
	if(isVisitor){
		warnVisitorToLogIn();
		return;
	}
    var indexes = additionalServicesTable.rows().eq(0).filter(function(rowIdx) {
        return additionalServicesTable.cell(rowIdx, 0).data() === name ? true : false;
    });
    var selectedRow = additionalServicesTable.rows(indexes).nodes().to$();
    selectedRow.addClass('reservedAdditionalService');
    selectedRow.find("i").attr("class", "fa fa-minus");
    selectedRow.find("button").attr("onclick", `dereserveAdditionalService('${name}')`);
}

function dereserveAdditionalService(name) {
    var indexes = additionalServicesTable.rows().eq(0).filter(function(rowIdx) {
        return additionalServicesTable.cell(rowIdx, 0).data() === name ? true : false;
    });
    var selectedRow = additionalServicesTable.rows(indexes).nodes().to$();
    selectedRow.removeClass('reservedAdditionalService');
    selectedRow.find("i").attr("class", "fa fa-plus");
    selectedRow.find("button").attr("onclick", `reserveAdditionalService('${name}')`);
}

function renderQuickHotelReservations(data){
	quickHotelReservationsTable.clear().draw();
	$.each(data, function(i, val) {
		var additionalServiceNames = val.additionalServiceNames.join('<br>');
		quickHotelReservationsTable.row.add([
										val.id,
										val.discountedPrice,
										val.discount,
										moment(val.fromDate).format("DD.MM.YYYY HH:mm"),
										moment(val.toDate).format("DD.MM.YYYY HH:mm"),
										val.hotelRoomNumber,
										additionalServiceNames,
										`<button onclick="reserveQuickHotelReservation('${val.id}')" class="btn btn-default">Reserve</button>` ])
						.draw(false);
			});
}

function reserveRoomNumber(roomNumber, roomPrice){
	if(isVisitor){
		warnVisitorToLogIn();
		return;
	}
	var drp = $('#searchRoomsDateRange').data('daterangepicker');
	
	var quickFlightReservation = JSON.parse(localStorage.getItem("quickFlightReservation"));
	if(quickFlightReservation != null){
		toastr["error"]("Cannot reserve hotel if quick flight is reserved");
		return;
	}
	
	var flightReservation = JSON.parse(localStorage.getItem("flightReservation"));
	if(flightReservation === null){
		toastr["error"]("Flight must be reserved first");
		return;
	}
		
	
	var country = flightReservation["other"]["countryName"];
	if(country !== searchHotelCountry){
		toastr["error"]("Hotel country must be same as flight destination country");
		return;
	}
	
	var start = drp.startDate.toDate();
	if(start === null || start === "") {
		toastr["error"]("Start date must have a value");
		return;
	} else if(!moment(JSON.parse(localStorage.getItem("flightReservation"))["other"]["landingTime"], 'DD.MM.YYYY hh:mm')
			.isSame(start, 'date')){
		toastr["error"]("Hotel room reservation start date must be same as flight landing date");
		return;
	}
	
	var price = 0;
	additionalServiceNames = [];
	additionalServicesTable.rows('.reservedAdditionalService').every(function ( rowIdx, tableLoop, rowLoop ) {
	    additionalServiceNames.push(this.data()[0]);
	    price += this.data()[1];
	} );
	price += roomPrice*Math.round((drp.endDate.toDate()-drp.startDate.toDate())/(1000*60*60*24));
	var hotelRes = {'fromDate' : drp.startDate.toDate(),
					'toDate' : drp.endDate.toDate(),
					'hotelRoomNumber' : roomNumber,
					'additionalServiceNames' : additionalServiceNames,
					'hotelName' : shownHotel,
					'price' : price,
					'quickReservationID' : null};
	localStorage.setItem("hotelRes", JSON.stringify(hotelRes));
	$('#hotelRes').text(shownHotel);
	additionalServicesTable.$('tr.reservedAdditionalService').removeClass('reservedAdditionalService');
	toastr["success"]("Hotel room reservation successfully added to cart");
}

function reserveQuickHotelReservation(quickID){
	if(isVisitor){
		warnVisitorToLogIn();
		return;
	}
	
	var flightReservation = JSON.parse(localStorage.getItem("flightReservation"));
	if(flightReservation === null){
		toastr["error"]("Flight must be reserved first");
		return;
	}
		
	
	var country = flightReservation["other"]["countryName"];
	if(country !== searchHotelCountry){
		toastr["error"]("Hotel country must be same as flight destination country");
		return;
	}
	
	
	var val = null;
	quickHotelReservationsTable.rows().every(function ( rowIdx, tableLoop, rowLoop ) {
	    if(this.data()[0] == quickID){
	    	val = this.data(); 
	    	return;
	    }
	} );
	
	var start = moment(val[3], "DD.MM.YYYY HH:mm").toDate();
	if(start === null || start === "") {
		toastr["error"]("Start date must have a value");
		return;
	} else if(!moment(JSON.parse(localStorage.getItem("flightReservation"))["other"]["landingTime"], 'DD.MM.YYYY hh:mm')
			.isSame(start, 'date')){
		toastr["error"]("Quick hotel room reservation start date must be same as flight landing date");
		return;
	}
	
	var hotelRes = {'fromDate' : start,
			'toDate' : moment(val[4], "DD.MM.YYYY HH:mm").toDate(),
			'hotelRoomNumber' : val[5],
			'additionalServiceNames' : val[6].split('<br>'),
			'hotelName' : shownHotel,
			'price' : val[1],
			'quickReservationID' : quickID};
	localStorage.setItem("hotelRes", JSON.stringify(hotelRes));
	$('#hotelRes').text(shownHotel);
	toastr["success"]("Quick hotel room reservation successfully added to cart");
}

function searchRooms(e) {
    e.preventDefault();
    var drp = $('#searchRoomsDateRange').data('daterangepicker');
    $.ajax({
        type: "GET",
        url: searchRoomsURL + shownHotel,
        contentType: "application/json",
        data: {
            'fromDate': drp.startDate.toDate(),
            'toDate': drp.endDate.toDate(),
            'forPeople': $('#searchRoomsForPeople').val(),
            'fromPrice': $("#searchRoomsPrice").slider('getValue')[0],
            'toPrice': $("#searchRoomsPrice").slider('getValue')[1],
            'fromGrade': $("#searchRoomsGrade").slider('getValue')[0],
            'toGrade': $("#searchRoomsGrade").slider('getValue')[1]
        },
        dataType: "json",
        headers: createAuthorizationTokenHeader(tokenKey),
        success: function(data) {
            renderRooms(data);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });
}

/* UTILITY */

function setUpMap(destMap, latitude, longitude, div, draggable, zoom=MAP_ZOOM, addPin = true) {
	if (destMap != null) {
		destMap.off();
		destMap.remove();
	}
	destMap = L.map(div).setView([latitude, longitude], zoom);
    L.tileLayer(tileLayerURL, {
        maxZoom: MAX_MAP_ZOOM,
        id: MAP_ID
    }).addTo(destMap);
    if(addPin){
	    var marker = L.marker([latitude, longitude], {
	        draggable: draggable
	    }).addTo(destMap);
	    if (draggable) {
	        marker.on('dragend', function(e) {
	            $("#latitude").val(marker.getLatLng().lat);
	            $("#longitude").val(marker.getLatLng().lng);
	        });
	    }
    }
    return destMap;
}

function setUpTableFilter(tableID, exceptColumn=""){
	var newRow = $(tableID + ' thead tr').clone(true);
	newRow.find("th").filter(function() {
	    return $(this).text() === exceptColumn;
	}).html("");
	newRow.appendTo(tableID + ' thead');
	$(tableID + ' thead tr:eq(1) th').each(
			function(i) {
				var title = $(this).text();
				if(title != ""){
					$(this).html(
							'<input type="text" placeholder="Filter"/>');
	
					$('input', this).on('keyup change', function() {
						table = $(tableID).DataTable();
						if (table.column(i).search() !== this.value) {
							table.column(i).search(this.value).draw();
						}
					});
				}
			});
}

/* FLIGHT RESERVATION */

function showFriendsStep(e) {
	if(isVisitor){
		warnVisitorToLogIn();
		return;
	}
    e.preventDefault();
    var userPass = $("#userPassNumber").val();
    var passRegEx = /[0-9]{9}/;
    if (userPass == "" || userPass == undefined || passRegEx.test(userPass) == false) {
        toastr["error"]("Invalid passport number.");
        return;
    }
    var numOfBags = $("#numOfBags").val();
    if (isNaN(numOfBags) || numOfBags < 0) {
        toastr["error"]("Invalid number of bags.");
        return;
    }
    var numberOfPassengers = seatsToReserve.length;
    if (numberOfPassengers == 0) {
        toastr["error"]("You did not choose any seat.");
        return;
    }
    
    var shownFlight = JSON.parse(localStorage.getItem("shownFlight"));
    
    var flightReservation = {
        "flightCode": localStorage.getItem("flightCode"),
        "numberOfPassengers": numberOfPassengers,
        "seatsLeft": numberOfPassengers - 1,
        "invitedFriends": [],
        "passengers": [{
            "firstName": "",
            "lastName": "",
            "passport": userPass,
            "numberOfBags": numOfBags
        }],
        "seats": seatsToReserve,
        "other" : {"startDestination" : shownFlight["startDestination"],
        			"endDestination" : shownFlight["endDestination"],
        			"departureTime" : shownFlight["departureTime"],
        			"landingTime" : shownFlight["landingTime"],
        			"airlineName" : shownFlight["airlineName"],
        			"flightDistance" : shownFlight["flightDistance"],
        			"connections" : shownFlight["connections"],
        			"roundTrip" : shownFlight["roundTrip"],
        			"returningDepartureTime" : shownFlight["returningDepartureTime"],
        			"returningLandingTime" : shownFlight["returningLandingTime"],
        			"countryName" : shownFlight["countryName"],
        			"pricePerBag" : shownFlight["pricePerBag"],
        			"firstClassPrice" : shownFlight["firstClassPrice"],
        			"businessClassPrice" : shownFlight["businessClassPrice"],
        			"economyClassPrice" : shownFlight["economyClassPrice"]}
    };
    localStorage.setItem("flightReservation", JSON.stringify(flightReservation));
    if (flightReservation["seatsLeft"] == 0) {
        toastr["success"]("Successfully added flight reservation to cart.");
        $('#showFlightModal').modal('toggle');
        localStorage.removeItem("quickFlightReservation");
        localStorage.setItem("flightReservation", JSON.stringify(flightReservation));
        var startDest = shownFlight["startDestination"];
        var endDest = shownFlight["endDestination"];
        var flightDate = shownFlight["departureTime"];
        $("#flightRes").html(startDest + "-" + endDest + " " + flightDate);
        $("#vehicleCountry").val(shownFlight["countryName"]);
        $("#searchHotelCountry").val(shownFlight["countryName"]);
        localStorage.setItem("flightRes", "true");
    }
    $.ajax({
        type: 'GET',
        url: getFriendsURL,
        headers: createAuthorizationTokenHeader(tokenKey),
        success: function(data) {
            if (data != null) {
                var table = $('#inviteFriendsTable').DataTable();
                table.clear().draw();
                $.each(data, function(i, val) {
                    table.row.add([val.email, val.firstName, val.lastName,
                        "<input type='checkbox' value='" + val.email + "'>"
                    ]).draw(false);
                });
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });
    $("#reserveDiv").hide();
    $("#reserveDivFriends").show();
}

function showLastStep(e) {
	e.preventDefault();
	var numOfInvited = $('#reserveDivFriends').find('input[type=checkbox]:checked').length;
	var flightReservation = JSON.parse(localStorage.getItem("flightReservation"));
	if (numOfInvited > flightReservation["seatsLeft"]) {
		toastr["error"]("You want to invite more friends than number of seats reserved.");
		return;
	}
	var invitedFriends = $("#reserveDivFriends").find('input[type=checkbox]:checked');
	$.each(invitedFriends, function(i, friend) {
		flightReservation["invitedFriends"].push(friend.value);
		flightReservation["seatsLeft"]--;
	});
	if (flightReservation["seatsLeft"] == 0) {
		toastr["success"]("Successfully added flight reservation to cart.");
		$('#showFlightModal').modal('toggle');
		localStorage.removeItem("quickFlightReservation");
		localStorage.setItem("flightReservation", JSON.stringify(flightReservation));
		var shownFlight = JSON.parse(localStorage.getItem("shownFlight"));
		var startDest = shownFlight["startDestination"];
		var endDest = shownFlight["endDestination"];
		var flightDate = shownFlight["departureTime"];
		$("#flightRes").html(startDest + "-" + endDest + " " + flightDate);
		$("#vehicleCountry").val(shownFlight["countryName"]);
	    $("#searchHotelCountry").val(shownFlight["countryName"]);
		localStorage.setItem("flightRes", "true");
	}
	else {
		var lastStepTable = $("#passengersTable");
		for (var i = 0; i < flightReservation["seatsLeft"]; i++) {
			var passengerNumber = flightReservation["numberOfPassengers"] - flightReservation["seatsLeft"] + i + 1;
			var tableRow = "<tr><td>Passenger</td><td>" + passengerNumber + "</td></tr>";
			tableRow += "<tr><td>First name</td><td><input type='text' name='passFirstName'/></td><tr>";
			tableRow += "<tr><td>Last name</td><td><input type='text' name='passLastName'/></td><tr>";
			tableRow += "<tr><td>Passport number</td><td><input type='text' name='passportNumber'/></td><tr>";
			tableRow += "<tr><td>Number of bags</td><td><input type='number' name='bags'/></td><tr>";
			tableRow += "<tr><td></td><td></td></tr>";
			lastStepTable.append(tableRow);
		}
		$("#reserveDivFriends").hide();
		$("#reserveDivPassengers").show();
		localStorage.setItem("flightReservation", JSON.stringify(flightReservation));
	}	
}

function endReservation(e) {
    e.preventDefault();
    var flightReservation = JSON.parse(localStorage.getItem("flightReservation"));
    var firstNames = $("input[name='passFirstName']").map(function() {
        return $(this).val();
    }).get();
    var lastNames = $("input[name='passLastName']").map(function() {
        return $(this).val();
    }).get();
    var passports = $("input[name='passportNumber']").map(function() {
        return $(this).val();
    }).get();
	var passRegEx = /[0-9]{9}/;
	var invalid = false;
    $.each(passports, function(i, val) {
        if (val == "" || val == undefined || passRegEx.test(val) == false) {
            invalid = true;
        }
    });
    if (invalid) {
    	toastr["error"]("Invalid passport number.");
    	return;
    }
    if (new Set(passports).size !== passports.length) {
    	toastr["error"]("Two persons can not have same passport number.");
    	return;
    }
    var bags = $("input[name='bags']").map(function() {
        return $(this).val();
    }).get();
    for (var i = 0; i < firstNames.length; i++) {
        if ((!firstNames[i].trim()) || (!lastNames[i].trim()) || (!passports[i].trim())) {
            toastr["error"]("Invalid data for passengers.");
            return;
        }
        if (isNaN(bags[i]) || bags[i] < 0 || bags[i] == "") {
            toastr["error"]("Invalid number of bags.");
            return;
        }
    }
    for (var i = 0; i < firstNames.length; i++) {
        flightReservation["passengers"].push({
            "firstName": firstNames[i],
            "lastName": lastNames[i],
            "passport": passports[i],
            "numberOfBags": bags[i]
        });
        flightReservation["seatsLeft"]--;
    }
    toastr["success"]("Successfully added flight reservation to cart.");
    $('#showFlightModal').modal('toggle');
    localStorage.removeItem("quickFlightReservation");
    localStorage.setItem("flightReservation", JSON.stringify(flightReservation));
    var shownFlight = JSON.parse(localStorage.getItem("shownFlight"));
    var startDest = shownFlight["startDestination"];
    var endDest = shownFlight["endDestination"];
    var flightDate = shownFlight["departureTime"];
    $("#flightRes").html(startDest + "-" + endDest + " " + flightDate);
    $("#vehicleCountry").val(shownFlight["countryName"]);
    $("#searchHotelCountry").val(shownFlight["countryName"]);
    localStorage.setItem("flightRes", "true");
}

function getDiscountInfo(){
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
				$('#usedDiscountPoints').prop('max', Math.min(Number(data.maxDiscountPoints), Number($('#availableDiscountPoints').val())));
				$('#discountPerExtraReservation').val(data.discountPerExtraReservation);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}


function recalculatePrices(){
	var discount = 0;
	$("#flightPriceRes").val(0);
	$("#hotelPriceRes").val(0);
	$("#carPriceRes").val(0);
	var flightReservation = JSON.parse(localStorage.getItem("flightReservation"));
	var quickFlightReservation = JSON.parse(localStorage.getItem("quickFlightReservation"));
	if(quickFlightReservation != null || flightReservation == null)
		return;
	var hotelRes = JSON.parse(localStorage.getItem("hotelRes"));
	if(hotelRes != null)
		discount += Number($('#discountPerExtraReservation').val());
	var carRes = JSON.parse(localStorage.getItem("carRes"));
	if(carRes != null)
		discount += Number($('#discountPerExtraReservation').val());
	discount += Number($('#usedDiscountPoints').val());
	
    var price = 0;
    for(let pass of flightReservation["passengers"]){
	    	price += pass["numberOfBags"]*flightReservation["other"]["pricePerBag"];
	}
	var dict = {'f':flightReservation["other"]["firstClassPrice"], 'b':flightReservation["other"]["businessClassPrice"],'e':flightReservation["other"]["economyClassPrice"]};
		var seats = flightReservation["seats"]
	seats.splice(1, 0 + flightReservation["invitedFriends"].length);
	for(let seat of seats){
		price += dict[seat.substr(-1)];
	}
	$("#flightPriceRes").val(price*(1-discount/100));

	if(hotelRes !=null){
		 if(hotelRes["quickReservationID"] != null)
			 $("#hotelPriceRes").val(hotelRes["price"]);			 
		 else
			 $("#hotelPriceRes").val(hotelRes["price"]*(1-discount/100));			 
			 
	}
	
	if (carRes != null) {
		if(carRes["quickVehicleReservationID"] != null)
			 $("#carPriceRes").val(carRes["price"]);			 
		 else
			 $("#carPriceRes").val(carRes["price"]*(1-discount/100));	
	}
	
	calculateTotalPrice();
	
}

function continueReservation(e) {
    e.preventDefault();
	if(isVisitor){
		warnVisitorToLogIn();
		return;
	}
    getDiscountInfo();
    $("#flightPriceRes").val(0);
    $("#hotelPriceRes").val(0);
    $("#carPriceRes").val(0);
    $('#discountTable').show();
    
    var flightReservation = JSON.parse(localStorage.getItem("flightReservation"));
    var quickFlightReservation = JSON.parse(localStorage.getItem("quickFlightReservation"));
    if(quickFlightReservation == null && flightReservation == null){
    	return;
    }
    
    if(quickFlightReservation != null){
    	$('#discountTable').hide();
    	var data = quickFlightReservation["other"];
    	$("#startDestRes").text(data["startDestination"]);
        $("#endDestRes").text(data["endDestination"]);
        $("#depTimeRes").text(data["departureTime"]);
        $("#landTimeRes").text(data["landingTime"]);
        if (data["roundTrip"]) {
        	$("#resRetDepTime").text(data["returningDepartureTime"]);
        	$("#resRetLandTime").text(data["returningLandingTime"]);
        	$(".resRoundTrip").show();
        }
        else {
        	$(".resRoundTrip").hide();
        }
        $("#flightAirlineRes").text(data["airlineName"]);
        var date1 = moment(data["departureTime"], 'DD.MM.YYYY hh:mm');
        var date2 = moment(data["landingTime"], 'DD.MM.YYYY hh:mm');
        var diff = date2.diff(date1, 'minutes');
        $("#flightDurationRes").text(diff);
        $("#flightDistanceRes").text(data["flightDistance"]);
        $('#flightConnectionsRes').find('option').remove();
        var conn = $("#flightConnectionsRes");
        if (data["connections"].length == 0) {
            conn.append("<option value=''></option>");
        } else {
            $.each(data["connections"], function(i, val) {
                conn.append("<option value=" + val + ">" + val +
                    "</option>");
            });
        }
        $("#numOfSeatsRes").text(quickFlightReservation["passengers"].length);
        $('#seatsRes').find('option').remove();
        var flightSeats = $("#seatsRes");
        var seats = quickFlightReservation["seats"]
        if (seats.length == 0) {
            flightSeats.append("<option value=''></option>");
        } else {
            $.each(seats, function(i, val) {
                flightSeats.append("<option value=" + val + ">" + val +
                    "</option>");
            });
        }

        $("#flightPriceRes").val(quickFlightReservation["price"]);
        
        $("#cancelFlightReservationButton").click(function() {
        	localStorage.removeItem("quickFlightReservation");
            $("#flightRes").html("No flight reserved");
            getReservations();
            loadProfileData();

            $('#showReservationModal').modal('hide');
        });
    	
    }
    
    else if(flightReservation != null){
	    var data = flightReservation["other"];
	    
	    $("#startDestRes").text(data["startDestination"]);
	    $("#endDestRes").text(data["endDestination"]);
	    $("#depTimeRes").text(data["departureTime"]);
	    $("#landTimeRes").text(data["landingTime"]);
	    if (data["roundTrip"]) {
	    	$("#resRetDepTime").text(data["returningDepartureTime"]);
	    	$("#resRetLandTime").text(data["returningLandingTime"]);
	    	$(".resRoundTrip").show();
	    }
	    else {
	    	$(".resRoundTrip").hide();
	    }
	    $("#flightAirlineRes").text(data["airlineName"]);
	    var date1 = moment(data["departureTime"], 'DD.MM.YYYY hh:mm');
	    var date2 = moment(data["landingTime"], 'DD.MM.YYYY hh:mm');
	    var diff = date2.diff(date1, 'minutes');
	    $("#flightDurationRes").text(diff);
	    $("#flightDistanceRes").text(data["flightDistance"]);
	    $('#flightConnectionsRes').find('option').remove();
	    var conn = $("#flightConnectionsRes");
	    if (data["connections"].length == 0) {
	        conn.append("<option value=''></option>");
	    } else {
	        $.each(data["connections"], function(i, val) {
	            conn.append("<option value=" + val + ">" + val +
	                "</option>");
	        });
	    }
	    $("#numOfSeatsRes").text(flightReservation["passengers"].length);
	    $('#seatsRes').find('option').remove();
	    var flightSeats = $("#seatsRes");
	    var seats = flightReservation["seats"]
	    seats.splice(1, 0 + flightReservation["invitedFriends"].length);
	    if (seats.length == 0) {
	        flightSeats.append("<option value=''></option>");
	    } else {
	        $.each(seats, function(i, val) {
	            flightSeats.append("<option value=" + val + ">" + val +
	                "</option>");
	        });
	    }
	    
	    
	    $("#cancelFlightReservationButton").click(function() {
        	localStorage.removeItem("flightReservation");
            localStorage.removeItem("flightRes");
            localStorage.removeItem("hotelRes");
            $("#hotelRes").text("");
            localStorage.removeItem("carRes");
            $("#carRes").text("");
            $("#flightRes").html("No flight reserved");
            getReservations();
            loadProfileData();
            $('#showReservationModal').modal('hide');
        });
	    
	    
	    var hotelRes = JSON.parse(localStorage.getItem("hotelRes"));
	    if(hotelRes != null){
			$("#fromDateRes").text(moment(hotelRes["fromDate"]).format('DD.MM.YYYY'));
			$("#toDateRes").text(moment(hotelRes["toDate"]).format('DD.MM.YYYY'));
			$("#roomNumberRes").text(hotelRes["hotelRoomNumber"]);
			$("#hotelResId").text(hotelRes["hotelName"]);
			$('#addServRes').find('option').remove();
			var addServices = $("#addServRes");
		    if (hotelRes["additionalServiceNames"].length == 0) {
		        addServices.append("<option value=''></option>");
		    } else {
		        $.each(hotelRes["additionalServiceNames"], function(i, val) {
		            addServices.append("<option value=" + val + ">" + val +
		                "</option>");
		        });
		    }
		   

		    $("#cancelHotelReservationButton").click(function() {
		    	localStorage.removeItem("hotelRes");
		    	$("#hotelRes").text("");
		    	getReservations();
		    	loadProfileData();
		    	$("#hotelResHeader").hide();
				$("#showHotelReservationTable").hide();
				recalculatePrices();
	        });
		    
		    $("#hotelResHeader").show();
			$("#showHotelReservationTable").show();
	    }
	    else {
	    	$("#hotelResHeader").hide();
	    	$("#showHotelReservationTable").hide();
	    }
	    
	    var carRes = JSON.parse(localStorage.getItem("carRes"));
	    if (carRes != null) {
	    	$("#fromDateCarRes").text(moment(carRes["fromDate"]).format('DD.MM.YYYY'));
	    	$("#toDateCarRes").text(moment(carRes["toDate"]).format('DD.MM.YYYY'));
	    	$("#bOfficeRes").text(carRes["branchOfficeName"]);
	    	$("#modelCarRes").text(carRes["vehicleModel"]);
	    	$("#prodCarRes").text(carRes["vehicleProducer"]);
	    	$("#carPriceRes").val(carRes["price"]);
	    	$("#cancelCarReservationButton").click(function() {
	    		localStorage.removeItem("carRes");
	    		$("#carRes").text("");
	    		getReservations();
	    		loadProfileData();
	    		$("#carResHeader").hide();
		    	$("#showCarReservationTable").hide();
		    	recalculatePrices();
	        });
		    
	    	$("#carResHeader").show();
	    	$("#showCarReservationTable").show();
	    }
	    else {
	    	$("#carResHeader").hide();
	    	$("#showCarReservationTable").hide();
	    }
    }
	
    recalculatePrices();
    $("#cartDiv").show();
    $("#showReservationModal").modal();
}

function calculateTotalPrice(){
	$('#totalPrice').val( Number($("#flightPriceRes").val()) + Number($("#hotelPriceRes").val()) +  Number($("#carPriceRes").val()));
}

function confirmReservation(){
    var quickFlightReservation = JSON.parse(localStorage.getItem("quickFlightReservation"));
    var flightRes = localStorage.getItem("flightRes");
    var hotelRes = JSON.parse(localStorage.getItem("hotelRes"));
    var carRes = JSON.parse(localStorage.getItem("carRes"));
    if (quickFlightReservation != null) {
    	if (hotelRes != null || carRes != null) {
    		toastr["error"]("You can not reserve hotel or car with quick flight reservation.")
    		return;
    	}
    	var userPass = quickFlightReservation["passengers"][0]["passport"];
    	if (userPass === "") {
    		toastr["error"]("You did not give a passport number. Please reserve again.");
    		return;
    	}
    	$.ajax({
            type: 'POST',
            url: reserveQuickFlightReservationURL + 0,
            headers: createAuthorizationTokenHeader(tokenKey),
            data: JSON.stringify({
                "quickReservationID": quickFlightReservation["quickReservationID"],
                "passengers": quickFlightReservation["passengers"]
            }),
            success: function(data) {
                if (data != null) {
                    toastr[data.toastType](data.message);
                    localStorage.removeItem("quickFlightReservation");
                    $("#flightRes").html("No flight reserved");
                    getReservations();
                    loadProfileData();
                }
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                alert("AJAX ERROR: " + textStatus);
            }
        });
    	return;
    }
    
    if (flightRes === null) {
        toastr["error"]("You have to reserve flight first.");
        return;
    }
    if (hotelRes === null && carRes === null) {
        var flightReservation = JSON.parse(localStorage.getItem("flightReservation"));
        $.ajax({
            type: 'POST',
            url: reserveFlightURL + $('#usedDiscountPoints').val(),
            headers: createAuthorizationTokenHeader(tokenKey),
            data: JSON.stringify({
                "flightCode": flightReservation["flightCode"],
                "invitedFriends": flightReservation["invitedFriends"],
                "numberOfPassengers": flightReservation["numberOfPassengers"],
                "passengers": flightReservation["passengers"],
                "seats": flightReservation["seats"]
            }),
            success: function(data) {
                if (data != null) {
                    toastr[data.toastType](data.message);
                    localStorage.removeItem("flightReservation");
                    localStorage.removeItem("flightRes");
                    $("#flightRes").html("No flight reserved");
                    getReservations();
                    loadProfileData();
                }
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                alert("AJAX ERROR: " + textStatus);
            }
        });
    } else if (hotelRes != null && carRes === null) {
        // FLIGHT + HOTEL
        var flightReservation = JSON.parse(localStorage.getItem("flightReservation"));
        flightRes = {
            "flightCode": flightReservation["flightCode"],
            "invitedFriends": flightReservation["invitedFriends"],
            "numberOfPassengers": flightReservation["numberOfPassengers"],
            "passengers": flightReservation["passengers"],
            "seats": flightReservation["seats"]
        };
        $.ajax({
            type: 'POST',
            url: reserveFlightHotelURL + $('#usedDiscountPoints').val(),
            headers: createAuthorizationTokenHeader(tokenKey),
            data: JSON.stringify({
                'flightReservation': flightRes,
                'hotelReservation': hotelRes
            }),
            success: function(data) {
                if (data != null) {
                    toastr[data.toastType](data.message);
                    localStorage.removeItem("flightReservation");
                    localStorage.removeItem("flightRes");
                    localStorage.removeItem("hotelRes");
                    $("#flightRes").text("No flight reserved");
                    $("#hotelRes").text("");
                    getReservations();
                    loadProfileData();
                }
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                alert("AJAX ERROR: " + textStatus);
            }
        });
    } else if (hotelRes === null && carRes != null) {
    	var flightReservation = JSON.parse(localStorage.getItem("flightReservation"));
        flightRes = {
            "flightCode": flightReservation["flightCode"],
            "invitedFriends": flightReservation["invitedFriends"],
            "numberOfPassengers": flightReservation["numberOfPassengers"],
            "passengers": flightReservation["passengers"],
            "seats": flightReservation["seats"]
        };
        $.ajax({
            type: 'POST',
            url: reserveFlightVehicleURL + $('#usedDiscountPoints').val(),
            headers: createAuthorizationTokenHeader(tokenKey),
            data: JSON.stringify({
                'flightReservation': flightRes,
                'vehicleReservation': carRes
            }),
            success: function(data) {
                if (data != null) {
                    toastr[data.toastType](data.message);
                    localStorage.removeItem("flightReservation");
                    localStorage.removeItem("flightRes");
                    localStorage.removeItem("carRes");
                    $("#flightRes").text("No flight reserved");
                    $("#carRes").text("");
                    getReservations();
                    loadProfileData();
                }
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                alert("AJAX ERROR: " + textStatus);
            }
        });
    } else {
        // FLIGHT + HOTEL + CAR
    	var flightReservation = JSON.parse(localStorage.getItem("flightReservation"));
        flightRes = {
            "flightCode": flightReservation["flightCode"],
            "invitedFriends": flightReservation["invitedFriends"],
            "numberOfPassengers": flightReservation["numberOfPassengers"],
            "passengers": flightReservation["passengers"],
            "seats": flightReservation["seats"]
        };
        $.ajax({
            type: 'POST',
            url: reserveFlightHotelVehicleURL + $('#usedDiscountPoints').val(),
            headers: createAuthorizationTokenHeader(tokenKey),
            data: JSON.stringify({
                'flightReservation': flightRes,
                'vehicleReservation': carRes,
                'hotelReservation': hotelRes
            }),
            success: function(data) {
                if (data != null) {
                    toastr[data.toastType](data.message);
                    localStorage.removeItem("flightReservation");
                    localStorage.removeItem("flightRes");
                    localStorage.removeItem("carRes");
                    localStorage.removeItem("hotelRes");
                    $("#flightRes").text("No flight reserved");
                    $("#carRes").text("");
                    $("#hotelRes").text("");
                    getReservations();
                    loadProfileData();
                }
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                alert("AJAX ERROR: " + textStatus);
            }
        });
    }
    $('#showReservationModal').modal('hide');
}

function getReservations() {
    $.ajax({
        type: 'GET',
        url: getReservationsURL,
        headers: createAuthorizationTokenHeader(tokenKey),
        success: function(data) {
            if (data != null) {
                var table = $("#reservationsTable").DataTable();
                table.clear().draw();
                var cancel = "<button id='cancelResButton' class='btn btn-default'>Cancel</button>";
                $.each(data, function(i, val) {
                    table.row.add([val.id, val.reservationInf, val.dateOfReservation, val.price, cancel ]).draw(false);
                });
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });
}

function cancelReservation(id) {
	$.ajax({
        type: 'DELETE',
        url: cancelReservationURL,
        headers: createAuthorizationTokenHeader(tokenKey),
        contentType: "application/json",
        data: id.toString(),
        success: function(data) {
            if (data.toastType == "success") {
            	var table = $("#reservationsTable").DataTable();
            	table.clear().draw();
            	getReservations();
            	loadProfileData(); // TO UPDATE AVAILABLE DISCOUNT POINTS
            }
            toastr[data.toastType](data.message);
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });
}

function renderDestinations(data) {
	var destTable = $("#airlineDestinationsTable").DataTable();
	destTable.clear().draw();
	$.each(data, function(i, val) {
		destTable.row.add([ val.nameOfDest, val.longitude, val.latitude ]).draw(false);
	});
}

function renderQuickFlightReservations(data) {
	var quickFlightResTable = $("#quickAirlineReservationsTable").DataTable();
	quickFlightResTable.clear().draw();
	$.each(data, function(i, val) {
		quickFlightResTable.row.add([	val.id,
										val.flightCode,
										val.discountedPrice,
										val.discount,
										val.startDestination + "-" + val.endDestination,
										val.departureTime,
										val.landingTime,
										val.seat,
										val.seatClass,
										val.returningDepartureTime || "no return",
										val.returningLandingTime || "no return",
										`<button onclick="reserveQuickFlightReservation('${val.id}', 
											'${val.startDestination}', '${val.endDestination}', '${val.departureTime}')" class="btn btn-default">Reserve</a>` ])
						.draw(false);
			});
}

function reserveQuickFlightReservation(quickID, startDest, endDest, flightDate){
	let table = $('#quickAirlineReservationsTable').DataTable();
	var val = null;
	table.rows().every(function ( rowIdx, tableLoop, rowLoop ) {
	    if(this.data()[0] == quickID){
	    	val = this.data(); 
	    	return;
	    }
	} );
	
	$.ajax({
        type: 'GET',
        url: getDetailedFlightURL,
        contentType: "application/json",
        data: {
            'flightCode': val[1]
        },
        dataType: "json",
        headers: createAuthorizationTokenHeader(tokenKey),
        success: function(data) {
            if (data != null) {
            	var quickFlightReservation = {
            	        "quickReservationID" : quickID,
            	        "passengers": [{
            	            "firstName": "",
            	            "lastName": "",
            	            "passport": "",
            	            "numberOfBags": 0
            	        }],
            	        "seats" : [val[7]],
            	        "price" : val[2],
            	        "other" : data
            	    };
            	localStorage.removeItem("flightReservation");
            	localStorage.removeItem("flightRes");
            	localStorage.setItem("quickFlightReservation", JSON.stringify(quickFlightReservation));
            	$("#flightRes").html(startDest + "-" + endDest + " " + flightDate);
            	$("#passportBagsModal").modal('show');
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });

}

function getPassportAndBags(e) {
	e.preventDefault();
	var userPass = $("#modalPass").val();
	var passRegEx = /[0-9]{9}/;
    if (userPass == "" || userPass == undefined || passRegEx.test(userPass) == false) {
        toastr["error"]("Invalid passport number.");
        return;
    }
    var numOfBags = $("#modalBags").val();
    if (isNaN(numOfBags) || numOfBags < 0) {
        toastr["error"]("Invalid number of bags.");
        return;
    }
    if (numOfBags === "") {
    	numOfBags = 0;
    }
    var quickFlightReservation = JSON.parse(localStorage.getItem("quickFlightReservation"));
    quickFlightReservation["passengers"][0]["passport"] = userPass;
    quickFlightReservation["passengers"][0]["numberOfBags"] = numOfBags;
    localStorage.setItem("quickFlightReservation", JSON.stringify(quickFlightReservation));
    $("#passportBagsModal").modal('hide');
}

function loadReservation(res_id) {
	$.ajax({
        type: 'GET',
        url: getDetailedReservationURL,
        contentType: "application/json",
        data: {
            'resID': res_id
        },
        dataType: "json",
        headers: createAuthorizationTokenHeader(tokenKey),
        success: function(data) {
            if (data != null) {
                $("#startDestRes").text(data["startDestination"]);
                $("#endDestRes").text(data["endDestination"]);
                $("#depTimeRes").text(data["departureTime"]);
                $("#landTimeRes").text(data["landingTime"]);
                if (data["roundTrip"]) {
                	$("#resRetDepTime").text(data["returningDepartureTime"]);
                	$("#resRetLandTime").text(data["returningLandingTime"]);
                	$(".resRoundTrip").show();
                }
                else {
                	$(".resRoundTrip").hide();
                }
                $("#flightAirlineRes").text(data["airlineName"]);
                var date1 = moment(data["departureTime"], 'DD.MM.YYYY hh:mm');
                var date2 = moment(data["landingTime"], 'DD.MM.YYYY hh:mm');
                var diff = date2.diff(date1, 'minutes');
                $("#flightDurationRes").text(diff);
                $("#flightDistanceRes").text(data["flightDistance"]);
                $('#flightConnectionsRes').find('option').remove();
                var conn = $("#flightConnectionsRes");
                if (data["connections"].length == 0) {
                    conn.append("<option value=''></option>");
                } else {
                    $.each(data["connections"], function(i, val) {
                        conn.append("<option value=" + val + ">" + val +
                            "</option>");
                    });
                }
                $("#numOfSeatsRes").text(data["numOfFlightSeats"]);
                $('#seatsRes').find('option').remove();
                var flightSeats = $("#seatsRes");
                if (data["seats"].length == 0) {
                    flightSeats.append("<option value=''></option>");
                } else {
                    $.each(data["seats"], function(i, val) {
                        flightSeats.append("<option value=" + val + ">" + val +
                            "</option>");
                    });
                }
                /* HOTEL RESERVATION */
                if (data["hotelRes"] != null) {
                	$("#fromDateRes").text(moment(data["hotelRes"]["fromDate"]).format('DD.MM.YYYY'));
                	$("#toDateRes").text(moment(data["hotelRes"]["toDate"]).format('DD.MM.YYYY'));
                	$("#roomNumberRes").text(data["hotelRes"]["hotelRoomNumber"]);
                	$("#hotelResId").text(data["hotelRes"]["hotelName"]);
                	$('#addServRes').find('option').remove();
                	var addServices = $("#addServRes");
                    if (data["hotelRes"]["additionalServiceNames"].length == 0) {
                        addServices.append("<option value=''></option>");
                    } else {
                        $.each(data["hotelRes"]["additionalServiceNames"], function(i, val) {
                            addServices.append("<option value=" + val + ">" + val +
                                "</option>");
                        });
                    }
                    $("#hotelResHeader").show();
                	$("#showHotelReservationTable").show();
                }
                else {
                	$("#hotelResHeader").hide();
                	$("#showHotelReservationTable").hide();
                }
                /* CAR RESERVATION */
                if (data["vehicleRes"] != null) {
                	$("#fromDateCarRes").text(moment(data["vehicleRes"]["fromDate"]).format('DD.MM.YYYY'));
                	$("#toDateCarRes").text(moment(data["vehicleRes"]["toDate"]).format('DD.MM.YYYY'));
                	$("#bOfficeRes").text(data["vehicleRes"]["branchOfficeName"]);
                	$("#modelCarRes").text(data["vehicleRes"]["vehicleModel"]);
                	$("#prodCarRes").text(data["vehicleRes"]["vehicleProducer"]);
                	$("#carResHeader").show();
                	$("#showCarReservationTable").show();
                }
                else {
                	$("#carResHeader").hide();
                	$("#showCarReservationTable").hide();
                }
            }
        },
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("AJAX ERROR: " + textStatus);
        }
    });
}