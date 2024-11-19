const hubService = require("../endpoints/hub/HubService")
const zoneService = require("../endpoints/zone/ZoneService")
const limit = 0.2

/* UPDATE */
// auto on: automatically water, if moisture_value reaches the limit
// auto off: sets is_dry in zone to true, if moisture_value is over the limit
function autoWatering(query, sensor) {
    console.log("autoWatering: if auto is on, water automatically")
    hubService.getHub(query.hub_id, function (err, hub) {
        if (hub) {
            if (sensor) {
                if (sensor.moisture_value <= limit) {
                    zoneService.getZone(query, function (err, zone) {
                        if (zone) {
                            zone.is_dry = true
                            if (hub.auto === true) {
                                zone.is_pending = true
                            }
                            else {
                                console.log("hub is not on auto")
                            }
                            zone.save(function (err, result) {
                                if (result) {
                                    console.log("zone updated: " + result)
                                }
                                else {
                                    console.log("error saving the zone: " + err)
                                }
                            })
                        }
                        else {
                            console.log("zone not found: " + err)
                        }
                    })
                }
                else {
                    zoneService.getZone(query, function (err, zone) {
                        if (zone) {
                            zone.is_dry = false
                            console.log("zone is dry: false")
                            zone.save(function (err, result) {
                                if (result) {
                                    console.log("zone updated: " + result)
                                }
                                else {
                                    console.log("error saving the zone: " + err)
                                }
                            })
                        }
                        else {
                            console.log("zone not found: " + err)
                        }
                    })
                    console.log("moisture_value over: " + limit + " ,the moisture value is: " + sensor.moisture_value)
                }
            }
            else {
                console.log("sensor not found: " + err)
            }
        }
        else {
            console.log("hub not found: " + err)
        }
    })
}

module.exports = {
    autoWatering
}
