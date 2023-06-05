const express = require('express');
const userAuthenticatedRouter = express.Router();
userAuthenticatedRouter.use(function(req, res, next) {
    if ( !req.session.user ) {
        // dejamos correr la petición
        next();
    } else {
        if(req.session.user==="admin@email.com"){
            res.redirect("/users/list");
        }else{
            res.redirect("/offers/own");
        }
    }
});
module.exports = userAuthenticatedRouter;
