var EventDataCollector = require('./eventDataCollector').EventDataCollector;

var eventDataCollector= new EventDataCollector('localhost', 27017);

Recommender = function() {
};

Recommender.prototype.getApps = function(event, callback) {
	var eventDate = new Date(event.time);
	var slot = getTimeSlotOfDay(eventDate);
	var id = event.id;
	var connectType = 0;
	if(event.mobileData)
		if(event.mobileData == '3g')
			connectType = 3;
	eventDataCollector.findById(id, function(error, result) {
		var events = result;
		var appsByTime = getAppsByTimeSlot(slot, events);
		var appsByConnectivity = getAppsByConnectivity(connectType, events);
		var allApps = appsByConnectivity.concat(appsByTime);
		console.log(allApps);
		callback(null, allApps)
	});
};

getAppsByConnectivity = function(connectType, events) {
	var apps = [];
	if(connectType == 3) {
		apps.push('com.google.android.apps.maps');
	}
	return apps;
};

getAppsByTimeSlot = function(slot, events) {
	var mostUsed = {};
	for (var i = 0; i < events.length; i++) {
		var event = events[i];
		var date = new Date(event.time);
		var eventSlot = getTimeSlotOfDay(date);
		
		if(eventSlot == slot) {
			if(mostUsed[event.app]) {
				mostUsed[event.app] = mostUsed[event.app] + 1;
			}
			else {
				mostUsed[event.app] = 1;
			}
		}
		
	}
	mostUsed = filterList(mostUsed);
	var appList = sortAppMap(mostUsed);
	if(slot == 4) {
		var suggestedNewApp = ['com.amazon.kindle'];
		appList = suggestedNewApp.concat(appList);
	}
	return appList;
};

function filterList(map){
	var filterList = ["com.chronos.reco", "com.sec.android.app.launcher", "com.android.contacts",  "com.android.vending", "com.google.android.gm", "com.android.settings", "null", "com.android.stk", "com.sec.android.app.controlpanel"];
	for(var key in map){
		if(!isDefined(key) || filterList.indexOf(key) != -1 ){
			delete map[key];
		}
	}
	return map;
}

function isDefined(val){
	if(val == "" || val == " " || val == undefined){
		return false;
	}
	return true;
}

sortAppMap = function(appMap) {
	var appList = [];
	for(var app in appMap) {
		appList.push({'app' : app, counter: appMap[app]});
	}
	console.log(appList);
	appList.sort(function(a,b) {
		if(a.counter < b.counter) {
			return 1;
		}
		if(a.counter > b.counter) {
			return -1;
		}
		return 0;
	});
	var apps = [];
	for(var i = 0; i < appList.length; i++) {
		apps.push(appList[i].app);
	}
	console.log(apps);
	return apps;
};

getTimeSlotOfDay = function (date) {
	var hour = date.getHours();
	if(hour > 5 && hour < 12) 
		return 1;
	if(hour >= 12 && hour < 5)
		return 2;
	if(hour >=5 && hour < 10)
		return 3;
	return 4;
};

exports.Recommender = Recommender;
