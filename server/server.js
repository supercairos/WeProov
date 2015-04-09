var express = require('express');
var http = require('http');
var fs = require('fs');
var bodyParser = require('body-parser')
var logger = require('./libs/logger.js');
var config = require('./config/config.js');
var passport = require('passport');

var app = express();

app.use(function(req, res, next) {
	logger.info("Accepting connection from %s at %s for %s", req.ip, Date.now(), req.originalUrl);
	next();
});

// parse application/x-www-form-urlencoded
app.use(bodyParser.urlencoded({ 
	extended: true 
}));

// parse application/json
app.use(bodyParser.json());

// Passport
app.use(passport.initialize());

// Upload dir
app.use('/uploads', express.static('uploads'));


// dynamically include routes (Controller)
fs.readdirSync( __dirname + '/controllers').forEach(function (file) {
	if(file.substr(-3) == '.js') {
		logger.info('Inserting controller : ' + file);
		require(__dirname + '/controllers/' + file)(app);
	}
});

//TODO: Change to a SSL Server at one point;
//This was generated through the commands:
//openssl genrsa -out privatekey.pem 1024
//openssl req -new -key privatekey.pem -out certrequest.csr
//openssl x509 -req -in certrequest.csr -signkey privatekey.pem -out certificate.pem

//var options = {
//	key: fs.readFileSync('certs/privatekey.pem'),
//	cert: fs.readFileSync('certs/certificate.pem')
//};

// Create our HTTPS server listening on port 3000.
//https.createServer(options, app).listen( config.get("port") , function () {
//	logger.info('IBetYa server is running on port %d', config.get("port"));
//});

http.createServer(app).listen( config.get("port") , function () {
	logger.info('WeProov server is running on port %d', config.get("port"));
});
