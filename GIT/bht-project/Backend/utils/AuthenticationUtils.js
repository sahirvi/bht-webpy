const userService = require("../endpoints/user/UserService")
const jwt = require("jsonwebtoken")
const atob = require("atob")
const config = require("config")
const tokenKey = config.get("session.tokenKey")
const tokenAlgorithm = config.get("session.tokenAlgorithm")
const tokenExpiry = config.get("session.tokenExpiry")
const tokenRenewal = config.get("session.tokenRenewal")

// verifying a token, set req.user and req.role, if token is expiring, generating a new "refresh" token
function isAuthenticated(req, res, next) {

    console.log("AuthenticationUtils: Want to check the token")
    if (typeof req.headers.authorization !== 'undefined') {
        let token = req.headers.authorization.split(' ')[1]

        jwt.verify(token, tokenKey, { algorithm: tokenAlgorithm }, (err, result) => {
            if (err) {
                console.log("error, verifying the token")
                res.status(401).json({ error: "you are not authorized" })
                return
            }

            if (result) {
                console.log("token is valid")

                var payload = JSON.parse(atob(token.split('.')[1]))

                userService.getUserByID(payload.id, function (err, user) {
                    if (user) {
                        req.user = user.email
                        req.role = user.role
                        console.log("user found with ID: " + user.email + " and role: " + user.role)
                        next()
                    }
                    else {
                        console.log("user not found " + err)
                        next()
                    }
                })

                var nowUnixSeconds = Math.round(Number(new Date()) / 1000)
                if (payload.exp - nowUnixSeconds < tokenRenewal) {
                    const newToken = jwt.sign({ 'id': payload.id }, tokenKey, {
                        expiresIn: tokenExpiry,
                        algorithm: tokenAlgorithm
                    })
                    console.log("new token added: " + newToken)
                    res.header('Authorization', 'Bearer ' + newToken)
                    next()
                }
            }
        })
    } else {
        res.status(401).json({ error: "you are not authorized" })
        return
    }
}

module.exports = {
    isAuthenticated
}