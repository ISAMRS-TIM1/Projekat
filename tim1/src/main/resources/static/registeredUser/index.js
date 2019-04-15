var logoutURL = "../logout";
var loadUserInfoURL = "../api/getUserInfo";
var saveChangesURL = "../api/editUser";

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
		
		$.ajax({
			type : 'POST',
			url : saveChangesURL,
			contentType : 'application/json',
			dataType : "json",
			data : formToJSON(firstName, lastName, phone, address),
			success: function(data){
				if(data != null){
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
});

function loadData(){
	$.ajax({
		type : 'GET',
		url : loadUserInfoURL,
		dataType : "json",
		success: function(data){
			$('input[name="fname"]').val(data.firstName);
			$('input[name="lname"]').val(data.lastName);
			$('input[name="phone"]').val(data.phone);
			$('input[name="address"]').val(data.address);
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert("AJAX ERROR: " + textStatus);
		}
	});
}

function formToJSON(firstName, lastName, phone, address){
	return JSON.stringify({
		"firstName": firstName,
		"lastName": lastName,
		"phoneNumber": phone,
		"address": address
	});
}