var User = require('../models/User.js');
//var NetworkException = require('../models/NetworkException.js');
var log = require('../libs/logger.js');

var GcmSender = require('../libs/gcm.js');
var gcm = require('node-gcm');
var passport = require('passport');
var crypto = require('crypto');

/**
 * This file is the controller for the /users root node; It will handle Signup, Login, Logout, PasswordReset and so on;
 **/

module.exports = function(app) {

	/**
	 * This is the signup action; Pass a valid JSON body containing at least 
	 **/
	app.post('/users/register', function (req, res) {


		if (!req.body) {
			log.error("A request with no body was made");
			return res.status(400).json(new NetworkException({
				code: 1,
				message:"Please provide a body with your request"
			})).end();
		}

		// // TODO: Check if username or email exist in database; 
		var myUser = new User({
			username: req.body.username,
			email: req.body.email,
			password: User.hash(req.body.password),
			token: crypto.randomBytes(256).toString('hex')
		});

		myUser.save(function (err, user) {
			if (err){
				log.error("User %s failed: %s ", myUser.name, err);
				res.status(500).json(new NetworkException({
					code: 1,
					message:"This is the error message"
				})).end();
			} else {
				log.info("A new user registered %s (token:%s)", user.username, user.token);
				res.status(200).json( user ).end();
			}
		});
	});

	app.get('/users/gcm', function(req, res) {
		// or with object values
		var message = new gcm.Message({
		    collapseKey: 'demo',
		    delayWhileIdle: true,
		    timeToLive: 3,
		    data: {
		        key1: 'message1',
		        key2: 'message2'
		    }
		});

		
		User.find(function (err, users) {
			if (err) return log.error(err);
			var registrationIds = [];

			users.forEach(function(entry) {
				if(entry.gcm_token){
				    log.info("Adding regid : %s", entry.gcm_token);
				    registrationIds.push(entry.gcm_token);
			    }
			});

			if (registrationIds.length > 0) {
				GcmSender.send(message, registrationIds, 4, function (err, result) {
					if(err){
						log.error(err);
					}

				    log.info(result);
				    res.sendStatus(200);
				    return;
				});
			} else {
				log.error("Could not find any user to send notif to :(");
				res.sendStatus(500);
			}
		});	
	});

	app.put('/users/gcm/:gcm_token', 
		passport.authenticate('bearer', { session: false }), 
		function(req, res) {
			req.user.update({ gcm_token : req.params.gcm_token }, function (err, affected, raw) {
				if (err) {
					log.error("Error is " + err);
					res.sendStatus(500);
					return;
				}
				log.info('The number of updated documents was %d', affected);
				log.info('The raw response from Mongo was %s', raw);

				res.status(200).json( req.user ).end();
			});
		}
	);

	app.get('/users/login', 
		passport.authenticate('basic', { 
			session: false 
		}), 
		function(req, res) {
			res.status(200).json( req.user ).end();
		}
	);

	app.get('/users/facebook/login',
		passport.authenticate('facebook-token', {
			session: false
		}),
		function(req, res) {
			res.status(200).json( req.user ).end();
		}
	);

	app.get('/users/autocomplete', 
		function(req, res) {
			log.info("Looking for an username starting by %s", req.query.q);
			if(req.query.q && req.query.q.length > 2){
				User.find({ 'username':  { $regex: new RegExp(req.query.q, 'i') }}, 'username picture', function(err, usernames) {
					if(err){
						log.error("Database error %s ", err);
						res.sendStatus(500);
					}

					res.status(200).json(usernames).end();
				});
			} else {
				log.info("Too short");
				res.status(200).json(new Array()).end();
			}
		}
	);
	

}
