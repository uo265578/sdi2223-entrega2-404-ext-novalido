const {check} = require('express-validator');
const chatsRepository = require("../repositories/chatsRepository");
const messagesRepository = require("../repositories/messagesRepository");
const {ObjectId} = require("mongodb");
const offersRepository = require("../repositories/offerRepository");

const checkChatIsNotUndefined = check('chatId').not().matches('undefined');
const checkIsParticipant = check('chatId').if(checkChatIsNotUndefined).custom(async (chatId, { req }) => {
    let filter = {$and: [{"_id": ObjectId(chatId)}, {$or: [{"owner": req.body.user}, {"buyer": req.body.user}]}]}
    let options = {};
    let chat = await chatsRepository.findChat(filter, options);

    if (chat === null || typeof chat === "undefined") {
        throw new Error("El usuario no pertenece a la conversacion");
    }

    return true;
})
exports.messagesValidatorInsert = [
    check('content', 'El mensaje no puede estar vacio').trim().not().isEmpty(),
    checkIsParticipant,
    check('chatId').if(checkChatIsNotUndefined).custom(async (chatId, { req}) => {
        let filter = {"_id": ObjectId(chatId)}
        let options = {};
        let chat = await chatsRepository.findChat(filter, options);

        if (chat === null || typeof chat === "undefined") {
            throw new Error("La conversacion no existe");
        }

        return true;
    }),
    check('offerId').custom(async (offerId, { req}) => {

        let filter = {"_id": ObjectId(offerId)}
        let options = {};
        let offer = await offersRepository.findOne(filter, options);
        if (offer === null || typeof offer === "undefined") {
            throw new Error("La oferta vinculada no existe")
        }

        return true;
    }),
    check('offerId').if(check('chatId').matches('undefined')).custom(async (offerId, { req}) => {
        let filter = {"_id": ObjectId(offerId)}
        let options = {};
        let offer = await offersRepository.findOne(filter, options);
        if (! (offer === null || typeof offer === "undefined")) {
            if(offer.owner === req.body.user) {
                throw new Error("El propietario no puede empezar la conversacion");
            }
        }
        return true;
    })
]

exports.messagesValidatorGet = [
    checkIsParticipant
]
// const validateMessageRead = async (req, res, next) => {
//     await check('messageId', 'Se requiere un id de mensaje').trim().not().isEmpty().run(req);
//     await check('messageId').custom(async (value, { req }) => {
//         let filter = { _id: ObjectId(value) };
//         let options = {};
//         let msg = await messagesRepository.findMessage(filter, options);
//         if (msg === null || msg === undefined) {
//             throw new Error("El mensaje no existe");
//         }
//         if (msg.isRead) {
//             throw new Error("El mensaje ya ha sido leido");
//         }
//         let chatId = msg.chatId;
//         filter = { _id: chatId };
//         let chat = await chatsRepository.findChat(filter, options);
//
//         if (chat.owner !== res.user && chat.buyer !== res.user) {
//             throw new Error("No puedes leer un mensaje de un chat que no es tuyo");
//         }
//     }).run(req);
//
//     const errors = validationResult(req);
//     req.validationErrors = errors.array();
//     next();
// };
exports.messagesValidatorRead = [
    check('id', 'Se requiere un id de mensaje').trim().not().isEmpty(),
    check('id').custom(async (value, { req }) => {
        let filter = { _id: ObjectId(value) };
        let options = {};
        let msg = await messagesRepository.findMessage(filter, options);
        if (msg === null || msg === undefined) {
            throw new Error("El mensaje no existe");
        }
        if (msg.isRead) {
            throw new Error("El mensaje ya ha sido leido");
        }
        let chatId = msg.chatId;
        filter = { _id: chatId };
        let chat = await chatsRepository.findChat(filter, options);

        if (chat.owner !== req.body.user && chat.buyer !== req.body.user) {
            throw new Error("No puedes leer un mensaje de un chat que no es tuyo");
        }
    })
];