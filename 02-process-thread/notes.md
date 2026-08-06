# ⚡ Complete Guide to Processes and Threads for Android Developers

---

## 📌 Part 1: Program vs Process — What is the Difference?

### 💡 Start With What You Already Know
You have an app on your phone. Let's say it is **WhatsApp**. That app exists as a file stored on your phone's storage. When you are **NOT** using it, it just sits there quietly, doing absolutely nothing. It is just a bunch of code sitting on disk.

> 📁 **Program:** That file sitting on storage is called a **Program**.

The moment you tap on WhatsApp, something magical happens. Android picks up that program from storage, loads it into RAM, assigns it CPU time, gives it resources, and it comes alive and starts doing things.

> 🏃 **Process:** That living, running, breathing instance is called a **Process**.

---

### 🍳 The Simplest Definition

```
PROGRAM = A recipe written in a cookbook (just instructions on paper)
PROCESS = A chef actively cooking that recipe RIGHT NOW
```

- The recipe does **nothing** by itself.
- The moment a chef starts cooking it, it becomes alive.
- That **"active cooking"** is the process.

---

### 🔬 A More Technical Way to See It

```
┌─────────────────────────────────────────────────────────┐
│                        PROGRAM                          │
│                                                         │
│  - Stored on STORAGE (disk)                             │
│  - Passive, does nothing                                │
│  - Just a file (WhatsApp.apk)                           │
│  - EXISTS even when phone is off                        │
│  - ONE copy of the file                                 │
└─────────────────────────────────────────────────────────┘
                            ↓
                 User taps the app icon
                            ↓
┌─────────────────────────────────────────────────────────┐
│                        PROCESS                          │
│                                                         │
│  - Loaded into RAM                                      │
│  - ACTIVE, doing things right now                       │
│  - Has its own memory space                             │
│  - Has its own resources (CPU time, RAM, files)         │
│  - DISAPPEARS when you close the app                    │
│  - The SAME program can create MULTIPLE processes       │
└─────────────────────────────────────────────────────────┘
```

---

### 📑 One Program → Multiple Processes
Can the same program create multiple processes? **YES!**

Think about Chrome browser on your computer:

```
Chrome Program (one .exe file on disk)
                    ↓
        You open Chrome → Process 1 (your Chrome window)
        
Chrome Program (same file)
                    ↓
        Your friend logs into the same computer
        and opens Chrome → Process 2 (their Chrome window)
```

Both are running from the same program file but they are **two completely separate processes**. They have their own RAM space, their own data, and they do not interfere with each other.

> 📱 **Android Example:** Every single app on your Android phone runs as its own separate process. WhatsApp is one process, Instagram is another, YouTube is another. They are completely isolated from each other.

```
Android Phone Running:
┌─────────────────────────────────────────────────────────┐
│                           RAM                           │
│                                                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐   │
│  │  Process 1  │  │  Process 2  │  │  Process 3  │   │
│  │             │  │             │  │             │   │
│  │  WhatsApp   │  │  Instagram  │  │   YouTube   │   │
│  │             │  │             │  │             │   │
│  │  Own RAM    │  │  Own RAM    │  │  Own RAM    │   │
│  │  Own data   │  │  Own data   │  │  Own data   │   │
│  └─────────────┘  └─────────────┘  └─────────────┘   │
│                                                         │
│  These processes CANNOT access each other's data.      │
│  WhatsApp cannot read Instagram's memory.              │
│  This is called PROCESS ISOLATION. (Security!)         │
└─────────────────────────────────────────────────────────┘
```

---

### 📦 What Does a Process Actually Contain?
When the OS creates a process, it gives it a dedicated package of resources.

```
┌──────────────────────────────────────────────────────────┐
│                         PROCESS                          │
│                                                          │
│  ┌───────────────────────────────────────────────────┐  │
│  │  CODE SECTION                                     │  │
│  │  The actual program instructions loaded from disk │  │
│  │  (WhatsApp's code for sending messages, etc.)     │  │
│  └───────────────────────────────────────────────────┘  │
│                                                          │
│  ┌───────────────────────────────────────────────────┐  │
│  │  DATA SECTION                                     │  │
│  │  Global variables and data                        │  │
│  │  (app settings, constants)                        │  │
│  └───────────────────────────────────────────────────┘  │
│                                                          │
│  ┌───────────────────────────────────────────────────┐  │
│  │  HEAP                                             │  │
│  │  Dynamically allocated memory                     │  │
│  │  (objects you create while app is running)        │  │
│  └───────────────────────────────────────────────────┘  │
│                                                          │
│  ┌───────────────────────────────────────────────────┐  │
│  │  STACK                                            │  │
│  │  Function calls and local variables               │  │
│  │  (temporary data for current operations)          │  │
│  └───────────────────────────────────────────────────┘  │
│                                                          │
│  ┌───────────────────────────────────────────────────┐  │
│  │  PROCESS CONTROL BLOCK (PCB)                      │  │
│  │  The OS's file on this process                    │  │
│  │  Contains: Process ID (PID), state, priority      │  │
│  └───────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘
```

