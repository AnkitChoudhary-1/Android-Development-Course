# 🐧 Complete Guide to Linux Command Line for Android Developers

---

## 🐧 Part 1: What is Linux and Why Developers Use It

### 💡 What is Linux?
Most people have used Windows or macOS. Linux is the third major operating system, but with one enormous difference — it is **free and open source**.

```
CLOSED SOURCE (Windows, macOS) vs OPEN SOURCE (Linux):

Closed Source (Windows, macOS):
  - Source code is SECRET.
  - Only Microsoft/Apple engineers can inspect and modify it.
  - Paid licensing fees.

Open Source (Linux):
  - Source code is PUBLIC for anyone in the world to read, modify, and improve.
  - Free of cost.
  - Transparent & maintained by a global community.
```

---

### 🌐 The Family Tree — Linux is Everywhere

```
LINUX RUNS ON:

🖥️ Web Servers      → 94%+ of global web servers (Google, AWS, Netflix) run Linux.
📱 Android Phones    → Android is built on top of the Linux Kernel!
☁️ Cloud Computing   → AWS, GCP, Azure infrastructures rely on Linux VMs.
🤖 Supercomputers    → 100% of the world's top 500 supercomputers run Linux.
🏠 Embedded Devices  → Smart TVs, routers, IoT, Raspberry Pi.
```

---

### 📦 What is a Linux Distribution (Distro)?
Linux itself is the core OS **Kernel**. Organizations bundle the kernel with shell programs, system tools, and GUIs to create complete Operating Systems called **Distros**.

```
POPULAR DISTROS:
  - Ubuntu    → Most popular & beginner-friendly for developers.
  - Debian    → Extremely stable (Ubuntu is built on Debian).
  - Fedora    → Cutting-edge enterprise distro sponsored by Red Hat.
  - Android   → A specialized Linux distribution built for mobile devices!
```

---

## 💻 Part 2: What is a Terminal?

> 💻 **Terminal / CLI:** A text-based interface used to control an operating system by typing commands instead of clicking graphical buttons.

```
GUI vs CLI:

GUI (Graphical User Interface):
  - Click icons, drag & drop files
  - Intuitive for general users
  - Hard to automate batch operations

CLI (Command Line Interface):
  - Type text commands into a Shell (e.g., Bash / Zsh)
  - Blazing fast & scriptable
  - Essential for professional software development
```

```
ANATOMY OF A TERMINAL PROMPT:

  rohit@ubuntu:~/Projects/MyApp$ 
  ───── ────── ─────────────── ─
    │     │           │        │
  User Hostname Current Dir  Prompt ($ = normal user, # = root)
```

---

## 🧭 Part 3: Navigation Commands

### 🗺️ The Linux File System Hierarchy

```
LINUX DIRECTORY TREE:

/                          ← ROOT (Top-level directory, like C:\ in Windows)
├── home/                  ← User home folders
│   └── rohit/             ← YOUR home directory (represented by ~)
│       ├── Documents/
│       ├── Downloads/
│       └── Projects/
├── etc/                   ← System configuration files
├── usr/                   ← Installed applications & binaries
├── bin/                   ← Core system command binaries (ls, cd, etc.)
└── var/                   ← Variable logs and data
```

---

### 📍 Core Navigation Commands

#### 1. `pwd` (Print Working Directory)
Displays your current absolute path in the file system.

```bash
$ pwd
# Output: /home/rohit/Projects/AndroidApp
```

#### 2. `ls` (List)
Lists files and directories in the target location.

```bash
# Basic list
$ ls

# Long format with permissions, file size, owner, and date
$ ls -l

# Show hidden files (files starting with a dot like .gitignore)
$ ls -a

# Combine long format + hidden files + human-readable sizes
$ ls -laSh
```

#### 3. `cd` (Change Directory)
Navigates between directories.

