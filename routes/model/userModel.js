var mongoose = require('mongoose');
var Schema = mongoose.Schema;
var userSchema = new Schema({
    username    : {type: String, index: true, require: true},
	password    : {type: String, require: true}
});
exports.User = mongoose.model('User', userSchema);