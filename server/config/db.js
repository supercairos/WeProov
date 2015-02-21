var mongoose = require('mongoose');
var config = require('./config.js');
var logger = require('../libs/logger.js');

mongoose.connect(config.get("mongoose:uri"));

mongoose.connection.on('error', function callback() {
	logger.error('Could not connect to %s', config.get("mongoose:uri"));
});

mongoose.connection.once('open', function callback () {
	logger.info('Database is connected to %s', config.get("mongoose:uri"));
});


module.exports = mongoose;