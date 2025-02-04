package com.example.qr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CreerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_creer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = listOf(
            Option("Wi-Fi", WifiFragment()),
            Option("Site Web", SiteWebFragment()),
            Option("Email", EmailFragment()),
            Option("Adresse", AddressFragment()),
            Option("SMS", SmsFragment()),
            Option("Texte", TextFragment()),
            Option("Contact", ContactFragment()),
            Option("Téléphone", TelephoneFragment())
        )

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewOptions)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = OptionAdapter(options) { fragment ->
            navigateToFragment(fragment)
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
