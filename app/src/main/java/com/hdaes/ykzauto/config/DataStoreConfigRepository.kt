package com.hdaes.ykzauto.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.clear
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hdaes.ykzauto.notification.NotificationRule
import com.hdaes.ykzauto.schedule.TimeWindow
import com.hdaes.ykzauto.schedule.WeekdayRule
import com.hdaes.ykzauto.workflow.TargetElementRule
import com.hdaes.ykzauto.workflow.WorkflowConfig
import java.io.IOException
import java.time.DayOfWeek
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.appConfigDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_config"
)

class DataStoreConfigRepository(context: Context) : ConfigRepository {
    private val dataStore = context.applicationContext.appConfigDataStore

    override val config: Flow<AppConfig> = dataStore.data
        .catch { error ->
            if (error is IOException) {
                emit(emptyPreferences())
            } else {
                throw error
            }
        }
        .map(::readConfig)

    override suspend fun replace(config: AppConfig) {
        dataStore.edit { preferences ->
            writeConfig(preferences, config)
        }
    }

    override suspend fun update(transform: (AppConfig) -> AppConfig) {
        dataStore.edit { preferences ->
            writeConfig(preferences, transform(readConfig(preferences)))
        }
    }

    override suspend fun reset() {
        dataStore.edit(MutablePreferences::clear)
    }

    private fun readConfig(preferences: Preferences): AppConfig {
        val defaults = AppConfig.DEFAULT
        val defaultWorkflow = defaults.workflowConfig

        val stepTimeoutMillis = (preferences[Keys.STEP_TIMEOUT_MILLIS]
            ?: defaultWorkflow.stepTimeoutMillis).coerceAtLeast(1_000L)
        val overallTimeoutMillis = (preferences[Keys.OVERALL_TIMEOUT_MILLIS]
            ?: defaultWorkflow.overallTimeoutMillis).coerceAtLeast(stepTimeoutMillis)

        return AppConfig(
            targetAppPackageName = preferences[Keys.TARGET_APP_PACKAGE]
                ?: defaults.targetAppPackageName,
            notificationRule = NotificationRule(
                keywords = preferences[Keys.NOTIFICATION_KEYWORDS]
                    ?: defaults.notificationRule.keywords,
                matchAllKeywords = preferences[Keys.MATCH_ALL_NOTIFICATION_KEYWORDS]
                    ?: defaults.notificationRule.matchAllKeywords,
                ignoreCase = preferences[Keys.IGNORE_NOTIFICATION_CASE]
                    ?: defaults.notificationRule.ignoreCase
            ),
            workflowConfig = WorkflowConfig(
                pageLoadKeywords = preferences[Keys.PAGE_LOAD_KEYWORDS]
                    ?: defaultWorkflow.pageLoadKeywords,
                targetButtonRule = readElementRule(
                    preferences = preferences,
                    textKey = Keys.TARGET_BUTTON_TEXT,
                    exactMatchKey = Keys.TARGET_BUTTON_EXACT_MATCH,
                    clickableKey = Keys.TARGET_BUTTON_REQUIRE_CLICKABLE,
                    enabledKey = Keys.TARGET_BUTTON_REQUIRE_ENABLED,
                    defaults = defaultWorkflow.targetButtonRule
                ),
                confirmButtonRule = readElementRule(
                    preferences = preferences,
                    textKey = Keys.CONFIRM_BUTTON_TEXT,
                    exactMatchKey = Keys.CONFIRM_BUTTON_EXACT_MATCH,
                    clickableKey = Keys.CONFIRM_BUTTON_REQUIRE_CLICKABLE,
                    enabledKey = Keys.CONFIRM_BUTTON_REQUIRE_ENABLED,
                    defaults = defaultWorkflow.confirmButtonRule
                ),
                maxScrollCount = (preferences[Keys.MAX_SCROLL_COUNT]
                    ?: defaultWorkflow.maxScrollCount).coerceAtLeast(0),
                stepTimeoutMillis = stepTimeoutMillis,
                overallTimeoutMillis = overallTimeoutMillis
            ),
            weekdayRule = readWeekdayRule(preferences, defaults.weekdayRule),
            allowedTimeWindows = readTimeWindows(preferences, defaults.allowedTimeWindows),
            enableNightWatch = preferences[Keys.ENABLE_NIGHT_WATCH]
                ?: defaults.enableNightWatch,
            enableNodeTreeLogging = preferences[Keys.ENABLE_NODE_TREE_LOGGING]
                ?: defaults.enableNodeTreeLogging
        )
    }

    private fun readElementRule(
        preferences: Preferences,
        textKey: Preferences.Key<String>,
        exactMatchKey: Preferences.Key<Boolean>,
        clickableKey: Preferences.Key<Boolean>,
        enabledKey: Preferences.Key<Boolean>,
        defaults: TargetElementRule
    ) = TargetElementRule(
        text = preferences[textKey] ?: defaults.text,
        exactTextMatch = preferences[exactMatchKey] ?: defaults.exactTextMatch,
        requireClickable = preferences[clickableKey] ?: defaults.requireClickable,
        requireEnabled = preferences[enabledKey] ?: defaults.requireEnabled
    )

    private fun readWeekdayRule(
        preferences: Preferences,
        defaults: WeekdayRule
    ): WeekdayRule {
        val storedDays = preferences[Keys.ALLOWED_WEEKDAYS] ?: return defaults
        val parsedDays = storedDays.mapNotNull { dayName ->
            runCatching { DayOfWeek.valueOf(dayName) }.getOrNull()
        }.toSet()
        return if (storedDays.isNotEmpty() && parsedDays.isEmpty()) defaults else WeekdayRule(parsedDays)
    }

