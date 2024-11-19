const User = require("./UserModel")
const jwt = require("jsonwebtoken")
const config = require("config")
const Hub = require("../hub/HubModel")
const tokenKey = config.get("session.tokenKey");
const tokenAlgorithm = config.get("session.tokenAlgorithm")
const tokenExpiry = config.get("session.tokenExpiry")

/* CREATE */
// add an user with its props
function register(props, callback) {
    console.log("UserService: add a new user")
    let newUser = new User(props)
    newUser.save(function (err, result) {
        if (err) {
            console.log("user could not be saved" + err)
            callback(err, null)
        }
        else {
            console.log("user saved" + result)
            callback(null, result)
        }
    })
}

// login of an user with email and password
function login(req, callback) {
    console.log("UserService: login and create token")
    const base64Credentials = req.headers.authorization.split(' ')[1]
    const credentials = Buffer.from(base64Credentials, 'base64').toString('ascii')
    const [email, password] = credentials.split(':')
    if (!email) {
        console.log("error: no Hhader")
        callback("Hhader missing", null, null)
        return
    }
    getUserByID(email, function (err, user) {
        if (user) {
            console.log("found user, check the password")
            console.log(JSON.stringify(user))
            user.comparePassword(password, function (err, isMatch) {
                if (isMatch) {
                    console.log("Password is correct, creating Token")

                    let token = jwt.sign({ 'id': user.email }, tokenKey, {
                        expiresIn: tokenExpiry,
                        algorithm: tokenAlgorithm
                    })
                    console.log("Token created: " + token)

                    callback(null, null, token)
                }
                else {
                    console.log("Token could not be created")
                    callback(err, null)
                }
            })
        }
        else {
            console.log("UserService: did not find user by email: " + email)
            callback(err, null)
        }
    })
}

/* GET (AN USER BY EMAIL) */
// get an user by email
function getUserByID(email, callback) {
    console.log("UserService: get an user by user ID")
    User.findOne({ email: email })
        .then(user => {
            if (user) {
                console.log("user with email: " + user.email + " found")
                callback(null, user)
            }
            else {
                console.log("user could not be found")
                callback(err, null)
            }
        })
        .catch(err => {
            console.log("could not find anything")
            callback(err, null)
        })
}

/* UPDATE */
// update existing user
function updateUser(props, params, callback) {
    console.log("UserService: update an user")
    if (props) {
        getUserByID(params.email, function (err, user) {
            if (user) {
                Object.assign(user, props)
                user.save(function (err, result) {
                    if (result) {
                        console.log("new user assigned")
                        callback(null, result)
                    }
                    else {
                        console.log("new hub could not be assigned")
                        callback(err, null)
                    }
                })
            }
            else {
                console.log("could not find the user with email: " + user.email)
                callback(err, null)
            }
        })
    }
}

/* UPDATE */
// find an hub (with the random generated user_key), delete user_key and map the email of an user to the hub
function postUserKey(props, callback) {
    console.log("UserService: assign user to hub")
    if (props.user_key) {
        Hub.findOne({ user_key: props.user_key })           // find hub with random generated user_key
            .then(hub => {
                User.findOne({ email: props.email })        // find user with email
                    .then(user => {
                        hub.user = user._id                 // link hub to user with object_ID
                        hub.user_key = null                 // delete generated user_key
                        hub.save(function (err, result) {
                            if (result) {
                                console.log("user assigned to hub: " + result)
                                callback(null, result)
                            }
                            else {
                                console.log("user could not be assigned to hub: " + err)
                                callback(err, null)
                            }
                        })
                    })
            })
            .catch(err => {
                return callback(err, null)
            })
    }
}

/* DELETE */
// delete user from database
function deleteUser(params, callback) {
    console.log("UserService: delete an user")
    getUserByID(params.email, function (err, result) {
        if (result) {
            try {
                result.remove()
                console.log("user deleted: " + result)
                callback(null, result)
            }
            catch (err) {
                console.log("user could not be deleted: " + err)
                callback(err, null)
            }
        }
        else {
            console.log("user not found")
            callback(err, null)
        }
    })
}

module.exports = {
    register,
    login,
    getUserByID,
    updateUser,
    postUserKey,
    deleteUser
}