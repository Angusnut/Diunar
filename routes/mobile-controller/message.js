var express = require('express');
var mongoose = require('mongoose');
var Emitter = require('events').EventEmitter;
var User = mongoose.model('User');
var Message = mongoose.model('Message');



var tags = ["书", "手机", "电脑", "钱包", "证件", "钥匙", "衣服", "数码", "其他"];

exports.postMessage = function(req, res){
    var emitter = new Emitter();
    if(req.session.user){
    	User.findOne({username : req.session.user.username}, function (err, user){
            if(err){
            	res.send({status : false, hint : "系统错误"});
            } else {
            	if(!user){
            	    res.send({status: false, hint: "发送信息需要先登陆"});
            	} else {
            		emitter.emit('addMessage');
            	}
            }
        });
    } else {
        res.send({status: false, hint: "发送信息需要先登陆"});
    }
    emitter.on('addMessage', function(){
        var currentMessage = new Message({
        	title          : req.body.title,
            posterId       : req.session.user.username,
            type           : req.body.type,
            tag            : req.body.tag,
            time           : new Date(),
            location       : {x: req.body.x, y: req.body.y, place: req.body.place},
            description    : req.body.description == undefined ? "": req.body.description,
            contactInfo    : req.body.contactInfo
        });
        currentMessage.save(function(err, doc){
            if(err){
                res.send({status : false, hint : "系统错误"});
            } else {
            	currentMessage.messageId = doc._id;
                currentMessage.x = req.body.x;
                currentMessage.y = req.body.y;
            	res.send({status: true, hint: "信息发布成功", message: currentMessage});
            }
        });
    });
};

exports.editMessage = function(req, res){
    var emitter = new Emitter();
    if(req.session.user.username){
    	User.findOne({username : req.session.user.username}, function (err, user){
            if(err){
            	res.send({status : false, hint : "系统错误"});
            } else {
            	if(!user){
            		res.send({status: false, hint: "修改信息需要先登陆"});
            	} else {
            		emitter.emit('editMessage');
            	}
            }
        });
    } else {
    	res.send({status: false, hint: "修改信息需要先登陆"});
    }
    emitter.on('editMessage', function(){
        Message.update({_id: req.body._id}, {$set: {
        	title          : req.body.title,
        	posterId       : req.session.user.username,
            type           : req.body.type,
            tag            : req.body.tag,
            time           : new Date(),
            location       : {x: req.body.x, y: req.body.y, place: req.body.place},
            description    : req.body.description == undefined ? "": req.body.description,
            contactInfo    : req.body.contactInfo
        }}, {}, function(err, doc){
            if (!err && doc) {
                res.send({status: true, hint: "修改成功"});
            }else{
                res.send({status: false, hint: "修改失败"});
            }
        });
    });
};

exports.deleteMessage = function(req, res){
    var emitter = new Emitter();
    if(req.session.user.username){
    	User.findOne({username : req.session.user.username}, function (err, user){
            if(err){
            	res.send({status : false, hint : "系统错误"});
            } else {
            	if(!user){
            		res.send({status: false, hint: "删除信息需要先登陆"});
            	} else {
                    emitter.emit('deleteMessage');
            	}
            }
        });
    } else {
    	res.send({status: false, hint: "删除信息需要先登陆"});
    }
    emitter.on('deleteMessage', function(){
        Message.remove({_id: req.body._id},function(err, doc) {
            if (!err) {
                res.send({status: true, hint: "删除成功"});
            } else {
                res.send({status: false, hint: "删除失败"});
            }
        });
    });
};



function cmp(a, b){
    return a.dist < b.dist;
};


//查询的时候可以按照type,tag,time,location查询
exports.queryMessage = function(req, res){
    var typeSet = [0, 1];
    if(req.body.type != undefined){
        typeSet = [req.body.type];
    }
    var tagSet = tags;
    if(req.body.tag != undefined){
    	tagSet = [req.body.tag];
    }
    var timeStart = new Date(2015,1,1);
    var timeEnd = new Date();
    if(req.body.time != undefined){
    	var year = Number(req.body.time.split("-")[0]);
    	var month = Number(req.body.time.split("-")[1]);
    	var day = Number(req.body.time.split("-")[2]);
    	timeStart = new Date(year, month-1, day, 0, 0, 0);
    	timeEnd = new Date(year, month-1, day, 23, 59, 59);
    }
    Message.find(
        {
            type: {"$in": typeSet},
            tag : {"$in": tagSet},
            time: {"$gte": timeStart, "$lte": timeEnd}
        }, function(err, messages){
            var msgs = [];
            for(var i = 0; i < messages.length; i++){
                var current = {
                    _id: messages[i]._id,
                    type: messages[i].type,
                    title: messages[i].title,
                    tag: messages[i].tag,
                    description: messages[i].description,
                    time: messages[i].time,
                    location: messages[i].location,
                    contactInfo: messages[i].contactInfo,
                }
                if(req.body.location != undefined && req.body.location != null){
                    x = req.body.location.x;
                    y = req.body.location.y;
                    for(var i = 0; i < messages.length; i++){
                        current[i].dist = (current[i].location.x - x)*(current[i].location.x - x) + (current[i].location.y - y)*(current[i].location.y - y);
                    }
                }
                msgs[i] = current;
            }
            if(req.body.location != undefined && req.body.location != null){
                msgs.sort(cmp);
            } 
            res.send({status : true, hint : "查询成功", messages : msgs.slice(req.body.page*10, req.body.page*10+10)});
        }
    );
};

exports.queryMyMessage = function(req, res){
	if(!req.session.user){
		res.send({status: false, hint: "请先登录"});
	} else {
		Message.find({posterId: req.session.user.username}, function(err, messages){
            if(err){
            	res.send({status: false, hint: "系统错误"});
            } else {
            	res.send({status: true, hint: "查询成功", messages: messages});
            }
		});
	}
};

