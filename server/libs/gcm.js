var gcm = require('node-gcm');
var config = require('../config/config.js');

module.exports = new gcm.Sender(config.get('gcm:id'));