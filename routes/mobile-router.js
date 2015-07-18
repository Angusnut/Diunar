var express = require('express');
var router = express.Router();
var cookieParser = require('cookie-parser');
var user = require('./mobile-controller/user');
var message = require('./mobile-controller/message');

router.get('/', function(req, res) {
  res.render('register.html', { username: 'Undefined' });
});

router.get('/logintest', function(req, res) {
  res.render('login.html', { username: 'Undefined' });
});


//请求参数username,password
//返回参数：
//    1、请求失败情况 返回json格式{status: false, hint: "XXXX(错误信息)"}
//    2、请求成功情况 返回json格式{status: true, hint: "注册成功"}
router.post('/register', user.register);

//参数同上
router.post('/login', user.login);

//无参数
router.post('/logout', user.logout);



//请求参数：
//    title, 标题
//    type, 0表示失物信息，1表示捡拾信息
//    tag, 物品标签
//    location, 位置，使用百度地图API
//    description, 描述, 这个是用户选填的部分,可以为undefined,其他参数均为必填的部分
//    contactInfo, 联系方式
//返回参数：
//    1、请求失败情况 返回json格式{status: false, hint: "XXXX(错误信息)"}
//    2、请求成功情况 返回json格式{status: true, hint: "XXXX(成功信息)", message: currentMessage(已经添加到数据库中的message)}
router.post('/postMessage', message.postMessage);

//请求参数:
//    title, 标题
//    type, 0表示失物信息，1表示捡拾信息
//    tag, 物品标签
//    location, 位置，使用百度地图API
//    description, 描述, 可以为undefined
//    contactInfo, 联系方式
//返回参数：
//    1、请求失败情况 返回json格式{status: false, hint: "XXXX(错误信息)"}
//    2、请求成功情况 返回json格式{status: true, hint: "XXXX(成功信息)"}
router.post('/editMessage', message.editMessage);

//请求参数, messageId, 为数据库中message的id
//返回参数, 
//    1、请求失败 返回{status: false, hint: "XXXX(错误信息)"}
//    2、请求成功 返回{status: true, hint: "XXXX(成功信息)"}
router.post('/deleteMessage', message.deleteMessage);

//请求参数type,tag,time,location, 四个均为选填
//返回参数,
//    1、请求失败 返回{status: false, hint: "XXXX(错误信息)"}
//    2、请求成功 返回{status: true, hint: "XXXX(成功信息)",  messages: messages(查询到的message的数组)}
router.post('/queryMessage', message.queryMessage);

//无参数，登陆之后即可请求
//返回参数,
//    1、请求失败 返回{status: false, hint: "XXXX(错误信息)"}
//    2、请求成功 返回{status: true, hint: "XXXX(成功信息)",  messages: messages(查询到的message的数组)}
router.post('/queryMyMessage', message.queryMyMessage);







module.exports = router;
