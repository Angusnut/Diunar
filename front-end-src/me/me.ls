lost = $('#me-lost')
found = $('#me-found')
lostMsgs = $('#me-lost-msgs')
foundMsgs = $('#me-found-msgs')

lost.click !->
  lost.addClass 'tab-checked'
  found.removeClass 'tab-checked'
  lostMsgs.show!
  foundMsgs.hide!

found.click !->
  found.addClass 'tab-checked'
  lost.removeClass 'tab-checked'
  foundMsgs.show!
  lostMsgs.hide!

lost.click!
