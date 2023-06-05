const {ObjectId} = require("mongodb");
const {showError, showErr, checkError } = require("./errorHandler");
const { validationResult } = require("express-validator");
const {buyOfferValidator, markFeaturedOffer, deleteOfferValidator} = require("../validators/offerValidator");
const {offerValidatorInsert} = require("./../validators/offerValidator");

module.exports = function (app, userRepository, offerRepository, logger) {

    app.get('/offers/buy/:id', buyOfferValidator, async function (req, res) {
        try{
            logger.log("PET", "Mapping: /offers/buy - Method: GET - Params: id=" + req.params.id);

            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                res.status(422);
                showErr(errors, res, req);
                res.redirect("/shop");
            }
            else{
                let offerId = ObjectId(req.params.id);
                let userEmail = req.session.user;

                let user = await userRepository.findUser({email: userEmail})
                let offer = await offerRepository.findOne({_id: offerId});

                user.money -= offer.price;
                offer.boughtBy = user.email;

                await offerRepository.update(offer, {_id: offer._id});
                await userRepository.update(user, {_id: user._id});
                req.session.money = user.money;
                res.redirect("/offers/bought");
            }
        } catch (e){
            showError( "Se ha producido un error al comprar la oferta", res, req);
        }
    });

    app.get('/offers/feature/:id', markFeaturedOffer, async function (req, res) {
        try{
            logger.log("PET", "Mapping: /offers/feature - Method: GET - Params: id=" + req.params.id);

            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                res.status(422);
                showErr(errors, res, req);
            }
            else{
                let offerId = ObjectId(req.params.id);
                let userEmail = req.session.user;

                let user = await userRepository.findUser({email: userEmail})
                let offer = await offerRepository.findOne({_id: offerId});

                user.money -= 20;
                offer.featured = true;

                await offerRepository.update(offer, {_id: offer._id});
                await userRepository.update(user, {_id: user._id});
                req.session.money = user.money;
            }
            res.redirect("/offers/own");
        } catch (e){
            showError( "Se ha producido un error al comprar la oferta", res, req);
        }
    });

    app.get('/offers/bought', buyOfferValidator, async function (req, res) {
        try{
            logger.log("PET", "Mapping: /offers/bought - Method: GET - Params: -" + req.params.id);

            let userEmail = req.session.user;
            let user = await userRepository.findUser({email: userEmail});

            let page = req.query.page ? parseInt(req.query.page) : 1;

            let result = await offerRepository.getOffersPg({boughtBy: user.email}, {}, page);

            let offers = result.offers;
            let lastPage = Math.min(Math.ceil(result.total / 5), page + 2);

            if(page > lastPage){
                page = 1;
            }

            let pages = [];

            for(let i = page - 2; i <= page + 2; i++){
                if(i > 0 && i <= lastPage){
                    pages.push(i);
                }
            }

            let response = {
                offers: offers,
                pages: pages,
                currentPage: page,
                session:req.session,
                lastPage: lastPage
            }

            res.render("offers/bought.twig", response);
        } catch (e){
            showError( "Se ha producido un error al comprar la oferta", res, req);
        }
    });

    app.get('/shop', async function (req, res) {
        logger.log("PET", "Mapping: /shop - Method: GET - Params: -")

        let filter = {};
        let options = {sort: {title: 1}};
        if (req.query.search != null && typeof (req.query.search) != "undefined" && req.query.search != "") {
            filter = {
                "title": {
                    $regex: ".*" + req.query.search + ".*",
                    $options: "i" // Búsqueda insensible a mayúsculas y minúsculas
                }
            };
        }

        let page = parseInt(req.query.page); // Es String !!!
        if (typeof req.query.page === "undefined" || req.query.page === null || req.query.page === "0") { //Puede no venir el param
            page = 1;
        }
        offerRepository.getOffersPg(filter, options, page).then(result => {
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
                offers: result.offers,
                pages: pages,
                currentPage: page,
                session:req.session,
                search: req.query.search,
                lastPage: lastPage
            }

            res.render("offers/shop.twig", response);
        }).catch(error => {
            showError( "Se ha producido un error al listar las ofertas", res, req);
        });
    });

    app.get('/offers/featured', async function (req, res) {
        try{

            let page = req.query.page ? parseInt(req.query.page) : 1;

            let result = await offerRepository.getOffersPg({featured: true}, {}, page);

            let offers = result.offers;

            let lastPage = Math.min(Math.ceil(result.total / 5), page + 2);

            if(page > lastPage){
                page = 1;
            }

            let pages = [];

            for(let i = page - 2; i <= page + 2; i++){
                if(i > 0 && i <= lastPage){
                    pages.push(i);
                }
            }

            let response = {
                offers: offers,
                pages: pages,
                currentPage: page,
                session:req.session,
                lastPage: lastPage
            }

            res.render("offers/featured.twig", response);

        } catch (e){
            showError( "Se ha producido un error al mostrar las ofertas la oferta", res, req);
        }
    });

    app.get('/offers/add', function (req, res) {
        res.render("offers/add.twig", {session: req.session});
    });

    app.post('/offers/add', offerValidatorInsert, async function (req, res) {
        const errors = validationResult(req);
        let featured = req.body.featured !== null && req.body.featured !== undefined
        let offer = {
            title: req.body.title,
            details: req.body.details,
            publicationDate: new Date(),
            price: req.body.price,
            owner: req.session.user,
            featured: featured
        }
        let user = await userRepository.findUser({email: req.session.user})
        if (!errors.isEmpty()) {
            let response = {
                offer: offer,
                session: req.session
            }
            showErr(errors, res, req);
            res.render("offers/add.twig", response);
        } else {
            offerRepository.insertOffer(offer).then(offerId => {
                if(offerId == null){
                    res.send("Error al insertar la oferta");
                } else {
                    if (offer.featured) {
                        user.money -= 20;
                        req.session.money = user.money;
                        userRepository.update(user, {_id: user._id}).then(result => {
                            res.redirect("/offers/own");
                        });
                    } else {
                        res.redirect("/offers/own");
                    }
                }
            }).catch(error => {
                showError( "Se ha producido un error al insertar la oferta", res, req);
            })
        }
    });

    app.get('/offers/own', async function (req, res) {
        try{
            let filter = {owner: req.session.user};
            let options = {sort: {title: 1}};//{sort: {title: 1}};

            let page = req.query.page ? parseInt(req.query.page) : 1;
            let result = await offerRepository.getOffersPg(filter, options, page);

            let offers = result.offers;
            const lastPage = Math.min(Math.ceil(result.total / 5), page + 2);

            if (page > lastPage) {
                page = 1;
            }

            let pagesSet = new Set();
            for (let i = page - 2; i <= page + 2; i++) {
                if (i > 0 && i <= lastPage) {
                    pagesSet.add(i);
                }
            }
            let pages = Array.from(pagesSet);

            let response = {
                offers: offers,
                pages: pages,
                currentPage: page,
                session: req.session,
                lastPage: lastPage
            }
            res.render("offers/list.twig", response);
        } catch (e){
            showError( "Se ha producido un error al mostrar las ofertas propias", res, req);
        }
    });

    app.get('/offers/delete/:id', deleteOfferValidator, async function (req, res) {
        const errors = validationResult(req);
        console.log(errors);
        if (!errors.isEmpty()) {
            showErr(errors, res, req);
            res.redirect("/offers/own");
        } else {
            let filter = {_id: ObjectId(req.params.id)};
            try {
                let result = await offerRepository.deleteOffers(filter, {});
                console.log(result);
                res.redirect("/offers/own");
            } catch (e) {
                showError( "Se ha producido un error al eliminar la oferta", res, req);
            }
        }
    });
}