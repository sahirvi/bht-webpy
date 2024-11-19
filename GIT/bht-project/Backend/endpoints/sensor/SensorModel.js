const mongoose = require("mongoose")

const sensorSchema = new mongoose.Schema({
    hub_id: Number,
    zone_id: Number,
    battery: { type: Number, default: 1 },
    moisture_value: { type: Number, default: 1 },
}, { timestamps: true }
)

const Sensor = mongoose.model("Sensor", sensorSchema)

module.exports = Sensor