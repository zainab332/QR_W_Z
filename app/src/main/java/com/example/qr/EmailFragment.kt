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

class EmailFragment : Fragment() {

    private var qrBitmap: Bitmap? = null // Stocker le QR code généré

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emailEditText: EditText = view.findViewById(R.id.emailEditText)
        val subjectEditText: EditText = view.findViewById(R.id.subjectEditText)
        val bodyEditText: EditText = view.findViewById(R.id.bodyEditText)
        val generateButton: Button = view.findViewById(R.id.generateEmailQR)
        val qrImageView: ImageView = view.findViewById(R.id.qrImageView)
        val saveButton: ImageView = view.findViewById(R.id.saveQrCode)
        val shareButton: ImageView = view.findViewById(R.id.shareQrCode)

        generateButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val subject = subjectEditText.text.toString().trim()
            val body = bodyEditText.text.toString().trim()

            if (email.isNotEmpty() && subject.isNotEmpty() && body.isNotEmpty()) {
                val mailTo = "mailto:$email?subject=$subject&body=$body"
                qrBitmap = generateQRCode(mailTo)
                if (qrBitmap != null) {
                    qrImageView.setImageBitmap(qrBitmap) // Afficher l'image générée
                    saveButton.visibility = View.VISIBLE
                    shareButton.visibility = View.VISIBLE
                } else {
                    Toast.makeText(context, "Failed to generate QR code", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
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

    // Fonction pour générer le QR code
    private fun generateQRCode(content: String): Bitmap? {
        return try {
            val barcodeEncoder = BarcodeEncoder()
            barcodeEncoder.encodeBitmap(content, BarcodeFormat.QR_CODE, 512, 512)
        } catch (e: Exception) {
            e.printStackTrace()
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