```bash
# Move into a folder
$ cd Projects/AndroidApp

# Go up ONE level
$ cd ..

# Go up TWO levels
$ cd ../..

# Go to your HOME directory (~ shortcut)
$ cd ~

# Switch back to PREVIOUS directory
$ cd -
```

---

## 📁 Part 4: File and Folder Management Commands

### 1. `mkdir` (Make Directory)
Creates new directories.

```bash
# Single directory
$ mkdir MyAndroidApp

# Create nested directories all at once (-p flag)
$ mkdir -p MyAndroidApp/app/src/main/java
```

---

### 2. `touch` (Create Empty File)
Creates a new blank file or updates existing file timestamps.

```bash
$ touch README.md .gitignore local.properties
```

---

### 3. `rm` (Remove / Delete)
Deletes files or directories.

> [!CAUTION]
> **PERMANENT DELETION:** `rm` deletes files permanently! There is **NO Trash / Recycle Bin** in the command line.

```bash
# Delete a single file
$ rm notes.txt

# Delete a directory and all its contents recursively (-r flag)
$ rm -r OldProject/

# Force delete directory without confirmation (-rf)
$ rm -rf build/
```

---

### 4. `cp` (Copy)
Copies files or directories.

```bash
# Copy file
$ cp MainActivity.kt MainActivity_backup.kt

# Copy directory recursively (-r flag)
$ cp -r WeatherApp/ WeatherApp_Backup/
```

---

### 5. `mv` (Move / Rename)
Moves files or renames them in-place.

```bash
# Rename file
$ mv OldName.kt NewName.kt

# Move file into a directory
$ mv NewName.kt app/src/main/java/
```

---

## 📄 Part 5: Viewing & Inspecting File Content

| Command | Usage | Best For |
| :--- | :--- | :--- |
| **`cat`** | `cat file.txt` | Printing full short file contents directly to terminal. |
| **`less`** | `less large.log` | Interactive page-by-page viewing of long logs (`q` to quit, `/` to search). |
| **`head`** | `head -n 20 file.txt` | Viewing the first $N$ lines of a file. |
| **`tail`** | `tail -n 20 file.txt` | Viewing the last $N$ lines of a file. |
| **`tail -f`** | `tail -f app.log` | **Live real-time streaming** of appending log files (Press `Ctrl+C` to exit). |

---

## 🔐 Part 6: File Permissions & `sudo`

### 🔑 Understanding `rwx` Permissions

```
PERMISSION STRING BREAKDOWN:

  - r w x r - x r - -
  │ └─┬─┘ └─┬─┘ └─┬─┘
  │ Owner Group Others
  │  rwx   r-x   r--
  File type (- = file, d = directory)
```

- **`r` (Read = 4):** View file contents / list directory.
- **`w` (Write = 2):** Edit file / add or delete files inside directory.
- **`x` (Execute = 1):** Execute script as program / enter directory with `cd`.

---

### 🛠️ `chmod` — Changing Permissions

```bash
# Numeric Mode: Sum values for Owner, Group, Others
# rwx (4+2+1=7), r-x (4+0+1=5), r-- (4+0+0=4) -> 755

$ chmod 755 gradlew          # Make script executable by owner & readable by all
$ chmod 600 ~/.ssh/id_rsa    # Secure SSH private key (owner read/write only)
$ chmod +x deploy.sh         # Add execute permission to script
```

---

### 🔑 `sudo` — Superuser Execution
`sudo` (*SuperUser DO*) grants administrative root privileges to execute restricted system commands.

```bash
$ sudo apt update
```

> [!WARNING]
> **Use `sudo` with extreme caution.** Running commands with root privileges overrides system protections. Never execute `sudo rm -rf /`.

---

## 📦 Part 7: Package Management with `apt`

`apt` (*Advanced Package Tool*) is the package manager for Ubuntu and Debian Linux distros.

