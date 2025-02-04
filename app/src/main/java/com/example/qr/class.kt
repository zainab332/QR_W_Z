package com.example.qr

data class ScanItem(
    val type: ScanType, // Type du scan (Wi-Fi ou lien)
    val content: String, // Contenu principal (SSID ou URL)
    val date: String // Date du scan
)


enum class ScanType {
    WIRELESS, // Pour les scans Wi-Fi
    LINK // Pour les liens ou autres contenus
}
