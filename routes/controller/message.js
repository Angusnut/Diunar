var express = require('express');
var mongoose = require('mongoose');
var Emitter = require('events').EventEmitter;
var User = mongoose.model('User');
var Message = mongoose.model('Message');


var tags = ["书", "手机", "电脑", "钱包", "证件", "钥匙", "衣服", "数码", "其他"];





exports.home = function(req, res){
    var username = (req.session.user == undefined) ? undefined : req.session.user.username;
    Message.find({type: 0}).sort({'time': -1}).exec(function(err, messages){
        var lostMsgs = [];
        for(var i = 0; i < messages.length; i++){
            var current = {
                _id: messages[i]._id,
                title: messages[i].title,
                tag: messages[i].tag,
                detail: messages[i].description,
                date: messages[i].time,
                location: messages[i].location,
                publisher: messages[i].posterId,
                contact: messages[i].contactInfo
            }
            lostMsgs[i] = current;
        }
        res.render('home', {username: username, msgs: lostMsgs.slice(0, 10), tags: tags, pageCount: Math.ceil(lostMsgs.length/10)});
    });
};


exports.me = function(req, res){
    var size = 10;
    if(!req.session.user){
        res.redirect('/home');
    } else {
        Message.find({posterId: req.session.user.username}).sort({'time': -1}).limit(size).exec(function(err, messages){
            var lostMsgs = [];
            var foundMsgs = [];
            var idx1 = 0;
            var idx2 = 0;
            for(var i = 0; i < size; i++){
                if(i == messages.length){
                    break;
                }
                var current = {
                    _id: messages[i]._id,
                    title: messages[i].title,
                    tag: messages[i].tag,
                    detail: messages[i].description,
                    date: messages[i].time,
                    location: messages[i].location,
                    contact: messages[i].contactInfo
                }
                if(messages[i].type == 0){
                    lostMsgs[idx1++] = current;
                } else {
                    foundMsgs[idx2++] = current;
                }
            }
            res.render('me', {username: req.session.user.username, lostMsgs: lostMsgs, foundMsgs: foundMsgs});
        });
    }
};


exports.queryMessageById = function(req, res){
    var id = req.params.id;
    Message.findById(id, function(err, message){
        msg = {
            type: message.type,
            title: message.title,
            tag: message.tag,
            detail: message.description,
            date: message.time,
            location: message.location,
            contact: message.contactInfo,
            publisher: message.posterId
        };
        var username = (req.session.user == undefined) ? undefined: req.session.user.username;
        res.render('msg', {username: username, msg: msg, tags: tags});
    });
};

exports.createMessage = function(req, res){
    if(!req.session.user){
        res.redirect('/home');
    } else {
        res.render('create-msg', {username: req.session.user.username, tags: tags});
    }
};

exports.postMessage = function(req, res){
    var emitter = new Emitter();
    if(req.session.user){
        User.findOne({username : req.session.user.username}, function (err, user){
            if(err){
                res.send("系统错误");
            } else {
                if(!user){
                    res.send("发送信息需要先登陆");
                } else {
                    emitter.emit('addMessage');
                }
            }
        });
    } else {
        res.send("发送信息需要先登陆");
    }
    emitter.on('addMessage', function(){
        var currentMessage = new Message({
            title          : req.body.title,
            posterId       : req.session.user.username,
            type           : req.body.type,
            tag            : req.body.tag,
            time           : new Date(),
            location       : req.body.location,
            description    : req.body.detail == undefined ? "": req.body.detail,
            contactInfo    : req.body.contact
        });
        currentMessage.save(function(err, doc){
            if(err){
                res.send("系统错误");
            } else {
                res.send("ok");
            }
        });
    });
};

exports.editMessage = function(req, res){
    var emitter = new Emitter();
    if(req.session.user){
        User.findOne({username : req.session.user.username}, function (err, user){
            if(err){
                res.send("系统错误");
            } else {
                if(!user){
                    res.send("修改信息需要先登陆");
                } else {
                    emitter.emit('editMessage');
                }
            }
        });
    } else {
        res.send("修改信息需要先登陆");
    }
    emitter.on('editMessage', function(){
        Message.findById(req.body._id, function(err, message){
            var update = {$set: {
                title      : req.body.title == undefined ? message.title: require.body.title,
                tag        : req.body.tag == undefined ? message.tag: req.body.tag,
                description: req.body.detail == undefined ? message.description: req.body.detail,
                contactInfo: req.body.contact == undefined ? message.contactInfo: req.body.contact,
                location   : req.body.location == undefined ? message.location: req.body.location
            }};
            Message.update({_id: req.body._id}, update, {}, function(err, doc){
                if (!err && doc) {
                    res.send("ok");
                }else{
                    res.send("修改失败");
                }
            });
        });
    });
};

exports.deleteMessage = function(req, res){
    var emitter = new Emitter();
    if(req.session.user){
        User.findOne({username : req.session.user.username}, function (err, user){
            if(err){
                res.send("系统错误");
            } else {
                if(!user){
                    res.send("删除信息需要先登陆");
                } else {
                    emitter.emit('deleteMessage');
                }
            }
        });
    } else {
        res.send("删除信息需要先登陆");
    }
    emitter.on('deleteMessage', function(){
        Message.remove({_id: req.body._id},function(err, doc) {
            if (!err && doc) {
                res.send("ok");
            } else {
                res.send("删除失败");
            }
        });
    });
};


function cmp(a, b){
    return a.dist > b.dist;
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
    if(req.body.date != undefined){
        timeStart = new Date(req.body.date+' 00:00:00');
        timeEnd = new Date(req.body.date+' 23:59:59');
    }
    Message.find(
        {
            type: {"$in": typeSet},
            tag : {"$in": tagSet},
            time: {"$gte": timeStart, "$lte": timeEnd}
        }).sort({'time': -1}).exec(function(err, messages){
            var msgs = [];
            for(var i = 0; i < messages.length; i++){
                var current = {
                    _id: messages[i]._id,
                    title: messages[i].title,
                    tag: messages[i].tag,
                    detail: messages[i].description,
                    date: messages[i].time,
                    location: messages[i].location,
                    contact: messages[i].contactInfo
                }
                if(req.body.location != undefined && req.body.location != null){
                    x = req.body.location.x;
                    y = req.body.location.y;
                    // for(var i = 0; i < messages.length; i++){
                        // current[i].dist = (current[i].location.x - x)*(current[i].location.x - x) + (current[i].location.y - y)*(current[i].location.y - y);
                        current.dist = (current.location.x - x)*(current.location.x - x) + (current.location.y - y)*(current.location.y - y);
                    // }
                }
                msgs[i] = current;
            }
            if(req.body.location != undefined && req.body.location != null){
                msgs.sort(cmp);
            } 
            res.render('r-query-msgs', {msgs: msgs.slice(req.body.page*10, req.body.page*10+10), pageCount: Math.ceil(msgs.length/10)});
        }
    );
};