> 🆔 **Process Control Block (PCB):** Think of it as the process's government ID card. Every process gets a unique **Process ID (PID)** — like a unique citizen ID number. The OS uses this to track and manage every process.

```
When you open WhatsApp on Android:
  WhatsApp Process → PID: 1847
  
When you open Instagram:
  Instagram Process → PID: 2034
  
When you open YouTube:
  YouTube Process → PID: 2891
```

---

## 🧵 Part 2: What is a Thread?

### ⚠️ The Problem With a Single Process
A process can do work, but what if a process needs to do **MULTIPLE things AT THE SAME TIME**?

Imagine WhatsApp needs to:
1. Show you the chat screen
2. Download the photo your friend just sent
3. Play the audio message
4. Check for new messages every few seconds
5. Save messages to the database

If the process could only do **ONE** thing at a time, life would be terrible:
- It shows the chat screen → *pauses*
- Then downloads the photo → *pauses*
- Then plays audio → *pauses*
- Then checks messages → *pauses*

You would **not** be able to scroll while photos download. You would **not** be able to type while audio plays.

> 🛠️ **The Solution:** THREADS.

---

### 💡 What is a Thread?
A **Thread** is the smallest unit of execution within a process. It is like a worker inside a process.

A process can have one or many threads. All threads inside a process share the same memory (RAM space), but each thread has its own job to do.

```
Thread = A single sequence of instructions being executed

Just like a CPU follows fetch-decode-execute,
a thread is the thing that actually CARRIES OUT
those instructions one step at a time.
```

---

### 🍽️ The Best Analogy: A Restaurant

```
┌──────────────────────────────────────────────────────────┐
│                  THE RESTAURANT ANALOGY                  │
│                                                          │
│  PROGRAM   = The restaurant's recipe book (on the shelf) │
│                                                          │
│  PROCESS   = The restaurant ITSELF (the whole operation) │
│              - Has its own kitchen (memory/RAM)          │
│              - Has its own ingredients (resources)       │
│              - Has its own address (PID)                 │
│              - Separate from other restaurants           │
│                                                          │
│  THREADS   = The WORKERS inside the restaurant           │
│              - Chef (Thread 1): Cooks the food           │
│              - Waiter (Thread 2): Serves customers       │
│              - Cashier (Thread 3): Handles billing       │
│              - Cleaner (Thread 4): Cleans tables         │
│                                                          │
│  All workers SHARE the same kitchen (same memory)       │
│  But each does their OWN specific job                   │
│                                                          │
│  Workers from McDonald's (another process) CANNOT       │
│  enter this restaurant's kitchen. (Process isolation)   │
└──────────────────────────────────────────────────────────┘
```

---

### 🏠 How a Thread Lives Inside a Process

```
┌─────────────────────────────────────────────────────────┐
│                         PROCESS                         │
│                  (WhatsApp, PID: 1847)                  │
│                                                         │
│          SHARED MEMORY (all threads can access)         │
│  ┌─────────────────────────────────────────────────┐  │
│  │  Code, Heap, Data, Files, Network connections   │  │
│  └─────────────────────────────────────────────────┘  │
│                                                         │
│  ┌───────────┐  ┌───────────┐  ┌───────────┐         │
│  │ Thread 1  │  │ Thread 2  │  │ Thread 3  │         │
│  │           │  │           │  │           │         │
│  │ Main/UI   │  │ Download  │  │ Database  │         │
│  │ Thread    │  │ Thread    │  │ Thread    │         │
│  │           │  │           │  │           │         │
│  │ Own Stack │  │ Own Stack │  │ Own Stack │         │
│  │ Own Regs  │  │ Own Regs  │  │ Own Regs  │         │
│  └───────────┘  └───────────┘  └───────────┘         │
│                                                         │
│  All threads SHARE the heap (RAM space)                │
│  Each thread has its OWN stack (call history)          │
└─────────────────────────────────────────────────────────┘
```

