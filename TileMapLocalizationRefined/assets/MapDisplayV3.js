var map;
var projection;
var minZoomLevel = 10;
var maxZoomLevel = 14;
var mapLength = 40 * 0.8;
var paddingWidth = 10.5 * 0.8;
var TILE_SIZE = 256;
var TILE_SIZE_IN_WORLD_COORDINATE = TILE_SIZE / Math.pow(2, minZoomLevel);
var mapNWLatLng = new google.maps.LatLng(0, 0);
var mapNWPoint;
var mapSEPoint;
var mapSELatLng;
var mapSWPoint;
var mapSWLatLng;
var mapCenterPoint;
// google.maps.Point(mapNWPoint.x/2+mapSEPoint.x/2,mapNWPoint.y/2+mapSEPoint.y/2);
var mapCenterLatLng;// = projection.fromPointToLatLng(mapCenterPoint);
var currentPositionX;
var currentPositionY;
var myLocationLatLng;
var myLocationMarker = null;
var myLocationInfoWindow = null;
var insMarker = null;
var currentMapTypeId;
var latestTapLatlng;
var latestTapPoint;
var isMouseDown = false;
var isDuringDiving = false;
function xjtuTilesMapType() {
}

xjtuTilesMapType.prototype.tileSize = new google.maps.Size(TILE_SIZE, TILE_SIZE);
xjtuTilesMapType.prototype.maxZoom = 14; // 最大显示级别
xjtuTilesMapType.prototype.minZoom = 10; // 最小显示级别

// 设置加载本地地图图片路径
xjtuTilesMapType.prototype.getTile = function(coord, zoom, ownerDocument) {
	var div = ownerDocument.createElement('div');

	var row = coord.y - Math.pow(2, zoom - 1);
	var col = coord.x - Math.pow(2, zoom - 1);
	var level = zoom - minZoomLevel;
	var xjtuTilesPath = './xjtuTiles/';
	var basePath = xjtuTilesPath;
	// console.log(level + ' ' + row + ' ' + col);
	if (row >= 0 && col >= 0 && row < Math.pow(2, level)
			&& col < Math.pow(2, level)) {

		var filePath = basePath + 'xjtu_' + level + '_' + row + '_' + col
				+ ".jpg";
		// console.log('FilePath:' + filePath);
		div.innerHTML = '<img name="" src="' + filePath + '"/>';

		div.style.width = this.tileSize.width + 'px';
		div.style.height = this.tileSize.height + 'px';
		return div;
	} else {
		var filePath = basePath + 'blank.jpg';
		div.innerHTML = '<img name="" src="' + filePath + '"/>';

		div.style.width = this.tileSize.width + 'px';
		div.style.height = this.tileSize.height + 'px';
		return div;
	}

};

// 实例化本地地图对象
var xjtuTilesMapType = new xjtuTilesMapType();

function QJTilesMapType() {
}

QJTilesMapType.prototype.tileSize = new google.maps.Size(TILE_SIZE, TILE_SIZE);
QJTilesMapType.prototype.maxZoom = 14; // 最大显示级别
QJTilesMapType.prototype.minZoom = 10; // 最小显示级别

// 设置加载本地地图图片路径
QJTilesMapType.prototype.getTile = function(coord, zoom, ownerDocument) {
	var div = ownerDocument.createElement('div');

	var row = coord.y - Math.pow(2, zoom - 1);
	var col = coord.x - Math.pow(2, zoom - 1);
	var level = zoom - minZoomLevel;
	var qj3TilesPath = './QJL3Tiles256JPG/';
	var basePath = qj3TilesPath;
	console.log(level + ' ' + row + ' ' + col);
	if (row >= 0 && col >= 0 && row < Math.pow(2, level)
			&& col < Math.pow(2, level)) {

		var filePath = basePath + 'xjtu_' + level + '_' + row + '_' + col
				+ ".jpg";
		// console.log('FilePath:' + filePath);
		div.innerHTML = '<img name="" src="' + filePath + '"/>';

		div.style.width = this.tileSize.width + 'px';
		div.style.height = this.tileSize.height + 'px';
		return div;
	} else {
		var filePath = basePath + 'blank.jpg';
		div.innerHTML = '<img name="" src="' + filePath + '"/>';

		div.style.width = this.tileSize.width + 'px';
		div.style.height = this.tileSize.height + 'px';
		return div;
	}

};

// 实例化本地地图对象
var qjTilesMapType = new QJTilesMapType();

function NewXjtuTilesMapType() {
}

NewXjtuTilesMapType.prototype.tileSize = new google.maps.Size(TILE_SIZE,
		TILE_SIZE);
