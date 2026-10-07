package com.example.wakeonlan.utils

import android.content.Context

object SettingsStore {
    private const val PREFS = "wol_prefs"
    private const val KEY_MAC = "mac"
    private const val KEY_IP = "ip"

    const val PORT = 9

    fun save(
        context: Context,
        mac: String,
        ip: String,
    ) {
        context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_MAC, mac)
            .putString(KEY_IP, ip)
            .apply()
    }

    fun getMac(context: Context): String? = prefs(context).getString(KEY_MAC, null)?.takeIf { it.isNotBlank() }

    fun getIp(context: Context): String? = prefs(context).getString(KEY_IP, null)?.takeIf { it.isNotBlank() }

    fun isConfigured(context: Context): Boolean = getMac(context) != null && getIp(context) != null

    fun isValidMac(mac: String): Boolean = Regex("^([0-9A-Fa-f]{2}[:-]){5}[0-9A-Fa-f]{2}$").matches(mac)

    fun isValidIp(ip: String): Boolean {
        val parts = ip.split(".")
        return parts.size == 4 && parts.all { p -> p.toIntOrNull()?.let { it in 0..255 } == true }
    }

    private fun prefs(context: Context) = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
