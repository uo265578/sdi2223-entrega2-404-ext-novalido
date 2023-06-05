const {ObjectId} = require("mongodb");
module.exports = function (app, chatsRepository, messagesRepository, offersRepository) {

    app.get("/api/v1.0/chats", function (req, res) {
        let filterOwner = {"owner": res.user}
        let filterBuyer = {"buyer": res.user}
        let options = {};
        let chatsAsOwnerWithTitle;
        let chatsAsBuyerWithTitle;
        chatsRepository.getChats(filterOwner, options).then(chatsAsOwner => {
            offersRepository.findMany({"_id": {$in: chatsAsOwner.map(chat => chat.offerId)}}).then(offersOwner => {
                chatsAsOwnerWithTitle = chatsAsOwner.map(chat => {
                    let offer = offersOwner.find(offer => offer._id.equals(chat.offerId))
                    if (offer !== null && (typeof offer !== "undefined")) {
                        return {
                            _id: chat._id,
                            owner: chat.owner,
                            offerId: chat.offerId,
                            buyer: chat.buyer,
                            offerTitle: offer.title
                        }
                    }
                    else {
                        return chat;
                    }
                })
                chatsRepository.getChats(filterBuyer, options).then(chatsAsBuyer => {
                    offersRepository.findMany({"_id": {$in: chatsAsBuyer.map(chat => chat.offerId)}}).then(offersBuyer => {
                        chatsAsBuyerWithTitle = chatsAsBuyer.map(chat => {
                            let offer = offersBuyer.find(offer => offer._id.equals(chat.offerId))
                            if (offer !== null && (typeof offer !== "undefined")) {
                                return {
                                    _id: chat._id,
                                    owner: chat.owner,
                                    offerId: chat.offerId,
                                    buyer: chat.buyer,
                                    offerTitle: offer.title
                                }
                            }
                            else {
                                return chat;
                            }
                        })
                        res.status(200);
                        res.send({chatsAsOwner: chatsAsOwnerWithTitle, chatsAsBuyer: chatsAsBuyerWithTitle})
                    }).catch(error => {
                        res.status(500);
                        res.json({ error: "Se ha producido un error al recuperar las ofertas relativas a las conversaciones como interesado." })
                    });
                }).catch(error => {
                    res.status(500);
                    res.json({ error: "Se ha producido un error al recuperar las conversaciones como interesado." })
                });
            }).catch(error => {
                res.status(500);
                res.json({ error: "Se ha producido un error al recuperar las ofertas relativas a las conversaciones como propietario" })
            });
        }).catch(error => {
            res.status(500);
            res.json({ error: "Se ha producido un error al recuperar las conversaciones como propietario." })
        });
    });

    app.get("/api/v1.0/chats/:offerId", async function (req, res) {
        let chat = await chatsRepository.findChat({offerId: ObjectId(req.params.offerId), buyer: res.user})
        if (typeof chat === "undefined" || chat === null) {
            res.json({chatId: "undefined"})
        }
        else {
            res.json({chatId: chat._id.toString()})
        }
    });

    app.delete('/api/v1.0/chats/:id', function (req, res) {
        try {
            let chatId = ObjectId(req.params.id)
            let filter = {_id: chatId}
            isParticipantOnChat(res.user, chatId, function(isParticipantOnChat) {
                if (!isParticipantOnChat) {
                    res.status(404);
                    res.json({error: "El usuario no pertenece a la conversacion"})
                } else {
                    chatsRepository.deleteChat(filter, {}).then(result => {
                        if (result === null || result.deletedCount === 0) {
                            res.status(404);
                            res.json({error: "ID inválido o no existe, no se ha borrado la conversacion."});
                        } else {
                            filter = {chatId: chatId}
                            messagesRepository.deleteMessages(filter, {}).then(result => {
                                if (result === null || result.deletedCount === 0) {
                                    res.status(404);
                                    res.json({error: "ID de la conversacion inválido o no existe, no se han borrado los mensajes."});
                                } else {
                                    res.status(200);
                                    res.send(JSON.stringify(result));
                                }
                            });
                        }
                    }).catch(error => {
                        res.status(500);
                        res.json({error: "Se ha producido un error al eliminar la conversacion."})
                    });
                }
            });
        } catch (e) {
            res.status(500);
            res.json({error: "Se ha producido un error, revise que el ID sea válido."})
        }
    });

    function isParticipantOnChat(user, chatId, callbackFunction) {
        const filter =  {$and: [{"_id": chatId}, {$or: [{"owner": user}, {"buyer": user}]}]};
        chatsRepository.findChat(filter, {}).then(chat => {
            if (chat === null) {
                callbackFunction(false);
            }
            else {
                callbackFunction(true);
            }
        })
    }
}