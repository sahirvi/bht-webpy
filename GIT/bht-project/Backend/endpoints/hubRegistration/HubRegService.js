const Hub = require("../hub/HubModel")
const jwt = require("jsonwebtoken")
const config = require("config")
const generateKey = require("../../utils/NumberGenerator")
const tokenKey = config.get("session.tokenKey")
const tokenAlgorithm = config.get("session.tokenAlgorithm")
const tokenExpiry = config.get("session.tokenExpiry")

/* UPDATE */
// login of an hub with hub_id and factory_key in header generating token and insert user_key in hub
function checkHubKey(req, callback) {
    if (req) {
        console.log("HubRegService: login of an existing hub and creating token and user_key")
        const base64Credentials = req.headers.authorization.split(' ')[1]
        const credentials = Buffer.from(base64Credentials, 'base64').toString('ascii')
        const [hub_id, factory_key] = credentials.split(':')

        if (!hub_id) {
            console.log("error: header is missing")
            callback("header missing", null, null)
            return
        }

        Hub.findOne({ hub_id: hub_id })
            .then(hub => {
                if (hub) {
                    console.log("found hub, check the factory_key")
                    hub.comparePassword(factory_key, function (err, result) {
                        if (result) {
                            console.log("factory_key is correct, creating user_key and token")

                            let token = jwt.sign({ 'id': hub.hub_id }, tokenKey, {
                                expiresIn: tokenExpiry,
                                algorithm: tokenAlgorithm
                            })
                            console.log("token created: " + token)

                            if (hub.user) {
                                return callback(null, hub, token)
                            }

                            let user_key = generateKey.getRandom(9)
                            console.log("user_key: " + user_key)
                            Object.assign(hub, { user_key: user_key })
                            hub.save()

                            callback(null, hub, token)
                        }
                        else {
                            console.log("factory_key is not correct, could not create user_key and token ")
                            callback(err, null, null)
                        }
                    })
                }
                else {
                    console.log("did not find hub by hub ID: " + hub_id)
                    callback(err, null, null)
                }

            }).catch(err => {
                return callback(err, null, null)
            })
    }
}

module.exports = {
    checkHubKey
}