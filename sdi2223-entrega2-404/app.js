let createError = require('http-errors');
let express = require('express');
let path = require('path');
let cookieParser = require('cookie-parser');
let logger = require('morgan');


let app = express();

app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header("Access-Control-Allow-Credentials", "true");
  res.header("Access-Control-Allow-Methods", "POST, GET, DELETE, UPDATE, PUT");
  res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, token");
  // Debemos especificar todas las headers que se aceptan. Content-Type , token
  next();
});


let jwt = require('jsonwebtoken');
app.set('jwt', jwt);
let expressSession = require('express-session');
app.use(expressSession({
  secret: 'abcdefg',
  resave: true,
  saveUninitialized: true
}));


//Crypto
let crypto = require('crypto');
app.set('clave','abcdefg');
app.set('crypto',crypto);

//Body parser
let bodyParser = require('body-parser');
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

let indexRouter = require('./routes/index');

//Mongo
const { MongoClient } = require("mongodb");
const url = 'mongodb://localhost:27017';
//const url = 'mongodb+srv://uo265578:1234@mywallapop.tzzqjb4.mongodb.net/?retryWrites=true&w=majority';

app.set('connectionStrings', url);

//Routers
const userSessionRouter = require('./routes/userSessionRouter');//Si no se inicio sesion, redirige al login
const userAdminRouter = require('./routes/userAdminRouter');//Si se inicio sesion y no es admin, muestra error
const userStandardRouter = require('./routes/userStandardRouter');//Si se inicio sesion y no es estandar, muestra error
const userAuthenticatedRouter = require('./routes/userAuthenticatedRouter');//Evita que te autentiques más de una vez
const errorRouter = require('./routes/errorRouter');

app.use("/users/logout",userSessionRouter);
app.use("/logs/list",userSessionRouter);
app.use("/logs/list",userAdminRouter);
app.use("/users/list",userSessionRouter);
app.use("/users/list",userAdminRouter);
app.use("/users/delete/:id",userSessionRouter);
app.use("/users/delete/:id",userAdminRouter);
app.use("/users/signup",userAuthenticatedRouter); //Si estás autenticado ya, redirige a la página principal de cada rol
app.use("/users/login",userAuthenticatedRouter); //Si estás autenticado ya, redirige a la página principal de cada rol
app.use("/offers/buy/:id",userSessionRouter);
app.use("/offers/buy/:id",userStandardRouter);
app.use("/offers/bought",userSessionRouter);
app.use("/offers/bought",userStandardRouter);
app.use("/offers/feature/:id",userSessionRouter);
app.use("/offers/feature/:id",userStandardRouter);
//app.use("/shop",userStandardRouter);

//Error router
app.use("/shop", errorRouter);
app.use("/offers/add", errorRouter);
app.use("/offers/bought", errorRouter);
app.use("/offers/featured", errorRouter);
app.use("/users/login", errorRouter);
app.use("/users/list", errorRouter);
app.use("/users/signup", errorRouter);

app.use("/offers/add",userSessionRouter);
app.use("/offers/add",userStandardRouter);
app.use("/offers/own",userSessionRouter);
app.use("/offers/own",userStandardRouter);
app.use("/offers/delete/:id", userSessionRouter);
app.use("/offers/delete/:id", userStandardRouter);
//app.use("/offers/buy/:id",userSessionRouter);
//app.use("/offers/buy/:id",userStandardRouter);
const userTokenRouter = require('./routes/userTokenRouter');
app.use("/api/v1.0/offers/", userTokenRouter);
app.use("/api/v1.0/messages/", userTokenRouter);
app.use("/api/v1.0/chats/", userTokenRouter);

//Repositories
const usersRepository = require("./repositories/usersRepository.js");
usersRepository.init(app, MongoClient);

const logsRepository = require("./repositories/logsRepository.js");
logsRepository.init(app, MongoClient);

const offersRepository = require("./repositories/offerRepository.js");
offersRepository.init(app, MongoClient);

const messagesRepository = require("./repositories/messagesRepository.js");
messagesRepository.init(app, MongoClient);

const chatsRepository = require("./repositories/chatsRepository.js");
chatsRepository.init(app, MongoClient);

//Initial data
const initRepository = require("./repositories/initRepository.js");
initRepository.init(app,MongoClient,usersRepository,offersRepository,chatsRepository, messagesRepository);

//Logger
let log4js = require("log4js");
let loggerConsole = log4js.getLogger();
loggerConsole.level = "trace";
const appLogger = require("./routes/logger.js")
appLogger.init(loggerConsole,logsRepository)


//Endpoints
require("./routes/users.js")(app, usersRepository,offersRepository,chatsRepository,messagesRepository,logsRepository,appLogger);
require("./routes/logs.js")(app, logsRepository,appLogger);
require("./routes/offers.js")(app, usersRepository, offersRepository, appLogger);
require("./routes/api/offersAPI.js")(app, offersRepository, appLogger);
require("./routes/api/messagesAPI.js")(app, messagesRepository, chatsRepository, offersRepository);
require("./routes/api/chatsAPI.js")(app, chatsRepository, messagesRepository, offersRepository);
require("./routes/api/usersAPI.js")(app, usersRepository,appLogger);

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'twig');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', indexRouter);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
