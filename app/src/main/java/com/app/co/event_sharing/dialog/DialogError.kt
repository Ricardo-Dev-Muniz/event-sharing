package com.app.co.event_sharing.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.app.co.event_sharing.R

class DialogError(context: Context) {
    private var dialog: AlertDialog
    private var layBtnExit: LinearLayout

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_error, null)

        val builder = AlertDialog.Builder(context)
        builder.setView(view)

        dialog = builder.create()

        view?.setBackgroundResource(android.R.color.transparent)
        builder.create()

        layBtnExit = view.findViewById(R.id.layBtnExit)
        layBtnExit.setOnClickListener {
            dismiss()
        }
    }

    fun show() {
        dialog.show()
    }

    private fun dismiss() {
        dialog.dismiss()
    }
}