# Android Development Course

**A zero-to-hero curriculum for mastering modern Android engineering — built to prepare developers for technical interviews at top product companies.**

[![Android](https://img.shields.io/badge/Android-Roadmap-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/AnkitChoudhary-1/Android-Development-Course)
[![Kotlin](https://img.shields.io/badge/Kotlin-Mastery-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://github.com/AnkitChoudhary-1/Android-Development-Course)
[![Status](https://img.shields.io/badge/Status-In%20Progress-FF4500?style=for-the-badge)](https://github.com/AnkitChoudhary-1/Android-Development-Course)
[![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)](#license)

---

## Table of Contents

- [Overview](#overview)
- [Who This Course Is For](#who-this-course-is-for)
- [Why This Course](#why-this-course)
- [Roadmap](#roadmap)
- [Repository Structure](#repository-structure)
- [How to Use This Repo](#how-to-use-this-repo)
- [Roadmap Diagram](#roadmap-diagram)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

> **Started:** August 5, 2026

This repository is a structured, ground-up curriculum covering low-level computer fundamentals, advanced Kotlin concurrency, Jetpack Compose, system design, and production-grade Android architecture — built specifically to prepare developers for technical interviews at MAANG-level and other top product companies.

Rather than stopping at "how to build an app," this course goes into **how things actually work under the hood**: memory layout, CPU cycles, garbage collection, reactive state management, custom UI internals, and scalable mobile architecture.

## Who This Course Is For

This roadmap is built for:

- **Beginners** who want a structured, ground-up path into Android instead of scattered tutorials.
- **Self-taught developers** looking to fill gaps in computer science fundamentals and low-level Android internals.
- **Working developers** preparing for technical interviews at product-based companies, who need system design and architecture depth beyond day-to-day app work.

**Prerequisites:** none beyond basic programming familiarity — Phase 0 assumes no prior computer science background and builds up from there.

**Time commitment:** this is a deep, comprehensive course — expect roughly 1–1.5 years of consistent, active study to complete it end to end.

**Maintenance:** new phases and notes are added regularly as the course develops.

## Why This Course

Top tech companies rarely test whether you can wire up a button and an API call. They evaluate:

| Area | What's Tested |
|---|---|
| **Low-level fundamentals** | CPU cache locality, memory allocation, leaks, thread safety |
| **Async concurrency** | Kotlin Coroutines, Channels, StateFlow/SharedFlow, backpressure |
| **System architecture** | MVVM, MVI, Clean Architecture, SOLID, Dependency Injection |
| **Modern UI** | Jetpack Compose recomposition, layout phases, state hoisting, custom modifiers |
| **Mobile system design** | Offline-first caching, real-time sync, media streaming, SDK design |

This course is structured around those exact evaluation areas.

## Roadmap

A 16-phase path from absolute fundamentals to interview-ready, matching [`Roadmap.png`](./Roadmap.png).

| Phase | Title | Focus | Status |
|---|---|---|---|
| 0 | Become a Computer Engineer | CPU, RAM, storage, processes/threads, networking basics, Git, Linux | ✅ Completed |
| 1 | Kotlin Mastery | Null safety, collections, OOP, sealed classes, coroutines, Flow, generics | ✅ Completed |
| 2 | Android Foundations | Activity/Intent/Manifest, app lifecycle, process management, config changes | ✅ Completed |
| 3 | Jetpack Compose | Composables, state, side effects, lists, gestures, animations, performance | ✅ Completed |
| 4 | Real UI Engineering | Clones of Spotify, WhatsApp, Telegram, Instagram, Google Photos, and more | 🟢 In Progress |
| 5 | Architecture | MVVM, Repository pattern, UseCases, Clean Architecture, Hilt, navigation | ⚪ Planned |
| 6 | Data | Room, DataStore, SQL, caching strategies, offline-first, pagination | ⚪ Planned |
| 7 | Networking | REST, Retrofit/OkHttp, serialization, auth/JWT, multipart, WebSockets | ⚪ Planned |
| 8 | Coroutines & Flow | Suspending functions, dispatchers, structured concurrency, cancellation | ⚪ Planned |
| 9 | Performance | Memory leaks, recomposition, jank, profilers, baseline profiles | ⚪ Planned |
| 10 | Testing | Unit/UI/integration testing, mocking, TDD | ⚪ Planned |
| 11 | Android Internals | Binder/IPC, AIDL, ART, DEX, APK, Gradle, R8, JNI | ⚪ Planned |
| 12 | System Design (Android) | How WhatsApp, Spotify, Instagram, Maps, and notifications work at scale | ⚪ Planned |
| 13 | Open Source | Reading code, forking, PRs, code review, commit hygiene | ⚪ Planned |
| 14 | Professional Android | Play Store deployment, CI/CD, Fastlane, Crashlytics, release management | ⚪ Planned |
| 15 | Interview Preparation | Android interview questions, LLD, machine coding, DSA, resume/GitHub prep | ⚪ Planned |

Each phase builds on the last — fundamentals first, then language mastery, then framework depth, then architecture and scale.

## Repository Structure

```
Android-Development-Course/
├── README.md
├── Roadmap.png
├── phase-0-computer-engineer/
│   ├── notes.md
│   └── Computer Fundamentals.png
└── ...                          # phases 1–15, added as the course progresses
```

Each phase folder contains topic notes and supporting diagrams for that stage of the roadmap.

## How to Use This Repo

1. **Clone the repository:**
   ```bash
   git clone https://github.com/AnkitChoudhary-1/Android-Development-Course.git
   ```
2. **Start at Phase 0** — even if you already know some Android, the fundamentals phases fill in the gaps most tutorials skip.
3. **Work through each phase in order.** The curriculum is cumulative — later phases assume the concepts from earlier ones.
4. **Star ⭐ the repo** to keep track of new phases as they're published.

## Roadmap Diagram

![Android Development Roadmap](./Roadmap.png)

## Contributing

This is primarily a personal learning-in-public project, but feedback, corrections, and suggestions are welcome:

- Found an error or outdated info? Open an [issue](https://github.com/AnkitChoudhary-1/Android-Development-Course/issues).
- Have a suggestion for a topic or resource? Issues and discussions are the best place to raise it.

## License

Licensed under the [MIT License](LICENSE).

---

<p align="center">Made for aspiring Android engineers. Star ⭐ this repository to follow along.</p>

<p align="center"><sub>Diagrams and infographics in this repository were created with ChatGPT.</sub></p>
