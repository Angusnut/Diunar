var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var messageSchema = new Schema({
	title           : {type: String, require: true},
    posterId        : {type: String, index: true, require: true},
    type            : {type: Number, require: true},         //0表示失物信息，1表示捡拾信息
    tag             : {type: String, index: true, require: true},
    time            : {type: Date, index: true, require: true},
    location        : {x: Number, y: Number, place: String},
    description     : String,
    contactInfo     : {type: String, require: true}
});
exports.Message = mongoose.model('Message', messageSchema);