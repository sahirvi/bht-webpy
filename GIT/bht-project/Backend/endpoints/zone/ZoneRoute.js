const express = require("express")
const router = express.Router()
const zoneService = require("./ZoneService")
const AuthenticationUtils = require("../../utils/AuthenticationUtils")

/* CREATE */
// add a new zone
router.post("/addZone", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("ZoneRoute: Want to create a zone")
    zoneService.addZone(req.body, function (err, result) {
        if (result) {
            console.log("zone added to database")
            res.status(200).send(result)
        }
        else {
            console.log("zone could not be added to database")
            res.status(400).send("zone could not be added: " + err)
        }
    })
})

/* READ */
// get list of all zone's
router.get("/getAllZones", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("ZoneRoute: Want to get all zone's")
    zoneService.getAllZones(function (err, result) {
        if (result) {
            console.log("zone's sended")
            res.status(200).send(result)
        }
        else {
            console.log("zone's could not be sended")
            res.status(404).send("Could not send all zone's: " + err)
        }
    })
})

/* READ */
// get list of all zone's for hub ID
router.get("/getAllZonesbyHubId/:hub_id", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("SensorRoute: Want to get all zone's by hub ID")
    zoneService.getAllZonesbyHubID(req.params.hub_id, function (err, result) {
        if (result) {
            console.log("zone's sended")
            res.status(200).send(result)
        }
        else {
            console.log("zone's could not be sended")
            res.status(404).send("Could not send all zone's: " + err)
        }
    })
})

/* READ */
// get zone by hub ID and zone ID
router.get("/getZone", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("ZoneRoute: Want to get zone by hub ID and zone ID")
    zoneService.getZone(req.query, function (err, result) {
        if (result) {
            console.log("zone sended")
            res.status(200).send(result)
        }
        else {
            console.log("zone could not be sended")
            res.status(404).send("Could not send zone: " + err)
        }
    })
})

/* READ */
// get all zones that are pending
router.get("/getAllPendingZones/:hub_id", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("ZoneRoute: Want to get all zones that are pending")
    zoneService.getAllPendingZones(req.params.hub_id, function (err, result) {
        if (result) {
            console.log("zones sended")
            res.status(200).send(result)
        }
        else {
            console.log("zones could not be sended")
            res.status(404).send("Could not send zones: " + err)
        }
    })
})

/* UPDATE */
// update zone with req.body
router.put("/updateZone", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("ZoneRoute: Want to update zone is_pending")
    zoneService.updateZone(req.query, req.body, function (err, result) {
        if (result) {
            console.log("zone updated")
            res.status(200).send(result)
        }
        else {
            console.log("zone not updated")
            res.status(409).send("Could not update zone " + err)
        }
    })
})

/* DELETE */
// delete zone with hub_id and zone_id
router.delete("/deleteZone", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("ZoneRoute: Want to delete a zone")
    zoneService.deleteZone(req.query, function (err, result) {
        if (result) {
            console.log("zone deleted")
            res.status(200).send(result)
        }
        else {
            console.log("zone not deleted")
            res.status(404).send("zone could not be deleted: " + err)
        }
    })
})

module.exports = router