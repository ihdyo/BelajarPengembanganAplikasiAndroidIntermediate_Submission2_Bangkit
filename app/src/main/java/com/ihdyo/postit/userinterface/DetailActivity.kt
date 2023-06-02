package com.ihdyo.postit.userinterface

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ihdyo.postit.R
import com.ihdyo.postit.adapter.StoryListAdapter
import com.ihdyo.postit.database.ListStoryDetail
import com.ihdyo.postit.databinding.ActivityDetailBinding
import com.ihdyo.postit.helper.LocationConverter

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story = intent.getParcelableExtra<ListStoryDetail>(EXTRA_STORY) as ListStoryDetail
        setStory(story)

        supportActionBar?.title = getString(R.string.detail_title, story.name)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    private fun setStory(story: ListStoryDetail) {
        binding.apply {
            tvDetailName.text = story.name
            tvDetailDesc.text = story.description
            tvDetailDate.text = StoryListAdapter.formatDateToString(story.createdAt.toString())
        }

        binding.tvAddress.text = LocationConverter.getStringAddress(
            LocationConverter.toLatlng(story.lat, story.lon),
            this
        )

        Glide.with(this)
            .load(story.photoUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .into(binding.imageDetailStory)
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}