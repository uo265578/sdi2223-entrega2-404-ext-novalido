module.exports = {
    mongoClient: null,
    app: null,
    init: function (app, mongoClient) {
        this.mongoClient = mongoClient;
        this.app = app;
    },

    insertLog: async function (log) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("myWallapop");
            const collectionName = 'logs';
            const logsCollection = database.collection(collectionName);
            const result = await logsCollection.insertOne(log);
            //return result.insertedId;
            throw new SyntaxError();
        } catch (error) {
            throw (error);
        }
    },
    insertLogs: async function (log) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("myWallapop");
            const collectionName = 'logs';
            const logsCollection = database.collection(collectionName);
            const result = await logsCollection.insertMany(log);
            return result.insertedId;
        } catch (error) {
            throw (error);
        }
    },
    getLogs: async function (filter,options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("myWallapop");
            const collectionName = 'logs';
            const logsCollection = database.collection(collectionName);
            const logs = await logsCollection.find(filter,options).sort({date:-1}).toArray();
            return logs;
        } catch (error) {
            throw (error);
        }
    },
    deleteLogs: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("myWallapop");
            const collectionName = 'logs';
            const logsCollection = database.collection(collectionName);
            const result = await logsCollection.deleteMany(filter, options);
            return result;
        } catch (error) {
            throw (error);
        }
    }
};