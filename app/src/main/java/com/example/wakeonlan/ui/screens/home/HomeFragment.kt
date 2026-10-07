package com.example.wakeonlan.ui.screens.home

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.wakeonlan.R
import com.example.wakeonlan.utils.SettingsStore
import com.example.wakeonlan.utils.WakeHelper

class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        val ctx = requireContext().applicationContext
        val macInput = view.findViewById<EditText>(R.id.mac_input)
        val ipInput = view.findViewById<EditText>(R.id.ip_input)
        val saveButton = view.findViewById<Button>(R.id.save_button)
        val wakeButton = view.findViewById<Button>(R.id.wake_button)
        val hintText = view.findViewById<TextView>(R.id.hint_text)

        // Load previously saved values
        macInput.setText(SettingsStore.getMac(ctx) ?: "")
        ipInput.setText(SettingsStore.getIp(ctx) ?: "")

        fun refreshWakeButton() {
            val configured = SettingsStore.isConfigured(ctx)
            wakeButton.isEnabled = configured
            hintText.visibility = if (configured) View.GONE else View.VISIBLE
        }
        refreshWakeButton()

        saveButton.setOnClickListener {
            val mac = macInput.text.toString().trim()
            val ip = ipInput.text.toString().trim()

            when {
                !SettingsStore.isValidMac(mac) ->
                    macInput.error = "Use the format AA:BB:CC:DD:EE:FF"
                !SettingsStore.isValidIp(ip) ->
                    ipInput.error = "Use a valid IPv4 address"
                else -> {
                    SettingsStore.save(ctx, mac, ip)
                    refreshWakeButton()
                    Toast.makeText(ctx, "Saved", Toast.LENGTH_SHORT).show()
                }
            }
        }

        wakeButton.setOnClickListener {
            WakeHelper.wake(ctx) { msg ->
                Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
