package io.github.maksymilianrozanski.icalreader

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.github.maksymilianrozanski.icalreader.viewmodel.ViewModelImpl
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModelImpl: ViewModelImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModelImpl = ViewModelProviders.of(this).get(ViewModelImpl::class.java)

        val helloWorldObserver = Observer<String> { newText ->
            HelloWorldTextView.text = newText
        }

        viewModelImpl.helloWorldData.observe(this, helloWorldObserver)

        setContentView(R.layout.activity_main)
    }
}
