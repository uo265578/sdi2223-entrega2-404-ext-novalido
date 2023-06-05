module.exports = {
    mongoClient: null,
    app: null,
    init: function (app, mongoClient) {
        this.mongoClient = mongoClient;
        this.app = app;
    },
    insertOffer: async function (offer) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("myWallapop");
            const collectionName = 'offers';
            const offersCollection = database.collection(collectionName);
            const result = await offersCollection.insertOne(offer);
            return result.insertedId;
        } catch (error) {
            throw (error);
        }
    },
    deleteOffers: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("myWallapop");
            const collectionName = 'offers';
            const offersCollection = database.collection(collectionName);
            const result = await offersCollection.deleteMany(filter, options);
            return result;
        } catch (error) {
            throw (error);
        }
    },
    findOne: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("myWallapop");
            const collectionName = 'offers';
            const offerCollection = database.collection(collectionName);
            const offer = await offerCollection.findOne(filter, options);
            return offer;
        } catch (error) {
            throw (error);
        }
    },
    findMany: async function (filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("myWallapop");
            const collectionName = 'offers';
            const offerCollection = database.collection(collectionName);
            const offer = await offerCollection.find(filter, options);
            return offer.toArray();
        } catch (error) {
            throw (error);
        }
    },
    update: async function (newOffer, filter, options) {
        try {
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("myWallapop");
            const collectionName = 'offers';
            const offerCollection = database.collection(collectionName);
            const result = await offerCollection.updateOne(filter, {$set: newOffer}, options);
            return result;
        } catch (error) {
            throw (error);
        }
    },
    getOffersPg: async function(filter, options, page) {
        try {
            const limit = 5;
            const client = await this.mongoClient.connect(this.app.get('connectionStrings'));
            const database = client.db("myWallapop");
            const collectionName = 'offers';
            const offersCollection = database.collection(collectionName);

            const offersCount = await offersCollection.countDocuments(filter);
            console.log("offersCount: " + offersCount);

            const cursor = offersCollection.find(filter, options).skip((page - 1) * limit).limit(limit);
            const offers = await cursor.toArray();
            const result = {offers: offers, total: offersCount};

            return result;
        } catch (error) {
            throw (error);
        }
    },
}