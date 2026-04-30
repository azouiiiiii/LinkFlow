# LinkFlow ⏰  
> 一款基于 Android MVVM + WorkManager 的轻量级任务调度与提醒系统

---

## 📌 项目简介

LinkFlow 是一个支持“创建日程 → 定时触发 → 系统通知 → 动作跳转”的 Android 任务调度系统。

核心特点是将 **本地数据存储（Room）** 与 **后台任务调度（WorkManager）** 解耦，实现可靠的延时提醒机制。

---

## 🧠 架构设计（重点）

本项目采用 **MVVM + 分层解耦架构**：


UI 层
└── MainActivity

业务层
└── ViewModel
└── Scheduler（任务调度）

数据层
└── Repository
└── Room Database (Dao / Entity)

后台执行层
└── WorkManager
└── Worker

系统能力层
└── Notification
└── JumpHandler（跳转模块）


---

## 📁 项目结构


com.example.linkflow
│
├── MainActivity.kt # UI交互入口
│
├── core/ # 通用基础能力
│ ├── constants.kt # 常量定义
│ ├── logger.kt # 日志封装
│ └── utils.kt # 工具函数
│
├── data/ # 数据库层
│ └── AppDatabase.kt
│
├── schedule/ # 日程模块（核心业务）
│ ├── Dao.kt
│ ├── Entity.kt
│ ├── Repository.kt
│ ├── ViewModel.kt
│ └── ViewModelFactory.kt
│
├── reminder/ # 定时提醒系统
│ ├── Notification.kt
│ ├── Scheduler.kt
│ └── Worker.kt
│
├── jump/ # 跳转系统（策略模式）
│ ├── JumpManager.kt
│ ├── JumpHandler.kt
│ ├── AppJumpHandler.kt
│ ├── BrowseJumpHandler.kt
│ └── DeeplinkJumpHandler.kt


---

## 🔔 核心功能

### ✔ 日程管理
- 创建日程（内容 + 时间）
- 本地数据库持久化（Room）

### ✔ 定时调度
- WorkManager 延迟执行任务
- Scheduler 统一调度入口

### ✔ 提醒系统
- Android Notification 推送
- 支持点击跳转

### ✔ 跳转系统（扩展设计）
- App 跳转
- Web 跳转
- DeepLink 跳转
- Handler 策略模式解耦

---

## ⚙️ 技术栈

- Kotlin
- MVVM 架构
- Room Database
- WorkManager
- NotificationManager
- Coroutine
- Strategy Pattern（Jump模块）

---

## 🧩 核心流程


用户创建日程
↓
ViewModel 处理业务逻辑
↓
Repository 写入 Room
↓
Scheduler 计算延迟时间
↓
WorkManager 注册任务
↓
Worker 到点执行
↓
读取数据库
↓
Notification 推送提醒
↓
JumpHandler 执行跳转


---

## 🚀 如何运行

### 1. 克隆项目
```bash
git clone https://github.com/yourname/linkflow.git
2. 用 Android Studio 打开
File → Open → 选择项目目录
3. Sync Gradle

等待依赖下载完成

4. 运行项目

点击 ▶ Run

📱 运行环境
Android Studio Hedgehog+
Android SDK 33+
Kotlin 1.9+
Gradle 8+
⚠️ 注意事项
Android 13+ 需开启通知权限（POST_NOTIFICATIONS）
WorkManager 为系统调度，不保证精确秒级执行
模拟器建议使用 Pixel 系列（稳定）
🧱 已完成模块
✔ Schedule（日程系统）
✔ Reminder（调度+通知）
✔ Jump（跳转策略）
✔ Core（工具层）
✔ Data（Room数据库）
🔥 后续扩展方向
📌 UI 日程列表（RecyclerView）
📌 任务状态系统（pending / done）
📌 重复任务支持
📌 通知点击进入 App 内页面
📌 多端同步（云端版本）
