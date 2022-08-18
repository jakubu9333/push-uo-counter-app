package com.jakubu9333.pushupcounter

import android.view.View
import android.widget.Button

import androidx.databinding.BindingAdapter


/**
 *
 * @author Jakub Uhlarik
 */
@BindingAdapter("unseen")
fun bindButton(
    button: Button,
    invisible: Boolean
) {
    if (invisible) {
        button.visibility=View.INVISIBLE
    } else {
        button.visibility=View.VISIBLE
    }
}

@BindingAdapter("seen")
fun bindEntryText(
    view: View,
    visible: Boolean
) {
    if (visible) {
        view.visibility=View.VISIBLE
    } else {
        view.visibility=View.INVISIBLE
    }
}
