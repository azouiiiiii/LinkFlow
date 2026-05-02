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

        // 🔔 通知权限
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }

        val addButton = findViewById<Button>(R.id.addButton)
        val inputText = findViewById<EditText>(R.id.inputText)
        val inputTime = findViewById<EditText>(R.id.inputTime)
        val confirmButton = findViewById<Button>(R.id.confirmButton)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        // ViewModel 初始化
        val dao = AppDatabase.getDatabase(this).scheduleDao()
        val repository = ScheduleRepository(dao)
        val factory = ScheduleViewModelFactory(repository, application)
        viewModel = ViewModelProvider(this, factory)[ScheduleViewModel::class.java]

        // ⭐ RecyclerView（带删除功能）
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

        // 🔥 监听数据库变化（自动刷新列表）
        lifecycleScope.launch {
            viewModel.allSchedules.collect {
                adapter.submitList(it)
            }
        }

        var selectedTimeMillis: Long = 0

        // 🕒 时间选择
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

        // ➕ 显示输入框
        addButton.setOnClickListener {
            addButton.visibility = View.GONE
            inputText.visibility = View.VISIBLE
            inputTime.visibility = View.VISIBLE
            confirmButton.visibility = View.VISIBLE
        }

        // ✅ 提交日程
        confirmButton.setOnClickListener {

            val content = inputText.text.toString()

            if (content.isBlank()) return@setOnClickListener

            // 防止选择过去时间
            if (selectedTimeMillis <= System.currentTimeMillis()) {
                selectedTimeMillis = System.currentTimeMillis() + 5000
            }

            viewModel.addSchedule(content, selectedTimeMillis)

            // 🔄 重置 UI
            inputText.setText("")
            inputTime.setText("")
            selectedTimeMillis = 0

            inputText.visibility = View.GONE
            inputTime.visibility = View.GONE
            confirmButton.visibility = View.GONE
            addButton.visibility = View.VISIBLE
        }
    }
}