---

### 📊 What Each Thread Owns vs Shares

#### ✅ SHARED between ALL threads in a process:
- **Code:** All threads run the same app's code
- **Heap memory:** Objects created dynamically
- **Global variables:** App-wide states & constants
- **Files:** File descriptors opened by the process
- **Network connections:** Open sockets

#### 🔒 PRIVATE to each thread:
- **Stack:** Its own call history and local variables
- **Program Counter (PC):** Which instruction it is currently executing
- **Registers:** CPU registers allocated for that thread
- **Thread State:** Running, waiting, sleeping

---

### ⚡ Why Sharing Memory is Useful AND Dangerous

#### Useful:
```
Thread 1 downloads a photo and stores it in shared heap memory.
Thread 2 (UI thread) can immediately read that photo and display it.
No complex copying needed. They just share the same memory location.
```

#### Dangerous:
```
Thread 1 is updating a user's name in memory: "Roh..."
Thread 2 reads the name at the SAME moment: "Roh..."
Thread 1 finishes: "Rohit"
But Thread 2 already read an incomplete value!
```

> [!CAUTION]
> **RACE CONDITION:** When multiple threads concurrently access and mutate shared memory without synchronization, unpredicted results occur. Race conditions are among the hardest software bugs to diagnose.

---

## 🔀 Part 3: Single-Threaded vs Multi-Threaded Programs

### 🐢 Single-Threaded Programs
A single-threaded program has only one thread. It can only do one thing at a time. Each task must wait for the previous one to finish.

```
SINGLE-THREADED EXECUTION:

Time →  0s    1s    2s    3s    4s    5s    6s    7s    8s
        │─────────────│─────────────│──────────────│───────│
        │  Load UI    │   Download  │  Save to DB  │  Done │
        │  (2 sec)    │  (2 sec)    │  (2 sec)     │       │
        │─────────────│─────────────│──────────────│───────│

Total time: 6 seconds
During download: UI is FROZEN. User cannot interact.
During DB save: UI is FROZEN. User cannot interact.
```

> 🎮 **Real Example:** In a simple single-threaded game:
> 1. Read player input (1ms)
> 2. Calculate physics (5ms)
> 3. Draw screen (10ms)
> 4. Play sound (3ms)
> 
> *During physics calculation, touch input is completely IGNORED!*

---

### 🚀 Multi-Threaded Programs
A multi-threaded program has multiple threads working simultaneously across cores.

```
MULTI-THREADED EXECUTION:

Time →  0s         1s         2s         3s
        │
        ├── Thread 1 (UI)    ────────────────────────→
        │   Shows screen, responds to touches ALL THE TIME
        │
        ├── Thread 2 (Download) ──────────│
        │   Downloads photo (finishes at 2s)
        │
        ├── Thread 3 (Database) ──────────────────│
        │   Saves messages (finishes at 3s)
        │
        └── Thread 4 (Network) ──────────────────────→
            Keeps checking for new messages continuously

Everything happens SIMULTANEOUSLY.
The user can interact with UI the entire time.
```

---

### 🏛️ A Brilliant Real-Life Comparison

#### 🏦 Single-Threaded: One person doing everything at a bank
- Employee greets customer → verifies ID → processes transaction → prints receipt → calls next customer.
- *The line gets longer and everyone is frustrated.*

#### 🏢 Multi-Threaded: Multiple specialized employees
- **Employee 1:** Greets & verifies ID (UI Thread)
- **Employee 2:** Processes transactions (IO Thread)
- **Employee 3:** Handles loans (Background Thread)
- **Employee 4:** Manages ATM (Network Thread)
- *All share the bank vault (shared memory). Service is fast and responsive.*

---

### 📱 Types of Threads in Android

```
┌──────────────────────────────────────────────────────────┐
│                  ANDROID APP THREADS                     │
│                                                          │
│  ┌────────────────────────────────────────────────────┐  │
│  │             MAIN THREAD (UI Thread)                │  │
│  │                                                    │  │
│  │  - Created AUTOMATICALLY when app starts           │  │
│  │  - Handles ALL user interface work                 │  │
│  │  - Draws buttons, text, images on screen           │  │
│  │  - Responds to button clicks and touches           │  │
│  │  - Updates animations                              │  │
│  │                                                    │  │
│  │  RULE: NEVER do heavy work here!                  │  │
│  │  If this thread is blocked, the app FREEZES.      │  │
│  └────────────────────────────────────────────────────┘  │
│                                                          │
│  ┌────────────────────────────────────────────────────┐  │
│  │           BACKGROUND THREADS                       │  │
│  │                                                    │  │
│  │  - Created by the developer when needed            │  │
│  │  - Handle heavy work away from UI                  │  │
│  │  - Examples:                                       │  │
│  │    • Downloading files from internet               │  │
│  │    • Reading/writing from database                 │  │
│  │    • Processing images                             │  │
│  │    • Doing heavy calculations                      │  │
│  │                                                    │  │
│  │  RULE: Can NOT directly update the UI!            │  │
│  │  Must pass results back to Main Thread.           │  │
│  └────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘
```

