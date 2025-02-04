package com.example.qr

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes

class CustomScanAdapter(
    context: Context,
    private val items: List<ScanItem>,
    private val onDeleteClick: (ScanItem) -> Unit // Callback pour gérer la suppression
) : ArrayAdapter<ScanItem>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item = items[position]

        // Réutiliser la vue existante si possible
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.list_item_scan, parent, false
        )

        // Récupérer les vues
        val iconView: ImageView = view.findViewById(R.id.icon)
        val primaryText: TextView = view.findViewById(R.id.primary_text)
        val secondaryText: TextView = view.findViewById(R.id.secondary_text)
        val deleteButton: ImageView = view.findViewById(R.id.delete)

        // Définir l'icône en fonction du type de scan
        iconView.setImageResource(getIconForType(item.type))

        // Définir le texte principal en fonction du type
        primaryText.text = when (item.type) {
            ScanType.WIRELESS -> extractSSID(item.content) // Extraire et afficher uniquement le SSID
            ScanType.LINK -> item.content // Afficher le lien brut
        }

        // Définir la date
        secondaryText.text = item.date

        // Gérer le clic sur l'icône de suppression
        deleteButton.setOnClickListener {
            onDeleteClick(item) // Appeler le callback de suppression
        }

        return view
    }

    // Fonction utilitaire pour récupérer l'icône correspondant au type de scan
    @DrawableRes
    private fun getIconForType(type: ScanType): Int {
        return when (type) {
            ScanType.WIRELESS -> R.drawable.ic_wifi // Icône pour les scans Wi-Fi
            ScanType.LINK -> R.drawable.ic_link // Icône pour les scans de liens
        }
    }

    // Fonction pour extraire le SSID à partir d'une chaîne brute
    private fun extractSSID(content: String): String {
        return if (content.startsWith("WIFI:")) {
            // Extraction du SSID à partir d'un format comme "WIFI:S:<SSID>;"
            val ssidRegex = "S:([^;]*)".toRegex()
            val matchResult = ssidRegex.find(content)
            matchResult?.groupValues?.get(1) ?: "SSID inconnu"
        } else {
            content // Retourner le contenu brut si le format n'est pas celui d'un Wi-Fi
        }
    }
}
