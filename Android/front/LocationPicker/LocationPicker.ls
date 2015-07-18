window.LocationPicker = (id)!->
  location = null
  marker = null
  enable = true
  changeCb = null

  geocoder = new BMap.Geocoder
  setLocation = (point, cb)!->
    # fetch address
    geocoder.getLocation point, (res)!->
      location := {
        x: point.lat
        y: point.lng
        place: res.address
      }
      # update marker
      if marker then map.removeOverlay marker
      marker := new BMap.Marker(point)
      map.addOverlay marker
      changeCb and changeCb!
      cb and cb!

  map = new BMap.Map(id)
  map.enableScrollWheelZoom!
  map.addControl new BMap.NavigationControl
  point = new BMap.Point(113.398523, 23.073402)
  map.centerAndZoom(point, 17)
  map.addEventListener 'click', (e)!-> if enable then setLocation e.point

  @location = (point, cb)->
    # get
    if not point then return location
    # set
    else setLocation point, cb

  @enable = !-> enable := true
    
  @disable = !-> enable := false

  @change = (_changeCb)!->
    changeCb := _changeCb
