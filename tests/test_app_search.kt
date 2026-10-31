import com.opus.airvia.AppSearch

var failures = 0

fun check(name: String, cond: Boolean) {
    if (cond) println("PASS: $name") else {
        println("FAIL: $name")
        failures++
    }
}

fun main() {
    check("blank matches all", AppSearch.matches("YouTube", "com.google.android.youtube", "  "))
    check("label, any case", AppSearch.matches("YouTube Music", "com.google.android.apps.youtube.music", "youtube"))
    check("package name", AppSearch.matches("Spotify", "com.spotify.music", "spotify.music"))
    check("all words must match", AppSearch.matches("YouTube Music", "x", "you music"))
    check("missing word fails", !AppSearch.matches("YouTube", "x", "you music"))
    check("no match", !AppSearch.matches("Gmail", "com.google.android.gm", "telegram"))
    if (failures > 0) System.exit(1)
}
