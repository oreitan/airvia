package com.opus.airvia

/** Pure-JVM matching for the per-app capture picker's search box. */
object AppSearch {
    /**
     * True when every whitespace-separated word of [query] appears in the
     * app's [label] or [packageName], ignoring case. Blank query matches all.
     */
    fun matches(label: String, packageName: String, query: String): Boolean {
        val words = query.trim().lowercase().split(Regex("\\s+")).filter { it.isNotEmpty() }
        if (words.isEmpty()) return true
        val hay = (label + " " + packageName).lowercase()
        return words.all { hay.contains(it) }
    }
}
