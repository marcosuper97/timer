package com.example.timer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.timer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mainThreadHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mainThreadHandler = Handler(Looper.getMainLooper())

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener() {
            if (binding.editText.text.isEmpty()) {
                Toast.makeText(this, "Введите время", LENGTH_LONG).show()
            } else {
                val seconds: Int = binding.editText.text.toString().toInt()
                mainThreadHandler?.post(startTimer(seconds))
                binding.startButton.isEnabled = false
            }

        }

        binding.stop.setOnClickListener() {
            mainThreadHandler!!.postAtFrontOfQueue(stopTimer())
        }
    }

    companion object {
        private const val ONE_SECOND = 1000L
    }

    private fun startTimer(time: Int): Runnable {
        return Runnable {
            var leftTime = time
            if (leftTime > 0) {
                val minutes = leftTime / 60
                val seconds = leftTime % 60
                binding.timer.text = "%02d:%02d".format(minutes, seconds)
                leftTime -= 1
                mainThreadHandler?.postDelayed(startTimer(leftTime), ONE_SECOND)
            } else {
                binding.timer.text = "00:00"
                Toast.makeText(this, "Время вышло!", LENGTH_LONG).show()
                binding.startButton.isEnabled = true
            }
        }
    }

    private fun stopTimer(): Runnable {
        return Runnable {
            binding.timer.text = "СОСАЛ?"
            Toast.makeText(this, "Таймер сброшен", LENGTH_LONG).show()
            binding.startButton.isEnabled = true
            mainThreadHandler!!.removeCallbacksAndMessages(null)
        }
    }

}