---

### 💻 Code Comparison: Single vs Multi-Threaded in Android

```kotlin
// ❌ SINGLE THREADED (WRONG WAY) — Everything on Main Thread
fun onLoginButtonClicked() {
    
    // Step 1: Validate input (Main Thread)
    val email = emailField.text.toString()
    val password = passwordField.text.toString()
    
    // Step 2: Call the server (Main Thread - BLOCKING!)
    // Takes 3-5 seconds -> UI FREEZES!
    // Android will trigger "Application Not Responding" (ANR)!
    val result = loginToServer(email, password)
    
    // Step 3: Update UI (Main Thread)
    if (result.success) {
        showHomeScreen()
    }
}
```

```kotlin
// ✅ MULTI THREADED (CORRECT WAY) — Using Kotlin Coroutines
fun onLoginButtonClicked() {
    
    // Step 1: Validate input (Main Thread - fast)
    val email = emailField.text.toString()
    val password = passwordField.text.toString()
    
    // Launch Coroutine tied to Lifecycle
    lifecycleScope.launch {
        
        showLoadingSpinner() // Main Thread (UI update)
        
        // Step 2: Switch to IO Background Thread for network call
        val result = withContext(Dispatchers.IO) {
            loginToServer(email, password) // Background Thread
            // Main thread remains 100% FREE & responsive
        }
        
        // Step 3: Automatically returns to Main Thread
        hideLoadingSpinner()
        if (result.success) {
            showHomeScreen() // Main Thread (UI update)
        }
    }
}
```

---

## ⏱️ Part 4: How the OS Manages Multiple Processes — Context Switching

### ❓ The Big Question
Your phone has **8 CPU cores**, but you might have **50 processes** running at the same time. How do 50 processes run on just 8 cores?

> 🔄 **Answer:** CONTEXT SWITCHING.

---

### 🔄 What is Context Switching?
The OS gives each process and thread a tiny slice of CPU time — typically around **10-20 milliseconds** (0.01 – 0.02 seconds). Then it switches to the next one.

```
REALITY: One core, multiple processes taking turns

Time (milliseconds):
0ms    10ms   20ms   30ms   40ms   50ms   60ms
│──────│──────│──────│──────│──────│──────│
│  P1  │  P2  │  P3  │  P1  │  P2  │  P3  │
└──────┴──────┴──────┴──────┴──────┴──────┘

P1 = WhatsApp process
P2 = Instagram process
P3 = Android System process

Each process gets 10ms of CPU time.
Then it pauses and waits for its next turn.

ILLUSION: Looks like all 3 run at the same time
REALITY:  They take turns rapidly
```

---

### 📋 What Actually Happens During a Context Switch?

```
┌──────────────────────────────────────────────────────────┐
│                  CONTEXT SWITCH STEPS                    │
│                                                          │
│  1. PAUSE Process A                                      │
│     "Stop right there, save progress"                    │
│                                                          │
│  2. SAVE Process A's state (its "context")              │
│     - Save Program Counter (instruction position)        │
│     - Save CPU Register values                           │
│     - Save Stack pointer                                 │
│     (Bookmarking a page in a book)                       │
│                                                          │
│  3. LOAD Process B's state (its "context")              │
│     - Restore Process B's Program Counter                │
│     - Restore Process B's registers                      │
│     - Restore Process B's stack                          │
│                                                          │
│  4. RESUME Process B                                     │
│     Runs for its time slice (10-20ms)                    │
│                                                          │
│  5. REPEAT continuously across active threads            │
└──────────────────────────────────────────────────────────┘
```

---

### 🤹 Real Life Analogies

#### 🤹 The Juggler (CPU Cores & Processes)
- **Balls:** Processes
- **Hands:** CPU Cores
- A juggler can only hold 1-2 balls at once, but keeps 5 balls in motion by catching and throwing rapidly. To an observer, all 5 balls appear floating simultaneously.

