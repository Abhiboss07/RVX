package com.rvx.companion.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rvx.companion.glyph.GlyphEffect
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "rvx_settings")

/**
 * Every user-facing feature is gated behind a toggle here (project rule: "every feature
 * must include a settings toggle"). Values are exposed as a single [SettingsState] flow so
 * the UI and services observe one source of truth.
 */
class SettingsRepository(private val context: Context) {

    val state: Flow<SettingsState> = context.dataStore.data.map { p ->
        SettingsState(
            analyticsEnabled = p[Keys.ANALYTICS] ?: true,
            edgeLightingEnabled = p[Keys.EDGE_LIGHTING] ?: false,
            glyphEnabled = p[Keys.GLYPH] ?: true,
            flashNotificationsEnabled = p[Keys.FLASH] ?: false,
            amoledTheme = p[Keys.AMOLED] ?: false,
            dynamicColor = p[Keys.DYNAMIC_COLOR] ?: true,
            performanceProfile = PerformanceProfile.fromId(p[Keys.PERF_PROFILE] ?: 0),
            edgeColor = p[Keys.EDGE_COLOR] ?: DEFAULT_EDGE_COLOR,
            edgeFollowsArt = p[Keys.EDGE_FOLLOWS_ART] ?: true,
            glyphEffect = GlyphEffect.fromId(p[Keys.GLYPH_EFFECT] ?: 0),
        )
    }

    suspend fun setAnalytics(v: Boolean) = put(Keys.ANALYTICS, v)
    suspend fun setEdgeLighting(v: Boolean) = put(Keys.EDGE_LIGHTING, v)
    suspend fun setGlyph(v: Boolean) = put(Keys.GLYPH, v)
    suspend fun setFlashNotifications(v: Boolean) = put(Keys.FLASH, v)
    suspend fun setAmoled(v: Boolean) = put(Keys.AMOLED, v)
    suspend fun setDynamicColor(v: Boolean) = put(Keys.DYNAMIC_COLOR, v)
    suspend fun setEdgeColor(color: Int) = context.dataStore.edit { it[Keys.EDGE_COLOR] = color }
    suspend fun setEdgeFollowsArt(v: Boolean) = put(Keys.EDGE_FOLLOWS_ART, v)
    suspend fun setPerformanceProfile(p: PerformanceProfile) =
        context.dataStore.edit { it[Keys.PERF_PROFILE] = p.id }
    suspend fun setGlyphEffect(e: GlyphEffect) =
        context.dataStore.edit { it[Keys.GLYPH_EFFECT] = e.id }

    private suspend fun put(key: Preferences.Key<Boolean>, v: Boolean) =
        context.dataStore.edit { it[key] = v }

    private object Keys {
        val ANALYTICS = booleanPreferencesKey("analytics_enabled")
        val EDGE_LIGHTING = booleanPreferencesKey("edge_lighting_enabled")
        val GLYPH = booleanPreferencesKey("glyph_enabled")
        val FLASH = booleanPreferencesKey("flash_notifications_enabled")
        val AMOLED = booleanPreferencesKey("amoled_theme")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val PERF_PROFILE = intPreferencesKey("performance_profile")
        val EDGE_COLOR = intPreferencesKey("edge_color")
        val EDGE_FOLLOWS_ART = booleanPreferencesKey("edge_follows_art")
        val GLYPH_EFFECT = intPreferencesKey("glyph_effect")
    }

    companion object {
        const val DEFAULT_EDGE_COLOR = 0xFF4D6DFF.toInt()
    }
}

data class SettingsState(
    val analyticsEnabled: Boolean = true,
    val edgeLightingEnabled: Boolean = false,
    val glyphEnabled: Boolean = true,
    val flashNotificationsEnabled: Boolean = false,
    val amoledTheme: Boolean = false,
    val dynamicColor: Boolean = true,
    val performanceProfile: PerformanceProfile = PerformanceProfile.BALANCED,
    val edgeColor: Int = SettingsRepository.DEFAULT_EDGE_COLOR,
    val edgeFollowsArt: Boolean = true,
    val glyphEffect: GlyphEffect = GlyphEffect.BREATHING,
)

enum class PerformanceProfile(val id: Int, val label: String) {
    BATTERY_SAVER(1, "Battery Saver"),
    BALANCED(0, "Balanced"),
    PERFORMANCE(2, "Performance");

    companion object {
        fun fromId(id: Int): PerformanceProfile = entries.firstOrNull { it.id == id } ?: BALANCED
    }
}
