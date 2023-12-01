package com.example.eventbushelpersample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.checkdebugtoolsample.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.ds.helper.EventBusHelper

class Event

class MainActivity : AppCompatActivity() {

    private val eventBusHelperResumeOnly by lazy {
        EventBusHelper(Event::class.java)
    }

    private val eventBusHelper by lazy {
        EventBusHelper(Event::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val text: TextView = findViewById(R.id.text)

        CoroutineScope(Dispatchers.Default).launch {
            delay(2000)
            Log.d("EventBusHelper", "publish event thread=${Thread.currentThread().name}")
            EventBusHelper.publish(Event())
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                eventBusHelperResumeOnly.start({
                    Log.d(
                        "EventBusHelper",
                        "received event on resume only thread=${Thread.currentThread().name}"
                    )
                    text.text = "${text.text}\nEvent Received(resumed only)"
                }, null)
            }
        }

        eventBusHelper.start({
            Log.d("EventBusHelper", "received event thread=${Thread.currentThread().name}")
            text.text = "${text.text}\nEvent Received"
        }, lifecycle)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("EventBusHelper", "onDestroy()")
    }
}