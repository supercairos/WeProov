var winston = require('winston');

module.exports = new winston.Logger({
    transports : [
        new winston.transports.Console({
            colorize:   true,
            level:      'debug'
        }), new (winston.transports.File)({ 
            filename: './logs/' + Date.now() + '.log',
            //handleExceptions: true,
            json: false
        })
    ]
});
