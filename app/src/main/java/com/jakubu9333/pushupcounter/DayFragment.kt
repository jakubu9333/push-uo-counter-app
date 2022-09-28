package com.jakubu9333.pushupcounter

import android.R
import android.app.PendingIntent

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
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
    fun addToCount(date: LocalDate): Boolean {
        val numberText = binding.editCounterNum.text.toString()
        binding.editCounterNum.setText("", TextView.BufferType.SPANNABLE)
        if (numberText.isBlank()) {

            return false
        }
        val number = numberText.toInt()
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


            val notblank = addToCount(date.date)
            if (notblank) {
                notifyInTime(30 * 60)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun notifyInTime(i: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = this.context?.let {
            NotificationCompat.Builder(it, "my_channel_01")
                .setSmallIcon(R.drawable.arrow_up_float)
                .setContentTitle("Push ups")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
               //#TODO old notifications delete
        }

        val PROGRESS_MAX = i
        var PROGRESS_CURRENT = 0
        if (builder != null) {
            context?.let {
                NotificationManagerCompat.from(it).apply {
                    // Issue the initial notification with zero progress
                    builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false)

                    notify(0, builder.build())
                    object : CountDownTimer(i * 1000L, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            PROGRESS_CURRENT += 1
                            builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false)
                            val time = millisUntilFinished / 1000
                            val stringTime = (" ${time / 60} minutes ${time % 60} seconds")
                            builder.setContentText(stringTime)
                            notify(0, builder.build())

                        }

                        override fun onFinish() {
                            builder.setContentText("Time to do push ups")
                                .setProgress(0, 0, false)
                            notify(0, builder.build())
                        }
                    }.start()
                }
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}