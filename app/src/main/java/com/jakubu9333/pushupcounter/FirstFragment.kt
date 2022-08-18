package com.jakubu9333.pushupcounter

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.jakubu9333.pushupcounter.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        val date= DateServicesData()
        binding.date=date


        super.onViewCreated(view, savedInstanceState)

        binding.buttonNext.setOnClickListener {
            date.addDay()
        }
        binding.buttonPrev.setOnClickListener{
            date.minusDay()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}