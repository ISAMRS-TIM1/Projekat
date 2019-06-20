var rootURL1 = "../login";

$(document).ready(function(){
	$("#logButton").click(function(e){
		e.preventDefault();
		document.location.href = rootURL1;
	});
});