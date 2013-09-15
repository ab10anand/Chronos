var Db = require('mongodb').Db;
var Connection = require('mongodb').Connection;
var Server = require('mongodb').Server;
var BSON = require('mongodb').BSON;
var ObjectID = require('mongodb').ObjectID;

EventDataCollector = function (host, port) {
	this.db= new Db('node-event-data', new Server(host, port, {safe: false}, {auto_reconnect: true}, {}));
	this.db.open(function(){});
};

EventDataCollector.prototype.getCollection= function(callback) {
  this.db.collection('event-data', function(error, event_collection) {
    if( error ) callback(error);
    else callback(null, event_collection);
  });
};

EventDataCollector.prototype.showAll = function(callback) {
	this.getCollection(function(error, event_collection) {
		if( error ) callback(error)
		else {
			event_collection.find().toArray(function(error, results) {
				if(error) callback(error)
				else callback(null, results);
			});
		}
	});
};

EventDataCollector.prototype.save = function(events, callback) {
	this.getCollection(function(error, event_collection) {
      		if( error ) callback(error)
      		else {
        		if( typeof(events.length)=="undefined")
          			events = [events];

        		for( var i =0;i< events.length;i++ ) {
          			event = events[i];
          			event.created_at = new Date();
        		}

        		event_collection.insert(events, function() {
          			callback(null, events);
        		});
      		}
    	});
};

exports.EventDataCollector = EventDataCollector;
