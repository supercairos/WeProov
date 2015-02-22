var database = require('../config/db.js');
var log = require('../libs/logger.js');

var crypto = require('crypto');

var UsersSchema = database.Schema({
    username: { type: String, index: true },
    email: String,
    password: String,
    token: String,
    gcm_token: String
});

// This is a static method;
UsersSchema.statics.hash = function(password){
	return  crypto.createHash("sha256").update(password, "utf8").digest("base64"); 
}

UsersSchema.methods.compareHash = function (otherPassword) {
	return this.password == UsersSchema.statics.hash(otherPassword);
}

module.exports = database.model('Users', UsersSchema);