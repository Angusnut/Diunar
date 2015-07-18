require! {
  fs
  gulp
  async
  'child_process': cp
  'gulp-less': less
  'gulp-livescript': ls
}

pages = null
getPages = !->
  pages := []
  files = fs.readdirSync '.'
  files.forEach (file)!-> if file != 'gulpfile.ls' then pages.push file

copyJade = (cb)!->
  async.parallel (pages.map (page)-> (cb)!->
    gulp.src "#{page}/*.jade"
        .pipe gulp.dest '../views'
        .on 'end', cb
  ), cb

copyJs = (cb)!->
  async.parallel (pages.map (page)-> (cb)!->
    gulp.src "#{page}/*.js"
        .pipe gulp.dest "../public/pages/#{page}"
        .on 'end', cb
  ), cb

compileLess = (cb)!->
  async.parallel (pages.map (page)-> (cb)!->
    gulp.src "#{page}/*.less"
        .pipe less!
        .pipe gulp.dest "../public/pages/#{page}"
        .on 'end', cb
  ), cb

compileLs = (cb)!->
  async.parallel (pages.map (page)-> (cb)!->
    gulp.src "#{page}/*.ls"
        .pipe ls!
        .pipe gulp.dest "../public/pages/#{page}"
        .on 'end', cb
  ), cb

watchAll = (cb)!->
  gulp.watch '**/*', !->
    console.log 'rebuild'
    buildAll !->
  gulp.watch '../routes/**/*', !->
    console.log 'restart'
    startServer !->
  console.log 'watching...'

buildAll = (cb)!->
  console.log 'building...'
  getPages!
  async.parallel [
    copyJade
    copyJs
    compileLess
    compileLs
  ], !->
    console.log 'builded.'
    cb!

p = null
startServer = (cb)!->
  console.log 'starting server...'
  if p then p.kill!
  p := cp.spawn 'node', ['../server.js']
  p.stdout.on 'data', (chunk)!-> console.log chunk.toString!
  p.stderr.on 'data', (chunk)!-> console.log chunk.toString!
  console.log 'server started.'
  cb!

doAll = (cb)!->
  async.series [
    buildAll
    startServer
    watchAll
  ], cb

gulp.task 'build', buildAll
gulp.task 'watch', doAll
gulp.task 'default', doAll
