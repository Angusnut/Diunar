lost = $('#home-lost')
found = $('#home-found')
pages = $('#home-pages')

lost.addClass 'tab-checked'

locationPicker = new LocationPicker('home-map-container')

fetchMsgs = (page)!->
  page = if typeof page != 'undefined' then page else parseInt(pages.attr('curPage'))
  pages.attr('curPage', page)
  $.ajax {
    type: 'POST'
    url: '/r-query-msgs'
    contentType: 'application/json'
    data: JSON.stringify({
      page: page
      type: if $('.tab-checked').text! == 'Lost' then 0 else 1
      tag: $('input[name="tag"]:checked').val! or undefined
      date: $('input[name="date"]').val! or undefined
      location: locationPicker.location!
    })
    success: (html)!->
      $('#home-msgs-container').html(html)
      setPage!
  }

lost.click !->
  lost.addClass 'tab-checked'
  found.removeClass 'tab-checked'
  fetchMsgs!

found.click !->
  found.addClass 'tab-checked'
  lost.removeClass 'tab-checked'
  fetchMsgs!

$('input[name="date"]').change !-> fetchMsgs!
$('input[name="tag"]').change !-> fetchMsgs!
locationPicker.change !-> fetchMsgs!

setPage = !->
  pageCount = parseInt($('#r-query-msgs-page-count').text!)
  curPage = parseInt($('#home-pages').attr('curPage'))
  if curPage >= pageCount then curPage = 0
  pages.html('')
  for i from 0 til pageCount
    page = $(document.createElement('span'))
    if i != curPage
      a = $(document.createElement('a'))
      a.attr('page', i)
      a.text(i + 1)
      a.click !-> fetchMsgs parseInt($(@).attr('page'))
      page.append(a)
    else
      page.text(i + 1)
      pages.attr('curPage', i)
    pages.append(page)

setPage!
