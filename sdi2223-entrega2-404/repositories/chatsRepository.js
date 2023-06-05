module.exports = {
    mongoClient: null,
    app: null,
    init: function (app, mongoClient) {
        this.mongoClient = mongoClient;
        this.app = app;
    },
    getChats: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("myWallapop");
            const collectionName = 'chats';
            const chatsCollection = database.collection(collectionName);
            const chats = await chatsCollection.find(filter, options).toArray();
            return chats;
        } catch (error) {
            throw (error);
        }
    },
    insertChat: async function (chat) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("myWallapop");
            const collectionName = 'chats';
            const chatsCollection = database.collection(collectionName);
            const result = await chatsCollection.insertOne(chat)
            return result.insertedId;
        } catch (error) {
            throw (error);
        }
    },
    findChat: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("myWallapop");
            const collectionName = 'chats';
            const chatsCollection = database.collection(collectionName);
            const result = await chatsCollection.findOne(filter, options)
            return result;
        } catch (error) {
            throw (error);
        }
    },
    findMany: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("myWallapop");
            const collectionName = 'chats';
            const chatsCollection = database.collection(collectionName);
            const chats = await chatsCollection.find(filter, options);
            return chats.toArray();
        } catch (error) {
            throw (error);
        }
    },
    deleteChat: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("myWallapop");
            const collectionName = 'chats';
            const chatsCollection = database.collection(collectionName);
            const result = await chatsCollection.deleteOne(filter, options);
            return result;
        } catch (error) {
            throw (error);
        }
    },
    deleteChats: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("myWallapop");
            const collectionName = 'chats';
            const chatsCollection = database.collection(collectionName);
            const result = await chatsCollection.deleteMany(filter, options);
            return result;
        } catch (error) {
            throw (error);
        }
    },
}