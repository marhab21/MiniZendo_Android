package com.marhabweb.minizendo

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*



/**
 * Created by martine on 12/19/17.
 * Screen for the actual session timing
 */

open class Session() : RealmObject() {

    @PrimaryKey
    private var id = UUID.randomUUID().toString()
    var title = "Meditation"
    var durationInSeconds = 10
    var displayDuration = 0

    fun getId() : String {
        return id
    }

    override fun toString() : String {
        return getTimeDisplay()
    }


    constructor(hr: Int, mins: Int) : this()  {
        durationInSeconds = getDurationInSeconds(hr, mins)
        displayDuration = durationInSeconds / 60
    }


    fun getDurationInSeconds(hr: Int, mins: Int) : Int {
        var fromHours = 0
        var fromMins = 0
        if (hr > 0) {
            fromHours = hr * 3600
        }
        if (mins > 0) {
            fromMins = mins * 60
        }
        return fromHours + fromMins
    }


    fun getTimeDisplay() : String {

        var hrText = "hour"
        var minText = "minutes"
        var totalText: String

        val minutes = displayDuration
        val hours = minutes / 60

        val mins = minutes % 60
        // Formatting
        if (hours > 0) {
            if (hours > 1) {
                hrText = "hours"
            }
            totalText = "$hours $hrText, "
        } else {
            totalText = ""
        }

        if (minutes == 1) {
            minText = "minute"
        }

        totalText += "$mins $minText"
        return totalText
    }


    // Adds a random message at the end of the session upon notification
    fun addMessage() : String {
        val const = Constants()
        val total = const.msgGeneric.size
        val index = rand(0, total)
        return const.msgGeneric[index]
    }

    private fun rand (from: Int, to: Int) : Int {
        val random = Random()
        return random.nextInt(to - from) + from
    }


}
/*
extension Session: Comparable {

    static func == (lhs: Session, rhs: Session) -> Bool {
        return lhs.durationInSeconds == rhs.durationInSeconds
    }

    static func < (lhs: Session, rhs: Session) -> Bool {
        return lhs.durationInSeconds < rhs.durationInSeconds
    }
}
*/