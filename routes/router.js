var express = require('express');
var router = express.Router();
var cookieParser = require('cookie-parser');
var user = require('./controller/user');
var message = require('./controller/message');
var mobile = require('./mobile-router');


router.use('/mobile', mobile);


router.get('/', message.home);
//data requests
router.get('/s-signout', user.logout);
router.post('/s-signin', user.login);
router.post('/s-signup', user.register)


router.get('/home', message.home);
router.get('/me', message.me);

//pages requests
router.get('/msg/:id', message.queryMessageById);
router.get('/create-msg', message.createMessage);

//data requests
router.post('/s-create-msg', message.postMessage);
router.post('/s-modify-msg', message.editMessage);
router.post('/s-remove-msg', message.deleteMessage);

router.post('/r-query-msgs', message.queryMessage);



/*
router.get('/', function(req, res) {
  res.render('register.html', { username: 'Undefined' });
});

router.get('/logintest', function(req, res) {
  res.render('login.html', { username: 'Undefined' });
});


router.post('/register', user.register);

router.post('/login', user.login);

router.post('/logout', user.logout);

router.post('/postMessage', message.postMessage);

router.post('/editMessage', message.editMessage);

router.post('/deleteMessage', message.deleteMessage);



router.post('/queryMyMessage', message.queryMyMessage);
*/

module.exports = router;
