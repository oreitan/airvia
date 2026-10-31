import com.opus.airvia.dlna.Dlna.DlnaDevice
import com.opus.airvia.dlna.DlnaCache

var failures = 0

fun check(name: String, cond: Boolean) {
    if (cond) println("PASS: $name") else {
        println("FAIL: $name")
        failures++
    }
}

fun dev(udn: String, name: String = "Beam") =
    DlnaDevice(udn, name, "http://10.0.0.5:1400/d.xml", "http://10.0.0.5:1400/av", "http://10.0.0.5:1400/rc")

fun main() {
    val beam = dev("uuid:A")
    val tv = dev("uuid:B", "TV")
    check("empty scan keeps reachable known", DlnaCache.merge(listOf(beam), emptyList()) { true } == listOf(beam))
    check("drops unreachable known", DlnaCache.merge(listOf(beam), emptyList()) { false }.isEmpty())
    check("found wins and is not duplicated", DlnaCache.merge(listOf(beam), listOf(beam)) { false } == listOf(beam))
    check("adds new + keeps old", DlnaCache.merge(listOf(beam), listOf(tv)) { true }.map { it.udn } == listOf("uuid:B", "uuid:A"))
    val round = DlnaCache.decode(DlnaCache.encode(listOf(beam, dev("uuid:C").copy(renderingControlUrl = null))))
    check("encode/decode round-trip", round.size == 2 && round[0] == beam && round[1].renderingControlUrl == null)
    check("decode null/garbage", DlnaCache.decode(null).isEmpty() && DlnaCache.decode("x\ty").isEmpty())
    if (failures > 0) System.exit(1)
}
