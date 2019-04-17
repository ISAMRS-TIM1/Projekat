const logoutURL = "../logout";
const loadUserInfoURL = "../api/getUserInfo";
const saveChangesURL = "../api/editUser";
const getAirlinesURL = "../api/getAirlines";
const getHotelsURL = "../api/getHotels";
const getRentACarsURL = "../api/getRentACars";


const tokenKey = "jwtToken";

$(document).ready(function(){
	loadData();
	
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
			data : userEditFormToJSON(firstName, lastName, phone, address, email),
			success: function(data){
				if(data != ""){
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
					toastr["error"](data);
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert("AJAX ERROR: " + textStatus);
			}
		});
	});
	
	$('a[href="#profile"]').click(function(){
		loadProfileData();
	});
	
	$('#airlinesTable').DataTable( {
        "paging": false,
        "info": false,
    } );
	
	$('#hotelsTable').DataTable( {
        "paging": false,
        "info": false,
    } );
	
	$('#rentACarsTable').DataTable( {
        "paging": false,
        "info": false,
    } );
});

function loadData(){
	loadProfile();
	loadService(getAirlinesURL, "#airlinesTable");
	loadService(getHotelsURL, "#hotelsTable");
	loadService(getRentACarsURL, "#rentACarsTable");
}

function loadProfile(){
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
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function loadService(url, tableID){
	let token = getJwtToken("jwtToken");
	$.ajax({
		type : 'GET',
		url : url,
		dataType : "json",
		headers: createAuthorizationTokenHeader(tokenKey),
		success: function(data){
			if(data != null){
				var table = $(tableID).DataTable();
				$.each(data, function(i, val) {
					table.row.add([val.name, val.averageGrade, val.numberOfAdmins]).draw(false);
				});
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function userEditFormToJSON(firstName, lastName, phone, address, email){
	return JSON.stringify({
		"firstName": firstName,
		"lastName": lastName,
		"phoneNumber": phone,
		"address": address,
		"email": email
	});
}