NewXjtuTilesMapType.prototype.maxZoom = 15; // 最大显示级别
NewXjtuTilesMapType.prototype.minZoom = 10; // 最小显示级别

// 设置加载本地地图图片路径
NewXjtuTilesMapType.prototype.getTile = function(coord, zoom, ownerDocument) {
	var div = ownerDocument.createElement('div');

	var row = coord.y - Math.pow(2, zoom - 1);
	var col = coord.x - Math.pow(2, zoom - 1);
	var level = zoom - minZoomLevel;
	var xjtuTilesPath = './newXJTU8192/';
	var basePath = xjtuTilesPath;
	// console.log(level + ' ' + row + ' ' + col);
	if (row >= 0 && col >= 0 && row < Math.pow(2, level)
			&& col < Math.pow(2, level)) {

		var filePath = basePath + 'xjtu_' + level + '_' + row + '_' + col
				+ ".jpg";
		// console.log('FilePath:' + filePath);
		div.innerHTML = '<img name="" src="' + filePath + '"/>';

		div.style.width = this.tileSize.width + 'px';
		div.style.height = this.tileSize.height + 'px';
		return div;
	} else {
		var filePath = basePath + 'blank.jpg';
		div.innerHTML = '<img name="" src="' + filePath + '"/>';

		div.style.width = this.tileSize.width + 'px';
		div.style.height = this.tileSize.height + 'px';
		return div;
	}

};

// 实例化本地地图对象
var newxjtuTilesMapType = new NewXjtuTilesMapType();

function updateMyLocation() {

	currentPositionX = window.android.getX();
	currentPositionY = window.android.getY();
	// currentPositionX = Math.random() * 19 * 0.8;
	// currentPositionY = Math.random() * 40 * 0.8;

	if (currentPositionX > 0 && currentPositionY > 0) {

		var myLocationWorldX = (paddingWidth + currentPositionX) / mapLength
				* TILE_SIZE_IN_WORLD_COORDINATE + mapSWPoint.x;
		var myLocationWorldY = mapSWPoint.y - currentPositionY / mapLength
				* TILE_SIZE_IN_WORLD_COORDINATE;

		myLocationLatLng = projection.fromPointToLatLng(new google.maps.Point(
				myLocationWorldX, myLocationWorldY));

		if (myLocationMarker.getPosition().equals(myLocationLatLng) == false) {
			myLocationMarker.setPosition(myLocationLatLng);

			mapRefresh();
		}
	}
}

function mapRefresh() {

	if (isMouseDown == false) {
		isDuringDiving = true;
		var curZoom = map.getZoom();
		if (curZoom != minZoomLevel) {
			map.setZoom(minZoomLevel);
		} else
			map.setZoom(curZoom + 1);

		map.setZoom(curZoom);
		isDuringDiving = false;
	}
}

