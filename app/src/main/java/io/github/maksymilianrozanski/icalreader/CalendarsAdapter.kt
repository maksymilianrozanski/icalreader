package io.github.maksymilianrozanski.icalreader

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import io.github.maksymilianrozanski.icalreader.data.WebCalendar
import io.github.maksymilianrozanski.icalreader.viewmodel.ViewModelInterface

class CalendarsAdapter(
    private val context: Context,
    var list: MutableList<WebCalendar>,
    private val viewModelInterface: ViewModelInterface
) :
    RecyclerView.Adapter<CalendarsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.calendar_list_row, parent, false)
        return ViewHolder(view, viewModelInterface)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViews(list[position])
    }

    fun setData(newList: MutableList<WebCalendar>) {
        this.list = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View, private val viewModelInterface: ViewModelInterface) :
        RecyclerView.ViewHolder(itemView) {
        var calendarNameTextView = itemView.findViewById(R.id.calendarNameTextView) as TextView
        var deleteButton = itemView.findViewById(R.id.deleteCalendarButton) as Button

        fun bindViews(calendar: WebCalendar) {
            calendarNameTextView.text = calendar.calendarName
            calendarNameTextView.setOnClickListener {
                viewModelInterface.requestSavedCalendarData(calendar)
                (itemView.parent.parent.parent as DrawerLayout).closeDrawers()
            }
            deleteButton.setOnClickListener {
                deletionConfirmDialog(context, calendar).show()
            }
        }

        private fun deletionConfirmDialog(context: Context, calendar: WebCalendar): AlertDialog {
            return AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.delete))
                .setMessage(context.getString(R.string.are_you_sure_you_want_to_delete) + " ${calendar.calendarName}?")
                .setPositiveButton(context.getString(R.string.delete)) { _, _ ->
                    run {
                        viewModelInterface.deleteCalendar(calendar)
                        (itemView.parent.parent.parent as DrawerLayout).closeDrawers()
                    }
                }.setNegativeButton(context.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }.create()
        }
    }
}
