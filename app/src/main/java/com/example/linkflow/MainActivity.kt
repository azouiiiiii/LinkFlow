package com.example.linkflow

import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.linkflow.data.AppDatabase
import com.example.linkflow.nlp.KeywordMatcher
import com.example.linkflow.schedule.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ScheduleViewModel
    private lateinit var adapter: ScheduleAdapter

    private var currentSelectedDate: String = ""
    private var editingSchedule: Schedule? = null
    private var selectedTimeMillis = 0L

    private var selectedReminderType = ReminderType.STATIC

    private var selectedAppType = AppType.WECHAT

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
        val appGroup = findViewById<RadioGroup>(R.id.appGroup)
        val extraInput = findViewById<EditText>(R.id.extraInput)
        val confirmButton = findViewById<Button>(R.id.confirmButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        val reminderGroup = findViewById<RadioGroup>(R.id.reminderTypeGroup)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val dao = AppDatabase.getDatabase(this).scheduleDao()
        val repository = ScheduleRepository(dao)
        val factory = ScheduleViewModelFactory(repository, application)
        viewModel = ViewModelProvider(this, factory)[ScheduleViewModel::class.java]

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        currentSelectedDate = sdf.format(Date())
        viewModel.setSelectedDate(currentSelectedDate)

        adapter = ScheduleAdapter(

            onItemClick = { schedule ->

                populateFormForEdit(
                    schedule,
                    inputText,
                    inputTime,
                    reminderGroup,
                    appGroup,
                    extraInput,
                    confirmButton
                )
            },

            onItemLongClick = { schedule ->
                AlertDialog.Builder(this)
                    .setTitle("删除日程")
                    .setMessage("确定删除这个日程吗？")
                    .setPositiveButton("删除") { _, _ ->
                        viewModel.deleteSchedule(schedule)
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
        )

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
            resetForm(inputContainer, addButton, confirmButton, inputText, inputTime, reminderGroup, appGroup, extraInput)
        }

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

        appGroup.setOnCheckedChangeListener { _, checkedId ->
            val appType = when (checkedId) {
                R.id.wechatBtn -> AppType.WECHAT
                R.id.biliBtn -> AppType.BILIBILI
                R.id.zhihuBtn -> AppType.ZHIHU
                R.id.alipayBtn -> AppType.ALIPAY
                R.id.tencentBtn -> AppType.TENCENT_MEETING
                R.id.browserBtn -> AppType.BROWSER
                else -> return@setOnCheckedChangeListener
            }
            selectAppInGroup(appGroup, extraInput, appType)
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
                    inputTime.setText(String.format("%02d:%02d", hour, minute))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        inputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (editingSchedule != null) return

                val content = s?.toString() ?: return
                val matchedApp = KeywordMatcher.matchAppType(content)

                if (matchedApp != null) {
                    selectAppInGroup(appGroup, extraInput, matchedApp)
                    reminderGroup.check(R.id.dynamicBtn)
                }
            }
        })

        addButton.setOnClickListener {
            editingSchedule = null
            inputContainer.visibility = View.VISIBLE
            addButton.visibility = View.GONE
            selectedTimeMillis = 0L
            reminderGroup.check(R.id.staticBtn)
            selectedReminderType = ReminderType.STATIC
            appGroup.visibility = View.GONE
            selectedAppType = AppType.WECHAT
            appGroup.check(R.id.wechatBtn)
            extraInput.setText("")
            inputText.setText("")
            inputTime.setText("")
            confirmButton.text = "保存"
        }

        confirmButton.setOnClickListener {
            val content = inputText.text.toString()
            val extraData = extraInput.text.toString()

            if (content.isBlank()) {
                return@setOnClickListener
            }

            if (
                selectedAppType == AppType.BROWSER &&
                extraData.isNotEmpty() &&
                !extraData.startsWith("http://") &&
                !extraData.startsWith("https://")
            ) {
                Toast.makeText(
                    this,
                    "请输入正确网址（以 http:// 或 https:// 开头）",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (selectedTimeMillis == 0L) {
                selectedTimeMillis = System.currentTimeMillis() + 5000
            }

            if (selectedTimeMillis <= System.currentTimeMillis()) {
                Toast.makeText(
                    this,
                    "所选时间已过期，已自动调整为当前时间",
                    Toast.LENGTH_SHORT
                ).show()
                selectedTimeMillis = System.currentTimeMillis() + 5000
            }

            val currentEdit = editingSchedule
            if (currentEdit != null) {
                viewModel.updateSchedule(
                    currentEdit.copy(
                        content = content,
                        triggerTime = selectedTimeMillis,
                        date = currentSelectedDate,
                        appType = selectedAppType,
                        extraData = extraData,
                        reminderType = selectedReminderType
                    )
                )
            } else {
                viewModel.addSchedule(
                    content,
                    selectedTimeMillis,
                    currentSelectedDate,
                    selectedAppType,
                    extraData,
                    selectedReminderType
                )
            }
            resetForm(inputContainer, addButton, confirmButton, inputText, inputTime, reminderGroup, appGroup, extraInput)
        }

        cancelButton.setOnClickListener {

            resetForm(inputContainer, addButton, confirmButton, inputText, inputTime, reminderGroup, appGroup, extraInput)
        }
    }

    private fun selectAppInGroup(appGroup: RadioGroup, extraInput: EditText, appType: AppType) {
        selectedAppType = appType

        val id = when (appType) {
            AppType.WECHAT -> R.id.wechatBtn
            AppType.BILIBILI -> R.id.biliBtn
            AppType.ZHIHU -> R.id.zhihuBtn
            AppType.ALIPAY -> R.id.alipayBtn
            AppType.TENCENT_MEETING -> R.id.tencentBtn
            AppType.BROWSER -> R.id.browserBtn
        }
        appGroup.check(id)

        when (appType) {
            AppType.BILIBILI -> {
                extraInput.visibility = View.VISIBLE
                extraInput.hint = "请输入视频BV号（可选）"
            }
            AppType.ZHIHU -> {
                extraInput.visibility = View.VISIBLE
                extraInput.hint = "请输入问题ID（可选）"
            }
            AppType.TENCENT_MEETING -> {
                extraInput.visibility = View.VISIBLE
                extraInput.hint = "请输入会议号（可选）"
            }
            AppType.BROWSER -> {
                extraInput.visibility = View.VISIBLE
                extraInput.hint = "请输入网址（可选）"
            }
            else -> {
                extraInput.visibility = View.GONE
            }
        }
    }

    private fun populateFormForEdit(
        schedule: Schedule,
        inputText: EditText,
        inputTime: EditText,
        reminderGroup: RadioGroup,
        appGroup: RadioGroup,
        extraInput: EditText,
        confirmButton: Button
    ) {
        editingSchedule = schedule
        selectedTimeMillis = schedule.triggerTime

        inputText.setText(schedule.content)
        inputTime.setText(
            SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(Date(schedule.triggerTime))
        )

        when (schedule.reminderType) {
            ReminderType.DYNAMIC -> {
                reminderGroup.check(R.id.dynamicBtn)
                appGroup.visibility = View.VISIBLE
            }
            ReminderType.STATIC -> {
                reminderGroup.check(R.id.staticBtn)
                appGroup.visibility = View.GONE
            }
        }

        selectAppInGroup(appGroup, extraInput, schedule.appType)

        extraInput.setText(schedule.extraData)
        confirmButton.text = "更新"

        findViewById<LinearLayout>(R.id.inputContainer).visibility = View.VISIBLE
        findViewById<Button>(R.id.addButton).visibility = View.GONE
    }

    private fun resetForm(
        inputContainer: LinearLayout,
        addButton: Button,
        confirmButton: Button,

        inputText: EditText,
        inputTime: EditText,

        reminderGroup: RadioGroup,
        appGroup: RadioGroup,

        extraInput: EditText
    ) {

        editingSchedule = null

        selectedTimeMillis = 0L

        inputContainer.visibility = View.GONE

        addButton.visibility = View.VISIBLE

        confirmButton.text = "保存"

        inputText.setText("")

        inputTime.setText("")

        extraInput.setText("")

        reminderGroup.check(R.id.staticBtn)

        appGroup.check(R.id.wechatBtn)

        appGroup.visibility = View.GONE

        extraInput.visibility = View.GONE

        selectedReminderType = ReminderType.STATIC

        selectedAppType = AppType.WECHAT
    }
}
