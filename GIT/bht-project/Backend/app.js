var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
const database = require("./database/db");
var bodyParser = require('body-parser')
const http = require('http')

var indexRouter = require('./routes/index');
var allRoutes = require("./endpoints/allRoutes");

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));
app.use(bodyParser.json());

app.use('/', indexRouter);
app.use("/hub", allRoutes.hubRoute);
app.use("/hubRegistration", allRoutes.hubRegistrationRoute)
app.use('/sensor', allRoutes.sensorRoute);
app.use("/user", allRoutes.userRoute);
app.use("/zone", allRoutes.zoneRoute);

database.initDB(function (err, db) {
  if (db) {
    console.log("Datenbank läuft")
  }
  else {
    console.log("Anbindung Datenbank gescheitert.")
  }
})

// catch 404 and forward to error handler
app.use(function (req, res, next) {
  next(createError(404));
});

// error handler
app.use(function (err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
