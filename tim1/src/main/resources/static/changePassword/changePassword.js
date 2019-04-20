const TOKEN_KEY = 'jwtToken';
const changePasswordURL = "auth/changePassword";
var airlineAdminURL = "../airlineAdmin";
var hotelAdminURL = "../hotelAdmin";
var rentACarAdminURL = "../carAdmin";
var sysAdminURL = "../sysAdmin";

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

$(document).on("click", "#submitButton", function(e){
	var password = $("#pass").val();
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
})