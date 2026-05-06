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
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ScheduleViewModel
    private lateinit var adapter: ScheduleAdapter

    // 用于记录当前日历选中的日期字符串 (格式: yyyy-MM-dd)
    private var currentSelectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val addButton = findViewById<Button>(R.id.addButton)
        val inputContainer = findViewById<LinearLayout>(R.id.inputContainer)
        val inputText = findViewById<EditText>(R.id.inputText)
        val inputTime = findViewById<EditText>(R.id.inputTime)
        val inputUrl = findViewById<EditText>(R.id.inputUrl)
        val confirmButton = findViewById<Button>(R.id.confirmButton)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        // 初始化 ViewModel
        val dao = AppDatabase.getDatabase(this).scheduleDao()
        val repository = ScheduleRepository(dao)
        val factory = ScheduleViewModelFactory(repository, application)
        viewModel = ViewModelProvider(this, factory)[ScheduleViewModel::class.java]

        // 1. 初始化当前日期为今天，并告知 ViewModel
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        currentSelectedDate = sdf.format(Date())
        viewModel.setSelectedDate(currentSelectedDate)

        // 设置适配器
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
            viewModel.schedulesForSelectedDate.collect { filteredList ->
                adapter.submitList(filteredList)
            }
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // 月份从 0 开始计数
            currentSelectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)

            // 通知 ViewModel 日期变了，UI 会通过上面的 collect 自动刷新
            viewModel.setSelectedDate(currentSelectedDate)

            // 切换日期时，隐藏输入框
            inputContainer.visibility = View.GONE
            addButton.visibility = View.VISIBLE
        }

        var selectedTimeMillis: Long = 0

        inputTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            // 确保时间戳的日期部分与当前日历选中的一致
            val dateParts = currentSelectedDate.split("-")
            calendar.set(Calendar.YEAR, dateParts[0].toInt())
            calendar.set(Calendar.MONTH, dateParts[1].toInt() - 1)
            calendar.set(Calendar.DAY_OF_MONTH, dateParts[2].toInt())

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
            inputContainer.visibility = View.VISIBLE
        }

        confirmButton.setOnClickListener {
            val content = inputText.text.toString()
            val url = inputUrl.text.toString()

            if (content.isBlank()) return@setOnClickListener

            if (url.isNotEmpty() && !url.startsWith("http")) {
                Toast.makeText(this, "请输入正确网址", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 如果没选时间，默认使用当前时刻（但日期必须对齐）
            if (selectedTimeMillis == 0L) {
                selectedTimeMillis = System.currentTimeMillis() + 5000
            }

            // 调用 ViewModel 添加日程
            viewModel.addSchedule(content, selectedTimeMillis, url, currentSelectedDate)

            // 重置 UI
            inputText.setText("")
            inputTime.setText("")
            inputUrl.setText("")
            selectedTimeMillis = 0 // 重置选中的时间戳
            inputContainer.visibility = View.GONE
            addButton.visibility = View.VISIBLE
        }
    }
}