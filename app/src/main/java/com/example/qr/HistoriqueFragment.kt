package com.example.qr

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoriqueFragment : Fragment() {

    private val scannerViewModel: ScannerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val view = inflater.inflate(R.layout.fragment_historique, container, false)

        val listView: ListView = view.findViewById(R.id.history_list_view)

        // Observe the scan results from ViewModel
        scannerViewModel.scanResults.observe(viewLifecycleOwner) { scanResults ->
            val items = scanResults.map { result ->
                // Format the current date and time
                val currentDate = getCurrentFormattedDate()

                // Determine the type (LINK or WIRELESS)
                if (result.startsWith("http")) {
                    ScanItem(ScanType.LINK, result, currentDate) // LINK type
                } else {
                    ScanItem(ScanType.WIRELESS, result, currentDate) // WIRELESS type (SSID assumed)
                }
            }

            // Set the adapter with the transformed scan items
            val adapter = CustomScanAdapter(requireContext(), items){ itemToDelete ->
                scannerViewModel.deleteScanResult(itemToDelete.content)
            }
            listView.adapter = adapter

            // Handle item clicks
            listView.setOnItemClickListener { _, _, position, _ ->
                val selectedItem = items[position]
                val intent = Intent(requireContext(), ResultActivity::class.java)

                intent.putExtra("SCAN_TYPE", selectedItem.type.name)
                intent.putExtra("SCAN_CONTENT", selectedItem.content)
                intent.putExtra("SCAN_DATE", selectedItem.date)


                startActivity(intent)
            }
        }

        return view
    }

    // Helper function to get the current date formatted as "dd/MM/yyyy HH:mm"
    private fun getCurrentFormattedDate(): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return formatter.format(Date())
    }
}


//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import android.widget.Button
//import android.widget.ListView
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//
//class HistoriqueFragment : Fragment() {
//
//    private val scannerViewModel: ScannerViewModel by activityViewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val view = inflater.inflate(R.layout.fragment_historique, container, false)
//
//        val listView: ListView = view.findViewById(R.id.history_list_view)
//        val clearButton: Button = view.findViewById(R.id.clear_history_button)
//
//        // Observer les résultats sauvegardés
//        scannerViewModel.scanResults.observe(viewLifecycleOwner) { scanResults ->
//            val adapter = ArrayAdapter(
//                requireContext(),
//                android.R.layout.simple_list_item_1,
//                scanResults
//            )
//            listView.adapter = adapter
//        }
//
//        // Effacer l'historique
//        clearButton.setOnClickListener {
//            scannerViewModel.clearHistory()
//        }
//
//        return view
//    }
//}





