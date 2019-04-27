const logoutURL = "../logout";
const loadUserInfoURL = "../api/getUserInfo";
const saveChangesURL = "../api/editUser";
const getPlaneSeatsURL = "/api/getPlaneSeats";
const searchUsersURL = "/api/searchUsers";
const friendInvitationURL = "/sendInvitation";
const acceptInvitationURL = "/api/acceptInvitation";
const declineInvitationURL = "/api/declineInvitation";
const getFriendsURL = "/api/getFriends";

const tokenKey = "jwtToken";

var userMail = "";

$(document).ready(function(){
	loadData();
	setUpToastr();
	var socket = new SockJS('/friendsEndpoint');
	var stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		stompClient.subscribe("/friendsInvitation/" + userMail, function(data) {
			var table = $('#friendsTable').DataTable();
			try {
				var dataJSON = JSON.parse(data.body);
				var sendInv = "<div id='invFriendStatus'><button id='sendInvButton' class='btn btn-default'>Accept</button>";
				sendInv += "<button id='sendInvButton' class='btn btn-default'>Decline</button></div>";
				table.row.add([ dataJSON.email, dataJSON.firstName, dataJSON.lastName, sendInv ]).draw(false);
			}
			catch(err) {
				table.clear().draw();
				getFriends();
			}
		});
	});
	
	$('#friendsTable').DataTable({
        "paging": false,
        "info": false,
        "scrollY": "17vw",
        "scrollCollapse": true,
        "retrieve": true,
    });
	
	$('#reservationsTable').DataTable({
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
	
	$(".nav li").click(function(){
        $(this).addClass("active");
        $(this).siblings().removeClass("active");
    });
	
	$("#logout").click(function(){
		document.location.href = logoutURL;
	});
	
	$('.nav-tabs a').click(function(){
		  $(this).tab('show');
	});
	
	$('.edit').click(function(){
		if($(this).siblings().first().is('[readonly]')) {
			$(this).siblings().first().removeAttr('readonly');
		} else {
			$(this).siblings().first().prop('readonly', 'true');
		}
	});
	
	$('#friendsTable tbody').on( 'click', 'td', function (event) {
		var tgt = $(event.target);
		if (tgt[0].innerHTML == "Accept") {
			var table = $("#friendsTable").DataTable();
			var rowData = table.cell(this).row().data();
			acceptInvitation(rowData[0], table.cell(this).row().index());
		}
		else if (tgt[0].innerHTML == "Decline") {
			var table = $("#friendsTable").DataTable();
			var rowData = table.cell(this).row().data();
			declineInvitation(rowData[0], table.cell(this).row().index());
		}
	});
	
	$('#usersTable tbody').on( 'click', 'td', function (event) {
		var tgt = $(event.target);
		if (tgt[0].id == "sendInvButton") {
			var table = $("#usersTable").DataTable();
			var rowData = table.cell(this).row().data();
			friendInvitation(rowData[0], tgt[0].parentElement.id);
		}
	});
	
	$("#searchUserForm").submit(function(e) {
		  e.preventDefault();
		  let firstName = $("#userFirstName").val();
		  let lastName = $("#userLastName").val();
		  $.ajax({
				type : 'GET',
				url : searchUsersURL,
				headers: createAuthorizationTokenHeader(tokenKey),
				contentType : 'application/json',
				data : {"firstName" : firstName, "lastName" : lastName, "email" : userMail},
				success: function(data){
					var table = $('#usersTable').DataTable();
					table.clear().draw();
					$.each(data, function(i, val) {
						var sendInv = "<div id='status" + i + "'><button id='sendInvButton'" +
								" class='btn btn-default'>Send invitation</button></div>";
						table.row.add([ val.email, val.firstName, val.lastName, sendInv ]).draw(false);
					});
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					alert("AJAX ERROR: " + textStatus);
				}
			});
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
			url : saveChangesURL,
			contentType : 'application/json',
			dataType : "html",
			data : formToJSON(firstName, lastName, phone, address, email),
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
	
	$('a[href="#profile"]').click(function(){
		loadData();
	});
	
	$('a[data-toggle="tab"]').on('shown.bs.tab', function(e){
		$($.fn.dataTable.tables(true)).DataTable().columns.adjust();
	});
	
	getPlaneSeats();
});

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

function getFriends() {
	$.ajax({
		type : 'GET',
		url : getFriendsURL,
		headers: createAuthorizationTokenHeader(tokenKey),
		success: function(data){
			if (data != null) {
				var table = $('#friendsTable').DataTable();
				table.clear().draw();
				$.each(data, function(i, val) {
					if (val.status == "Invitation pending") {
						var sendInv = "<div id='invFriendStatus'><button id='sendInvButton' class='btn btn-default'>Accept</button>";
						sendInv += "<button id='sendInvButton' class='btn btn-default'>Decline</button></div>";
						table.row.add([ val.email, val.firstName, val.lastName, sendInv ]).draw(false);
					}
					else {
						table.row.add([ val.email, val.firstName, val.lastName, "<b>" + val.status + "</b>" ]).draw(false);
					}
				});
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function friendInvitation(invitedUser, idx) {
	idx = idx.substring(6);
	$.ajax({
		type : 'POST',
		url : friendInvitationURL,
		headers: createAuthorizationTokenHeader(tokenKey),
		data : invitedUser,
		success: function(data){
			if(data.toastType == "success") {
				toastr[data.toastType](data.message);
				$("#status" + idx).html("<b>Invitation sent</b>");
				var table = $('#friendsTable').DataTable();
				var userTable = $('#usersTable').DataTable();
				var row = userTable.row(idx).data();
				table.row.add([ row[0], row[1], row[2], "<b>Invitation sent</b>"]).draw(false);
			}
			else {
				toastr[data.toastType](data.message);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function acceptInvitation(acceptedUser, idx) {
	$.ajax({
		type : 'POST',
		url : acceptInvitationURL,
		headers: createAuthorizationTokenHeader(tokenKey),
		data :  acceptedUser,
		success: function(data){
			if(data.toastType == "success") {
				toastr[data.toastType](data.message);
				var table = $('#friendsTable').DataTable();
				var row = table.row(idx);
				table.cell({ row: idx, column: 3 }).data("<b>Accepted</b>").draw();
			}
			else {
				toastr[data.toastType](data.message);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function declineInvitation(declinedUser, idx) {
	$.ajax({
		type : 'POST',
		url : declineInvitationURL,
		headers: createAuthorizationTokenHeader(tokenKey),
		contentType : 'application/json',
		data :  declinedUser,
		success: function(data){
			if(data.toastType == "success") {
				toastr[data.toastType](data.message);
				var table = $('#friendsTable').DataTable();
				var rowForDelete = table.row(idx);
				table.row(rowForDelete).remove().draw(false);
			}
			else {
				toastr[data.toastType](data.message);
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

var firstPrice = 0;
var businessPrice = 0;
var economyPrice = 0;

function getPlaneSeats() {
	$.ajax({
		dataType : "json",
		url : getPlaneSeatsURL,
		success : function(data) {
			firstPrice = data["firstClassPrice"];
			businessPrice = data["businessClassPrice"];
			economyPrice = data["economyClassPrice"];
			renderPlaneSeats(data["planeSegments"], data["reservedSeats"]);
		}
	});
}

function loadData(){
	let token = getJwtToken("jwtToken");
	$.ajax({
		type : 'GET',
		url : loadUserInfoURL,
		dataType : "json",
		headers: createAuthorizationTokenHeader(tokenKey),
		success: function(data){
			if(data != null){
				$('input[name="fname"]').val(data.firstName);
				$('input[name="lname"]').val(data.lastName);
				$('input[name="phone"]').val(data.phone);
				$('input[name="address"]').val(data.address);
				$('#email').text(data.email);
				userMail = data.email;
				var table = $('#friendsTable').DataTable();
				table.clear().draw();
				$.each(data.friends, function(i, val) {
					if (val.status == "Invitation pending") {
						var sendInv = "<div id='invFriendStatus'><button id='sendInvButton' class='btn btn-default'>Accept</button>";
						sendInv += "<button id='sendInvButton' class='btn btn-default'>Decline</button></div>";
						table.row.add([ val.email, val.firstName, val.lastName, sendInv ]).draw(false);
					}
					else {
						table.row.add([ val.email, val.firstName, val.lastName, "<b>" + val.status + "</b>" ]).draw(false);
					}
				});
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function formToJSON(firstName, lastName, phone, address, email){
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

function showPlaneSeats(seats) {
	var $cart = $('#selected-seats'),
		$counter = $('#counter'),
		$total = $('#total'),
		sc = $('#seat-map').seatCharts({
		map: seats,
		seats: {
			f: {
				price   : firstPrice,
				classes : 'first-class', //your custom CSS class
				category: 'First Class'
			},
			e: {
				price   : economyPrice,
				classes : 'economy-class', //your custom CSS class
				category: 'Economy Class'
			},
			b: {
				price: businessPrice,
				classes : 'business-class',
				category : 'Business Class'
			},
			l: {
				classes : 'blank-class',
				category : 'Blank seat'
			},
			a: {
				classes: 'unavailable',
				category: 'Already booked'
			}				
		
		},
		naming : {
			top : false,
			left: false,
			getLabel : function (character, row, column) {
				return firstSeatLabel++;
			},
		},
		legend : {
			node : $('#legend'),
		    items : [
		    	[ 'f', 'available',   'First Class' ],
				[ 'b', 'available',   'Business Class'],
				[ 'e', 'available',   'Economy Class'],
				[ 'a', 'unavailable', 'Already Booked'],
				[ 'l', 'available', 'Blank seat']
		    ]					
		},
		click: function () {
			if (this.status() == 'available') {
				//let's create a new <li> which we'll add to the cart items
				$('<li>'+this.data().category+' Seat # '+this.settings.label+': <b>$'+this.data().price+'</b> <a href="#" class="cancel-cart-item">[cancel]</a></li>')
					.attr('id', 'cart-item-'+this.settings.id)
					.data('seatId', this.settings.id)
					.appendTo($cart);
				
				/*
				 * Lets update the counter and total
				 *
				 * .find function will not find the current seat, because it will change its stauts only after return
				 * 'selected'. This is why we have to add 1 to the length and the current seat price to the total.
				 */
				$counter.text(sc.find('selected').length+1);
				$total.text(recalculateTotal(sc)+this.data().price);
				
				return 'selected';
			} else if (this.status() == 'selected') {
				//update the counter
				$counter.text(sc.find('selected').length-1);
				//and total
				$total.text(recalculateTotal(sc)-this.data().price);
			
				//remove the item from our cart
				$('#cart-item-'+this.settings.id).remove();
			
				//seat has been vacated
				return 'available';
			} else if (this.status() == 'unavailable') {
				//seat has been already booked
				return 'unavailable';
			} else {
				return this.style();
			}
		}
	});

	//this will handle "[cancel]" link clicks
	$('#selected-seats').on('click', '.cancel-cart-item', function () {
		//let's just trigger Click event on the appropriate seat, so we don't have to repeat the logic here
		sc.get($(this).parents('li:first').data('seatId')).click();
	});
}

function recalculateTotal(sc) {
	var total = 0;
	
	//basically find every selected seat and sum its price
	sc.find('selected').each(function () {
		total += this.data().price;
	});
	
	return total;
}

function renderPlaneSeats(planeSegments, reserved) {
	if (planeSegments[0].length == 0 && planeSegments[1].length == 0 && planeSegments[2].length == 0) {
		showPlaneSeats([]);
	}
	else {
		reservedSeats = [];
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
		initializeSeats(maxRowBusiness - maxRowFirst, businessClass, segment.seats, 'b', 2);
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