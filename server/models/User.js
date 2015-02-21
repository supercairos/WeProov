var database = require('../config/db.js');

var log = require('../libs/logger.js');

var UsersSchema = database.Schema({
    username: { type: String, index: true },
    facebook_id: { type: String, index: true },
    email: String,
    password: String,
    token: String,
    gcm_token: String
});

// This is a static method;
UsersSchema.statics.hash = function(password){
	// TODO : This needs to be reworked; 
	return password + "Romain"; 
}

UsersSchema.methods.compareHash = function (otherPassword) {
	return this.password == UsersSchema.statics.hash(otherPassword);
}

module.exports = database.model('Users', UsersSchema);