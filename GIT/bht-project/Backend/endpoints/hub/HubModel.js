const mongoose = require("mongoose")
const bcrypt = require("bcrypt")
require("mongoose-type-email")

const hubSchema = new mongoose.Schema({
    hub_id: { type: Number, unique: true },
    user: { type: mongoose.Schema.Types.ObjectId, ref: "User", default: null },
    bucket_empty: { type: Boolean, default: false },
    outlet_count: Number,
    factory_key: { type: String, unique: true },
    user_key: { type: Number, default: null },
    auto: { type: Boolean, default: false }
}, { timestamps: true }
)

hubSchema.methods.comparePassword = function (candidatePassword, next) {
    bcrypt.compare(candidatePassword, this.factory_key, function (err, isMatch) {
        if (err)
            return next(err); next(null, isMatch)
    })
}

hubSchema.pre("save", function (next) {
    var hub = this;
    if (hub.isModified("factory_key")) {
        bcrypt.hash((hub.factory_key), 0).then((hashed_key) => {
            hub.factory_key = hashed_key;
            next();
        })
    } else next();
})

const Hub = mongoose.model("Hub", hubSchema)

module.exports = Hub