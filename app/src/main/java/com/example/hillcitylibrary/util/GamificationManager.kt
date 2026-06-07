package com.example.hillcitylibrary.util

import android.content.Context
import android.content.SharedPreferences
import com.example.hillcitylibrary.model.AchievementsList
import com.example.hillcitylibrary.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar

class GamificationManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("gamification_prefs", Context.MODE_PRIVATE)

    private val _userProfile = MutableStateFlow(loadProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // For emitting newly unlocked achievements to the UI
    private val _newlyUnlockedAchievement = MutableStateFlow<String?>(null)
    val newlyUnlockedAchievement: StateFlow<String?> = _newlyUnlockedAchievement.asStateFlow()

    private fun loadProfile(): UserProfile {
        val unlockedListStr = prefs.getString("unlocked_achievements", "") ?: ""
        val unlockedList = if (unlockedListStr.isNotEmpty()) unlockedListStr.split(",") else emptyList()

        return UserProfile(
            xp = prefs.getInt("xp", 0),
            level = prefs.getInt("level", 1),
            title = prefs.getString("title", "Beginner Reader") ?: "Beginner Reader",
            consecutiveDaysStreak = prefs.getInt("streak", 0),
            totalNightReadingMinutes = prefs.getInt("night_reading_mins", 0),
            lastReadDateMillis = prefs.getLong("last_read_date", 0L),
            unlockedAchievements = unlockedList,
            totalFocusReadingMinutes = prefs.getInt("total_focus_mins", 0),
            deepFocusConsecutiveDays = prefs.getInt("deep_focus_days", 0),
            totalStableSessions = prefs.getInt("total_stable_sessions", 0),
            highestStabilityScore = prefs.getInt("highest_stability", 0)
        )
    }

    private fun saveProfile(profile: UserProfile) {
        prefs.edit().apply {
            putInt("xp", profile.xp)
            putInt("level", profile.level)
            putString("title", profile.title)
            putInt("streak", profile.consecutiveDaysStreak)
            putInt("night_reading_mins", profile.totalNightReadingMinutes)
            putLong("last_read_date", profile.lastReadDateMillis)
            putString("unlocked_achievements", profile.unlockedAchievements.joinToString(","))
            putInt("total_focus_mins", profile.totalFocusReadingMinutes)
            putInt("deep_focus_days", profile.deepFocusConsecutiveDays)
            putInt("total_stable_sessions", profile.totalStableSessions)
            putInt("highest_stability", profile.highestStabilityScore)
            apply()
        }
        _userProfile.value = profile
    }

    fun clearAchievementNotification() {
        _newlyUnlockedAchievement.value = null
    }

    fun addReadingProgress(pagesRead: Int, timeSpentMinutes: Long) {
        val earnedXp = (pagesRead * 5) + (timeSpentMinutes * 2).toInt()
        addXp(earnedXp)
    }

    fun addXp(amount: Int) {
        val current = _userProfile.value
        var newXp = current.xp + amount
        var newLevel = current.level
        
        // Level up logic: every 100 XP is a level
        while (newXp >= newLevel * 100) {
            newLevel++
        }

        val newTitle = determineTitle(newLevel)
        
        saveProfile(current.copy(xp = newXp, level = newLevel, title = newTitle))
    }

    fun logReadingSession(minutes: Int, stableMinutes: Int, isNightMode: Boolean, isDeepFocus: Boolean) {
        val current = _userProfile.value
        
        // Calculate XP
        // Base XP: 1 per minute
        var xpEarned = minutes
        
        // Combo multipliers
        val comboMultiplier = when {
            minutes >= 45 -> 5
            minutes >= 20 -> 2
            minutes >= 10 -> 1
            else -> 1
        }
        
        xpEarned *= comboMultiplier
        
        if (isNightMode) {
            xpEarned = (xpEarned * 1.5).toInt() // Night mode bonus
        }
        
        // Stable reading bonus
        if (stableMinutes > 0) {
            xpEarned += (stableMinutes * 2)
        }

        addXp(xpEarned)

        // Update Stats
        val now = System.currentTimeMillis()
        var newStreak = current.consecutiveDaysStreak
        
        if (current.lastReadDateMillis > 0) {
            val lastCal = Calendar.getInstance().apply { timeInMillis = current.lastReadDateMillis }
            val nowCal = Calendar.getInstance().apply { timeInMillis = now }
            
            val diffDays = nowCal.get(Calendar.DAY_OF_YEAR) - lastCal.get(Calendar.DAY_OF_YEAR)
            val diffYears = nowCal.get(Calendar.YEAR) - lastCal.get(Calendar.YEAR)
            
            if (diffYears == 0 && diffDays == 1) {
                newStreak++
            } else if (diffDays > 1 || diffYears > 0) {
                newStreak = 1
            }
        } else {
            newStreak = 1
        }

        var newNightReadingMins = current.totalNightReadingMinutes
        if (isNightMode) {
            newNightReadingMins += minutes
        }

        var newTotalFocusMins = current.totalFocusReadingMinutes + stableMinutes
        var newDeepFocusDays = current.deepFocusConsecutiveDays
        if (isDeepFocus && newStreak > 0) {
            // Very simplistic tracking for deep focus streak
            val nowCal = Calendar.getInstance()
            val hour = nowCal.get(Calendar.HOUR_OF_DAY)
            if (hour == 0 && newStreak == 1) newDeepFocusDays = 1
            else if (isDeepFocus) newDeepFocusDays++
        }
        var newTotalStableSessions = current.totalStableSessions
        if (stableMinutes > 5) newTotalStableSessions++ // Count as a stable session if >5 mins of stability

        val updatedProfile = _userProfile.value.copy(
            consecutiveDaysStreak = newStreak,
            totalNightReadingMinutes = newNightReadingMins,
            lastReadDateMillis = now,
            totalFocusReadingMinutes = newTotalFocusMins,
            deepFocusConsecutiveDays = newDeepFocusDays,
            totalStableSessions = newTotalStableSessions
        )
        
        saveProfile(updatedProfile)
        
        checkAchievements(minutes, stableMinutes, isNightMode, isDeepFocus)
    }

    private fun determineTitle(level: Int): String {
        return when {
            level >= 100 -> "Eternal Librarian"
            level >= 80 -> "Archive Sage"
            level >= 60 -> "Mind Keeper"
            level >= 45 -> "Silent Scholar"
            level >= 30 -> "Page Wanderer"
            level >= 15 -> "Knowledge Hunter"
            level >= 5 -> "Archive Explorer"
            else -> "Beginner Reader"
        }
    }

    private fun checkAchievements(sessionMinutes: Int, stableMinutes: Int, isNightMode: Boolean, isDeepFocus: Boolean) {
        val current = _userProfile.value
        val unlocked = current.unlockedAchievements.toMutableList()
        var newlyUnlocked: String? = null

        if (isNightMode && current.totalNightReadingMinutes >= 30 && !unlocked.contains(AchievementsList.NightReader.id)) {
            unlocked.add(AchievementsList.NightReader.id)
            newlyUnlocked = AchievementsList.NightReader.id
        }

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if ((hour == 0 || hour == 1 || hour == 2) && !unlocked.contains(AchievementsList.MidnightScholar.id)) {
            unlocked.add(AchievementsList.MidnightScholar.id)
            newlyUnlocked = AchievementsList.MidnightScholar.id
        }

        if (sessionMinutes >= 60 && !unlocked.contains(AchievementsList.SilentFocus.id)) {
            unlocked.add(AchievementsList.SilentFocus.id)
            newlyUnlocked = AchievementsList.SilentFocus.id
        }

        if (current.consecutiveDaysStreak >= 7 && !unlocked.contains(AchievementsList.MoonlightStreak.id)) {
            unlocked.add(AchievementsList.MoonlightStreak.id)
            newlyUnlocked = AchievementsList.MoonlightStreak.id
        }

        if (stableMinutes >= 30 && !unlocked.contains(AchievementsList.StoneMind.id)) {
            unlocked.add(AchievementsList.StoneMind.id)
            newlyUnlocked = AchievementsList.StoneMind.id
        }

        if (current.totalStableSessions >= 5 && !unlocked.contains(AchievementsList.SilentMonk.id)) {
            unlocked.add(AchievementsList.SilentMonk.id)
            newlyUnlocked = AchievementsList.SilentMonk.id
        }

        if (current.deepFocusConsecutiveDays >= 7 && !unlocked.contains(AchievementsList.DeepScholar.id)) {
            unlocked.add(AchievementsList.DeepScholar.id)
            newlyUnlocked = AchievementsList.DeepScholar.id
        }

        if (sessionMinutes >= 60 && sessionMinutes == stableMinutes && !unlocked.contains(AchievementsList.Unshaken.id)) {
            unlocked.add(AchievementsList.Unshaken.id)
            newlyUnlocked = AchievementsList.Unshaken.id
        }

        if (newlyUnlocked != null) {
            saveProfile(current.copy(unlockedAchievements = unlocked))
            _newlyUnlockedAchievement.value = newlyUnlocked
        }
    }
}
