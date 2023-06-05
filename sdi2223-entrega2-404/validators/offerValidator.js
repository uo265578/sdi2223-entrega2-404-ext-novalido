const { check } = require('express-validator')
const offerRepository = require("../repositories/offerRepository");
const userRepository = require("../repositories/usersRepository");
const { ObjectId } = require("mongodb");

exports.buyOfferValidator = [
    check('id', 'Se debe de pasar el id de la oferta').custom(async (offerId, { req }) => {
        let userEmail = req.session.user;

        let filter = { email: userEmail };
        let options = {};
        let user = await userRepository.findUser(filter, options);

        filter = { _id: ObjectId(offerId) };
        let offer = await offerRepository.findOne(filter, options);

        if (offer === null || typeof offer === "undefined") {
            throw new Error("La oferta no existe");
        }

        if (offer.price > user.money) {
            throw new Error("No tiene suficiente saldo para comprar la oferta");
        }

        if (offer.boughtBy !== null && typeof offer.boughtBy !== "undefined") {
            throw new Error("La oferta ya esta comprada");
        }

        if (offer.owner === user.email) {
            throw new Error("No puede comprar una oferta propia");
        }

        return true;
    })
]

exports.deleteOfferValidator = [
    check('id', 'Se debe de pasar el id de la oferta').custom(async (offerId, { req }) => {
        let filter = {_id: ObjectId(offerId)};
        let offer = await offerRepository.findOne(filter, {});

        if (offer === null || typeof offer === "undefined") {
            throw new Error("La oferta no existe");
        }

        if (offer.boughtBy !== null && typeof offer.boughtBy !== "undefined") {
            throw new Error("No puedes borrar una oferta comprada");
        }

        if (offer.owner !== req.session.user) {
            throw new Error("No puedes borrar una oferta que no es tuya");
        }

        return true;
    })
]

exports.markFeaturedOffer = [
    check('id', 'Se debe de pasar el id de la oferta').custom(async (offerId, { req }) => {
        let userEmail = req.session.user;

        let filter = { email: userEmail };
        let options = {};
        let user = await userRepository.findUser(filter, options);

        filter = { _id: ObjectId(offerId) };
        let offer = await offerRepository.findOne(filter, options);

        if (offer === null || typeof offer === "undefined") {
            throw new Error("La oferta no existe");
        }

        if (20 > user.money) {
            throw new Error("No tiene suficiente saldo para destacar la oferta");
        }

        if (offer.featured) {
            throw new Error("La oferta ya está destacada");
        }

        return true;
    })
]

exports.offerValidatorInsert = [
    check('title', 'El titulo debe estar incluido y no estar vacío').trim().not().isEmpty(),
    check('title', 'El titulo debe contener como minimo 5 caracteres').trim().isLength({min: 5}),
    check('details', 'Los detalles deben estar incluidos y no estar vacios').trim().not().isEmpty(),
    check('details', 'Los detalles deben contener como minimo 5 caracteres').trim().isLength({min: 5}),
    check('price', 'El precio debe estar incluido y no estar vacio').trim().not().isEmpty(),
    check('price', 'El precio debe ser numerico').trim().isNumeric(),
    check('price').custom((value) => {
        if (value < 0) {
            throw new Error('El precio debe ser un numero positivo');
        }
        return true;
    }),
    check('featured').if(check('featured').exists()).custom(async (value, { req }) => {
        let userEmail = req.session.user;

        let filter = { email: userEmail };
        let options = {};
        let user = await userRepository.findUser(filter, options);

        if (20 > user.money) {
            throw new Error("No tiene suficiente saldo para destacar la oferta");
        }

        return true;
    })
]