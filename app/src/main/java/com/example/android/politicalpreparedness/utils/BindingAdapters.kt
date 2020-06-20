package com.example.android.politicalpreparedness.utils

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Election
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
                .load(imgUri)
                .apply(RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image))
                .into(imgView)
    }
}

@BindingAdapter("date")
fun bindDate(textView: TextView,date: Date?) {
    val calendar = Calendar.getInstance()
    date?.let {
        calendar.time = it
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT,Locale.getDefault())
        textView.text = dateFormat.format(calendar.time)
    }
}

@BindingAdapter("elections")
fun bindElections(recyclerView: RecyclerView,elections:LiveData<List<Election>>?) {
    elections?.value?.let { electionsList ->
        (recyclerView.adapter as ElectionListAdapter).apply {
            this.submitList(electionsList)
        }
    }
}

@BindingAdapter("status")
fun bindStatusImage(statusImage: ImageView,electionsApiStatus: ElectionsApiStatus?) {
    when(electionsApiStatus) {
        ElectionsApiStatus.LOADING -> {
            Log.d("LOADING", "bindStatusImage: Image is LOADING")
            statusImage.visibility = View.VISIBLE
            statusImage.setImageResource(R.drawable.loading_animation)
        }

        ElectionsApiStatus.ERROR -> {
            statusImage.visibility = View.VISIBLE
            statusImage.setImageResource(R.drawable.ic_connection_error)
        }
        ElectionsApiStatus.DONE -> statusImage.visibility = View.GONE
    }

}