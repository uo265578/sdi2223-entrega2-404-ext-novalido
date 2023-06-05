const express = require('express');
const userStandardRouter = express.Router();
const errorHandler = require("./errorHandler.js");
userStandardRouter.use(function(req, res, next) {
    if ( req.session.user!=="admin@email.com" ) {
        // dejamos correr la petición
        next();
    } else {
        console.log("va a: " + req.originalUrl);
        errorHandler.showError("Zona autorizada solo para usuarios estandar",res,req,"Acceso no autorizado");
    }
});
module.exports = userStandardRouter;
