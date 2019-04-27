package io.github.maksymilianrozanski.icalreader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import io.github.maksymilianrozanski.icalreader.data.CalendarForm
import kotlinx.android.synthetic.main.insert_calendar_fragment.*
import kotlinx.android.synthetic.main.insert_calendar_fragment.view.*

class AddCalendarDialogFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.insert_calendar_fragment, container)

        val calendarForm = CalendarForm(
            view.calendarNameEditText.text.toString(),
            view.calendarUrlEditText.text.toString()
        )

        val formObserver = Observer<CalendarForm> {
            calendarNameEditText.setText(it.calendarName)
            calendarNameEditText.error = it.nameError
            calendarUrlEditText.setText(it.calendarUrl)
            calendarUrlEditText.error = it.urlError
            println("inside observer of AddCalendarDialogFragment")
        }
        (activity as MainActivity).viewModel.calendarForm.observe(this, formObserver)

        view.findViewById<Button>(R.id.saveCalendar).setOnClickListener {
            calendarForm.calendarName = calendarNameEditText.text.toString()
            calendarForm.calendarUrl = calendarUrlEditText.text.toString()
            (activity as MainActivity).viewModel.calendarForm.value = calendarForm
            if (calendarForm.nameError == null && calendarForm.urlError == null){
                (activity as MainActivity).viewModel.saveNewCalendarFromLiveData()
            }
        }

        view.findViewById<Button>(R.id.cancelCreating).setOnClickListener {
            dismiss()
        }
        return view
    }
}