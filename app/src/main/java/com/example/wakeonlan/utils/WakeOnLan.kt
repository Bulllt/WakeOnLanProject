package com.example.wakeonlan.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.concurrent.thread

object MagicPacket {
    fun send(
        mac: String,
        broadcastIp: String,
        port: Int,
    ) {
        val macBytes = parseMac(mac)
        val payload =
            ByteArray(6) { 0xFF.toByte() } +
                ByteArray(16 * 6) { macBytes[it % 6] }

        DatagramSocket().use { socket ->
            socket.broadcast = true
            socket.send(
                DatagramPacket(payload, payload.size, InetAddress.getByName(broadcastIp), port),
            )
        }
    }

    private fun parseMac(mac: String): ByteArray {
        val parts = mac.split(":", "-")
        require(parts.size == 6) { "Invalid MAC address: $mac" }
        return ByteArray(6) { parts[it].toInt(16).toByte() }
    }
}

object WakeHelper {
    fun wake(
        context: Context,
        onResult: (String) -> Unit,
    ) {
        val appContext = context.applicationContext
        val mac = SettingsStore.getMac(appContext)
        val ip = SettingsStore.getIp(appContext)

        if (mac == null || ip == null) {
            onResult("Open the app and save your MAC and IP first")
            return
        }

        thread {
            val msg =
                try {
                    MagicPacket.send(mac, ip, SettingsStore.PORT)
                    "Magic packet sent"
                } catch (e: Exception) {
                    Log.e("WakeHelper", "Failed to send magic packet", e)
                    "Failed to send packet"
                }
            Handler(Looper.getMainLooper()).post { onResult(msg) }
        }
    }
}