// 网页Loading
function initialize() {
	var mapCenter = new google.maps.LatLng(0, 0); // 设置地图中心
	var mapOptions = {
		zoom : minZoomLevel, // 初始时，地图的显示级别
		center : new google.maps.LatLng(0, 0), // 设置地图中心点
		streetViewControl : false,
		disableDefaultUI : false, // default UI control
		// don't display the maptype control.
		mapTypeControl : false,
		mapTypeControlOptions : {
			mapTypeIds : [ 'xjtuTiles', 'QJTiles' ],
			style : google.maps.MapTypeControlStyle.DROPDOWN_MENU,
		},
		panControl : false,
		zoomControl : false,
		zoomControlOptions : {
			position : google.maps.ControlPosition.LEFT_CENTER,
			style : google.maps.ZoomControlStyle.LARGE
		}

	};

	map = new google.maps.Map(document.getElementById("mapContainer"),
			mapOptions);
	// map.setOptions(mapOptions);
	map.mapTypes.set('xjtuTiles', xjtuTilesMapType);
	map.mapTypes.set('QJTiles', qjTilesMapType);
	map.mapTypes.set('newXJTU8192', newxjtuTilesMapType);
	map.setMapTypeId('xjtuTiles');

	google.maps.event.addListenerOnce(map, "projection_changed", function() {
		// alert("projection:"+map.getProjection());
		projection = map.getProjection();
		mapNWLatLng = new google.maps.LatLng(0, 0);
		mapNWPoint = projection.fromLatLngToPoint(mapNWLatLng);
		mapSEPoint = new google.maps.Point(projection
				.fromLatLngToPoint(mapNWLatLng).x
				+ TILE_SIZE_IN_WORLD_COORDINATE, projection
				.fromLatLngToPoint(mapNWLatLng).x
				+ TILE_SIZE_IN_WORLD_COORDINATE);
		mapSELatLng = projection.fromPointToLatLng(mapSEPoint);
		mapSWPoint = new google.maps.Point(mapNWPoint.x, mapSEPoint.y);
		mapSWLatLng = projection.fromPointToLatLng(mapSWPoint);
		mapCenterPoint = new google.maps.Point(mapNWPoint.x / 2 + mapSEPoint.x
				/ 2, mapNWPoint.y / 2 + mapSEPoint.y / 2);
		mapCenterLatLng = projection.fromPointToLatLng(mapCenterPoint);
		map.setCenter(mapCenterLatLng);
		
	});

	myLocationMarker = new google.maps.Marker({
		position : new google.maps.LatLng(-170, -80),
		visible : true,
		map : map,
	});
	insMarker = new google.maps.Marker({
		position : new google.maps.LatLng(-170, -80),
		visible : true,
		map : map,
	});

	setInterval(function() {
		updateMyLocation();
	}, 1000);

	google.maps.event.addListener(map, 'mouseup', function(event) {
		// 3 seconds after the center of the map has changed, pan back to the
		// marker.
		isMouseDown = false;
		latestTapLatlng = event.latLng;
		latestTapPoint = projection.fromLatLngToPoint(latestTapLatlng);
		if (myLocationInfoWindow != null) {
			myLocationInfoWindow.close();
		}
		
	});

	google.maps.event.addListener(map, 'mousedown', function(event) {
		// 3 seconds after the center of the map has changed, pan back to the
		// marker.
		isMouseDown = true;
		if (myLocationInfoWindow != null) {
			myLocationInfoWindow.close();
		}
	});

	google.maps.event.addListener(map, 'zoom_changed', function(event) {
		// 3 seconds after the center of the map has changed, pan back to the
		// marker.
		var cz = map.getZoom();
		if (minZoomLevel < cz && cz < maxZoomLevel && isDuringDiving == false) {
			window.android.setZoomInEnable();
			window.android.setZoomOutEnable();
		}
		if (minZoomLevel == cz && isDuringDiving == false) {
			window.android.setZoomInEnable();
			window.android.setZoomOutDisable();
		}
		if (maxZoomLevel == cz && isDuringDiving == false) {
			window.android.setZoomInDisable();
			window.android.setZoomOutEnable();
		}
	});

	google.maps.event.addListener(map, 'maptypeid_changed', function() {
		console.log("maptypeid_changed event");
		currentMapTypeId = map.getMapTypeId();
		if (currentMapTypeId == 'xjtuTiles') {
			minZoomLevel = 10;
			maxZoomLevel = 14;

			myLocationMarker.setVisible(true);
			google.maps.event.trigger(map, 'zoom_changed');
			mapRefresh();
			map.setCenter(mapCenterLatLng);
		}

		if (currentMapTypeId == 'QJTiles') {
			minZoomLevel = 10;
			maxZoomLevel = 14;

			myLocationMarker.setVisible(false);
			if (myLocationInfoWindow != null) {
				myLocationInfoWindow.close();
			}
			google.maps.event.trigger(map, 'zoom_changed');
			mapRefresh();
			map.setCenter(mapCenterLatLng);
		}
		if (currentMapTypeId == 'newXJTU8192') {
			minZoomLevel = 10;
			maxZoomLevel = 15;

			myLocationMarker.setVisible(false);
			if (myLocationInfoWindow != null) {
				myLocationInfoWindow.close();
			}
			google.maps.event.trigger(map, 'zoom_changed');
			mapRefresh();
			map.setCenter(mapCenterLatLng);
		}

	});

	google.maps.event.trigger(map, 'zoom_changed');
	
}

function myLocationButtonListener() {
	map.panTo(myLocationLatLng);
	myLocationInfoWindow = new google.maps.InfoWindow({
		content : "我猜您的位置在这里, 这里可以添标准HTML内容",
	});

	myLocationInfoWindow.open(map, myLocationMarker);
}

function zoomInButtonListener() {
	if (map.getZoom() < maxZoomLevel) {
		map.setZoom(map.getZoom() + 1);
	}
}

function zoomOutButtonListener() {
	if (map.getZoom() > minZoomLevel) {
		map.setZoom(map.getZoom() - 1);
	}
}

function changeMapSpinnerListener(mapTypeId) {
	switch (mapTypeId) {
	case 0:
		map.setMapTypeId("xjtuTiles");
		break;
	case 1:
		map.setMapTypeId("QJTiles");
		break;
	case 2:
		map.setMapTypeId("newXJTU8192");
		break;
	}

}