module.exports = function (app, offerRepository, logger) {

    app.get("/api/v1.0/offers", async function (req, res) {
        try {
            //Logging
            await logger.log("PET", "Mapping: /api/v1.0/offers - Method: GET - Params: -")

            let filter = {owner: {$ne: res.user}};
            let options = {};
            let offers = await offerRepository.findMany(filter, options);
            res.status(200);
            res.json({offers: offers});
        } catch (e) {
            res.status(500);
            res.json({error: "Se ha producido un error :" + e})
        }
    });
}