    private fun readTimeWindows(
        preferences: Preferences,
        defaults: List<TimeWindow>
    ): List<TimeWindow> {
        val storedWindows = preferences[Keys.ALLOWED_TIME_WINDOWS] ?: return defaults
        val parsedWindows = storedWindows.mapNotNull(::decodeTimeWindow)
            .sortedBy(TimeWindow::startMinuteInclusive)
        return if (storedWindows.isNotEmpty() && parsedWindows.isEmpty()) defaults else parsedWindows
    }

    private fun decodeTimeWindow(value: String): TimeWindow? {
        val parts = value.split(':', limit = 2)
        if (parts.size != 2) return null
        val start = parts[0].toIntOrNull() ?: return null
        val end = parts[1].toIntOrNull() ?: return null
        return runCatching { TimeWindow(start, end) }.getOrNull()
    }

    private fun writeConfig(preferences: MutablePreferences, config: AppConfig) {
        val workflow = config.workflowConfig
        preferences[Keys.TARGET_APP_PACKAGE] = config.targetAppPackageName
        preferences[Keys.NOTIFICATION_KEYWORDS] = config.notificationRule.keywords
        preferences[Keys.MATCH_ALL_NOTIFICATION_KEYWORDS] = config.notificationRule.matchAllKeywords
        preferences[Keys.IGNORE_NOTIFICATION_CASE] = config.notificationRule.ignoreCase
        preferences[Keys.PAGE_LOAD_KEYWORDS] = workflow.pageLoadKeywords
        preferences[Keys.TARGET_BUTTON_TEXT] = workflow.targetButtonRule.text
        preferences[Keys.TARGET_BUTTON_EXACT_MATCH] = workflow.targetButtonRule.exactTextMatch
        preferences[Keys.TARGET_BUTTON_REQUIRE_CLICKABLE] = workflow.targetButtonRule.requireClickable
        preferences[Keys.TARGET_BUTTON_REQUIRE_ENABLED] = workflow.targetButtonRule.requireEnabled
        preferences[Keys.CONFIRM_BUTTON_TEXT] = workflow.confirmButtonRule.text
        preferences[Keys.CONFIRM_BUTTON_EXACT_MATCH] = workflow.confirmButtonRule.exactTextMatch
        preferences[Keys.CONFIRM_BUTTON_REQUIRE_CLICKABLE] = workflow.confirmButtonRule.requireClickable
        preferences[Keys.CONFIRM_BUTTON_REQUIRE_ENABLED] = workflow.confirmButtonRule.requireEnabled
        preferences[Keys.ALLOWED_WEEKDAYS] = config.weekdayRule.allowedDays.map(DayOfWeek::name).toSet()
        preferences[Keys.ALLOWED_TIME_WINDOWS] = config.allowedTimeWindows
            .map { window -> "${window.startMinuteInclusive}:${window.endMinuteInclusive}" }
            .toSet()
        preferences[Keys.MAX_SCROLL_COUNT] = workflow.maxScrollCount
        preferences[Keys.STEP_TIMEOUT_MILLIS] = workflow.stepTimeoutMillis
        preferences[Keys.OVERALL_TIMEOUT_MILLIS] = workflow.overallTimeoutMillis
        preferences[Keys.ENABLE_NIGHT_WATCH] = config.enableNightWatch
        preferences[Keys.ENABLE_NODE_TREE_LOGGING] = config.enableNodeTreeLogging
    }

    private object Keys {
        val TARGET_APP_PACKAGE = stringPreferencesKey("target_app_package")
        val NOTIFICATION_KEYWORDS = stringSetPreferencesKey("notification_keywords")
        val MATCH_ALL_NOTIFICATION_KEYWORDS = booleanPreferencesKey("match_all_notification_keywords")
        val IGNORE_NOTIFICATION_CASE = booleanPreferencesKey("ignore_notification_case")
        val PAGE_LOAD_KEYWORDS = stringSetPreferencesKey("page_load_keywords")
        val TARGET_BUTTON_TEXT = stringPreferencesKey("target_button_text")
        val TARGET_BUTTON_EXACT_MATCH = booleanPreferencesKey("target_button_exact_match")
        val TARGET_BUTTON_REQUIRE_CLICKABLE = booleanPreferencesKey("target_button_require_clickable")
        val TARGET_BUTTON_REQUIRE_ENABLED = booleanPreferencesKey("target_button_require_enabled")
        val CONFIRM_BUTTON_TEXT = stringPreferencesKey("confirm_button_text")
        val CONFIRM_BUTTON_EXACT_MATCH = booleanPreferencesKey("confirm_button_exact_match")
        val CONFIRM_BUTTON_REQUIRE_CLICKABLE = booleanPreferencesKey("confirm_button_require_clickable")
        val CONFIRM_BUTTON_REQUIRE_ENABLED = booleanPreferencesKey("confirm_button_require_enabled")
        val ALLOWED_WEEKDAYS = stringSetPreferencesKey("allowed_weekdays")
        val ALLOWED_TIME_WINDOWS = stringSetPreferencesKey("allowed_time_windows")
        val MAX_SCROLL_COUNT = intPreferencesKey("max_scroll_count")
        val STEP_TIMEOUT_MILLIS = longPreferencesKey("step_timeout_millis")
        val OVERALL_TIMEOUT_MILLIS = longPreferencesKey("overall_timeout_millis")
        val ENABLE_NIGHT_WATCH = booleanPreferencesKey("enable_night_watch")
        val ENABLE_NODE_TREE_LOGGING = booleanPreferencesKey("enable_node_tree_logging")
    }
}
