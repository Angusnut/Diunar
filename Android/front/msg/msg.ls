_id = location.pathname.substr 5

locationPicker = new LocationPicker('msg-map-container')
# locationPicker.location eval('r=' + $('#msg-location').text!)

initTextContainer = (id)!->
  container = $("##{id}")
  editAnchor = container.find('.msg-edit-anchor')
  preview = container.find('.msg-preview')
  form = container.find('.msg-form')
  form.hide!
  submitButton = container.find('button')
  input = form.find('.msg-input')
  state = 'preview'

  form.submit (e)!->
    if form[0].checkValidity!
      e.preventDefault!
      data = _id: _id
      fieldName = input.attr('name')
      value = input.val!
      data[fieldName] = value
      $.post '/s-modify-msg', data, (msg)!->
        if msg != 'ok' then alert msg
        else
          form.hide!
          preview.show!
          preview.text value
          editAnchor.text '[edit]'
          state := 'preview'

  editAnchor.click !->
    if state == 'preview'
      preview.hide!
      form.show!
      input.val preview.text!
      editAnchor.text '[save]'
      state := 'edit'
    else
      submitButton.click!

initRadioContainer = (id)!->
  container = $("##{id}")
  editAnchor = container.find('.msg-edit-anchor')
  preview = container.find('.msg-preview')
  form = container.find('.msg-form')
  form.hide!
  input = form.find('input')
  submitButton = container.find('button')
  state = 'preview'

  form.submit (e)!->
    if form[0].checkValidity!
      e.preventDefault!
      data = _id: _id
      fieldName = input.attr('name')
      value = form[0][fieldName].value
      data[fieldName] = value
      $.post '/s-modify-msg', data, (msg)!->
        if msg != 'ok' then alert msg
        else
          form.hide!
          preview.show!
          preview.text value
          editAnchor.text '[edit]'
          state := 'preview'

  editAnchor.click !->
    if state == 'preview'
      preview.hide!
      form.show!
      input.filter("[value='#{preview.text!}']").attr('checked', 'checked')
      editAnchor.text '[save]'
      state := 'edit'
    else
      submitButton.click!

initMapContainer = (id)!->
  container = $("##{id}")
  editAnchor = container.find('.msg-edit-anchor')
  locationPicker = new LocationPicker('msg-map-container')
  location = eval('r=' + $('#msg-location').text!)
  point = new BMap.Point(location.y, location.x)
  locationPicker.location point
  locationPicker.disable!
  state = 'preview'

  editAnchor.click !->
    if state == 'preview'
      locationPicker.enable!
      editAnchor.text '[save]'
      state := 'edit'
    else
      $.ajax {
        type: 'POST'
        url: '/s-modify-msg'
        contentType: 'application/json'
        data: JSON.stringify({
          _id: _id
          location: locationPicker.location!
        })
        success: (msg)!->
          if msg != 'ok' then alert msg
          else
            locationPicker.disable!
            editAnchor.text '[edit]'
            state := 'preview'
      }

initTextContainer 'msg-title-container'
initTextContainer 'msg-detail-container'
initTextContainer 'msg-contact-container'
initRadioContainer 'msg-tag-container'
initMapContainer 'msg-location-container'

$('#msg-remove-button').click !->
  $.post '/s-remove-msg', _id: _id, (msg)!->
    if msg != 'ok' then alert msg
    else location.href = '/home'
