package com.example.qr

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels


class ResultatFragment : Fragment() {

    private val scannerViewModel: ScannerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_resultat, container, false)
    }

    @SuppressLint("ServiceCast")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textSSid: TextView = view.findViewById(R.id.ssid)
        val textPASS: TextView = view.findViewById(R.id.pass)
        val resultTextView: TextView = view.findViewById(R.id.resultTextView)
        val resultTextViewL: TextView = view.findViewById(R.id.resultTextViewL)
        val ssidTextView: TextView = view.findViewById(R.id.ssidTextView)
        val passwordTextView: TextView = view.findViewById(R.id.passwordTextView)
        val copySSIDButton: TextView = view.findViewById(R.id.copySSIDButton)
        val copyPasswordButton: TextView = view.findViewById(R.id.copyPasswordButton)
        val copyLinkButton: TextView = view.findViewById(R.id.copyLinkButton)
        val copSSID: ImageView = view.findViewById(R.id.copSSID)
        val copPASS: ImageView = view.findViewById(R.id.copPASS)
        val CopLINK: ImageView = view.findViewById(R.id.CopLINK)

        val ssid = arguments?.getString("SSID")
        val password = arguments?.getString("PASSWORD")
        val scanResult = arguments?.getString("SCAN_RESULT")

        // Handle the Wi-Fi case with SSID and Password
        if (ssid != null && password != null) {
            ssidTextView.text = ssid
            passwordTextView.text = password
            textSSid.visibility = View.VISIBLE
            textPASS.visibility = View.VISIBLE
            ssidTextView.visibility = View.VISIBLE
            passwordTextView.visibility = View.VISIBLE
            copySSIDButton.visibility = View.VISIBLE
            copyPasswordButton.visibility = View.VISIBLE
            copyLinkButton.visibility = View.GONE

            // Show the copy SSID and Password icons
            copSSID.visibility = View.VISIBLE
            copPASS.visibility = View.VISIBLE
            CopLINK.visibility = View.GONE

        } else if (scanResult != null && isValidUrl(scanResult)) {
            resultTextViewL.text = scanResult
            textSSid.visibility = View.GONE
            textPASS.visibility = View.GONE
            ssidTextView.visibility = View.GONE
            passwordTextView.visibility = View.GONE
            copySSIDButton.visibility = View.GONE
            copyPasswordButton.visibility = View.GONE
            copyLinkButton.visibility = View.VISIBLE

            // Show the copy URL link icon
            copSSID.visibility = View.GONE
            copPASS.visibility = View.GONE
            CopLINK.visibility = View.VISIBLE
        } else {
            resultTextView.text = scanResult ?: "Aucun résultat trouvé"
            textSSid.visibility = View.GONE
            textPASS.visibility = View.GONE
            ssidTextView.visibility = View.GONE
            passwordTextView.visibility = View.GONE
            copySSIDButton.visibility = View.GONE
            copyPasswordButton.visibility = View.GONE
            copyLinkButton.visibility = View.GONE

            // Hide all the copy icons
            copSSID.visibility = View.GONE
            copPASS.visibility = View.GONE
            CopLINK.visibility = View.GONE
        }

        // Handle SSID copy action
        copySSIDButton.setOnClickListener {
            val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = android.content.ClipData.newPlainText("SSID", ssid)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "SSID copié!", Toast.LENGTH_SHORT).show()
        }

        // Handle Password copy action
        copyPasswordButton.setOnClickListener {
            val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = android.content.ClipData.newPlainText("Password", password)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Mot de passe copié!", Toast.LENGTH_SHORT).show()
        }

        // Handle URL link copy action
        copyLinkButton.setOnClickListener {
            val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = android.content.ClipData.newPlainText("URL", scanResult)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Lien copié!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidUrl(url: String): Boolean {
        return try {
            val uri = android.net.Uri.parse(url)
            uri.host != null
        } catch (e: Exception) {
            false
        }
    }
}


//class ResultatFragment : Fragment() {
//
//    private val scannerViewModel: ScannerViewModel by activityViewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_resultat, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val textSSid: TextView = view.findViewById(R.id.ssid)
//        val textPASS: TextView = view.findViewById(R.id.pass)
//        val resultTextView: TextView = view.findViewById(R.id.resultTextView)
//        val resultTextViewL: TextView = view.findViewById(R.id.resultTextViewL)
//        val ssidTextView: TextView = view.findViewById(R.id.ssidTextView)
//        val passwordTextView: TextView = view.findViewById(R.id.passwordTextView)
//        val copySSIDButton: TextView = view.findViewById(R.id.copySSIDButton)
//        val copyPasswordButton: TextView = view.findViewById(R.id.copyPasswordButton)
//        val copyLinkButton: TextView = view.findViewById(R.id.copyLinkButton)
//
//        val ssid = arguments?.getString("SSID")
//        val password = arguments?.getString("PASSWORD")
//        val scanResult = arguments?.getString("SCAN_RESULT")
//
//        if (ssid != null && password != null) {
//            ssidTextView.text = ssid
//            passwordTextView.text = password
//            textSSid.visibility = View.VISIBLE
//            textPASS.visibility = View.VISIBLE
//            ssidTextView.visibility = View.VISIBLE
//            passwordTextView.visibility = View.VISIBLE
//            copySSIDButton.visibility = View.VISIBLE
//            copyPasswordButton.visibility = View.VISIBLE
//            copyLinkButton.visibility = View.GONE
//        } else if (scanResult != null && isValidUrl(scanResult)) {
//            resultTextViewL.text = scanResult
//            textSSid.visibility = View.GONE
//            textPASS.visibility = View.GONE
//            ssidTextView.visibility = View.GONE
//            passwordTextView.visibility = View.GONE
//            copySSIDButton.visibility = View.GONE
//            copyPasswordButton.visibility = View.GONE
//            copyLinkButton.visibility = View.VISIBLE
//        } else {
//            resultTextView.text = scanResult ?: "Aucun résultat trouvé"
//        }
//
//        // Copie du SSID
//        copySSIDButton.setOnClickListener {
//            val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            val clip = android.content.ClipData.newPlainText("SSID", ssid)
//            clipboard.setPrimaryClip(clip)
//            Toast.makeText(context, "SSID copié!", Toast.LENGTH_SHORT).show()
//        }
//
//        // Copie du mot de passe
//        copyPasswordButton.setOnClickListener {
//            val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            val clip = android.content.ClipData.newPlainText("Password", password)
//            clipboard.setPrimaryClip(clip)
//            Toast.makeText(context, "Mot de passe copié!", Toast.LENGTH_SHORT).show()
//        }
//
//        // Copie du lien
//        copyLinkButton.setOnClickListener {
//            val clipboard = activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//            val clip = android.content.ClipData.newPlainText("URL", scanResult)
//            clipboard.setPrimaryClip(clip)
//            Toast.makeText(context, "Lien copié!", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun isValidUrl(url: String): Boolean {
//        return try {
//            val uri = android.net.Uri.parse(url)
//            uri.host != null
//        } catch (e: Exception) {
//            false
//        }
//    }
//}
