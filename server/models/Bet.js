var database = require('../config/db.js');

var log = require('../libs/logger.js');

var BetsSchema = database.Schema({
    title: String,
    description: String,
    picture: String
});

module.exports = database.model('Bets', BetsSchema);