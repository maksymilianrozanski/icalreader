package io.github.maksymilianrozanski.icalreader

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class EventsAdapter(private val context: Context, private var list: MutableList<CalendarEvent>) :
    RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var eventTitle = itemView.findViewById(R.id.eventTitleTextView) as TextView
        var eventDate = itemView.findViewById(R.id.eventDateTextView) as TextView

        fun bindViews(event: CalendarEvent) {
            eventTitle.text = event.name
            eventDate.text = event.date

        }
    }
}