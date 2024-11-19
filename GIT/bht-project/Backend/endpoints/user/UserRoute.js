const express = require("express")
const router = express.Router()
const userService = require("./UserService")
const AuthenticationUtils = require("../../utils/AuthenticationUtils")
const AccessControl = require("accesscontrol")
const config = require("config")
const ac = new AccessControl(config.get("authorization.grants"))

/* CREATE */
// add a new user
router.post("/register", function (req, res, next) {
    console.log("UserRoute: Want to create an user")
    userService.register(req.body, function (err, result) {
        if (result) {
            console.log("user added to database" + userSubset(result))
            res.status(200).send(userSubset(result))
        }
        else {
            console.log("user could not be added to database")
            res.status(400).send("user could not be added: " + err)
        }
    })
})

// login of an user with base64 credentials
router.post("/login", function (req, res, next) {
    console.log("UserRoute: Want to login an user")
    if (!req.headers.authorization || req.headers.authorization.indexOf('Basic ') === -1) {
        res.statusCode = 401
        res.setHeader('WWW-Authenticate', 'Basic realm="Secure Area"')
        return res.json({ message: "Missing Authorization Header!" })
    }

    else {
        userService.login(req, function (err, err, token) {
            if (token) {
                console.log("token has been created" + token)
                res.header('Authorization', 'Bearer ' + token)
                res.status(200).send("Authentication successful")
            }

            else {
                console.log("token has not been created, error: " + err)
                res.status(400).send("could not create token: " + err)
            }
        })
    }
})

/* READ */
// gets an user by email
router.get("/getUser/:email", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("UserRoute: Want to get user by user ID")
    let permission = (req.user === req.params.email) ? ac.can(req.role).readOwn("users") : ac.can(req.role).readAny("users")
    if (permission.granted) {
        userService.getUserByID(req.params.email, function (err, result) {
            if (result) {
                console.log("user sended")
                res.status(200).send(userSubset(result))
            }
            else {
                console.log("user could not be sended")
                res.status(404).send("Could not send user " + err)
            }
        })
    }
    else {
        console.log("user is not authorized")
        res.status(401).send("you are not authorized " + err)
    }
})

/* UPDATE */
// updates an user by email
router.put("/updateUser/:email", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("UserRoute: Want to update an user")
    let permission = (req.user === req.params.email) ? ac.can(req.role).updateOwn("users") : ac.can(req.role).updateAny("users")
    if (permission.granted) {
        userService.updateUser(req.body, req.params, function (err, result) {
            if (result) {
                console.log("user updated")
                res.status(200).send(userSubset(result))
            }
            else {
                console.log("user not updated")
                res.status(409).send("Could not update hub " + err)
            }
        })
    }
    else {
        console.log("user is not authorized")
        res.status(401).send("you are not authorized " + err)
    }
})

/* UPDATE */
// assign an hub found by user_key and adds an user found by email
router.post("/postUserKey", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("UserRoute: Want to assign an hub with user")
    let permission = (req.user === req.body.email) ? ac.can(req.role).updateOwn("users") : ac.can(req.role).updateAny("users")
    if (permission.granted) {
        userService.postUserKey(req.body, function (err, result) {
            if (result) {
                console.log("user assigned to the hub")
                res.status(200).send(result)
            }
            else {
                console.log("hub not assigned")
                res.status(409).send("could not assign the user to the hub with user_key: " + err)
            }
        })
    }
    else {
        console.log("user is not authorized")
        res.status(401).send("you are not authorized " + err)
    }
})

/* DELETE */
// deletes an user by email
router.delete("/deleteUser/:email", AuthenticationUtils.isAuthenticated, function (req, res, next) {
    console.log("UserRoute: Want to delete an user")
    let permission = (req.user === req.params.email) ? ac.can(req.role).deleteOwn("users") : ac.can(req.role).deleteAny("users")
    if (permission.granted) {
        console.log(req)
        userService.deleteUser(req.params, function (err, result) {
            if (result) {
                console.log("user deleted")
                res.status(200).send(userSubset(result))
            }
            else {
                console.log("user not deleted")
                res.status(404).send("user could not be deleted: " + err)
            }
        })
    }
    else {
        console.log("user is not authorized")
        res.status(401).send("you are not authorized " + err)
    }
})

// function to only send email and not password
function userSubset(user) {
    if (!user) { return null }
    else {
        let { _id, email, role } = user
        let subset = { _id, email, role }
        return subset
    }
}

module.exports = router