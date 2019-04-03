
const tileLayerURL = "https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw";
const MAP_ZOOM = 12;
const MAX_MAP_ZOOM = 19;
const MAP_ID = 'mapbox.streets';

const editHotelURL = "/api/editHotel";
const getAdditionalServicesURL = "/api/getAdditionalServicesURL";
const getRoomsURL = "/api/getRooms";

$(document).ready(function(){
	setUpMap();
	loadAdditionalServices();
	loadRooms();
	
})

function setUpMap(){
	var destMap = L.map('mapDiv').setView([45,0], 3);
	L.tileLayer(tileLayerURL, {
		maxZoom: MAX_MAP_ZOOM,
		id: MAP_ID
	}).addTo(destMap);
	var marker = L.marker([45,0],{
		draggable: true
	  }).addTo(destMap);
	marker.on('dragend', function (e) {
		$("#destinationLatitudeAdd").val(marker.getLatLng().lat);
		$("#destinationLongitudeAdd").val(marker.getLatLng().lng);
	  });
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


function loadAdditionalServices(){
	$.getJSON(getAdditionalServicesURL, function(data){
		var list = data == null ? [] : (data instanceof Array ? data : [ data ]);
		renderAdditionalServices(list);
	});
}

function renderAdditionalServices(list){
	var table = $("#additionalServicesTable");
	$.each(list, function(i, val){
		
	
	});
}

function loadRooms(){
	$.getJSON(getRoomsURL, function(data){
		var list = data == null ? [] : (data instanceof Array ? data : [ data ]);
		renderRooms(list);
	});
}

function renderRooms(list){
	var table = $("#roomsTable");
	$.each(list, function(i, val){
		
	
	});
}

