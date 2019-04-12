var rootURL1 = "../auth/register/";
var rootURL2 = "../index/index.html";
var rootURL3 = "../login";
	
$(document).on('submit', '#registrationForm', function(e){
	e.preventDefault();
	var email = $('input[name="email"]').val();
	var password = $('input[name="password"]').val();
	var firstName = $('input[name="fname"]').val();
	var lastName = $('input[name="lname"]').val();
	var phone = $('input[name="phone"]').val();
	var address = $('input[name="address"]').val();
	
	$.ajax({
		type : 'POST',
		url : rootURL1,
		contentType : 'application/json',
		dataType : "json",
		data : formToJSON(email, password, firstName, lastName, phone, address),
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
				toastr["error"](data.message, data.header);
				$('input[name="email"]').val("");
				$('input[name="password"]').val("");
				$('input[name="fname"]').val("");
				$('input[name="lname"]').val("");
				$('input[name="phone"]').val("");
				$('input[name="address"]').val("");
			} else{
				if(data){
					document.location.href = rootURL3;
				} else{
					console.log("error");
				}	
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	})
})

$(document).on("click", "#logButton", function(e){
	document.location.href = rootURL3
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

function formToJSON(email, password, firstName, lastName, phone, address){
	return JSON.stringify({
		"email": email,
		"password": password,
		"firstName": firstName,
		"lastName": lastName,
		"phoneNumber": phone,
		"address": address
	})
}