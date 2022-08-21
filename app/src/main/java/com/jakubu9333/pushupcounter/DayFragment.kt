package com.jakubu9333.pushupcounter

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import com.jakubu9333.pushupcounter.database.PushUps
import com.jakubu9333.pushupcounter.databinding.FragmentDayBinding
import com.jakubu9333.pushupcounter.viewmodels.PushUpsViewModel
import com.jakubu9333.pushupcounter.viewmodels.PushUpsViewModelFactory
import java.time.LocalDate

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class DayFragment : Fragment() {
    private val viewModel: PushUpsViewModel by activityViewModels {
        PushUpsViewModelFactory(
            (activity?.application as MainApp).database.pushUpsDao
        )
    }
    private var pushUpsItem: PushUps? = null

    private var _binding: FragmentDayBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDayBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun changingDay(date: LocalDate) {

        val pushUpsLive = viewModel.retrieveItem(date)
        pushUpsLive.observe(viewLifecycleOwner) { pushUps ->
            pushUpsItem = pushUps
            if (pushUps != null) {
                binding.textViewPushups.text = pushUps.counter.toString()
            } else {
                binding.textViewPushups.text = "0"
            }


        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addToCount(date: LocalDate) {
        val numberText = binding.editCounterNum.text.toString()
        binding.editCounterNum.setText("", TextView.BufferType.SPANNABLE)
        if (numberText.isBlank()) {

            return
        }
        val number = numberText.toInt()
        if (pushUpsItem == null) {
            viewModel.insertCounter(number, date)
        } else {
            viewModel.update(pushUpsItem!!, number)
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        val date = DateServicesData()
        changingDay(date.date)
        binding.date = date
        /*viewModel.getAll().asLiveData().observe(viewLifecycleOwner) { xd ->

        }*/

        binding.buttonNext.setOnClickListener {
            date.addDay()
            changingDay(date.date)
        }
        binding.buttonPrev.setOnClickListener {
            date.minusDay()
            changingDay(date.date)
        }
        binding.floatingActionButton.setOnClickListener {
            addToCount(date.date)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}