var rootURL1 = "../auth/login/"
var rootURL2 = "../index/index.html"
var rootURL3 = "../registration/registration.html"
	
$(document).on('submit', '#loginForm', function(e){
	e.preventDefault()
	var email = $('input[name="email"]').val()
	var password = $('input[name="password"]').val()
	$.ajax({
		type : 'POST',
		url : rootURL1,
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
				// put token in local storage and redirect to main page
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	})
})

$(document).on("click", "#regButton", function(e){
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

function formToJSON(username, password){
	return JSON.stringify({
		"username": username,
		"password": password
	})
}