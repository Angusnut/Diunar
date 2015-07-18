header = do ->
  signinAnchor = $('#layout-header-signin')
  signupAnchor = $('#layout-header-signup')
  userAnchor = $('#layout-header-user')
  createAnchor = $('#layout-header-create')

  signin = (username)!->
    signinAnchor.addClass 'layout-hide'
    signupAnchor.addClass 'layout-hide'
    userAnchor.removeClass 'layout-hide'
    createAnchor.removeClass 'layout-hide'
    userAnchor.text username

  signout = !->
    signinAnchor.removeClass 'layout-hide'
    signupAnchor.removeClass 'layout-hide'
    userAnchor.addClass 'layout-hide'
    createAnchor.addClass 'layout-hide'

  return {
    signin: signin
    signout: signout
  }

createHeaderItem = (container)!->
  alertMsg = container.find('.layout-header-container-alert')

  show = !->
    alertMsg.addClass 'layout-hide'
    container.find('input').val ''
    container.removeClass 'layout-hide'

  hide = !-> container.addClass 'layout-hide'

  alert = (msg)!->
    alertMsg.removeClass 'layout-hide'
    alertMsg.text msg

  return {
    show: show
    hide: hide
    alert: alert
  }

signin = createHeaderItem($('#layout-header-signin-container'))
signup = createHeaderItem($('#layout-header-signup-container'))

userMenu = do ->
  menu = $('#layout-header-user-menu')

  show = !-> menu.removeClass 'layout-hide'
  hide  = !-> menu.addClass 'layout-hide'

  return {
    show: show
    hide: hide
  }

$('#layout-header-signin-form').submit (e)!->
  e.preventDefault!
  data = {
    username: @username.value
    password: @password.value
  }
  $.post '/s-signin', data, (msg)!->
    if msg == 'ok'
      location.href = location.href
      # header.signin data.username
      # signin.hide!
    else
      signin.alert msg

$('#layout-header-signup-form').submit (e)!->
  e.preventDefault!
  if @password.value != @confirmPassword.value then signup.alert 'two passwords not match'
  else
    data = {
      username: @username.value
      password: @password.value
    }
    $.post '/s-signup', data, (msg)!->
      if msg == 'ok'
        location.href = location.href
        # header.signin data.username
        # signup.hide!
      else
        signup.alert msg

$('#layout-header-signin').click (e)!->
  e.preventDefault!
  signup.hide!
  signin.show!

$('#layout-header-signup').click (e)!->
  e.preventDefault!
  signin.hide!
  signup.show!

$('#layout-header-user').mouseenter !-> userMenu.show!

$('#layout-header-user-menu').mouseleave !-> userMenu.hide!

$('#layout-header-user-signout').click !->
  $.get '/s-signout', !-> location.href = location.href
  # userMenu.hide!
  # header.signout!
