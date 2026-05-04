package com.example.linkflow

import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linkflow.data.AppDatabase
import com.example.linkflow.schedule.*
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ScheduleViewModel
    private lateinit var adapter: ScheduleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        val addButton = findViewById<Button>(R.id.addButton)
        val inputText = findViewById<EditText>(R.id.inputText)
        val inputTime = findViewById<EditText>(R.id.inputTime)
        val inputUrl = findViewById<EditText>(R.id.inputUrl)   // ⭐ 新增
        val confirmButton = findViewById<Button>(R.id.confirmButton)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val dao = AppDatabase.getDatabase(this).scheduleDao()
        val repository = ScheduleRepository(dao)
        val factory = ScheduleViewModelFactory(repository, application)
        viewModel = ViewModelProvider(this, factory)[ScheduleViewModel::class.java]

        adapter = ScheduleAdapter { schedule ->
            AlertDialog.Builder(this)
                .setTitle("删除日程")
                .setMessage("确定删除这个日程吗？")
                .setPositiveButton("删除") { _, _ ->
                    viewModel.deleteSchedule(schedule)
                }
                .setNegativeButton("取消", null)
                .show()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.allSchedules.collect {
                adapter.submitList(it)
            }
        }

        var selectedTimeMillis: Long = 0

        inputTime.setOnClickListener {
            val calendar = Calendar.getInstance()

            TimePickerDialog(
                this,
                { _, hour: Int, minute: Int ->
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)

                    selectedTimeMillis = calendar.timeInMillis
                    inputTime.setText(String.format("%02d:%02d", hour, minute))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        addButton.setOnClickListener {
            addButton.visibility = View.GONE
            inputText.visibility = View.VISIBLE
            inputTime.visibility = View.VISIBLE
            inputUrl.visibility = View.VISIBLE   // ⭐
            inputUrl.visibility = View.VISIBLE
            confirmButton.visibility = View.VISIBLE
        }

        confirmButton.setOnClickListener {

            val content = inputText.text.toString()
            val url = inputUrl.text.toString()

            if (content.isBlank()) return@setOnClickListener

            if (!url.startsWith("http")) {
                Toast.makeText(this, "请输入正确网址", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedTimeMillis <= System.currentTimeMillis()) {
                selectedTimeMillis = System.currentTimeMillis() + 5000
            }

            viewModel.addSchedule(content, selectedTimeMillis, url)

            inputText.setText("")
            inputTime.setText("")
            inputUrl.setText("")
            selectedTimeMillis = 0

            inputText.visibility = View.GONE
            inputTime.visibility = View.GONE
            inputUrl.visibility = View.GONE
            confirmButton.visibility = View.GONE
            addButton.visibility = View.VISIBLE
        }
    }
}