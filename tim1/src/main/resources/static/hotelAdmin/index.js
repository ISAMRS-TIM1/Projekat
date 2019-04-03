
const tileLayerURL = "https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw";
const MAP_ZOOM = 12;
const MAX_MAP_ZOOM = 19;
const MAP_ID = 'mapbox.streets';

const editHotelURL = "/api/editHotel";
const getAdditionalServicesURL = "/api/getAdditionalServicesURL";
const getRoomsURL = "/api/getRooms";
const getHotelOfAdminURL = "/api/getHotelOfAdmin";

$(document).ready(function(){
	loadHotel();
	
})

function setUpMap(latitude, longitude){
	var destMap = L.map('mapDiv').setView([latitude, longitude], MAP_ZOOM);
	L.tileLayer(tileLayerURL, {
		maxZoom: MAX_MAP_ZOOM,
		id: MAP_ID
	}).addTo(destMap);
	var marker = L.marker([latitude, longitude],{
		draggable: true
	  }).addTo(destMap);
	marker.on('dragend', function (e) {
		$("#latitude").val(marker.getLatLng().lat);
		$("#longitude").val(marker.getLatLng().lng);
	  });
}

function hideModal(){
	$("#modalDialog").fadeOut(function(){
		$("#modalDialog").empty();
	});
	$("#overlayDiv").fadeOut();
}
function showModal(callback){
	$("#modalDialog").fadeIn(function(){
		if(callback !== undefined) callback();
	});
	$("#overlayDiv").fadeIn();
}


function editHotel(e){
	e.preventDefault();
	$.ajax({
		type: 'POST',
		url: editHotelURL,
		contentType: "application/json",
		data: JSON.stringify({
			//TODO
		}),
		dataType: "json",
		success: function(data){
			alert(data);
		}});
}


function loadHotel(){
	token = localStorage.getItem["token"];
	$.ajax({
		  dataType: "json",
		  url: getHotelOfAdminURL,
		  headers:{"Authorization":"Bearer" + token},
		  success: function(data){
			  	$("#hotelName").text(data["name"]);
			  	$("#hotelDescription").text(data["description"]);
			  	setUpMap(data["location"]["latitude"], data["location"]["longitude"]);
				renderAdditionalServices(data["additionalServices"]);
				renderRooms(data["rooms"]);
			}
		});
}

function renderAdditionalServices(list){
	var table = $("#additionalServicesTable");
	$.each(list, function(i, val){
		var tr = $("<tr class='tableData'></tr>");
		tr.append(`<td>${val.name}</td>`);
		tr.append(`<td>${val.price}</td>`);
		table.append(tr);
	});
}

function renderRooms(list){
	var table = $("#roomsTable");
	$.each(list, function(i, val){
		var tr = $("<tr class='tableData'></tr>");
		tr.append(`<td>${val.roomNumber}</td>`);
		tr.append(`<td>${val.defaultPriceOneNight}</td>`);
		tr.append(`<td>${val.numberOfPeople}</td>`);
		tr.append(`<td>${val.averageGrade}</td>`);
		table.append(tr);
	});
}

