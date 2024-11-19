/* UPDATE */
// timer to set is_pending to false, after a specific time of minutes
function timer(zone) {
    let addMinutes = 1
    setTimeout(function () {
        zone.is_pending = false
        zone.save(function (err, result) {
            if (result) {
                console.log("watering finished, changed is_pending for " + zone)
            }
            else {
                console.log("error saving the zone: " + err)
            }
        })
    }, (addMinutes * 60 * 1000))
}

module.exports = {
    timer
}