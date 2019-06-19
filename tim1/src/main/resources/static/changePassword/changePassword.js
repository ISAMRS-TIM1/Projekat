const TOKEN_KEY = "jwtToken";
const changePasswordURL = "auth/changePassword";
var airlineAdminURL = "../airlineAdmin";
var hotelAdminURL = "../hotelAdmin";
var rentACarAdminURL = "../carAdmin";
var sysAdminURL = "../sysAdmin";

$(document).ready(function(){
	initToastr();
	
	$("#submitButton").click(function(e){
		e.preventDefault();
		
		var password = $("#pass").val();
		
		if(password == null || password === ""){
			toastr["error"]("Password must not be empty");
			return;
		}
		
		changePassword(password);
	});
});

function changePassword(password){
	$.ajax({
		type : 'PUT',
		url : changePasswordURL,
		headers : createAuthorizationTokenHeader(TOKEN_KEY),
		contentType : "application/json",
		data : password,
		success : function(data) {
			setJwtToken(TOKEN_KEY, data.accessToken);
			if(data.userType == "ROLE_AIRADMIN"){
				document.location.href = airlineAdminURL;
			} else if(data.userType == "ROLE_HOTELADMIN"){
				document.location.href = hotelAdminURL;
			} else if(data.userType == "ROLE_RENTADMIN"){
				document.location.href = rentACarAdminURL;
			}
		}
	});
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
