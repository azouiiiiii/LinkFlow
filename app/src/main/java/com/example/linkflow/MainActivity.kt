// 所有ui交互都在这，注意是 交互 ，也就是书写逻辑的，设计ui具体形态在res/layout/activity_main.xml

package com.example.linkflow

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room  // 数据库类（Android studio封装）
import android.util.Log  // 日志类
import com.example.linkflow.data.AppDatabase
import com.example.linkflow.schedule.Schedule
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import android.app.TimePickerDialog
import java.util.Calendar

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import com.example.linkflow.schedule.ScheduleViewModel
import com.example.linkflow.schedule.ScheduleViewModelFactory
import com.example.linkflow.schedule.ScheduleRepository

lateinit var db: AppDatabase
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ScheduleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val addButton = findViewById<Button>(R.id.addButton)
        val inputText = findViewById<EditText>(R.id.inputText)
        val inputTime = findViewById<EditText>(R.id.inputTime)
        val confirmButton = findViewById<Button>(R.id.confirmButton)
        val resultText = findViewById<TextView>(R.id.resultText)

        // ViewModel（通过Factory）
        val dao = AppDatabase.getDatabase(this).scheduleDao()
        val repository = ScheduleRepository(dao)
        val factory = ScheduleViewModelFactory(repository, application)

        viewModel = ViewModelProvider(this, factory)[ScheduleViewModel::class.java]

        var selectedTimeMillis: Long = 0

        // 🕒 时间选择器（只在UI层处理）
        inputTime.setOnClickListener {

            val calendar = Calendar.getInstance()

            TimePickerDialog(
                this,
                { _, hour, minute ->

                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)

                    selectedTimeMillis = calendar.timeInMillis

                    inputTime.setText(
                        String.format("%02d:%02d", hour, minute)
                    )
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        // 显示输入
        addButton.setOnClickListener {
            addButton.visibility = View.GONE
            inputText.visibility = View.VISIBLE
            inputTime.visibility = View.VISIBLE
            confirmButton.visibility = View.VISIBLE
        }

        // 提交
        confirmButton.setOnClickListener {

            val content = inputText.text.toString()

            viewModel.addSchedule(
                content = content,
                triggerTime = selectedTimeMillis
            )

            resultText.text = content

            inputText.visibility = View.GONE
            inputTime.visibility = View.GONE
            confirmButton.visibility = View.GONE
        }
    }
}

