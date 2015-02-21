var Bet = require('../models/Bet.js');
var log = require('../libs/logger.js');

/**
 * This file is the controller for the /users root node; It will handle Signup, Login, Logout, PasswordReset and so on;
 **/

module.exports = function(app) {

	app.get('/bets', function(req, res) {
		Bet.find({}, function(err, bets) {
            if(err){
				log.error("Database error %s ", err);
				res.sendStatus(500);
			}

			res.status(200).json(bets).end();
        })
	});
	
	app.post('/bets', function(req, res) {

		if (!req.body) {
			log.error("A request with no body was made");
			return res.status(400).json(new NetworkException({
				code: 1,
				message:"Please provide a body with your request"
			})).end();
		}

		// // TODO: Check if username or email exist in database; 
		var myBet = new Bet({
			title: req.body.title,
			description: req.body.description,
			picture: req.body.picture
		});

		myBet.save(function (err, bet) {
			if (err){
				log.error("Bet %s failed: %s ", myBet.title, err);
				res.status(500).json(new NetworkException({
					code: 1,
					message:"This is the error message"
				})).end();
			} else {
				log.info("Save a new bet %s", bet.title);
				res.status(200).json( bet ).end();
			}
		});
	});

}