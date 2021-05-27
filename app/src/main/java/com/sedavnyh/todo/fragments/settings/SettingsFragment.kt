package com.sedavnyh.todo.fragments.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.sedavnyh.todo.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}