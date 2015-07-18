alertMsg = $('#create-msg-alert')
alertMsg.hide!

locationPicker = new LocationPicker('create-msg-map-container')

$('#create-msg-form').submit (e)!->
  e.preventDefault!
  $.ajax {
    type: 'POST'
    url: '/s-create-msg'
    contentType: 'application/json'
    data: JSON.stringify({
      title: @title.value
      type: @type.value
      tag: @tag.value
      contact: @contact.value
      detail: @detail.value
      location: locationPicker.location!
    })
    success: (msg)!->
      if msg == 'ok'
        location.href = '/home'
      else
        alertMsg.show!
        alertMsg.text msg
  }
