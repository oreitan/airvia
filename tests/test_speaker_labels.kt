import com.opus.airvia.SpeakerLabels

var failures = 0

fun check(name: String, cond: Boolean) {
    if (cond) println("PASS: $name") else {
        println("FAIL: $name")
        failures++
    }
}

fun main() {
    val raw = "192.168.1.141 - Sonos Beam - RINCON_542A1B915D6C01400"
    check("cleans Sonos UPnP name", SpeakerLabels.cleanName(raw) == "Sonos Beam")
    check("leaves plain names", SpeakerLabels.cleanName("Living Room TV") == "Living Room TV")
    check("room + model", SpeakerLabels.dlnaDisplayName(raw, "Office") == "Office · Sonos Beam")
    check("no room -> model", SpeakerLabels.dlnaDisplayName(raw, null) == "Sonos Beam")
    check("same name not doubled", SpeakerLabels.dlnaDisplayName("Sonos Beam", "Sonos Beam") == "Sonos Beam")
    if (failures > 0) System.exit(1)
}
