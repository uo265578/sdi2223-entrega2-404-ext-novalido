const express = require('express');
const { checkError } = require("./errorHandler");
const errorRouter = express.Router();

errorRouter.use(function(req, res, next) {
    if(req.session.isError){
        req.session.isError = false;
    }
    else{
        req.session.errors = undefined;
    }
    next();
});

module.exports = errorRouter;