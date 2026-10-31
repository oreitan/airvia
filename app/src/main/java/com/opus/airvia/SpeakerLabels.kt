package com.opus.airvia

/** Pure-JVM helpers for how speakers are named in the list. */
object SpeakerLabels {
    // Sonos reports "<ip> - <model> - RINCON_<mac>" as its UPnP friendly name.
    private val SONOS_UPNP_NAME =
        Regex("""^\d{1,3}(\.\d{1,3}){3} - (.+?) - RINCON_[0-9A-Fa-f]+$""")

    /** "192.168.1.141 - Sonos Beam - RINCON_542A…" -> "Sonos Beam"; others unchanged. */
    fun cleanName(name: String): String =
        SONOS_UPNP_NAME.find(name)?.groupValues?.get(2) ?: name

    /**
     * Name for a DLNA entry: when the same device is also listed over
     * AirPlay under [roomName], show "Room · Model" (e.g. "Office · Sonos Beam").
     */
    fun dlnaDisplayName(name: String, roomName: String?): String {
        val model = cleanName(name)
        return if (roomName.isNullOrBlank() || roomName == model) model else "$roomName · $model"
    }
}
