var rootURL1 = "../auth/register/";
var rootURL2 = "../index/index.html";
var rootURL3 = "../login";

$(document).ready(function(){
	initToastr();
	
	$(document).on('submit', '#registrationForm', function(e){
		e.preventDefault();
		var email = $('input[name="email"]').val();
		
		if(email == null || email === ""){
			toastr["error"]("Email must not be empty");
			return;
		}
		
		var password = $('input[name="password"]').val();
		
		if(password == null || password === ""){
			toastr["error"]("Password must not be empty");
			return;
		}
		
		var firstName = $('input[name="fname"]').val();
		
		if(firstName == null || firstName === ""){
			toastr["error"]("First name must not be empty");
			return;
		}
		
		var lastName = $('input[name="lname"]').val();
		
		if(lastName == null || lastName === ""){
			toastr["error"]("Last name must not be empty");
			return;
		}
		
		var phone = $('input[name="phone"]').val();
		
		if(phone == null || phone === ""){
			toastr["error"]("Phone must not be empty");
			return;
		}
		
		var address = $('input[name="address"]').val();
		
		if(address == null || address === ""){
			toastr["error"]("Address must not be empty");
			return;
		}
		
		sendRegistrationData(email, password, firstName, lastName, phone, address);
	});

	$("#logButton").click(function(e){
		document.location.href = rootURL3;
	});
});

function sendRegistrationData(email, password, firstName, lastName, phone, address){
	$.ajax({
		type : 'POST',
		url : rootURL1,
		contentType : 'application/json',
		dataType : "json",
		data : formToJSON(email, password, firstName, lastName, phone, address),
		success: function(data){
			if(data.message != undefined){
				toastr["error"](data.message, data.header);
				$('input[name="email"]').val("");
				$('input[name="password"]').val("");
				$('input[name="fname"]').val("");
				$('input[name="lname"]').val("");
				$('input[name="phone"]').val("");
				$('input[name="address"]').val("");
			} else{
				if(data){
					toastr["info"]("Please check out your email for verification");
					setTimeout(function(){ document.location.href = rootURL3; }, 3000);
				}
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function formToJSON(email, password, firstName, lastName, phone, address){
	return JSON.stringify({
		"email": email,
		"password": password,
		"firstName": firstName,
		"lastName": lastName,
		"phoneNumber": phone,
		"address": address
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