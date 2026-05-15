package com.example.linkflow

import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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

        // 以后 XML 里新增
        val appGroup = findViewById<RadioGroup>(R.id.appGroup)
        val extraInput = findViewById<EditText>(R.id.extraInput)

        val confirmButton = findViewById<Button>(R.id.confirmButton)

        val reminderGroup = findViewById<RadioGroup>(R.id.reminderTypeGroup)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val dao = AppDatabase.getDatabase(this).scheduleDao()
        val repository = ScheduleRepository(dao)
        val factory = ScheduleViewModelFactory(repository, application)
        viewModel = ViewModelProvider(this, factory)[ScheduleViewModel::class.java]

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        currentSelectedDate = sdf.format(Date())
        viewModel.setSelectedDate(currentSelectedDate)

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
            viewModel.schedulesForSelectedDate.collect {
                adapter.submitList(it)
            }
        }

        calendarView.setOnDateChangeListener { _, year, month, day ->
            currentSelectedDate =
                String.format("%d-%02d-%02d", year, month + 1, day)

            viewModel.setSelectedDate(currentSelectedDate)

            inputContainer.visibility = View.GONE
            addButton.visibility = View.VISIBLE
        }

        var selectedTimeMillis = 0L

        var selectedReminderType: ReminderType =
            ReminderType.STATIC

        var selectedAppType: AppType =
            AppType.WECHAT

        reminderGroup.setOnCheckedChangeListener { _, checkedId ->

            selectedReminderType = when (checkedId) {

                R.id.dynamicBtn -> {
                    appGroup.visibility = View.VISIBLE
                    ReminderType.DYNAMIC
                }

                else -> {
                    appGroup.visibility = View.GONE
                    extraInput.visibility = View.GONE
                    ReminderType.STATIC
                }
            }
        }

        // 以后 XML 对齐后启用
        appGroup.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {

                R.id.wechatBtn -> {
                    selectedAppType = AppType.WECHAT
                    extraInput.visibility = View.GONE
                }

                R.id.biliBtn -> {
                    selectedAppType = AppType.BILIBILI
                    extraInput.visibility = View.GONE
                }

                R.id.zhihuBtn -> {
                    selectedAppType = AppType.ZHIHU
                    extraInput.visibility = View.GONE
                }

                R.id.alipayBtn -> {
                    selectedAppType = AppType.ALIPAY
                    extraInput.visibility = View.GONE
                }

                R.id.tencentBtn -> {
                    selectedAppType = AppType.TENCENT_MEETING

                    extraInput.visibility = View.VISIBLE
                    extraInput.hint = "请输入会议号（可选）"
                }

                R.id.browserBtn -> {
                    selectedAppType = AppType.BROWSER

                    extraInput.visibility = View.VISIBLE
                    extraInput.hint = "请输入网址（可选）"
                }
            }
        }

        inputTime.setOnClickListener {

            val calendar = Calendar.getInstance()

            val parts = currentSelectedDate.split("-")

            calendar.set(Calendar.YEAR, parts[0].toInt())
            calendar.set(Calendar.MONTH, parts[1].toInt() - 1)
            calendar.set(Calendar.DAY_OF_MONTH, parts[2].toInt())

            TimePickerDialog(
                this,
                { _, hour, minute ->

                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)

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

        addButton.setOnClickListener {

            inputContainer.visibility = View.VISIBLE
            addButton.visibility = View.GONE

            selectedTimeMillis = 0L

            reminderGroup.check(R.id.staticBtn)

            selectedReminderType = ReminderType.STATIC
            appGroup.visibility = View.GONE
            selectedAppType = AppType.WECHAT

            extraInput.setText("")
        }

        confirmButton.setOnClickListener {

            val content = inputText.text.toString()

            val extraData =
                extraInput.text.toString()

            if (content.isBlank()) {
                return@setOnClickListener
            }

            if (
                selectedAppType == AppType.BROWSER &&
                extraData.isNotEmpty() &&
                !extraData.startsWith("http")
            ) {

                Toast.makeText(
                    this,
                    "请输入正确网址",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (selectedTimeMillis == 0L) {

                selectedTimeMillis =
                    System.currentTimeMillis() + 5000
            }

            viewModel.addSchedule(
                content,
                selectedTimeMillis,
                currentSelectedDate,
                selectedAppType,
                extraData,
                selectedReminderType
            )

            inputText.setText("")
            inputTime.setText("")
            extraInput.setText("")

            selectedTimeMillis = 0L

            inputContainer.visibility = View.GONE
            addButton.visibility = View.VISIBLE
        }
    }
}