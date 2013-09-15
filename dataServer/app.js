
/**
 * Module dependencies.
 */

var express = require('express');
var routes = require('./routes');
var user = require('./routes/user');
var http = require('http');
var path = require('path');
var EventDataCollector = require('./eventDataCollector').EventDataCollector;

var app = express();

// all environments
app.set('port', process.env.PORT || 3000);
app.set('views', __dirname + '/views');
app.set('view engine', 'jade');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.bodyParser());
app.use(express.methodOverride());
app.use(app.router);
app.use(require('stylus').middleware(__dirname + '/public'));
app.use(express.static(path.join(__dirname, 'public')));

// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}

var eventDataCollector= new EventDataCollector('localhost', 27017);

app.get('/event/showAll', function(req, res){
	eventDataCollector.showAll(function(error, result) {
		res.send(result);
	});
});
		
app.post('/event/save', function(req, res){
    eventDataCollector.save({
        id: req.param('id'),
        name: req.param('name'),
	email: req.param('email'),
	time: req.param('time'),
	battery: req.param('battery'),
	roaming: req.param('roaming'),
	screenLevel: req.param('screenLevel'),
	volume: req.param('volume'),
	vibrate: req.param('vibrate'),
	wifi: req.param('wifi'),
	mobileData: req.param('mobileData'),
	longitude: req.param('longitude'),
	latitude: req.param('latitude'),
	app: req.param('app'),
	runningAppList: req.param('runningAppList')
    }, function( error, docs) {
        res.send(docs);
    });
});

app.get('/', routes.index);
app.get('/users', user.list);

http.createServer(app).listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});
