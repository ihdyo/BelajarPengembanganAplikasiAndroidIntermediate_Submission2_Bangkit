package com.ihdyo.postit.ui

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ihdyo.postit.R
import com.ihdyo.postit.adapter.StoryAdapter
import com.ihdyo.postit.data.db.DataDetail
import com.ihdyo.postit.databinding.ActivityDetailBinding
import com.ihdyo.postit.helper.LocationConverter

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<DataDetail>(EXTRA_STORY)
        if (story != null) {
            setStory(story)
        } else {
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setStory(story: DataDetail) {
        binding.apply {
            tvDetailName.text = story.name
            tvDetailDesc.text = story.description
            tvDetailDate.text = StoryAdapter.formatDateToString(story.createdAt.toString())
            tvAddress.text = LocationConverter.getStringAddress(
                LocationConverter.toLatlng(story.lat, story.lon),
                this@DetailActivity
            )

            Glide.with(this@DetailActivity)
                .load(story.photoUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(imageDetailStory)
        }
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}