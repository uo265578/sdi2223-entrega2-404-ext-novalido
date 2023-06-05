const express = require('express');
const userAdminRouter = express.Router();
const errorHandler = require("./errorHandler.js");
userAdminRouter.use(function(req, res, next) {
    if ( req.session.user==="admin@email.com" ) {
        // dejamos correr la petición
        next();
    } else {
        console.log("va a: " + req.originalUrl);
        errorHandler.showError("Zona autorizada solo para administradores",res,req,"Acceso no autorizado");
    }
});
module.exports = userAdminRouter;
