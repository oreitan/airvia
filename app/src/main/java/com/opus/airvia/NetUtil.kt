package com.opus.airvia

import java.net.NetworkInterface

/** Small network helpers shared by the RAOP fallback and stream server. */
object NetUtil {
    /** Prefix length of the interface that owns [ip]; 24 when unknown. */
    fun localPrefix(ip: String): Int {
        try {
            val ifaces = NetworkInterface.getNetworkInterfaces() ?: return 24
            for (iface in ifaces) {
                for (ia in iface.interfaceAddresses) {
                    if (ia.address?.hostAddress == ip) return ia.networkPrefixLength.toInt()
                }
            }
        } catch (_: Exception) {
        }
        return 24
    }

    /**
     * Every usable host address of [ip]'s subnet except [ip] itself.
     * The prefix is clamped to /24../30 so a huge network is never swept.
     */
    fun subnetHosts(ip: String, prefix: Int): List<String> {
        val parts = ip.split('.').mapNotNull { it.toIntOrNull() }
        if (parts.size != 4 || parts.any { it !in 0..255 } || ip == "0.0.0.0") return emptyList()
        val p = prefix.coerceIn(24, 30)
        val addr = (parts[0] shl 24) or (parts[1] shl 16) or (parts[2] shl 8) or parts[3]
        val mask = -1 shl (32 - p)
        val net = addr and mask
        val bcast = net or mask.inv()
        return ((net + 1) until bcast).filter { it != addr }.map {
            "${(it ushr 24) and 255}.${(it ushr 16) and 255}.${(it ushr 8) and 255}.${it and 255}"
        }
    }

    /** Best-effort LAN IPv4 of this device (prefers wlan interfaces). */
    fun localIp(): String {
        var fallback: String? = null
        try {
            val ifaces = NetworkInterface.getNetworkInterfaces() ?: return "0.0.0.0"
            for (iface in ifaces) {
                if (!iface.isUp || iface.isLoopback || iface.isVirtual) continue
                for (addr in iface.inetAddresses) {
                    if (addr.isLoopbackAddress) continue
                    val host = addr.hostAddress ?: continue
                    if (host.contains(':')) continue // IPv6 — AirPlay/DLNA here are v4
                    if (iface.name.startsWith("wlan")) return host
                    if (fallback == null) fallback = host
                }
            }
        } catch (_: Exception) {
        }
        return fallback ?: "0.0.0.0"
    }
}
