package io.github.maksymilianrozanski.icalreader

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import io.github.maksymilianrozanski.icalreader.data.CalendarEvent
import java.text.SimpleDateFormat
import java.util.*

class EventsAdapter(private val context: Context, var list: MutableList<CalendarEvent>) :
    androidx.recyclerview.widget.RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(partent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.event_list_row, partent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViews(list[position])
    }

    fun setData(newList: MutableList<CalendarEvent>) {
        this.list = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        private val simpleDateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        var eventTitle = itemView.findViewById(R.id.eventTitleTextView) as TextView
        var eventDateStart = itemView.findViewById(R.id.eventDateStartTextView) as TextView
        var eventDateEnd = itemView.findViewById(R.id.eventDateEndTextView) as TextView
        var eventDescription = itemView.findViewById(R.id.eventDescriptionTextView) as TextView
        var eventLocation = itemView.findViewById(R.id.eventLocationTextView) as TextView

        fun bindViews(event: CalendarEvent) {
            eventTitle.text = event.title
            eventDateStart.text = simpleDateFormatter.format(event.dateStart)
            eventDateEnd.text = simpleDateFormatter.format(event.dateEnd)
            eventDescription.text = event.description
            eventLocation.text = event.location
        }
    }
}

fun getPositionOfFirstNotFinishedEvent(calendar: Calendar, list: List<CalendarEvent>): Int {
    val currentTime = calendar.time
    for (index in list.indices) {
        if (list[index].dateEnd > currentTime) {
            return index
        }
    }
    return list.size - 1
}