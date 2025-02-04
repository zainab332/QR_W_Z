package com.example.qr

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.File
import java.io.FileOutputStream

class WifiFragment : Fragment() {

    private var qrBitmap: Bitmap? = null // Stocker le QR code généré

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wifi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val wifiNameEditText: EditText = view.findViewById(R.id.wifiName)
        val wifiPasswordEditText: EditText = view.findViewById(R.id.wifiPassword)
        val generateButton: Button = view.findViewById(R.id.generateWifiQR)
        val qrImageView: ImageView = view.findViewById(R.id.qrImageView)
        val saveButton: ImageView = view.findViewById(R.id.saveQrCode)
        val shareButton: ImageView = view.findViewById(R.id.shareQrCode)

        generateButton.setOnClickListener {
            val wifiName = wifiNameEditText.text.toString().trim()
            val wifiPassword = wifiPasswordEditText.text.toString().trim()

            if (wifiName.isNotEmpty() && wifiPassword.isNotEmpty()) {
                val qrCodeData = "WIFI:T:WPA;S:$wifiName;P:$wifiPassword;;"
                qrBitmap = generateQRCode(qrCodeData, qrImageView)

                // Rendre les boutons visibles seulement si un QR code a été généré
                saveButton.visibility = View.VISIBLE
                shareButton.visibility = View.VISIBLE
            } else {
                Toast.makeText(context, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }

        saveButton.setOnClickListener {
            qrBitmap?.let {
                saveImageToGallery(requireContext(), it)
            } ?: Toast.makeText(context, "No QR code to save", Toast.LENGTH_SHORT).show()
        }

        shareButton.setOnClickListener {
            qrBitmap?.let {
                shareImage(requireContext(), it)
            } ?: Toast.makeText(context, "No QR code to share", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateQRCode(data: String, imageView: ImageView): Bitmap? {
        return try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 512, 512)
            imageView.setImageBitmap(bitmap) // Afficher le QR code
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error generating QR code", Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun saveImageToGallery(context: Context, bitmap: Bitmap) {
        val filename = "QRCode_${System.currentTimeMillis()}.png"
        val resolver = context.contentResolver

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/QRCode")
            }

            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    Toast.makeText(context, "QR code saved to gallery", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            val directory = context.getExternalFilesDir("QRCode")
            val file = directory?.let { dir -> File(dir, filename) }
            if (file != null) {
                FileOutputStream(file).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    Toast.makeText(context, "QR code saved to ${file.absolutePath}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun shareImage(context: Context, bitmap: Bitmap) {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "shared_qrcode.png")
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }

            val fileUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider", // Assurez-vous que votre provider est bien déclaré dans AndroidManifest.xml
                file
            )

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fileUri)
                type = "image/png"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share QR Code"))

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error sharing QR code", Toast.LENGTH_SHORT).show()
        }
    }
}
