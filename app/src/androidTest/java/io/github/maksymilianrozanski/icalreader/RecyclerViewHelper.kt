package io.github.maksymilianrozanski.icalreader

import androidx.annotation.NonNull
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import org.hamcrest.Description
import org.hamcrest.Matcher

fun atPosition(position: Int, @NonNull itemMatcher: Matcher<View>): Matcher<View> {
    checkNotNull(itemMatcher)
    return object : BoundedMatcher<View, androidx.recyclerview.widget.RecyclerView>(androidx.recyclerview.widget.RecyclerView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("has item at position $position: ")
            itemMatcher.describeTo(description)
        }

        override fun matchesSafely(view: androidx.recyclerview.widget.RecyclerView): Boolean {
            val viewHolder = view.findViewHolderForAdapterPosition(position)
                ?: // has no item on such position
                return false
            return itemMatcher.matches(viewHolder.itemView)
        }
    }
}