const {userValidatorLogIn} = require("../../validators/usersValidators");
const {validationResult} = require("express-validator");
module.exports = function (app, usersRepository,logger) {
    app.post('/api/v1.0/users/login',userValidatorLogIn, async function (req, res) {
        try {
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                //Logging
                await logger.log("LOGIN-ERR", "Mapping: /api/v1.0/users/login - Method: POST - Params: -")
                res.status(401);
                res.json({
                    errors:errors.array(),
                    authenticate:false
                })
            }else{
                //Logging
                await logger.log("LOGIN-EX", "Mapping: /api/v1.0/users/login - Method: POST - Params: -")

                let securePassword = app.get("crypto").createHmac('sha256', app.get('clave')).update(req.body.password).digest('hex');
                let filter = {
                    email:req.body.email,
                    password:securePassword
                }
                let user = await usersRepository.findUser(filter, {})
                let token=app.get('jwt').sign(
                    {user:user.email, time:Date.now()/1000}, "secreto");
                res.status(200);
                res.json({
                    message:"usuario autorizado",
                    authenticate:true,
                    token: token
                })
            }
        } catch (e) {
            res.status(500);
            res.json({
                errors:[{msg:"Se ha producido un error al verificar las credenciales"}],
                authenticate:false
            })
        }
    })
}