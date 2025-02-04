package com.example.qr

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class ScannerFragment : Fragment() {

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
    }

    private val scannerViewModel: ScannerViewModel by activityViewModels()
    private lateinit var barcodeView: DecoratedBarcodeView
    private var isFlashOn = false // État de la lampe


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scanner2, container, false)

        // Vérifier et demander la permission de la caméra
        if (hasCameraPermission()) {
            setupScanner(view)
        } else {
            requestCameraPermission()
        }

        return view
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    private fun setupScanner(view: View) {
        // Initialiser le scanner
        barcodeView = view.findViewById(R.id.barcode_scanner)
        barcodeView.decodeContinuous(callback)

        // Ajouter la gestion de la lampe
        val btnFlashToggle: ImageView = view.findViewById(R.id.btn_flash_toggle)
        btnFlashToggle.setOnClickListener {
            toggleFlash()
        }
    }

    private val callback = BarcodeCallback { result: BarcodeResult? ->
        result?.let {
            val scannedContent = it.text
            Toast.makeText(context, "Code scanné : $scannedContent", Toast.LENGTH_SHORT).show()

            // Ajouter le résultat au ViewModel
            scannerViewModel.addScanResult(scannedContent)

            // Vérifier si c'est un QR code Wi-Fi
            val wifiDetails = parseWifiDetails(scannedContent)

            // Afficher le résultat dans ResultatFragment
            val bundle = Bundle()
            if (wifiDetails != null) {
                bundle.putString("SSID", wifiDetails.first)
                bundle.putString("PASSWORD", wifiDetails.second)
            } else {
                bundle.putString("SCAN_RESULT", scannedContent)
            }

            val resultFragment = ResultatFragment()
            resultFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, resultFragment)
                .addToBackStack(null)
                .commit()

            // Ouvrir l'URL si valide
            if (isValidUrl(scannedContent)) {
                openInBrowser(scannedContent)
            }
        }
    }

    private fun toggleFlash() {
        if (isFlashOn) {
            barcodeView.setTorchOff()
            isFlashOn = false
            Toast.makeText(context, "Lampe désactivée", Toast.LENGTH_SHORT).show()
        } else {
            barcodeView.setTorchOn()
            isFlashOn = true
            Toast.makeText(context, "Lampe activée", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission accordée, initialiser le scanner
                view?.let { setupScanner(it) }
            } else {
                // Permission refusée, afficher un message
                Toast.makeText(
                    requireContext(),
                    "Permission de la caméra refusée.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::barcodeView.isInitialized) {
            barcodeView.resume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::barcodeView.isInitialized) {
            barcodeView.pause()
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

    private fun openInBrowser(url: String) {
        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
        startActivity(intent)
    }

    private fun parseWifiDetails(scannedContent: String): Pair<String, String>? {
        return if (scannedContent.startsWith("WIFI:")) {
            val ssid = Regex("S:([^;]+);").find(scannedContent)?.groups?.get(1)?.value.orEmpty()
            val password = Regex("P:([^;]+);").find(scannedContent)?.groups?.get(1)?.value.orEmpty()
            ssid to password
        } else {
            null
        }
    }
}


//package com.example.qr
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.Toast
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import com.journeyapps.barcodescanner.BarcodeCallback
//import com.journeyapps.barcodescanner.BarcodeResult
//import com.journeyapps.barcodescanner.DecoratedBarcodeView
//
//class ScannerFragment : Fragment() {
//
//    companion object {
//        private const val CAMERA_PERMISSION_REQUEST_CODE = 100
//    }
//
//    private val scannerViewModel: ScannerViewModel by activityViewModels()
//    private lateinit var barcodeView: DecoratedBarcodeView
//    private var isFlashOn = false // État de la lampe
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_scanner2, container, false)
//
////        // Initialiser le scanner
////        barcodeView = view.findViewById(R.id.barcode_scanner)
////        barcodeView.decodeContinuous(callback)
//
//        // Vérifier et demander la permission de la caméra
//        if (hasCameraPermission()) {
//            setupScanner(view)
//        } else {
//            requestCameraPermission()
//        }
//
//        return view
//    }
//
//    private fun hasCameraPermission(): Boolean {
//        return ContextCompat.checkSelfPermission(
//            requireContext(),
//            Manifest.permission.CAMERA
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun requestCameraPermission() {
//        ActivityCompat.requestPermissions(
//            requireActivity(),
//            arrayOf(Manifest.permission.CAMERA),
//            CAMERA_PERMISSION_REQUEST_CODE
//        )
//    }
//
//    private fun setupScanner(view: View) {
//        // Initialiser le scanner
//        barcodeView = view.findViewById(R.id.barcode_scanner)
//        barcodeView.decodeContinuous(callback)
//    }
//
//    // Ajouter la gestion de la lampe
//    val btnFlashToggle: ImageView = view.findViewById(R.id.btn_flash_toggle)
//    btnFlashToggle.setOnClickListener {
//        toggleFlash()
//    }
//
//    private val callback = BarcodeCallback { result: BarcodeResult? ->
//        result?.let {
//            val scannedContent = it.text
//            Toast.makeText(context, "Code scanné : $scannedContent", Toast.LENGTH_SHORT).show()
//
//            // Ajouter le résultat au ViewModel
//            scannerViewModel.addScanResult(scannedContent)
//
//            // Vérifier si c'est un QR code Wi-Fi
//            val wifiDetails = parseWifiDetails(scannedContent)
//
//            // Afficher le résultat dans ResultatFragment
//            val bundle = Bundle()
//            if (wifiDetails != null) {
//                bundle.putString("SSID", wifiDetails.first)
//                bundle.putString("PASSWORD", wifiDetails.second)
//            } else {
//                bundle.putString("SCAN_RESULT", scannedContent)
//            }
//
//            val resultFragment = ResultatFragment()
//            resultFragment.arguments = bundle
//
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, resultFragment)
//                .addToBackStack(null)
//                .commit()
//
//            // Ouvrir l'URL si valide
//            if (isValidUrl(scannedContent)) {
//                openInBrowser(scannedContent)
//            }
//        }
//    }
//
//    private fun toggleFlash() {
//        if (isFlashOn) {
//            barcodeView.setTorchOff()
//            isFlashOn = false
//            Toast.makeText(context, "Lampe désactivée", Toast.LENGTH_SHORT).show()
//        } else {
//            barcodeView.setTorchOn()
//            isFlashOn = true
//            Toast.makeText(context, "Lampe activée", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission accordée, initialiser le scanner
//                view?.let { setupScanner(it) }
//            } else {
//                // Permission refusée, afficher un message
//                Toast.makeText(
//                    requireContext(),
//                    "Permission de la caméra refusée.",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        barcodeView.resume() // Reprendre la caméra
//    }
//
//    override fun onPause() {
//        super.onPause()
//        barcodeView.pause() // Mettre en pause la caméra
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
//
//    private fun openInBrowser(url: String) {
//        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
//        startActivity(intent)
//    }
//
//    private fun parseWifiDetails(scannedContent: String): Pair<String, String>? {
//        return if (scannedContent.startsWith("WIFI:")) {
//            val ssid = Regex("S:([^;]+);").find(scannedContent)?.groups?.get(1)?.value.orEmpty()
//            val password = Regex("P:([^;]+);").find(scannedContent)?.groups?.get(1)?.value.orEmpty()
//            ssid to password
//        } else {
//            null
//        }
//    }
//}