```bash
# 1. Refresh package index catalog
$ sudo apt update

# 2. Upgrade all installed system packages
$ sudo apt upgrade

# 3. Install developer packages
$ sudo apt install git openjdk-17-jdk adb curl unzip

# 4. Remove a package
$ sudo apt remove nodejs

# 5. Clean up unused dependencies
$ sudo apt autoremove
```

---

## ⚡ Part 8: Essential Shortcuts & Utilities

### ⌨️ Terminal Shortcuts
- **`Tab`:** Autocomplete file paths & commands (**Use constantly!**).
- **`Ctrl + C`:** Immediately terminate / kill the running command.
- **`Ctrl + Z`:** Pause running process and push to background.
- **`Ctrl + L`:** Clear terminal screen (same as typing `clear`).
- **`Ctrl + R`:** Reverse search command history.

---

### 🔍 Search & Utility Commands

```bash
# Search text patterns inside files recursively
$ grep -r "TODO" --include="*.kt" .

# Find files by name pattern
$ find . -name "*.kt"

# Check available disk space
$ df -h

# Check RAM utilization
$ free -h
```

---

## 📱 Part 9: Why Android Developers Must Know Linux

1. **Android Kernel:** Android is an OS running on the Linux Kernel. `adb shell` drops you directly into a Linux CLI on your phone!
2. **Android Studio Terminal:** Built-in terminal executes shell scripts and `./gradlew` commands.
3. **ADB (Android Debug Bridge):** CLI tool for managing Android devices:
   ```bash
   $ adb devices
   $ adb install app-debug.apk
   $ adb logcat -s MainActivity
   $ adb shell ls /sdcard/Download/
   ```
4. **Gradle Wrapper Commands:**
   ```bash
   $ ./gradlew assembleDebug
   $ ./gradlew clean
   ```

---

## 📊 Complete Command Reference

| Category | Command | Description |
| :--- | :--- | :--- |
| **Navigation** | `pwd`, `ls -la`, `cd <dir>` | Check location, list files, change directory |
| **File Ops** | `mkdir -p`, `touch`, `rm -rf`, `cp -r`, `mv` | Manage directories and files safely |
| **Inspection** | `cat`, `less`, `head`, `tail -f` | Inspect and stream file content |
| **Permissions** | `chmod +x`, `chmod 755`, `sudo` | Manage script execution & admin rights |
| **Packages** | `sudo apt update`, `sudo apt install` | Manage system software dependencies |
| **Android Tools**| `adb devices`, `adb logcat`, `./gradlew` | Interact with Android runtime & builds |

---

## ❓ 5 Questions to Test Your Understanding

### 🎯 Question 1: Navigation & Pathing
> - **a)** What command prints your current terminal directory?
> - **b)** Differentiate between absolute path `/home/user/App` and relative path `App/src`.
> - **c)** How do you jump back to your user home directory instantly?

---

### 🍕 Question 2: File Operation Sequence
> Write CLI commands to:
> 1. Create directory structure `Android/Projects/DemoApp/src` in one command.
> 2. Create `MainActivity.kt` inside `src`.
> 3. Copy `MainActivity.kt` to `MainActivity_Backup.kt`.
> 4. Delete the backup file with confirmation.

---

### 🔐 Question 3: Permissions Decoding
> Inspect this permission string: `-rwxr-xr-- 1 user dev 2048 Jan 15 script.sh`
> - **a)** Can group members execute this script?
> - **b)** Can others write to this file?
> - **c)** Write the `chmod` numeric command to make this file `rw-r--r--`.

---

### 📦 Question 4: Package Manager Workflow
> - **a)** Why must you run `sudo apt update` before `sudo apt install`?
> - **b)** Write a single command to install `git`, `curl`, and `openjdk-17-jdk`.

---

### 📱 Question 5: Android CLI & Troubleshooting
> - **a)** How do you view live streaming logs from an app crash on a real Android device using ADB?
> - **b)** What Gradle Wrapper command cleans your build cache from the terminal?