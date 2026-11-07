import com.opus.airvia.NetUtil

var failures = 0

fun check(name: String, cond: Boolean) {
    if (cond) println("PASS: $name") else {
        println("FAIL: $name")
        failures++
    }
}

fun main() {
    val h24 = NetUtil.subnetHosts("192.168.1.43", 24)
    check("/24 has 253 hosts (254 minus self)", h24.size == 253)
    check("/24 range ends", h24.first() == "192.168.1.1" && h24.last() == "192.168.1.254")
    check(
        "excludes self, network, broadcast",
        "192.168.1.43" !in h24 && "192.168.1.0" !in h24 && "192.168.1.255" !in h24,
    )
    check("/16 clamped to /24", NetUtil.subnetHosts("10.0.5.9", 16).size == 253)
    check("/26 = 61 hosts", NetUtil.subnetHosts("192.168.1.70", 26).size == 61)
    check(
        "/26 stays in block",
        NetUtil.subnetHosts("192.168.1.70", 26).all {
            it.substringAfterLast('.').toInt() in 65..126
        },
    )
    check(
        "bad input",
        NetUtil.subnetHosts("0.0.0.0", 24).isEmpty() &&
            NetUtil.subnetHosts("nope", 24).isEmpty() &&
            NetUtil.subnetHosts("1.2.3.999", 24).isEmpty(),
    )
    if (failures > 0) System.exit(1)
}
