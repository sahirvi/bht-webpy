const Hub = require("./HubModel")
const userService = require("../user/UserService")
const zoneService = require("../zone/ZoneService")

/* CREATE */
// add a new hub with its props
function addHub(props, callback) {
    console.log("HubService: add a new hub")
    if (props) {
        let hub = new Hub(props)
        hub.save(function (err, result) {
            if (result) {
                console.log("hub saved")
                callback(null, result)
            }
            else {
                console.log("hub could not be saved" + err)
                callback(err, null)
            }
        })
    }
    else {
        console.log("no props found: " + err)
        callback(err, null)
    }
}

/* READ */
// get all hub's
function getAllHubs(callback) {
    console.log("HubService: Get all hub's")
    Hub.find()
        .then(hubs => {
            if (hubs) {
                console.log("list of hub's found")
                callback(null, hubs)
            }
            else {
                console.log("list of hub's could not be found" + err)
                callback(err, null)
            }
        })
        .catch(err => {
            console.log("could not find anything")
            callback(err, null);
        })
}

/* READ */
// get hub by ID
function getHub(hub_id, callback) {
    console.log("HubService: Get an hub by an user")
    if (hub_id) {
        Hub.findOne({ hub_id: hub_id })
            .then(hub => {
                if (hub) {
                    console.log("found the hub by hub_id: " + hub.hub_id)
                    callback(null, hub)
                }
                else {
                    console.log("could not find the hub with hub_id: " + hub.hub_id)
                    callback(err, null)
                }
            })
            .catch(err => {
                console.log("could not find anything")
                callback(err, null);
            })
    }

    else {
        console.log("hub_id is missing")
    }
}

/* READ */
// get hub by ID
function getHubByUser(email, callback) {
    console.log("HubService: Get an hub by email of an user")
    if (email) {
        userService.getUserByID(email, function (err, user) {
            Hub.findOne({ user: user._id })
                .then(hub => {
                    if (hub) {
                        console.log("found the hub user: " + user.email)
                        callback(null, hub)
                    }
                    else {
                        console.log("could not find the hub with user: " + user.email)
                        callback(err, null)
                    }
                })
                .catch(err => {
                    console.log("could not find anything")
                    callback(err, null);
                })
        })
    }

    else {
        console.log("email is missing")
    }
}

/* UPDATE */
// update existing hub
function updateHub(props, hub_id, callback) {
    console.log("HubService: Update an hub")
    if (props) {
        getHub(hub_id, function (err, hub) {
            if (hub) {
                Object.assign(hub, props)
                hub.save(function (err, result) {
                    if (result) {
                        console.log("new hub assigned")
                        callback(null, result)
                    }
                    else {
                        console.log("new hub could not be assigned")
                        callback(err, null)
                    }
                })
            }
            else {
                console.log("could not find the hub with hub_id: " + hub_id)
                callback(err, null)
            }
        })
    }
}

/* DELETE */
// delete hub with sensor's and zone's from database
function deleteHub(hub_id, callback) {
    console.log("HubService: Delete an hub")
    getHub(hub_id, function (err, result) {
        if (result) {
            try {
                result.remove()
                zoneService.deleteZonesByHubID(hub_id, function (err, result) {
                    if (result) {
                        console.log("sensor's deleted: " + result)
                    }
                    else {
                        console.log("sensor's could not be deleted: " + err)
                    }
                })
                console.log("hub deleted: " + result)
                callback(null, result)
            }
            catch (err) {
                console.log("hub could not be deleted: " + err)
                callback(err, null)
            }
        }
        else {
            console.log("hub not found with params")
            callback(err, null)
        }
    })
}

module.exports = {
    addHub,
    getAllHubs,
    getHub,
    getHubByUser,
    updateHub,
    deleteHub
}