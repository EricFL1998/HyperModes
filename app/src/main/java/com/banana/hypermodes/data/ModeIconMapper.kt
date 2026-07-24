package com.banana.hypermodes.data

import com.banana.hypermodes.R

/**
 * Maps colorful emojis used in the UI to monochromatic system-style vector icons for the status bar.
 */
object ModeIconMapper {

    private val EMOJI_TO_VECTOR = mapOf(
        "🌙" to "ic_stat_moon",
        "🚗" to "ic_stat_car",
        "💼" to "ic_stat_work",
        "🎮" to "ic_stat_game",
        "📖" to "ic_stat_book",
        "🏠" to "ic_stat_home",
        "💡" to "ic_stat_lightbulb",
        "📅" to "ic_stat_calendar",
        "🎧" to "ic_stat_headset",
        "🚶" to "ic_stat_walking",
        "🎨" to "ic_stat_palette",
        "❄️" to "ic_stat_snowflake",
        "🔕" to "ic_stat_mute",
        "🛠️" to "ic_stat_repair",
        "🎹" to "ic_stat_music",
        "🎬" to "ic_stat_movie",
        "🌿" to "ic_stat_nature",
        "🖥️" to "ic_stat_computer",
        "🚆" to "ic_stat_plane",
        "🍴" to "ic_stat_dining",
        "🛒" to "ic_stat_shopping",
        "🐾" to "ic_stat_pets",
        "🎟️" to "ic_stat_ticket",
        "👨‍👩‍👧" to "ic_stat_family",
        "❤️" to "ic_stat_heart",
        "⭐" to "ic_stat_star",
        "⏰" to "ic_stat_alarm",
        "🧘" to "ic_stat_zen",
        "✈️" to "ic_stat_plane",
        "📍" to "ic_stat_location",
        "🏋️" to "ic_stat_exercise"
    )

    /**
     * Get the resource name of the monochromatic vector for a given emoji.
     */
    fun getStatusBarIcon(emoji: String): String {
        return EMOJI_TO_VECTOR[emoji] ?: "ic_stat_zen"
    }

    /**
     * Get the resource ID of the monochromatic vector for a given emoji.
     */
    fun getStatusBarIconRes(emoji: String): Int {
        return when (getStatusBarIcon(emoji)) {
            "ic_stat_star" -> R.drawable.ic_stat_star
            "ic_stat_work" -> R.drawable.ic_stat_work
            "ic_stat_game" -> R.drawable.ic_stat_game
            "ic_stat_book" -> R.drawable.ic_stat_book
            "ic_stat_car" -> R.drawable.ic_stat_car
            "ic_stat_home" -> R.drawable.ic_stat_home
            "ic_stat_moon" -> R.drawable.ic_stat_moon
            "ic_stat_plane" -> R.drawable.ic_stat_plane
            "ic_stat_chat" -> R.drawable.ic_stat_chat
            "ic_stat_group" -> R.drawable.ic_stat_group
            "ic_stat_lightbulb" -> R.drawable.ic_stat_lightbulb
            "ic_stat_calendar" -> R.drawable.ic_stat_calendar
            "ic_stat_walking" -> R.drawable.ic_stat_walking
            "ic_stat_location" -> R.drawable.ic_stat_location
            "ic_stat_palette" -> R.drawable.ic_stat_palette
            "ic_stat_snowflake" -> R.drawable.ic_stat_snowflake
            "ic_stat_mute" -> R.drawable.ic_stat_mute
            "ic_stat_repair" -> R.drawable.ic_stat_repair
            "ic_stat_music" -> R.drawable.ic_stat_music
            "ic_stat_movie" -> R.drawable.ic_stat_movie
            "ic_stat_nature" -> R.drawable.ic_stat_nature
            "ic_stat_headset" -> R.drawable.ic_stat_headset
            "ic_stat_computer" -> R.drawable.ic_stat_computer
            "ic_stat_dining" -> R.drawable.ic_stat_dining
            "ic_stat_shopping" -> R.drawable.ic_stat_shopping
            "ic_stat_pets" -> R.drawable.ic_stat_pets
            "ic_stat_ticket" -> R.drawable.ic_stat_ticket
            "ic_stat_family" -> R.drawable.ic_stat_family
            "ic_stat_heart" -> R.drawable.ic_stat_heart
            "ic_stat_alarm" -> R.drawable.ic_stat_alarm
            "ic_stat_exercise" -> R.drawable.ic_stat_exercise
            else -> R.drawable.ic_stat_zen
        }
    }
}
