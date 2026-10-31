package com.opus.airvia.dlna

import com.opus.airvia.dlna.Dlna.DlnaDevice

/**
 * Remembers DLNA renderers between scans. Some speakers (Sonos) answer SSDP
 * multicast only intermittently, so a scan that hears nothing must not wipe
 * a speaker that is still reachable over plain HTTP.
 */
object DlnaCache {
    private const val FIELD = '\t'
    private const val RECORD = '\n'

    /** New scan results, plus remembered devices that are still reachable. */
    fun merge(
        known: List<DlnaDevice>,
        found: List<DlnaDevice>,
        stillReachable: (DlnaDevice) -> Boolean,
    ): List<DlnaDevice> {
        val out = LinkedHashMap<String, DlnaDevice>()
        for (d in found) out[d.udn] = d
        for (d in known) {
            if (d.udn in out) continue
            if (stillReachable(d)) out[d.udn] = d
        }
        return out.values.toList()
    }

    fun encode(devices: List<DlnaDevice>): String =
        devices.joinToString(RECORD.toString()) { d ->
            listOf(d.udn, d.name, d.location, d.avTransportUrl, d.renderingControlUrl ?: "")
                .joinToString(FIELD.toString()) { it.replace(FIELD, ' ').replace(RECORD, ' ') }
        }

    fun decode(raw: String?): List<DlnaDevice> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split(RECORD).mapNotNull { line ->
            val f = line.split(FIELD)
            if (f.size < 5 || f[0].isEmpty() || f[2].isEmpty() || f[3].isEmpty()) null
            else DlnaDevice(f[0], f[1], f[2], f[3], f[4].ifEmpty { null })
        }
    }
}
