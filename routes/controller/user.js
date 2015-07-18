var express = require('express');
var mongoose = require('mongoose');
var User = mongoose.model('User');


exports.register = function(req, res){
	var username_ = req.body.username;
	var password_ = req.body.password;
	var currentUser = new User({username : username_, password : password_});
    User.findOne({username : currentUser.username}, function (err, user){
        if(err){
            res.send("系统错误");
        } else {
            if (user) {
                res.send("该用户名已被使用");
            } else {
                currentUser.save(function(err, doc){
                    if(err){
                        res.send("系统错误");
                    } else {
                        req.session.user = {
                            username: username_,
                            password: password_
                        };
                        res.send("ok");
                    }
                });
            }
        }
    });
};

exports.login = function (req, res) {
    var username_ = req.body.username;
    var password_ = req.body.password;
    User.findOne({username : username_}, function (err, user){
        if(err){
            res.send("系统错误");
        } else {
            if (user) {
                if(user.password != password_){
                    res.send("密码错误");
                } else {
                    req.session.user = {
                        username: username_,
                        password: password_
                    };
                    res.send("ok");
                }
            } else {
                res.send("用户名不存在");
            }
        }
    });
};

exports.logout = function(req, res) {
    req.session.user = undefined;
    res.send();
};
