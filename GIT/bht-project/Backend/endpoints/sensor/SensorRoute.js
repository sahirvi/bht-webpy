const express = require("express")
const router = express.Router()
const sensorService = require("./SensorService")
const AuthenticationUtils = require("../../utils/AuthenticationUtils")

/* CREATE */
// add a new sensor + update fresh sensor
router.post("/addSensor", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("SensorRoute: Want to create a sensor and update fresh sensor")
    sensorService.addSensor(req.body, function (err, result) {
        const wait = async () => {
            try {
                const wait = await result
                console.log("sensor added to database")
                res.status(200).send(result)
            } catch (e) { }
        }
        if (result) {
            wait()
        }
        if (err) {
            console.log("sensor could not be added to database")
            res.status(400).send("sensor could not be added: " + err)
        }
    })
})


/* READ */
// get list of all fresh sensor's
router.get("/getAllSensors", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("SensorRoute: Want to get all sensor's with the newest data")
    sensorService.getAllSensors(function (err, result) {
        if (err) {
            console.log("sensor's could not be sended")
            res.status(404).send("Could not send all sensor's " + err)
        }
        if (result) {
            console.log("sensor's sended")
            res.status(200).send(result)
        }
    })
})

/* READ */
// get list of all sensor's for hub ID
router.get("/getAllSensorsByHubId/:hub_id", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("SensorRoute: Want to get all sensor's by hub ID")
    sensorService.getAllSensorsByHubID(req.params.hub_id, function (err, result) {
        if (result) {
            console.log("sensor's sended")
            res.status(200).send(result)
        }
        else {
            console.log("sensor's could not be sended")
            res.status(404).send("Could not send all sensor's " + err)
        }
    })
})

/* READ */
// get sensor by hub ID and zone ID
router.get("/getSensor", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("SensorRoute: Want to get sensor by hub ID and zone ID")
    sensorService.getSensor(req.query, function (err, result) {
        if (result) {
            console.log("sensor sended")
            res.status(200).send(result)
        }
        else {
            console.log("sensor could not be sended")
            res.status(404).send("Could not send sensor " + err)
        }
    })
})

/* UPDATE */
// updates an sensor by hub_id and zone_id
router.put("/updateSensor", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("SensorRoute: Want to update an sensor")
    sensorService.updateSensor(req.query, req.body, function (err, result) {
        if (result) {
            console.log("sensor updated")
            res.status(200).send(result)
        }
        else {
            console.log("sensor not updated")
            res.status(409).send("Could not update sensor " + err)
        }
    })
})

/* DELETE */
// deletes an sensor by hub_id and zone_id
router.delete("/deleteSensor", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("SensorRoute: Want to delete a sensor")
    sensorService.deleteSensor(req.query, function (err, result) {
        if (result) {
            console.log("sensor deleted")
            res.status(200).send(result)
        }
        else {
            console.log("sensor not deleted")
            res.status(404).send("sensor could not be deleted: " + err)
        }
    })
})

module.exports = router