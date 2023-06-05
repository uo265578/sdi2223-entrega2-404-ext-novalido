const {showError} = require("./errorHandler");
module.exports = function (app,logsRepository,logger) {
    //Estos objetos se usan para guardar la ultima seleccion del radio button al recargar la pagina
    var radioPred = {
        all:true,
        pet:false,
        alta:false,
        ex:false,
        err:false,
        logout:false
    }

    //GET: listado de logs
    app.get("/logs/list",function (req,res){
        //Logging
        logger.log("PET", "Mapping: /logs/list - Method: GET - Params: -")

        logsRepository.getLogs({}, {}).then(logs => {
            res.render("logs/list.twig", {logs: logs,radio:radioPred,session:req.session});
        }).catch(error => {
            showError("Se ha producido un error al listar los logs del sistema",res,req);
        });

    });

    app.get("/logs/filter/:type",function (req,res){
        //Logging
        logger.log("PET", "Mapping: /logs/filter - Method: GET - Params: type="+req.params.type)

        let type = req.params.type;
        //Para que al recargar la página se guarde la seleccion del boton
        let radio = {
            all:"ALL"===type,
            pet:"PET"===type,
            alta:"ALTA"===type,
            ex:"LOGIN-EX"===type,
            err:"LOGIN-ERR"===type,
            logout:"LOGOUT"===type,
        }
        let filter;
        if(type==="ALL"){
            filter = {}
        }else{
            filter = {type:type}
        }

        logsRepository.getLogs(filter, {}).then(logs => {
            res.render("logs/list.twig", {logs: logs,radio:radio,session:req.session});
        }).catch(error => {
            showError("Se ha producido un error al filtrar los logs del sistema",res,req);
        });

    });

    //Get borrado de logs por tipo
    app.get('/logs/delete/:type', function (req, res) {


        let type = req.params.type;
        let filter;
        if(type==="ALL"){
            filter = {}
        }else{
            filter = {type:type}
        }
        logsRepository.deleteLogs(filter, {}).then(result => {
            if (result === null || result.deletedCount === 0) {
                showError("No se han podido eliminar los logs",res,req);
            } else {
                //Logging
                logger.log("PET", "Mapping: /logs/delete- Method: GET - Params: type="+req.params.type)
                res.redirect("/logs/list");
            }
        }).catch(error => {
            showError("Se ha producido un error al eliminar los logs del sistema",res,req);
        });
    })
}
