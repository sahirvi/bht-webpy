const mongoose = require("mongoose")
const bcrypt = require("bcrypt")
require("mongoose-type-email")

const userSchema = new mongoose.Schema({
    email: { type: mongoose.SchemaTypes.Email, unique: true },
    password: String,
    role: { type: String, default: "user", enum: ["user", "admin"] }
}, { timestamps: true }
)

userSchema.pre("save", function (next) {
    var user = this
    if (user.isModified("password")) {
        bcrypt.hash(user.password, 10).then((hashedPassword) => {
            user.password = hashedPassword;
            next()
        })
    } else next()
})

userSchema.methods.comparePassword = function (candidatePassword, next) {
    bcrypt.compare(candidatePassword, this.password, function (err, isMatch) {
        if (err)
            return next(err); next(null, isMatch)
    })
}

const User = mongoose.model("User", userSchema)

module.exports = User