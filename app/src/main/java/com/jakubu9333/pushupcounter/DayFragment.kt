package com.jakubu9333.pushupcounter



import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import com.jakubu9333.pushupcounter.database.PushUps
import com.jakubu9333.pushupcounter.databinding.FragmentDayBinding
import com.jakubu9333.pushupcounter.viewmodels.PushUpsViewModel
import com.jakubu9333.pushupcounter.viewmodels.PushUpsViewModelFactory
import java.time.LocalDate


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class DayFragment : Fragment() {
    private lateinit var  fragmentContext: Context
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
    fun addToCount(date: LocalDate, number: Int): Boolean {

        if (pushUpsItem == null) {
            viewModel.insertCounter(number, date)
        } else {
            viewModel.update(pushUpsItem!!, number)
        }

        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // addSwipeControls()
        binding.lifecycleOwner = viewLifecycleOwner
        val date = DateServicesData()
        changingDay(date.date)
        binding.date = date
        val sp = PreferenceManager.getDefaultSharedPreferences(fragmentContext)

        binding.buttonNext.setOnClickListener {
            date.addDay()
            changingDay(date.date)
        }
        binding.buttonPrev.setOnClickListener {
            date.minusDay()
            changingDay(date.date)
        }
        binding.floatingActionButton.setOnClickListener {

            val notify = sp.getBoolean("notify", true)
            val minutes=sp.getInt("minutes",30)
            val seconds=sp.getInt("seconds",0)
            val number = numberInEntry()
            if (number > 0) {
                addToCount(date.date, number)
                if (notify) {
                    notifyClass.notifyInTime(minutes, seconds)
                }
            }
        }

    }

    /*private fun addSwipeControls() {
        binding.dayFragmentLayout.setOnTouchListener(){

        }
    }*/

    private lateinit var notifyClass:Notification

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.fragmentContext = context
        if (!this::notifyClass.isInitialized){
            this.notifyClass=Notification(context)
        }

    }




    //returns -1 for bad format
    private fun numberInEntry(): Int {
        val numberText = binding.editCounterNum.text.toString()
        if (numberText.isBlank()) {

            return -1
        }

        binding.editCounterNum.setText("", TextView.BufferType.SPANNABLE)
        return numberText.toInt()
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}