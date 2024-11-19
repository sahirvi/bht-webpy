const Zone = require("./ZoneModel")
const { timer } = require("../../utils/Timer")

/* CREATE */
// add a zone with its props
function addZone(props, callback) {
    console.log("ZoneService: add a new zone")
    if (props) {
        let newZone = new Zone(props)
        newZone.save(function (err, result) {
            if (err) {
                console.log("zone could not be saved" + err)
                callback(err, null)
            }
            else {
                console.log("zone saved")
                callback(null, result)
            }
        })
    }
}

/* READ */
// get all zones
function getAllZones(callback) {
    console.log("ZoneService: Get all zone's")
    Zone.find()
        .then(zones => {
            if (zones) {
                console.log("list of zone's found")
                callback(null, zones)
            }
            else {
                console.log("list of zone's could not be found" + err)
                callback(err, null)
            }
        })
        .catch(err => {
            console.log("could not find anything")
            callback(err, null)
        })
}

/* READ */
// get all zone's by hub ID
function getAllZonesbyHubID(hub_id, callback) {
    console.log("ZoneService: Get all zone's by hub ID")
    if (hub_id) {
        Zone.find({ hub_id: hub_id })
            .then(zones => {
                if (zones) {
                    console.log("list of zone's with hub_id: " + hub_id + " found")
                    callback(null, zones)
                }
                else {
                    console.log("list of zone's could not be found" + err)
                    callback(err, null)
                }
            })
            .catch(err => {
                console.log("could not find anything")
                callback(err, null)
            })
    }
}

/* READ */
// get one zone by hub ID and zone ID
function getZone(query, callback) {
    console.log("ZoneService: Get zone by hub ID and zone ID")
    if (!query) {
        console.log("query is undefined")
    }
    else {
        Zone.findOne({
            $and: [
                { hub_id: query.hub_id },
                { zone_id: query.zone_id }
            ]
        })
            .then(zone => {
                if (zone) {
                    console.log("zone with hub_id: " + query.hub_id + " and " + query.zone_id + " found")
                    callback(null, zone)
                }
                else {
                    console.log("zone could not be found")
                    callback(err, null)
                }
            })
            .catch(err => {
                console.log("could not find anything")
                callback(err, null)
            })
    }
}

/* READ */
// get all zones that are pending
function getAllPendingZones(hub_id, callback) {
    console.log("ZoneService: Get all that are pending")
    if (hub_id) {
        Zone.find({
            $and: [
                { hub_id: hub_id }, { is_pending: true }]
        })
            .then(zones => {
                if (zones) {
                    let i = 0;
                    let zonesArray = []
                    zones.forEach(element => {
                        zonesArray.push(zones[i].zone_id)
                        i = i + 1
                    })
                    console.log("zones found: " + zonesArray)
                    callback(null, zonesArray)
                }
                else {
                    console.log("zones could not be found")
                    callback(err, null)
                }
            })
            .catch(err => {
                console.log("could not find anything")
                callback(err, null)
            })
    }
}

/* UPDATE */
// update zone is_pending or is_watering to props
function updateZone(query, props, callback) {
    console.log("ZoneService: Update is_pending or is_watering of a zone with hub_id and zone_id")
    if (!query) {
        console.log("query is undefined")
    }
    else {
        Zone.findOne({
            $and: [
                { hub_id: query.hub_id },
                { zone_id: query.zone_id }
            ]
        })
            .then(zone => {
                if (zone) {
                    Object.assign(zone, props)
                    if (zone.is_watering == false && zone.is_pending == false) {
                        zone.last_watered = Date.now()
                        zone.is_dry = false
                    }
                    if (zone.is_watering == true && zone.is_pending == true) {
                        zone.watering_timestamp = Date.now()
                        timer(zone)
                    }
                    zone.save(function (err, result) {
                        if (err) {
                            console.log("new zone could not be assigned")
                            callback(err, null)
                        }
                        else {
                            console.log("new zone assigned")
                            callback(null, result)
                        }
                    })
                }
                else {
                    console.log("zone could not be found")
                }
            })
            .catch(err => {
                console.log("could not find anything")
                callback(err, null);
            })
    }
}

/* DELETE */
// delete a zone by hub_id and zone_id
function deleteZone(query, callback) {
    console.log("ZoneService: Delete a zone")
    getZone(query, function (err, result) {
        if (result) {
            try {
                result.remove()
                console.log("zone deleted: " + result)
                callback(null, result)
            }
            catch (err) {
                console.log("zone could not be deleted: " + err)
                callback(err, null)
            }
        }
        else {
            console.log("zone not found with query")
            callback(err, null)
        }
    })
}

/* DELETE */
// delete zones of an hub
// used in hub delete
function deleteZonesByHubID(hub_id, callback) {
    console.log("ZoneService: Delete a zone")
    getAllZonesbyHubID(hub_id, function (err, result) {
        if (result) {
            try {
                let i = 0;
                result.forEach(element => {
                    console.log(result[i])
                    result[i].remove()
                    i = i + 1
                })
                console.log("zones deleted: " + result)
                callback(null, result)
            }
            catch (err) {
                console.log("zones could not be deleted: " + err)
                callback(err, null)
            }
        }
        else {
            console.log("zone not found with hub_id")
            callback(err, null)
        }
    })
}

module.exports = {
    addZone,
    getAllZones,
    getAllZonesbyHubID,
    getZone,
    getAllPendingZones,
    updateZone,
    deleteZone,
    deleteZonesByHubID
}