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

class AddCalendarDialogFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.insert_calendar_fragment, container)

        view.findViewById<Button>(R.id.saveCalendar).setOnClickListener {
            val calendarName = calendarNameEditText.text.toString()
            val calendarUrl = calendarUrlEditText.text.toString()
            val calendarForm = CalendarForm(calendarName, calendarUrl)
            val formObserver = Observer<CalendarForm> {
                calendarNameEditText.setText(it.calendarName)
                calendarUrlEditText.setText(it.calendarUrl)
                println("inside observer of AddCalendarDialogFragment")
            }
            (activity as MainActivity).viewModelImpl.calendarForm.observe(this, formObserver)
            (activity as MainActivity).viewModelImpl.calendarForm.value = calendarForm
            (activity as MainActivity).viewModelImpl.saveNewCalendarFromLiveData()
        }

        view.findViewById<Button>(R.id.cancelCreating).setOnClickListener {
            dismiss()
        }
        return view
    }
}