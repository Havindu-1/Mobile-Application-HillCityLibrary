package com.example.hillcitylibrary.model

data class UserProfile(
    val xp: Int = 0,
    val level: Int = 1,
    val title: String = "Beginner Reader",
    val consecutiveDaysStreak: Int = 0,
    val totalNightReadingMinutes: Int = 0,
    val lastReadDateMillis: Long = 0,
    val unlockedAchievements: List<String> = emptyList(),
    val totalFocusReadingMinutes: Int = 0,
    val deepFocusConsecutiveDays: Int = 0,
    val totalStableSessions: Int = 0,
    val highestStabilityScore: Int = 0
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val isHidden: Boolean = false
)

object AchievementsList {
    val NightReader = Achievement(
        id = "night_reader",
        title = "Night Reader",
        description = "Read in dark environments for 30 minutes total."
    )
    val MidnightScholar = Achievement(
        id = "midnight_scholar",
        title = "Midnight Scholar",
        description = "Read after 12:00 AM."
    )
    val SilentFocus = Achievement(
        id = "silent_focus",
        title = "Silent Focus",
        description = "Read continuously for 1 hour."
    )
    val MoonlightStreak = Achievement(
        id = "moonlight_streak",
        title = "Moonlight Streak",
        description = "Maintain 7 consecutive nights of reading."
    )
    val StoneMind = Achievement(
        id = "stone_mind",
        title = "Stone Mind",
        description = "Stay focused for 30 minutes without major movement."
    )
    val SilentMonk = Achievement(
        id = "silent_monk",
        title = "Silent Monk",
        description = "Complete 5 stable reading sessions."
    )
    val FocusGuardian = Achievement(
        id = "focus_guardian",
        title = "Focus Guardian",
        description = "Maintain 90% stability during reading."
    )
    val MotionMaster = Achievement(
        id = "motion_master",
        title = "Motion Master",
        description = "Read while keeping perfect posture consistency."
    )
    val DeepScholar = Achievement(
        id = "deep_scholar",
        title = "Deep Scholar",
        description = "Enter Deep Focus Mode for 7 consecutive days."
    )
    val Unshaken = Achievement(
        id = "unshaken",
        title = "Unshaken",
        description = "Read 1 hour without distraction."
    )

    val All = listOf(
        NightReader, MidnightScholar, SilentFocus, MoonlightStreak,
        StoneMind, SilentMonk, FocusGuardian, MotionMaster, DeepScholar, Unshaken
    )
}
