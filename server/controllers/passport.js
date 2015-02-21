var config                  = require('../config/config.js');
var log						= require('../libs/logger.js');
var passport                = require('passport');
var crypto                  = require('crypto');

var User                    = require('../models/User.js');
var BasicStrategy           = require('passport-http').BasicStrategy;
var BearerStrategy          = require('passport-http-bearer').Strategy;
var FacebookTokenStrategy   = require('passport-facebook-token').Strategy;

passport.use(new BasicStrategy(
    function(userid, password, done) {
        log.info("Trying to login: %s with password %s (hashed: %s)", userid, password, User.hash(password));
        User.where({ username: userid, password: User.hash(password) }).findOne( function(err, user) {
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
                log.info("Generated token %s for user %s", user.token, user.username);
            } catch (ex) {
                log.info("Couldn't create a new token :( ", err);
                return done(ex); 
            }

            user.save(function (err, user) {
                if (err) { 
                    log.error("Database failed with error: %s", err);
                    return done(err); 
                }
                
                log.info("%s logged in success", user.username);
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

            log.info("Login success for user : " + user.username);
            return done(null, user);
        });
    }
));

passport.use(new FacebookTokenStrategy({
        clientID: config.get("facebook:app_id"),
        clientSecret: config.get("facebook:app_secret")
    }, function(accessToken, refreshToken, profile, done) {
        log.info("Found profile : %s", profile.id);

        var infos = {
            username: profile.displayName,
            email: profile.emails[0]['value'],
            facebook_id: profile.id.toString(),
            token: crypto.randomBytes(256).toString('hex')
        };

        User.findOneAndUpdate({ 'facebook_id' : profile.id.toString() }, infos, { multi: true, upsert: true }, function(err, doc){
            if(err){ log.error(err); }
            return done(err, doc);
        });
    }
));

module.exports = function(app) {

}

