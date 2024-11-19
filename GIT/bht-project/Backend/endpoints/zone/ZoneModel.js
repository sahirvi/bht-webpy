const mongoose = require("mongoose")

const zoneSchema = new mongoose.Schema({
    hub_id: Number,
    zone_id: Number,
    name: String,
    is_dry: { type: Boolean, default: false },
    is_pending: { type: Boolean, default: false },
    is_watering: { type: Boolean, default: false },
    watering_timestamp: { type: Date, default: Date.now },
    last_watered: { type: Date, default: Date.now }
}, { timestamps: true }
)

const Zone = mongoose.model("Zone", zoneSchema)

module.exports = Zone