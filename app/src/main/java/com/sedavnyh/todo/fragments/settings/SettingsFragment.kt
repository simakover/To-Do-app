package com.sedavnyh.todo.fragments.settings

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.sedavnyh.todo.R
import com.sedavnyh.todo.data.viewmodel.ToDoViewModel

class SettingsFragment : PreferenceFragmentCompat() {

    private val mToDoViewModel: ToDoViewModel by viewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val preference = findPreference<Preference>("delete_all")
        preference?.setOnPreferenceClickListener {
            confirmDeleteAll()
        }
    }

    // Confirmation alert on deletion
    private fun confirmDeleteAll(): Boolean {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mToDoViewModel.deleteAllData()
            Toast.makeText(requireContext(), "All items successfully removed", Toast.LENGTH_SHORT)
                .show()
            findNavController().navigate(R.id.action_settingsFragment_to_listFragment)
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete all items?")
        builder.setMessage("Are you sure you want to remove all items?")
        builder.create().show()

        return true
    }
}