var config                  = require('../config/config.js');
var log						= require('../libs/logger.js');
var passport                = require('passport');
var crypto                  = require('crypto');

var User                    = require('../models/User.js');
var BasicStrategy           = require('passport-http').BasicStrategy;
var BearerStrategy          = require('passport-http-bearer').Strategy;

passport.use(new BasicStrategy(
    function(userid, password, done) {
        log.info("Trying to login: %s with password %s (hashed: %s)", userid, password, User.hash(password));
        User.where({ email: userid.trim(), password: User.hash(password.trim()) }).findOne( function(err, user) {
            if (err) { 
                log.error("Database failed with error: %s", err);
                return done(err); 
            }

            if (!user) { 
                log.info("Couldn't find user :( ", err);
                return done(null, false); 
            }

            try {
                user.token = crypto.randomBytes(256).toString('hex');
                log.info("Generated token %s for user %s", user.token, user.email);
            } catch (ex) {
                log.info("Couldn't create a new token :( ", err);
                return done(ex); 
            }

            user.save(function (err, user) {
                if (err) { 
                    log.error("Database failed with error: %s", err);
                    return done(err); 
                }
                
                log.info("%s logged in success", user.email);
                return done(null, user);
            });
            
        });
    }
));

passport.use(new BearerStrategy(
    function(token, done) {
        log.info("Trying auth with token : " + token);
        User.findOne({ token: token }, function (err, user) {
            if (err) { return done(err); }
            if (!user) { 
                log.error("Could not log in user with token : " + token);
                return done(null, false); 
            }

            log.info("Login success for user : " + user.email);
            return done(null, user);
        });
    }
));

module.exports = function(app) {

}