#### 👩‍🏫 The Teacher With 30 Students
- **Teacher:** CPU Core
- **30 Students:** Active Processes
- The teacher spends 10 seconds per student, answering questions in round-robin fashion. Every student feels like they are receiving continuous assistance.

---

### 🚦 The OS Scheduler: Priority Rules

```
SCHEDULING PRIORITIES:

High Priority (gets maximum CPU time):
  - Android System UI (must maintain 60/120 FPS)
  - Audio playback (music cannot skip)
  - Foreground App (currently visible app)

Medium Priority:
  - Partially visible apps
  - Active background sync

Low Priority (runs when CPU is idle):
  - Background apps
  - Pre-loading content
  - Garbage collection
```

---

### 🔄 Process States: The Life of a Process

```
        ┌────────────────────────────────────────────┐
        │                                            │
        ▼                                            │
┌───────────────┐    Scheduled    ┌───────────────┐ │
│     NEW       │ ──────────────→ │     READY     │ │
│               │                 │               │ │
│ Process just  │                 │ Waiting for   │ │
│ created       │                 │ CPU time      │ │
└───────────────┘                 └───────────────┘
                                         │
                                   CPU assigned
                                         │
                                         ▼
                                  ┌───────────────┐
                   ┌────────────→ │    RUNNING    │
                   │              │               │
                   │              │ Executing on  │
                   │              │ CPU right now │
                   │              └───────────────┘
                   │                     │
              Time slice          ┌──────┴──────┐
              expired             │             │
                   │              │             │
                   │         Waiting for    Process
                   │         I/O or event   finished
                   │              │             │
                   └──────  ┌─────────────┐    ▼
                            │   WAITING/  │  ┌──────────┐
                            │   BLOCKED   │  │TERMINATED│
                            └─────────────┘  └──────────┘
                                   │
                          Event received/
                          I/O complete
                                   │
                                   ▼
                              Back to READY
```

| State | Description |
| :--- | :--- |
| **NEW** | Process is being initialized by OS kernel. |
| **READY** | Waiting in execution queue for CPU slice. |
| **RUNNING** | Actively executing instructions on CPU core. |
| **BLOCKED** | Paused waiting for I/O, database, or network response. |
| **TERMINATED** | Finished execution or killed by OS memory manager. |

---

### ⚠️ Context Switching Has a Cost

> [!WARNING]
> **Thrashing:** If too many processes run at once, the CPU spends more time saving and restoring registers (context switching overhead) than executing actual program instructions.

- **Time Cost:** Context switches take microsecond overhead that accumulates.
- **Battery & Thermal Cost:** Frequent switching causes excessive CPU wakeups, draining battery and generating heat.

---

## 🌐 Part 5: Complete System Hierarchy

```
┌─────────────────────────────────────────────────────────────┐
│                        ANDROID PHONE                        │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                 OPERATING SYSTEM                     │  │
│  │                 (Android OS)                         │  │
│  │  - Creates and manages processes                     │  │
│  │  - Schedules CPU time for each process               │  │
│  │  - Handles context switching                         │  │
│  └──────────────────────────────────────────────────────┘  │
│                           │                                 │
│              Manages and controls                           │
│                           │                                 │
│    ┌──────────────┬────────┴──────┬──────────────┐        │
│    ▼              ▼               ▼               ▼        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │Process 1 │  │Process 2 │  │Process 3 │  │Process 4 │  │
│  │WhatsApp  │  │Instagram │  │YouTube   │  │Android   │  │
│  │PID: 1847 │  │PID: 2034 │  │PID: 2891 │  │System    │  │
│  │          │  │          │  │          │  │PID: 1001 │  │
│  │Own Memory│  │Own Memory│  │Own Memory│  │          │  │
│  │          │  │          │  │          │  │          │  │
│  │Threads:  │  │Threads:  │  │Threads:  │  │Threads:  │  │
│  │-Main     │  │-Main     │  │-Main     │  │-System   │  │
│  │-Download │  │-Upload   │  │-Player   │  │-Binder   │  │
│  │-Database │  │-Stories  │  │-Buffer   │  │-GC       │  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘  │
│                           │                                 │
│              All compete for CPU time                       │
│                           │                                 │
│    ┌──────────────────────────────────────────────────┐    │
│    │                  CPU (8 Cores)                   │    │
│    │  Core1  Core2  Core3  Core4  Core5 Core6 Core7 Core8 │    │
│    │  [P1T1] [P2T1] [P3T1] [P4T1] [P1T2][P2T2][idle][idle]│    │
│    └──────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

---

## 🎯 Part 6: Why This is Critical for Android Developers

### 🚨 1. The Golden Rule of Android

> [!IMPORTANT]
> **NEVER BLOCK THE MAIN THREAD!**
> If the Main Thread is blocked for more than **5 seconds**, Android triggers an **Application Not Responding (ANR)** prompt, prompting the user to force-close your app.

#### 📌 Thread Responsibility Matrix:

| Thread Type | Allowed Operations | Prohibited Operations |
| :--- | :--- | :--- |
| **Main / UI Thread** | Drawing UI, handling touches, running animations, updating views | Network requests, DB queries, file I/O, heavy parsing |
| **Background Thread** | Network API calls, DB read/write, image processing, heavy sorting | Directly updating UI views or Android layout widgets |

---

### ⚡ 2. Kotlin Coroutines & Dispatchers

```kotlin
// Dispatchers tell Android which thread pool to execute work on:

