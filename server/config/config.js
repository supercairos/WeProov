var fs    = require('fs');
var nconf = require('nconf');

nconf.argv()
.env()
.file({ 
	file: './config/config.json',
	search: true
});

module.exports = nconf;
