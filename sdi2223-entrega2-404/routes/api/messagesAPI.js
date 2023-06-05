const {validationResult} = require('express-validator')
const {messagesValidatorInsert, messagesValidatorGet, messagesValidatorRead} = require('../../validators/messagesValidator')
const {ObjectId} = require("mongodb");
module.exports = function (app, messagesRepository, chatsRepository, offersRepository) {

    app.get("/api/v1.0/messages/:chatId/:offerId", messagesValidatorGet, function (req, res) {
        try {
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                res.status(409);
                res.json({errors: errors.array()});
            } else {
                if ( req.params.chatId === "undefined" || req.params.chatId === null) {
                    res.status(200)
                    res.json({messages: [], user: res.user})
                }
                else {
                    let chatId = ObjectId(req.params.chatId)
                    let filter = {"chatId": chatId}
                    let options = {};
                    messagesRepository.getMessages(filter, options).then(messages => {
                        if (messages === null) {
                            res.status(404);
                            res.json({error: "ID de conversacion invalido o no existe"})
                        } else {
                            res.status(200);
                            res.send({messages: messages, user: res.user});
                        }
                    }).catch(error => {
                        res.status(500);
                        res.json({error: "Se ha producido un error al recuperar los mensajes."})
                    });
                }
            }
        } catch (e) {
            res.status(500);
            res.json({error: "Se ha producido un error :" + e})
        }
    });

    app.post('/api/v1.0/messages', messagesValidatorInsert, async function (req, res) {
        try {
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                res.status(409);
                res.json({errors: errors.array()});
            } else {
                let chatId = req.body.chatId;
                if (chatId === null || typeof chatId === "undefined" || chatId === "undefined") {
                    let offer = await offersRepository.findOne({_id: ObjectId(req.body.offerId)}, {})
                    let chat = {
                        owner: offer.owner,
                        offerId: offer._id,
                        buyer: res.user
                    }
                    chatId = await chatsRepository.insertChat(chat);
                }
                let message = {
                    writer: res.user,
                    chatId: ObjectId(chatId),
                    content: req.body.content,
                    date: new Date().toLocaleString(),
                    isRead: false
                }
                let messageId = await messagesRepository.insertMessage(message);
                if (messageId === null) {
                    res.status(409);
                    res.json({errors: [{msg: "No se ha podido agregar el mensaje."}]});
                } else {
                    res.status(201);
                    res.json({
                        message: "Mensaje agregado correctamente.",
                        _id: messageId,
                        chatId: chatId
                    })
                }
            }
        } catch (e) {
            res.status(500);
            res.json({error: "Se ha producido un error al intentar enviar el mensaje: " + e})
        }
    });

    app.put('/api/v1.0/messages', messagesValidatorRead, async function (req, res) {
        try {
            const errors = validationResult(req);
            if (!errors.isEmpty()) {
                res.status(409);
                res.json({errors: errors.array()});
            } else {
                let messageId = ObjectId(req.body.id);
                let filter = {_id: messageId};
                let options = {};
                let message = await messagesRepository.findMessage(filter, options);
                let messageUpdated = {
                    writer: message.writer,
                    chatId: message.chatId,
                    content: message.content,
                    date: message.date,
                    isRead: true
                }
                let  updated = messagesRepository.updateMessage(messageUpdated, filter, options);
                if (updated === null){
                    res.status(404);
                    res.json({errors: [{msg: "La conversacion no ha podido ser actualizada, id no válido."}]});
                } else if (updated.modifiedCount === 0) {
                    res.status(404);
                    res.json({errors: [{msg: "No se ha modificado ningún mensaje."}]});
                } else {
                    res.status(200);
                    res.json({
                        message: "Mensaje actualizado correctamente.",
                        updated: updated
                    });
                }
            }
        } catch (e) {
            res.status(500);
            res.json({error: "Se ha producido un error al intentar leer el mensaje: " + e})
        }
    });

    function isParticipantOnChat(user, chatId, callbackFunction) {
        const filter = {$and: [{"_id": chatId}, {$or: [{"owner": user}, {"buyer": user}]}]};
        chatsRepository.findChat(filter, {}).then(chat => {
            if (chat === null) {
                callbackFunction(false);
            } else {
                callbackFunction(true);
            }
        })
    }

    function validateParticipantOnChat(user, chatId, callbackFunction) {
        getChatsFilteredBy({$and: [{"_id": chatId}, {$or: [{"owner": user}, {"buyer": user}]}]}, callbackFunction)
    }

    function validateFirstMessageSender(user, chatId, writerId, callbackFunction) {
        getChatsFilteredBy({$and: [{"_id": chatId}, {"buyer": user}]}, callbackFunction);
    }

    function getChatsFilteredBy(filter, callbackFunction) {
        let options = {};
        chatsRepository.getChats(filter, options).then(chatsList => {
            if (chatsList == null) {
                callbackFunction(false)
            } else {
                if (chatsList.isEmpty()) {
                    callbackFunction(false);
                } else {
                    callbackFunction(true);
                }
            }
        })
    }
}