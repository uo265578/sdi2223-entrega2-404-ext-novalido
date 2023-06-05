let express = require('express');
let router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  if(req.session.isError){
    req.session.isError = false;
  }
  else{
    req.session.errors = undefined;
  }

  res.render('index.twig',{session:req.session});
});

module.exports = router;
