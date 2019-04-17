const TOKEN_KEY = 'jwtToken';

const tileLayerURL = "https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw";
const MAP_ZOOM = 12;
const MAX_MAP_ZOOM = 19;
const MAP_ID = 'mapbox.streets';

$(document).ready(function() {
	setUpMap(45.267136, 19.833549, 'basicMapDiv');
	setUpMap(45.267136, 19.833549, 'branchMapDiv');
	
	$('#branchTable').DataTable({
        "paging": false,
        "info": false,
        "scrollY": "17vw",
        "scrollCollapse": true,
        "retrieve": true,
    });
	
	$('a[data-toggle="tab"]').on('shown.bs.tab', function(e){
		$($.fn.dataTable.tables(true)).DataTable().columns.adjust();
	});
});

function setUpMap(latitude, longitude, div) {
	var branchOfficeMap = L.map(div).setView([ latitude, longitude ], MAP_ZOOM);
	L.tileLayer(tileLayerURL, {
		maxZoom : MAX_MAP_ZOOM,
		id : MAP_ID
	}).addTo(branchOfficeMap);
	var marker = L.marker([ latitude, longitude ], {
		draggable : true
	}).addTo(branchOfficeMap);
}