module.exports = {
    showError: function(message,res,req,title="Se ha producido un error inesperado"){
        let options = {
            title:title,
            message:message,
            session:req.session
        }
        res.render("error.twig",options);
    },

    showErr(errors,res,req){
        req.session.errors = errors.array();
        req.session.isError = true;

    }
}