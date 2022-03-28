package com.marhabweb.minizendo_update

import java.util.*

open class Session {
// This is the original constructor, the one that creates the session


    var id : String = "id"
    var durationInSeconds: Int = 10

    var title: String = ""
    private var displayDuration: Int = 0

    // The session id, will be the length of time as a string.
    // It will always be unique. This constructor is called when the session
    // is started
    constructor( id: String,  time: Int)  {
        //this.id = time.toString()
        this.id = id
        this.durationInSeconds = time
        this.displayDuration = durationInSeconds / 60
        this.title = getTimeDisplay()
    }

   constructor (hr: Int, mins: Int) {
        this.durationInSeconds = getDurationInSeconds(hr, mins)
        this.id = this.durationInSeconds.toString()
        this.displayDuration = durationInSeconds / 60
        this.title = getTimeDisplay()
    }

    // Time will be calculated in seconds
    private fun getDurationInSeconds(hr: Int, mins: Int) : Int {
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

    // Time display inside the counter. It will always be in minutes.
    private fun getTimeDisplay() : String {

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

    // Adds a random quote at the end of the session upon notification
    fun addMessage() : String {
        val const = Constants()
        val total = const.msgGeneric.size
        val index = rand(0, total)
        return const.msgGeneric[index]
    }

    // Random number used to choose a quote
    @Suppress("SameParameterValue")
    private fun rand (from: Int, to: Int) : Int {
        val random = Random()
        return random.nextInt(to - from) + from
    }

}


