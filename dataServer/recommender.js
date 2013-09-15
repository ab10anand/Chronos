var EventDataCollector = require('./eventDataCollector').EventDataCollector;

var eventDataCollector= new EventDataCollector('localhost', 27017);

Recommender = function() {
};

Recommender.prototype.getApps = function(event, callback) {
	var eventDate = new Date(event.time);
	var slot = getTimeSlotOfDay(eventDate);
	var id = event.id;
	console.log("manas:" + id);
	var events;
	eventDataCollector.findById(id, function(error, result) {
		events = result;
		var appsByTime = getAppsByTimeSlot(slot, events);
		console.log(appsByTime);
		callback(null, appsByTime)
	});
};

getAppsByTimeSlot = function(slot, events) {
	var mostUsed = [];
	for (var i = 0; i < events.length; i++) {
		var event = events[i];
		var date = new Date(event.time);
		var eventSlot = getTimeSlotOfDay(date);
		
		if(eventSlot == slot) {
			mostUsed.push(event.app);
		}
		
	}
	return mostUsed;
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
