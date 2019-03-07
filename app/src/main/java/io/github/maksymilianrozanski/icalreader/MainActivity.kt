package io.github.maksymilianrozanski.icalreader

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: PostListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(PostListViewModel::class.java)

        val helloWorldObserver = Observer<String> { newText ->
            HelloWorldTextView.text = newText
        }

        viewModel.helloWorldData.observe(this, helloWorldObserver)

        setContentView(R.layout.activity_main)
    }
}
