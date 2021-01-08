package com.example.corotientexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis


class Main3Activity : AppCompatActivity() {
    private val RESULT_1 = "Result #1"
    private val RESULT_2 = "Result #2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            setNewText("Clicked!")
            fakeApiRequest()
        }
    }

    private fun fakeApiRequest() {
        val startTime = System.currentTimeMillis()
        val parentJob = CoroutineScope(IO).launch {
            val job1 = launch {
                val time1 = measureTimeMillis {
                    println("debug: launching job1 in thread: ${Thread.currentThread().name}")
                    val result1 = getResult1FromApi()
                    setTextOnMainThread("Got $result1")
                }
                println("debug: completed job1 in $time1 ms.")
            }

//            job1.join() //this will make job will be sequentially launching that will not launching in parallel


            val job2 = launch {
                val time2 = measureTimeMillis {
                    println("debug: launching job2 in thread: ${Thread.currentThread().name}")
                    val result2 = getResult2FromApi()
                    setTextOnMainThread("Got $result2")
                }
                println("debug: completed job2 in $time2 ms.")
            }

        }
        parentJob.invokeOnCompletion {
            println("debug: total elapsed time: ${System.currentTimeMillis() - startTime}")
        }

    }

    private suspend fun setTextOnMainThread(input: String) {
        withContext(Main) {
            setNewText(input)
        }
    }

    private fun setNewText(input: String) {
        val newText = text.text.toString() + "\n$input"
        text.text = newText
    }

    //suspend will mark this function as something that can be asynchronous.
    private suspend fun getResult1FromApi(): String {
        delay(1000)
        return RESULT_1
    }

    private suspend fun getResult2FromApi(): String {
        delay(1000)
        return RESULT_2
    }


}