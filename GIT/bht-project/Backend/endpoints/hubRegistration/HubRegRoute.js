const express = require("express")
const router = express.Router()
const hubRegService = require("./HubRegService")

// login of an hub with base64 credentials (generating token and user_key)
router.post("/postHubRegistration", function (req, res, next) {
    console.log("HubRegRoute: Want to login an hub, get token back and insert user_key in database")
    if (!req.headers.authorization || req.headers.authorization.indexOf('Basic ') === -1) {
        res.statusCode = 401
        res.setHeader('WWW-Authenticate', 'Basic realm="Secure Area"')
        return res.json({ message: "Missing Authorization Header!" })
    }

    else {
        hubRegService.checkHubKey(req, function (err, result, token) {
            if (token) {
                console.log("token has been created" + token)
                console.log("user_key in hub" + result)
                res.header('Authorization', 'Bearer ' + token)
                res.status(200).send(result)
            }
            else {
                console.log("token and user_key has not been created, error: " + err)
                res.status(400).send("could not create token and user_key: " + err)
            }
        })
    }
})

module.exports = router