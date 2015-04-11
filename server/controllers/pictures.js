var log = require('../libs/logger.js');
var multer  = require('multer');
var passport = require('passport');

module.exports = function(app) {

	app.post('/pictures', 
		//passport.authenticate('bearer', { session: false }), 
		multer({ 
		dest: 'uploads/',
		rename: function (fieldname, filename) {
			return filename.replace(/\W+/g, '-').toLowerCase() + Date.now()  
		}

	}), function(req, res){

		JSON.parse(req.body)
	
		log.info(req.body.picture);
		log.info(req.files);
	    res.status(204).end()
	    
	});

}