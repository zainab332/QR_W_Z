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
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.File
import java.io.FileOutputStream

class TextFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_texte, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textInput: EditText = view.findViewById(R.id.textInput)
        val generateButton: Button = view.findViewById(R.id.generateTextQr)
        val saveButton: ImageView = view.findViewById(R.id.saveQrCode)
        val shareButton: ImageView = view.findViewById(R.id.shareQrCode)
        val qrCodeImageView: ImageView = view.findViewById(R.id.qrImageView)

        var qrBitmap: Bitmap? = null // Store the bitmap of the QR code

        generateButton.setOnClickListener {
            val text = textInput.text.toString().trim()

            // Validate field
            if (text.isEmpty()) {
                Toast.makeText(context, "Please enter some text", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Generate QR code from text input
            qrBitmap = generateQRCode(text, qrCodeImageView)

            // Clear input field after generating QR code
            textInput.setText("")

            // Make save and share buttons visible
            saveButton.visibility = View.VISIBLE
            shareButton.visibility = View.VISIBLE

            Toast.makeText(context, "QR code generated", Toast.LENGTH_SHORT).show()
        }

        saveButton.setOnClickListener {
            if (qrBitmap != null) {
                saveImageToGallery(requireContext(), qrBitmap!!)
            } else {
                Toast.makeText(context, "No QR code to save", Toast.LENGTH_SHORT).show()
            }
        }

        shareButton.setOnClickListener {
            qrBitmap?.let { bitmap ->
                shareImage(requireContext(), bitmap)
            } ?: Toast.makeText(context, "No QR code to share", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateQRCode(data: String, imageView: ImageView): Bitmap? {
        return try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(data, BarcodeFormat.QR_CODE, 512, 512)
            imageView.setImageBitmap(bitmap)
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
            // Create a temporary file for the QR code
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs() // Create necessary folders
            val file = File(cachePath, "shared_qrcode.png")
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }

            // Get URI with FileProvider
            val fileUri = androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider", // Matches authority in manifest
                file
            )

            // Create and launch share intent
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