Dispatchers.Main
// Main/UI Thread -> Use for UI rendering, click handlers, state binding.

Dispatchers.IO
// Disk & Network Thread Pool -> Optimized for file I/O, database operations, HTTP calls.

Dispatchers.Default
// CPU-Intensive Thread Pool -> Optimized for sorting huge lists, JSON parsing, image filters.
```

```kotlin
// Real-world implementation pattern:
suspend fun loadUserProfile() {
    withContext(Dispatchers.IO) {
        val user = database.getUserById(userId) // DB read on IO thread
        val photo = downloadPhoto(user.photoUrl) // Network on IO thread
        
        withContext(Dispatchers.Main) {
            displayUserName(user.name) // Switch back to Main for UI update
            displayUserPhoto(photo)
        }
    }
}
```

---

### 🔐 3. Security & App Lifecycle

- **Process Isolation:** Apps run under unique Linux UIDs with private sandboxed memory, preventing unauthorized cross-app data leaks.
- **Low Memory Killer (LMK):** Android kills background processes when RAM is constrained. Developers must persist app state in `onSaveInstanceState` or `ViewModel` handles.

---

## 📊 Complete Summary Cheat Sheet

| Concept | Definition | Key Characteristic |
| :--- | :--- | :--- |
| **Program** | Inactive binary code stored on disk (e.g. `.apk`) | Passive, static file on storage |
| **Process** | Active running instance of a program in RAM | Has private memory space & unique PID |
| **Thread** | Smallest unit of execution inside a process | Shares process memory, has private stack |
| **Main Thread** | Primary UI thread created by Android OS | Renders 60/120 FPS UI; must never block |
| **Background Thread** | Developer-spawned threads for heavy tasks | Handles network/database operations safely |
| **Context Switch** | OS rapidly saving & restoring CPU register states | Enables concurrent illusion on CPU cores |
| **Process Isolation** | Security sandbox isolating app memory spaces | Prevents apps from reading other app data |

---

## ❓ 5 Questions to Test Your Understanding

### 🎯 Question 1: Program vs Process
> Your friend says: *"I have Instagram installed on my phone but I never open it. So Instagram is currently running as a process."*
> 
> **Is your friend correct? Explain why or why not.**

---

### 🍕 Question 2: Thread Allocation Scenario
> You are building a food delivery app. When the user taps **"Order Now"**:
> 1. Form validation (0.1s)
> 2. HTTP Server request (3-5s)
> 3. Local database cache (1s)
> 4. Display *"Order Placed!"* dialog on screen
> 
> **Which tasks should run on Main Thread vs Background Thread? Explain why.**

---

### 🔄 Question 3: Context Switching
> Your phone has **4 CPU cores** and **20 active processes**.
> - **a)** Is it physically possible for all 20 processes to execute at the exact same millisecond?
> - **b)** How does the OS create the illusion of simultaneous execution?
> - **c)** What is the technical term for this switching mechanism?

---

### ⚠️ Question 4: Race Conditions
> Thread 1 (UI) reads account balance while Thread 2 (Network) updates account balance simultaneously.
> - **a)** What bug can occur?
> - **b)** What is the technical term for this?
> - **c)** Why does this issue occur between threads but NOT between processes?

---

### 📷 Question 5: Real Development Decision
> Applying a filter on a 12MP photo takes **8 seconds**.
> - **a)** Which thread pool should run the filter calculation?
> - **b)** Which thread should update the progress bar?
> - **c)** What happens if the filter runs on the Main Thread?
> - **d)** How should the background thread communicate completion to the Main Thread?