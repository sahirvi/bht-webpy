const Sensor = require("./SensorModel")
const Auto = require("../../utils/Auto")

/* CREATE */
// add a sensor with its props
function addSensor(props, callback) {
    console.log("SensorService: add a new sensor")
    if (props) {
        let newSensor = new Sensor(props)
        newSensor.save(function (err, result) {
            if (err) {
                console.log("sensor could not be saved" + err)
                callback(err, null)
            }
            else {
                console.log("sensor saved")
                callback(null, result)
            }
        })
    }
}

/* READ */
// get all current sensor's
function getAllSensors(callback) {
    console.log("SensorService: Get all sensor's")
    Sensor.find()
        .then(sensors => {
            if (sensors) {
                console.log("list of sensor's found")
                callback(null, sensors)
            }
            else {
                console.log("list of sensor's could not be found" + err)
                callback(err, null)
            }
        })
        .catch(err => {
            console.log("could not find anything")
            callback(err, null)
        })
}

/* READ */
// get all sensor's by hub ID
function getAllSensorsByHubID(hub_id, callback) {
    console.log("SensorService: Get all sensor's by hub ID")
    if (!hub_id) {
        console.log("hub_id is undefined")
    }
    Sensor.find({ hub_id: hub_id })
        .then(sensors => {
            if (sensors) {
                console.log("list of sensor's with hub_id: " + hub_id + " found")
                callback(null, sensors)
            }
            else {
                console.log("list of sensor's could not be found" + err)
                callback(err, null)
            }
        })
        .catch(err => {
            console.log("could not find anything")
            callback(err, null)
        })
}

/* READ */
// get one sensor by hub ID and zone ID
function getSensor(query, callback) {
    console.log("SensorService: Get sensor by hub ID and zone ID")
    if (!query) {
        console.log("query is undefined")
    }
    else {
        Sensor.findOne({
            $and: [
                { hub_id: query.hub_id },
                { zone_id: query.zone_id }
            ]
        })
            .then(sensor => {
                if (sensor) {
                    console.log("sensor with hub_id: " + sensor.hub_id + " and " + sensor.zone_id + " found")
                    callback(null, sensor)
                }
                else {
                    console.log("sensor could not be found")
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
// update existing sensor
function updateSensor(query, props, callback) {
    console.log("SensorService: Update an sensor")
    if (query) {
        getSensor(query, function (err, sensor) {
            if (sensor) {
                Object.assign(sensor, props)
                sensor.save(function (err, result) {
                    if (result) {
                        Auto.autoWatering(query, sensor)
                        console.log("new sensor assigned")
                        callback(null, result)
                    }
                    else {
                        console.log("new sensor could not be assigned")
                        callback(err, null)
                    }
                })
            }
            else {
                console.log("could not find the sensor with hub_id: " + query.hub_id + " and zone_id: " + query.zone_id)
                callback(err, null)
            }
        })
    }
}

/* DELETE */
// delete sensor by hub_id and zone_id
function deleteSensor(query, callback) {
    console.log("SensorService: Delete a sensor")
    getSensor(query, function (err, result) {
        if (result) {
            try {
                result.remove()
                console.log("sensor deleted: " + result)
                callback(null, result)
            }
            catch (err) {
                console.log("sensor could not be deleted: " + err)
                callback(err, null)
            }
        }
        else {
            console.log("sensor not found with query")
            callback(err, null)
        }
    })
}

/* DELETE SENSORS BY HUB ID */
function deleteSensorsByHubID(hub_id, callback) {
    console.log("SensorService: Delete a sensor")
    getAllSensorsByHubID(hub_id, function (err, result) {
        if (result) {
            try {
                let i = 0;
                result.forEach(element => {
                    console.log(result[i])
                    result[i].remove()
                    i = i + 1
                })
                console.log("sensor's deleted: " + result)
                callback(null, result)
            }
            catch (err) {
                console.log("sensor's could not be deleted: " + err)
                callback(err, null)
            }
        }
        else {
            console.log("sensor not found with hub_id")
            callback(err, null)
        }
    })
}

module.exports = {
    addSensor,
    getAllSensors,
    getAllSensorsByHubID,
    getSensor,
    updateSensor,
    deleteSensor,
    deleteSensorsByHubID
}