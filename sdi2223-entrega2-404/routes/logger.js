module.exports = {
    logsRepository: null,
    logger:null,
    init(logger,logsRepository){
        this.logsRepository = logsRepository;
        this.logger = logger;
    },
    log: async function(type, message){
        let log = {
            type: type,
            date: new Date(),
            description: message
        }
        this.logsRepository.insertLog(log).then(logId => {
            this.logger.info("Type: "+type+" Message: "+message)
        }).catch(error => {
                //Nada
        });
    },
    logMany: async function(logArray){
        this.logsRepository.insertLogs(logArray).then(logId => {
            this.logger.info("Type: "+logArray[0].type+" Message: "+logArray[0].description)
            this.logger.info("Type: "+logArray[1].type+" Message: "+logArray[1].description)
        }).catch(error => {
            //Nada
        });
    }
}