const express = require("express")
const router = express.Router()
const hubService = require("./HubService")
const AuthenticationUtils = require('../../utils/AuthenticationUtils')

/* CREATE */
// add a new hub
router.post("/addHub", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("HubRoute: Want to create an hub")
    hubService.addHub(req.body, function (err, result) {
        if (result) {
            console.log("hub added to database")
            res.status(200).send(result)
        }
        else {
            console.log("hub could not be added to database")
            res.status(400).send("hub could not be added: " + err)
        }
    })
})

/* READ */
// get list of all hub's
router.get("/getAllHubs", AuthenticationUtils.isAuthenticated, function (req, res) {
    console.log("HubRoute: Want to get all hub's")
    hubService.getAllHubs(function (err, result) {
        if (result) {
            console.log("hub's sended")
            res.status(200).send(result)
        }
        else {
            console.log("hub's could not be sended")
            res.status(404).send("Could not send all hub's " + err)
        }
    })
})

/* READ */
// gets an hub by id
router.get("/getHub/:hub_id", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("HubRoute: Want to get hub by an ID")
    hubService.getHub(req.params.hub_id, function (err, result) {
        if (result) {
            console.log("hub sended")
            res.status(200).send(result)
        }
        else {
            console.log("hub could not be send")
            res.status(404).send("Could not send hub " + err)
        }
    })
})

/* READ */
// gets an hub by an user
router.get("/getHubByUser/", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("HubRoute: Want to get an hub by an user")
    hubService.getHubByUser(req.user, function (err, result) {
        console.log(req.user)
        if (result) {
            console.log("hub sended")
            res.status(200).send(result)
        }
        else {
            console.log("hub could not be send")
            res.status(404).send("Could not send hub " + err)
        }
    })
})

/* UPDATE */
// updates an hub by ID
router.put("/updateHub/:hub_id", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("HubRoute: Want to update an hub")
    hubService.updateHub(req.body, req.params.hub_id, function (err, result) {
        if (result) {
            console.log("hub updated")
            res.status(200).send(result)
        }
        else {
            console.log("hub not updated")
            res.status(409).send("Could not update hub " + err)
        }
    })
})

/* DELETE */
// deletes an hub by hub ID
router.delete("deleteHub/:hub_id", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("HubRoute: Want to delete an hub")
    hubService.deleteHub(req.params.hub_id, function (err, result) {
        if (result) {
            console.log("Hub deleted")
            res.status(200).send(result)
        }
        else {
            console.log("Hub not deleted")
            res.status(404).send("hub could not be deleted: " + err)
        }
    })
})

module.exports = router
