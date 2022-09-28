package com.jakubu9333.pushupcounter

import android.R
import android.app.PendingIntent

import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import kotlinx.coroutines.*
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
    fun addToCount(date: LocalDate, number:Int): Boolean {

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


            val number = numberInEntry()
            if (number>0) {
                addToCount(date.date,number)
                notifyInTime(30)
            }
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

    private var runningNotificationJob:Job= Job()
    @RequiresApi(Build.VERSION_CODES.M)
    private fun notifyInTime(minutes:Int=30, seconds: Int =0) {
        val scope = MainScope()

        //cancelation of past notificatons

        runningNotificationJob.cancel()



        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = this.context?.let {
            NotificationCompat.Builder(it, "my_channel_01")
                .setSmallIcon(R.drawable.arrow_down_float)
                .setContentTitle("Push ups")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)

        }
        val maxTime =60*minutes+seconds
        var timeCurrent = 0
        if (builder != null) {
            context?.let {
                NotificationManagerCompat.from(it).apply {
                    // Issue the initial notification with zero progress
                    builder.setProgress(maxTime, timeCurrent, false)
                    notify(0, builder.build())
                    runningNotificationJob=Job()
                    scope.launch(runningNotificationJob) {
                       repeat(maxTime){index->
                            timeCurrent += 1
                            builder.setProgress(maxTime, timeCurrent, false)
                            val time = (maxTime - index)
                            val stringTime = (" ${time / 60} minutes ${time % 60} seconds")
                            builder.setContentText(stringTime)
                            notify(0, builder.build())
                            delay(1000L)
                        }
                        builder.setContentText("Time to do push ups")
                            .setProgress(0, 0, false)
                        notify(0, builder.build())
                    }
                }
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}