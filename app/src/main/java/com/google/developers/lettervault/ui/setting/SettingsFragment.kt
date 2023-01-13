package com.google.developers.lettervault.ui.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.Data
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.developers.lettervault.R
import com.google.developers.lettervault.notification.NotificationWorker
import com.google.developers.lettervault.util.NOTIFICATION_CHANNEL_ID
import com.google.developers.lettervault.util.NightMode
import java.util.concurrent.TimeUnit

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        val theme = findPreference<ListPreference>(getString(R.string.pref_key_dark))
        val notification = findPreference<SwitchPreference>(getString(R.string.pref_key_notify))


        theme?.setOnPreferenceChangeListener { _, newValue ->
            when (newValue) {
                getString(R.string.pref_dark_auto) -> {
                    updateTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
                getString(R.string.pref_dark_on) -> {
                    updateTheme(AppCompatDelegate.MODE_NIGHT_YES)
                }
                else -> {
                    updateTheme(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
            return@setOnPreferenceChangeListener true
        }

        //for notification
        notification?.setOnPreferenceChangeListener { preference, newValue ->
            val channelName = getString(R.string.notify_channel_name)
            //TODO 13 : Schedule and cancel daily reminder using WorkManager with data channelName
            val workManager = WorkManager.getInstance(requireContext())
            if (newValue == true) {
                val data = Data.Builder()
                    .putString(NOTIFICATION_CHANNEL_ID, channelName)
                    .build()
                val periodicWorkRequest = PeriodicWorkRequest.Builder(
                    NotificationWorker::class.java,
                    1,
                    TimeUnit.DAYS
                ).setInputData(data).build()
                workManager.enqueue(periodicWorkRequest)
            } else {
                workManager.cancelAllWork()
            }
            true
        }


    }

    private fun updateTheme(nightMode: Int): Boolean {
        AppCompatDelegate.setDefaultNightMode(nightMode)
        requireActivity().recreate()
        return true
    }
}
