const {ObjectId} = require("mongodb");
const {showError, showErr} = require("./errorHandler");
const {validationResult} = require("express-validator");
const {userValidatorSignup, userValidatorDelete, userValidatorLogIn} = require("../validators/usersValidators");
const userRepository = require("../repositories/usersRepository");


module.exports = function (app, usersRepository, offerRepository, chatsRepository, messagesRepository, logsRepository, logger) {

    //GET: listado de usuarios
    app.get("/users/list", function (req, res) {
        //Logging
        logger.log("PET", "Mapping: /users/list - Method: GET - Params: -", res)


        let filter = {$nor: [{"email": "admin@email.com"}]}; //filtro para excluir al admin
        let options = {};
        let page = parseInt(req.query.page); // Es String !!!
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") {
            //Puede no venir el param
            page = 1;
        }
        usersRepository.getUsersPg(filter, options, page).then(result => {
            let lastPage = result.total / 4;
            if (result.total % 4 > 0) { // Sobran decimales
                lastPage = lastPage + 1;
            }
            let pages = []; // paginas mostrar
            for (let i = page - 2; i <= page + 2; i++) {
                if (i > 0 && i <= lastPage) {
                    pages.push(i);
                }
            }
            let response = {
                users: result.users,
                pages: pages,
                currentPage: page,
                session: req.session
            }

            res.render("users/list.twig", response);
        }).catch(error => {
            showError("Se ha producido un error al listar los usuarios", res, req);
        });
    });

    //Get borrado de usuarios
    app.get('/users/delete/:id', userValidatorDelete, async function (req, res) {
        try {
            //Logging
            logger.log("PET", "Mapping: /user/delete - Method: GET - Params: id=" + req.params.id)

            //Comprobamos que el administrador no se intenta borrar a si mismo
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                res.status(422);
                showErr(errors, res, req);
                res.redirect("/users/list")
            } else {
                let filter = {_id: ObjectId(req.params.id)};
                let user = await usersRepository.findUser(filter, {});
                let result = await usersRepository.deleteUser(filter, {});
                if (result === null || result.deletedCount === 0) {
                    showError("No se ha podido eliminar el usuario", res, req)
                } else {
                    //Eliminamos sus ofertas
                    filter = {owner: user.email};
                    let filter2 = {$or: [{owner: user.email}, {buyer: user.email}]};
                    let offers = await offerRepository.findMany(filter, {});
                    let chats = await chatsRepository.findMany(filter2, {});
                    let result2 = await offerRepository.deleteOffers(filter, {});
                    if (result2 === null || result2.deletedCount !== offers.length) {
                        showError("No se han podido eliminar las ofertas del usuario", res, req)
                    } else {//Eliminamos sus conversaciones
                        let result3 = await chatsRepository.deleteChats(filter2, {});
                        if (result3 === null || result3.deletedCount !== chats.length) {
                            showError("No se han podido eliminar los chats del usuario", res, req)
                        } else {//Eliminamos sus mensajes
                            let error = false;
                            for (let d = 0; d < chats.length; d++) {
                                let filter3 = {chatId: chats[d]._id}
                                let messages = await messagesRepository.findMany(filter3, {});
                                let result4 = await messagesRepository.deleteMessages(filter3, {});
                                if (result4 === null || result4.deletedCount !== messages.length) {
                                    error = true;
                                    break; //Si hay un error al borrar cualquier mensaje, se para el loop
                                }
                            }
                            if (error) {
                                showError("No se han podido eliminar los mensahes del usuario", res, req)
                            } else {
                                res.redirect("/users/list");
                            }
                        }
                    }


                }
            }
        } catch (error) {
            console.log(error.message)
            showError("No se ha podido eliminar el usuario", res, req)
        }

    })

    //GET: registro de usuarios
    app.get('/users/signup', function (req, res) {
        //Logging
        logger.log("PET", "Mapping: /user/signup - Method: GET - Params: -")

        res.render("signup.twig", {session: req.session});
    });

    //POST: registro de usuarios
    app.post('/users/signup', userValidatorSignup, function (req, res) {
        //Logging
        let log1 = {
            type: "PET",
            date: new Date(),
            description: "Mapping: /user/signup - Method: POST - Params: -"
        }
        let log2 = {
            type: "ALTA",
            date: new Date(),
            description: "Mapping: /user/signup - Method: POST - Params: -"
        }
        logger.logMany([log1, log2]);

        let password = req.body.password;
        let email = req.body.email;
        let name = req.body.name;
        let surname = req.body.surname;
        let securePassword = app.get("crypto").createHmac('sha256', app.get('clave'))
            .update(password).digest('hex');

        let userToInsert = {
            email: email,
            name: name,
            surname: surname,
            date: req.body.date,
            profile: "STANDARD",
            money: 100,
            password: securePassword
        }
        const errors = validationResult(req);
        if (!errors.isEmpty()) {
            res.status(422);
            showErr(errors, res, req);
            res.render("signup.twig", {session: req.session});
        } else {
            usersRepository.insertUser(userToInsert).then(userId => {
                req.session.user = req.body.email; //Auto login
                req.session.money = 100;
                res.redirect("/offers/own");
            }).catch(error => {
                showError("Se ha producido un error al registrar el usuario", res, req);
            });
        }
    });


    //Inicio de sesión
    /*
      Metodo get que responde al inicio de sesion
       */
    app.get('/users/login', function (req, res) {
        //Logging
        logger.log("PET", "Mapping: /users/login - Method: GET - Params: -")

        res.render("login.twig");
    })

    /*
    Metodo post que responde al inicio de sesion
     */
    app.post('/users/login', userValidatorLogIn, async function (req, res) {

        const errors = validationResult(req);
        if (!errors.isEmpty()) {
            showErr(errors, res, req);
            //Logging
            await logger.log("LOGIN-ERR", "Mapping: /users/login - Method: POST - Params: -")
            res.status(422);
            res.render("login.twig", {session: req.session});
        } else {
            //Logging
            await logger.log("LOGIN-EX", "Mapping: /users/login - Method: POST - Params: -")
            let securePassword = app.get("crypto").createHmac('sha256', app.get('clave')).update(req.body.password).digest('hex');
            let filter = {email: req.body.email, password: securePassword}
            let user = await userRepository.findUser(filter, {})
            req.session.user = req.body.email;
            req.session.money = user.money;

            if (req.session.user === "admin@email.com") {
                res.redirect("/users/list");
            } else {
                res.redirect("/offers/own");
            }
        }
    })

    /*
    Metodo get que se hace cuando el usuario cierra sesion
     */
    app.get('/users/logout', function (req, res) {
        //Logging
        logger.log("LOGOUT", "Mapping: /users/logout - Method: GET - Params: -")

        req.session.user = null;
        res.redirect("/users/login");
    })

}
