var rootURL1 = "../ProjekatWeb/auth/login/"
var rootURL2 = "../ProjekatWeb/index.html"
var rootURL3 = "../ProjekatWeb/registration.html"
	
$(document).on('submit', '#loginForm', function(e){
	e.preventDefault()
	var username = $(this).find('input[name="email"]').val()
	var password = $(this).find('input[name="password"]').val()
	$.ajax({
		type : 'POST',
		url : rootURL1,
		contentType : 'application/json',
		dataType : "json",
		data : formToJSON(username, password),
		success : function(data) {
			if(data.usernameGood && data.passwordGood){
				localStorage.setItem("message", data.message)
				document.location.href = rootURL2
			} else{
				toastr.options = {
						  "closeButton": false,
						  "debug": false,
						  "newestOnTop": false,
						  "progressBar": false,
						  "positionClass": "toast-top-center",
						  "preventDuplicates": false,
						  "onclick": null,
						  "showDuration": "300",
						  "hideDuration": "1000",
						  "timeOut": "5000",
						  "extendedTimeOut": "1000",
						  "showEasing": "swing",
						  "hideEasing": "linear",
						  "showMethod": "fadeIn",
						  "hideMethod": "fadeOut"
						}
				$(document).find('input[name="username"]').val("")
				$(document).find('input[name="password"]').val("")
				toastr["error"](data.message)
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + errorThrown);
		}
	})
})

$(document).on("click", "#regButton", function(e){
	document.location.href = rootURL3
})

$(document).on("click", "#eye", function(e){
	// fix this
	if($("#eyeicon").hasClass("glyphicon-eye-close")){
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