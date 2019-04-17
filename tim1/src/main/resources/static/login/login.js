var loginURL = "../auth/login/";
var registeredUserURL = "../registeredUser";
var registrationURL = "../register";
var airlineAdminURL = "../airlineAdmin";
var hotelAdminURL = "../hotelAdmin";
var rentACarAdminURL = "../rentACarAdmin";
var sysAdminURL = "../sysAdmin";

var TOKEN_KEY = 'jwtToken';
	
$(document).on('submit', '#loginForm', function(e){
	e.preventDefault()
	var email = $('input[name="email"]').val()
	var password = $('input[name="password"]').val()
	$.ajax({
		type : 'POST',
		url : loginURL,
		contentType : 'application/json',
		dataType : "json",
		data : formToJSON(email, password),
		success: function(data){
			if(data.message != undefined){
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
				toastr["error"](data.message, data.header)
				$('input[name="email"]').val("")
				$('input[name="password"]').val("")
			} else{
				setJwtToken(TOKEN_KEY, data.accessToken);			
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
	})
})

$(document).on("click", "#regButton", function(e){
	document.location.href = registrationURL
})

$(document).on("click", "#eye", function(e){
	if($("#eye-icon").hasClass("glyphicon-eye-close")){
		$("#eye-icon").removeClass("glyphicon-eye-close");
		$("#eye-icon").addClass("glyphicon-eye-open");
		$('input[name="password"]').attr("type", "text");
	} else{
		$("#eye-icon").removeClass("glyphicon-eye-open");
		$("#eye-icon").addClass("glyphicon-eye-close");
		$('input[name="password"]').attr("type", "password");
	}
})

function formToJSON(username, password){
	return JSON.stringify({
		"username": username,
		"password": password
	})
}