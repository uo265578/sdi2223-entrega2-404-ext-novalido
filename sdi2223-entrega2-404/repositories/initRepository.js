const { ObjectId } = require("mongodb");
module.exports = {
    mongoClient: null,
    app: null,
    usersRepository: null,
    offerRepository: null,
    chatsRepository: null,
    messagesRepository: null,
    init: async function (app, mongoClient, usersRepository, offerRepository, chatsRepository, messagesRepository) {
        this.mongoClient = mongoClient;
        this.app = app;
        this.usersRepository = usersRepository;
        this.offerRepository = offerRepository;
        this.chatsRepository = chatsRepository;
        this.messagesRepository = messagesRepository;

        const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
        const database = client.db("myWallapop");
        try { //Intenta borrar las colecciones si existen
            await database.collection("users").drop();
            await database.collection("offers").drop();
            await database.collection("chats").drop();
            await database.collection("messages").drop();
        } catch (e) {}


        let adminPassword = app.get("crypto").createHmac('sha256', app.get('clave'))
            .update("admin").digest('hex');
        //Crear el admin
        let admin = {
            _id: "admin", //Necesitamos que este ID sea fijo para un test
            email: "admin@email.com",
            name: "admin",
            surname: "admin",
            date: new Date(),
            profile: "ADMIN",
            money: 0,
            password: adminPassword
        }

        usersRepository.insertUser(admin).then(userId => {
            //Nada
        }).catch(error => {
        });

        let deletedUsers = [
            "user01@email.com",
            "user02@email.com",
            "user03@email.com",
            "user04@email.com",
            "user15@email.com"];

        //Crear los demas usuarios
        let email1 = "user";
        let email2 = "@email.com";
        let username = "";
        let securePassword = "";
        for (let i = 1; i < 16; i++) {
            if (i < 10) {
                username = email1 + "0" + i;
            } else {
                username = email1 + i;
            }

            securePassword = app.get("crypto").createHmac('sha256', app.get('clave'))
                .update(username).digest('hex');
            let user = {
                email: username + email2,
                name: username,
                surname: username,
                date: new Date(),
                profile: "STANDARD",
                money: 100,
                password: securePassword
            }

            if(user.email === "user10@email.com"){
                user.money = 0;
            }
            usersRepository.insertUser(user).then(userId => {

                for (let k = 1; k < 11; k++) {
                    let offer = {
                        title: "Oferta" + k + "-Usuario" + i,
                        details: "Descripcion",
                        publicationDate: new Date(),
                        price: 20,
                        featured: false,
                        owner: user.email
                    }
                    if(k==2){ //Para los tests, todas las ofertas 2 costaran 100
                        offer.price=100;
                    }
                    if(k==3){ //Para los tests, todas las ofertas 3 costaran 120
                        offer.price=120;
                    }
                    if(k==5){ //Para los tests, todas las ofertas 3 costaran 120
                        offer.price=120;
                    }
                    if(k==4 && !deletedUsers.includes(offer.owner) && offer.owner !== "user08@email.com"){
                        offer.boughtBy="user08@email.com";
                    }
                    if(k==6){ //Para los tests, todas las ofertas 3 costaran 120
                        offer.price=120;
                        offer.boughtBy = "user14@email.com"
                    }
                    if(k==7){
                        offer._id = ObjectId("645674ce37538dbbf7505e" + user.name.split("user")[1]);
                    }

                    offerRepository.insertOffer(offer).then(offerId => {
                        if (i < 4 && k < 4) {
                            for (let l = 1; l < 3; l++) {
                                let buyer = email1 + "1" + l + email2
                                let chat = {
                                    owner: user.email,
                                    offerId: offerId,
                                    buyer: buyer
                                }
                                chatsRepository.insertChat(chat).then(chatId => {
                                    let message = {
                                        writer: buyer,
                                        chatId: chatId,
                                        content: "Hola " + user.email + ", estoy interesado en tu oferta",
                                        date: new Date().toLocaleString(),
                                        isRead: false
                                    }
                                    message._id = ObjectId("645674ce37538dbbf7505" + l + "" + k + "" + i);
                                    messagesRepository.insertMessage(message).then(messageId => {
                                    }).catch(error => {
                                    });
                                }).catch(error => {
                                });
                            }
                        }
                    }).catch(error => {
                    });
                }

            }).catch(error => {
            });
        }


    },


};