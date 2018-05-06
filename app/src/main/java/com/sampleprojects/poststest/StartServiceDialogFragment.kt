package com.sampleprojects.poststest

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import android.content.Intent
import com.sampleprojects.poststest.services.AutoPopulateDBService


class StartServiceDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(R.string.dialog_should_start_autoadd_service)
                .setPositiveButton(R.string.start, { dialog, id ->
                    val i = Intent(context, AutoPopulateDBService::class.java)
                    i.action = AutoPopulateDBService.STARTFOREGROUND_ACTION
                    context.startService(i)
                })
                .setNegativeButton(R.string.cancel, { dialog, id ->
                    // NOOP User cancelled the dialog
                    dismiss()
                })
        // Create the AlertDialog object and return it
        return builder.create()
    }
}