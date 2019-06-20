var loginURL = "../auth/login/";
var registeredUserURL = "../registeredUser";
var registrationURL = "../register";
var airlineAdminURL = "../airlineAdmin";
var hotelAdminURL = "../hotelAdmin";
var rentACarAdminURL = "../carAdmin";
var sysAdminURL = "../sysAdmin";
var adminPasswordURL = "../changePassword";

var TOKEN_KEY = "jwtToken";

$(document).ready(function(){
	initToastr();
	
	$("#loginForm").submit(function(e){
		e.preventDefault();
		
		var email = $('input[name="email"]').val()
		
		if(email == null || email === ""){
			toastr["error"]("Email must not be empty");
			return;
		}
		
		var password = $('input[name="password"]').val()
		
		if(password == null || password === ""){
			toastr["error"]("Password must not be empty");
			return;
		}
		
		sendLoginData(email, password);
	});
	
	$("#regButton").click(function(e){
		e.preventDefault();
		document.location.href = registrationURL
	});
});

function sendLoginData(email, password){
	$.ajax({
		type : 'POST',
		url : loginURL,
		contentType : 'application/json',
		dataType : "json",
		data : formToJSON(email, password),
		success: function(data){
			if(data.message != undefined){
				toastr["error"](data.message, data.header)
				$('input[name="email"]').val("")
				$('input[name="password"]').val("")
			} else{
				setJwtToken(TOKEN_KEY, data.accessToken);
				if (data.passwordChanged == false) {
					document.location.href = adminPasswordURL;
					return;
				}
				if(data.userType == "ROLE_REGISTEREDUSER"){
					document.location.href = registeredUserURL;					
				} else if(data.userType == "ROLE_AIRADMIN"){
					document.location.href = airlineAdminURL;
				} else if(data.userType == "ROLE_HOTELADMIN"){
					document.location.href = hotelAdminURL;
				} else if(data.userType == "ROLE_RENTADMIN"){
					document.location.href = rentACarAdminURL;
				} else if(data.userType == "ROLE_SYSADMIN"){
					document.location.href = sysAdminURL;
				}
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function formToJSON(username, password){
	return JSON.stringify({
		"username": username,
		"password": password
	})
}

function initToastr(){
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