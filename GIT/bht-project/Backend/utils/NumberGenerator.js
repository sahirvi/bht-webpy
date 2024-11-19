/* CREATE */
// generating a random Key with a specific length
function getRandom(length) {
    return Math.floor(Math.pow(10, length - 1) + Math.random() * 9 * Math.pow(10, length - 1))
}

module.exports = {
    getRandom
}
