var app = require('./app');
//var cluster = require('cluster');
//var CPU = require('os').cpus();
// create listening port
var debug = require('debug')('SYserver');
app.set('port', process.env.PORT || 8888);


var server = app.listen(app.get('port'), function () {
    console.log('Express server listening on port ' + server.address().port);
});