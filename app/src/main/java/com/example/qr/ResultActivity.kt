package com.example.qr

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_resultat)

        val resultTextView: TextView = findViewById(R.id.resultTextView)
        val resultTextViewL: TextView = findViewById(R.id.resultTextViewL)
        val ssidTextView: TextView = findViewById(R.id.ssidTextView)
        val passwordTextView: TextView = findViewById(R.id.passwordTextView)
        val copySSIDButton: TextView = findViewById(R.id.copySSIDButton)
        val copyPasswordButton: TextView = findViewById(R.id.copyPasswordButton)
        val copyLinkButton: TextView = findViewById(R.id.copyLinkButton)

        val copSSID: ImageView = findViewById(R.id.copSSID)
        val copPASS: ImageView = findViewById(R.id.copPASS)
        val copLINK: ImageView = findViewById(R.id.CopLINK)

        // Get the data from the Intent
        val scanType = intent.getStringExtra("SCAN_TYPE")
        val scanContent = intent.getStringExtra("SCAN_CONTENT")
        val scanDate = intent.getStringExtra("SCAN_DATE")

        resultTextView.text = "Résultat du scan"
        resultTextViewL.text = scanContent

        // Reset visibility of all copy buttons
        copSSID.visibility = View.GONE
        copPASS.visibility = View.GONE
        copLINK.visibility = View.GONE
        copySSIDButton.visibility = View.GONE
        copyPasswordButton.visibility = View.GONE
        copyLinkButton.visibility = View.GONE

        if (scanType == "WIRELESS" && scanContent != null) {
            // Display Wi-Fi details
            val ssid = extractSSID(scanContent)
            val password = extractPassword(scanContent)

            ssidTextView.text = "SSID : $ssid"
            ssidTextView.visibility = View.VISIBLE
            passwordTextView.text = "Mot de Passe : $password"
            passwordTextView.visibility = View.VISIBLE

            // Show the icons for SSID and Password copy
            copSSID.visibility = View.VISIBLE
            copPASS.visibility = View.VISIBLE
            copySSIDButton.visibility = View.VISIBLE
            copyPasswordButton.visibility = View.VISIBLE

            // Hide the link display
            resultTextViewL.visibility = View.GONE
        }

        if (scanType == "LINK" && scanContent != null) {
            // Display link
            resultTextViewL.text = scanContent
            resultTextViewL.visibility = View.VISIBLE

            // Show the icon for link copy
            copLINK.visibility = View.VISIBLE
            copyLinkButton.visibility = View.VISIBLE
        }

        // Set button click listeners for copying content
        copySSIDButton.setOnClickListener {
            val ssid = extractSSID(scanContent ?: "")
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("SSID", ssid)
            clipboard.setPrimaryClip(clip)
            android.widget.Toast.makeText(this, "SSID copié", android.widget.Toast.LENGTH_SHORT).show()
        }

        copyPasswordButton.setOnClickListener {
            val password = extractPassword(scanContent ?: "")
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Mot de Passe", password)
            clipboard.setPrimaryClip(clip)
            android.widget.Toast.makeText(this, "Mot de passe copié", android.widget.Toast.LENGTH_SHORT).show()
        }

        copyLinkButton.setOnClickListener {
            if (scanType == "LINK" && scanContent != null) {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("Lien", scanContent)
                clipboard.setPrimaryClip(clip)
                android.widget.Toast.makeText(this, "Lien copié", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                android.widget.Toast.makeText(this, "Aucun lien valide à copier", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to extract SSID if available
    private fun extractSSID(content: String): String {
        return if (content.startsWith("WIFI:")) {
            // Extract SSID from Wi-Fi QR code format
            val ssidRegex = "S:([^;]*)".toRegex()
            val matchResult = ssidRegex.find(content)
            matchResult?.groupValues?.get(1) ?: "SSID inconnu"
        } else {
            content // Return the content if not in Wi-Fi format
        }
    }

    // Function to extract password if available
    private fun extractPassword(content: String): String {
        return if (content.startsWith("WIFI:")) {
            // Extract password from Wi-Fi QR code format
            val passwordRegex = "P:([^;]*)".toRegex()
            val matchResult = passwordRegex.find(content)
            matchResult?.groupValues?.get(1) ?: "Mot de passe inconnu"
        } else {
            "Aucun mot de passe"
        }
    }
}



//package com.example.qr
//
//import android.os.Bundle
//import android.view.View
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//
//class ResultActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_resultat)
//
//        val resultTextView: TextView = findViewById(R.id.resultTextView)
//        val resultTextViewL: TextView = findViewById(R.id.resultTextViewL)
//        val ssidTextView: TextView = findViewById(R.id.ssidTextView)
//        val passwordTextView: TextView = findViewById(R.id.passwordTextView)
//        val copySSIDButton: TextView = findViewById(R.id.copySSIDButton)
//        val copyPasswordButton: TextView = findViewById(R.id.copyPasswordButton)
//        val copyLinkButton: TextView = findViewById(R.id.copyLinkButton)
//
//        // Get the data from the Intent
//        val scanType = intent.getStringExtra("SCAN_TYPE")
//        val scanContent = intent.getStringExtra("SCAN_CONTENT")
//        val scanDate = intent.getStringExtra("SCAN_DATE")
//
//        resultTextView.text = "Résultat du scan"
//        resultTextViewL.text = scanContent
//
//        if (scanType == "WIRELESS" && scanContent != null) {
//            // Display Wi-Fi details
//            val ssid = extractSSID(scanContent)
//            val password = extractPassword(scanContent)
//
//            ssidTextView.text = "SSID : $ssid"
//            ssidTextView.visibility = View.VISIBLE
//            ssidTextView.text = ssid
//            copySSIDButton.visibility = View.VISIBLE
//
//            passwordTextView.text = "Mot de Passe : $password"
//            passwordTextView.visibility = View.VISIBLE
//            copyPasswordButton.visibility = View.VISIBLE
//
//            // Masquez resultTextViewL
//            resultTextViewL.visibility = View.GONE
//        }
//
//        if (scanType == "LINK" && scanContent != null) {
//            // Display link
//            copyLinkButton.visibility = View.VISIBLE
//        }
//
//        // Set button click listeners for copying content
//        copySSIDButton.setOnClickListener {
//            val ssid = extractSSID(scanContent ?: "")
//            val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
//            val clip = android.content.ClipData.newPlainText("SSID", ssid)
//            clipboard.setPrimaryClip(clip)
//            android.widget.Toast.makeText(this, "SSID copié", android.widget.Toast.LENGTH_SHORT).show()
//        }
//
//
//        copyPasswordButton.setOnClickListener {
//            val password = extractPassword(scanContent ?: "")
//            val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
//            val clip = android.content.ClipData.newPlainText("Mot de Passe", password)
//            clipboard.setPrimaryClip(clip)
//            android.widget.Toast.makeText(this, "Mot de passe copié", android.widget.Toast.LENGTH_SHORT).show()
//        }
//
//
//        copyLinkButton.setOnClickListener {
//            if (scanType == "LINK" && scanContent != null) {
//                val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
//                val clip = android.content.ClipData.newPlainText("Lien", scanContent)
//                clipboard.setPrimaryClip(clip)
//                android.widget.Toast.makeText(this, "Lien copié", android.widget.Toast.LENGTH_SHORT).show()
//            } else {
//                android.widget.Toast.makeText(this, "Aucun lien valide à copier", android.widget.Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    }
//
//    // Function to extract SSID if available
//    private fun extractSSID(content: String): String {
//        return if (content.startsWith("WIFI:")) {
//            // Extract SSID from Wi-Fi QR code format
//            val ssidRegex = "S:([^;]*)".toRegex()
//            val matchResult = ssidRegex.find(content)
//            matchResult?.groupValues?.get(1) ?: "SSID inconnu"
//        } else {
//            content // Return the content if not in Wi-Fi format
//        }
//    }
//    private fun extractPassword(content: String): String {
//        return if (content.startsWith("WIFI:")) {
//            // Extract password from Wi-Fi QR code format
//            val passwordRegex = "P:([^;]*)".toRegex()
//            val matchResult = passwordRegex.find(content)
//            matchResult?.groupValues?.get(1) ?: "Mot de passe inconnu"
//        } else {
//            "Aucun mot de passe"
//        }
//    }
//
//}
