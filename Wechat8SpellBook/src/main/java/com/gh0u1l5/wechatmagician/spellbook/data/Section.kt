package com.gh0u1l5.wechatmagician.spellbook.data

data class Section(
    val start: Int,  // Inclusive
    val end: Int,    // Exclusive
    val base: Int
) {
    operator fun contains(index: Int) = (start <= index) && (index < end)

    fun size() = end - start

    fun split(index: Int): List<Section> {
        val length = index - base
        return listOf(
            Section(start, start + length, base),
            Section(start + length, end - 1, base + length + 1)
        ).filter { it.size() != 0 }
    }
}