# Phase 5 — Architecture: UseCases and Clean Architecture

> **Topic 3 (proposed):** Give reusable application rules one clear home, so they are not copied into every ViewModel.
>
> **Proposed path:** `phase-5-Architecture/03-UseCases-and-Clean-Architecture/notes.md`
>
> **Prerequisites:** Topic 1 (Why Does MVVM Exist?), Topic 2 (Repository Pattern), Kotlin classes and interfaces, suspend functions, and Compose state from Phases 1–3.
>
> **Learning outcome:** Explain what a UseCase is, decide when one is worth adding, trace a rule through the UI, domain, and data layers, and explain Clean Architecture's layer boundaries without adding a layer for its own sake.

## Table of Contents

1. [Why a repository alone is not always enough](#1-why-a-repository-alone-is-not-always-enough)
2. [What a UseCase is](#2-what-a-usecase-is)
3. [A small Kotlin example](#3-a-small-kotlin-example)
4. [Clean Architecture layers](#4-clean-architecture-layers)
5. [Where each piece belongs](#5-where-each-piece-belongs)
6. [When not to add a UseCase](#6-when-not-to-add-a-usecase)
7. [Common misconceptions](#7-common-misconceptions)
8. [Practice and knowledge check](#8-practice-and-knowledge-check)
9. [Interview answer and checkpoint](#9-interview-answer-and-checkpoint)
10. [Official references](#10-official-references)

---

## 1. Why a repository alone is not always enough

Topic 2 gave each data type one owner: `PostRepository` owns posts, their caching, and their consistency. That solves a data problem. It does not, by itself, solve a *rule* problem.

Consider "like a post and record the interaction." That rule touches two repositories — posts and analytics — and it must happen the same way from the feed screen and from the profile screen. If each ViewModel writes that sequence itself, the rule is copied, and the copies can drift apart: one screen records the interaction, the other forgets to.

A UseCase is where that reusable rule lives, once, so ViewModels can call it instead of re-implementing it.

## 2. What a UseCase is

A UseCase — also called an interactor in some codebases — is a small class in the domain layer that:

- Represents one application rule, in the app's own terms.
- Coordinates one or more repositories to carry that rule out.
- Knows nothing about Compose, navigation, or a specific screen.
- Knows nothing about Retrofit, Room, or any other data source.

UseCases are normally named after the rule they perform, as a verb phrase: `LikePostUseCase`, `GetFeedPostsUseCase`, `ToggleBookmarkUseCase`. A common Kotlin convention is a single `operator fun invoke(...)`, so the UseCase can be called like a function.

| Part | Main question | Feed example |
|---|---|---|
| View | How should the current state appear, and what did the user do? | Feed composables, post cards, buttons |
| ViewModel | What state does this screen expose, and how does it respond to actions? | Feed loading state, error state, calling the UseCase |
| UseCase | What reusable rule should happen, across screens and repositories? | Like a post and record the interaction |
| Repository | Where does this data come from, and how is it kept consistent? | Posts from cache, server, or both; saving a like |
| Data source | How is one specific store or service accessed? | A network API, a Room database, a file, an in-memory cache |

A UseCase is not a ViewModel, and it is not a repository. It does not hold screen state, and it does not decide caching strategy. It uses repositories the way a ViewModel uses a UseCase: through an interface, for one job.

## 3. A small Kotlin example

The following snippets are teaching snippets, not a complete runnable Android project. They reuse Topic 2's `PostRepository` contract and extend it with a second repository, so the rule genuinely spans more than one data owner.

### 3.1 The rule, as a UseCase

```kotlin
class LikePostUseCase(
    private val postRepository: PostRepository,
    private val analyticsRepository: AnalyticsRepository
) {
    suspend operator fun invoke(postId: String) {
        postRepository.likePost(postId)
        analyticsRepository.recordInteraction(
            Interaction.LikedPost(postId = postId)
        )
    }
}
```

Read the code by responsibility:

- The UseCase owns the *sequence*: like first, then record.
- `PostRepository` owns the like itself — caching, local update, and server sync, as in Topic 2.
- `AnalyticsRepository` owns how an interaction is stored or sent.
- Neither repository knows about the other, and neither knows which screen triggered the rule.

### 3.2 The ViewModel calls the rule, not the repositories

```kotlin
class FeedViewModel(
    private val likePost: LikePostUseCase
) : ViewModel() {
    fun onLikeClicked(postId: String) {
        // Launch the UseCase, then expose the resulting screen state.
    }
}
```

The ViewModel's job shrinks to what Topic 1 assigned it: translate a user action into a rule, and expose screen state. If the rule changes — for example, a bookmark is also recorded — the ViewModel does not change; the UseCase does, once, for every screen that uses it.

### 3.3 Testing the rule in isolation

Because the UseCase depends only on repository interfaces, a test can substitute fakes for both repositories and verify the sequence: the post was liked, and the interaction was recorded. No screen, database, or network is needed to test the rule itself.

## 4. Clean Architecture layers

Clean Architecture formalizes the boundaries Topics 1 and 2 built, by adding a domain layer between the UI layer and the data layer:

```text
UI layer (View + ViewModel)
    ↓ depends on
Domain layer (UseCases + domain models)
    ↓ depends on
Data layer (Repositories + data sources)
```

The dependency rule is the point: inner layers do not know about outer layers.

- The domain layer defines what the app *does*, in its own models. It does not import Android UI classes, Retrofit types, or Room entities.
- The data layer implements repository interfaces defined for the domain layer, mapping network responses and database entities to domain models inside the data layer — exactly the mapping rule from Topic 2, Section 4.
- The UI layer depends on the domain layer, not on repositories directly, when a rule is involved. A screen that only needs data, with no reusable rule, may still use a repository directly; see Section 6.

This is the same picture as Topic 2, with one addition: the domain layer is where application rules live, so they are testable and reusable without a screen attached.

## 5. Where each piece belongs

| Component | Owns | Example |
|---|---|---|
| View | Rendering and user actions | Feed composables |
| ViewModel | Screen state and action handling | Feed loading state, calling `LikePostUseCase` |
| UseCase | A reusable rule spanning repositories | "Like a post and record the interaction" |
| Repository | Coordination, caching, and consistency for one data type | `PostRepository` |
| Data source | One store or service | A Retrofit API interface, a Room DAO |

Guidelines that keep the layers honest:

- A ViewModel should not coordinate two repositories for one rule. If it does, the rule has leaked into screen logic.
- A UseCase should not contain composables, navigation, screen state, caching decisions, or SQL.
- A repository should not call another repository to implement a business rule. If it does, that rule has quietly become a UseCase hiding in the data layer.
- A domain model should not be a database entity or a network response. Mapping belongs in the data layer.

## 6. When not to add a UseCase

Topic 2's guideline still applies, and it is worth repeating here because it is the most common over-engineering mistake in this phase:

> A UseCase is not required for every repository call. Create one when a rule is reused across screens, or when it genuinely combines more than one repository. Wrapping every single repository method in its own UseCase adds a layer with no useful responsibility.

Ask these questions before adding one:

1. Is this rule used from more than one screen or ViewModel?
2. Does it genuinely combine more than one repository, or enforce a sequence that must stay consistent?
3. Would a test of this rule, without a screen, catch a real mistake?

If the answer to all three is no, call the repository directly. The layer can be added later, when a real rule appears, without rewriting the screens — the ViewModel already depends on an interface either way.

## 7. Common misconceptions

| Misconception | More accurate understanding |
|---|---|
| Clean Architecture means every call must pass through a UseCase | A UseCase is for reusable rules. A simple data read can go through a repository directly. |
| A UseCase is just a wrapper around a repository | A wrapper adds no responsibility. A UseCase owns a rule: a sequence, a combination, or a decision reused across screens. |
| More layers are always better | Each layer needs a useful responsibility. A layer that only forwards calls is a cost, not an improvement. |
| The domain layer is where caching lives | Caching is a data policy. It belongs in the repository, not in a UseCase. |
| A UseCase should know about the database or API | It should depend only on repository interfaces. Data sources stay in the data layer. |
| Clean Architecture removes the need for a repository | The repository is the data-layer half of the picture. Clean Architecture builds on it; it does not replace it. |
| Domain models and database entities are the same thing | Domain models describe the app's rules. Entities describe storage. Mapping between them lives in the data layer. |

## 8. Practice and knowledge check

### Exercise 1: Assign responsibilities

Choose an owner for each item:

1. Deciding whether to show cached posts or fetch fresh ones.
2. Liking a post and recording the interaction, from both feed and profile.
3. Deciding what the feed screen shows while posts load.
4. The SQL query for the posts table.

<details>
<summary>Suggested answer</summary>

1. The repository owns the caching strategy.
2. The UseCase owns the reusable rule, coordinating the post and analytics repositories.
3. The ViewModel owns the loading state; the View renders it.
4. The DAO, as the local data source, owns the SQL query.

If you assigned item 2 to a ViewModel, the rule would have to be repeated — and kept consistent — in every screen that likes a post.

</details>

### Exercise 2: Decide whether to add a UseCase

For each case, say whether a UseCase is justified, and why:

1. Loading the feed on one screen, from one repository, with no other rule attached.
2. Toggling a bookmark, which must update the bookmark repository and refresh a "saved" count used on two screens.
3. A settings toggle that writes one value to one repository.

<details>
<summary>Suggested answer</summary>

1. No — a single repository call with no reusable rule. Call the repository directly.
2. Yes — a rule spanning repositories, reused across screens, with a sequence that must stay consistent.
3. No — a single write with no combination or reuse. A UseCase here would only forward the call.

</details>

### Knowledge check

<details>
<summary>When is a UseCase worth adding on top of a repository?</summary>

When a rule is reused across screens, or when it genuinely combines more than one repository. A UseCase that only forwards one repository call adds a layer with no useful responsibility.

</details>

<details>
<summary>What does the domain layer depend on, and what must it not know about?</summary>

It depends on repository interfaces and its own domain models. It must not know about Compose, navigation, Retrofit, or Room.

</details>

<details>
<summary>What breaks if a ViewModel coordinates two repositories directly?</summary>

The rule is copied into every screen that needs it, and the copies can drift apart. Changing the rule means changing every ViewModel that implements it.

</details>

## 9. Interview answer and checkpoint

> A UseCase is a small domain-layer class that owns one reusable application rule, such as liking a post and recording the interaction. It coordinates repository interfaces, without knowing about screens, navigation, databases, or network clients. Clean Architecture places that domain layer between the UI layer and the data layer, with dependencies pointing inward, so rules are testable without a screen and repositories remain swappable. A UseCase is not needed for every repository call — it earns its place when a rule is reused across screens or genuinely spans repositories.

Before continuing, make sure you can:

- [ ] Explain why a ViewModel coordinating two repositories becomes hard to keep consistent.
- [ ] Define a UseCase without mentioning a screen, a database, or a network client.
- [ ] Trace a Like action from the View, through the ViewModel and UseCase, to its repositories.
- [ ] Explain the Clean Architecture dependency rule in one sentence.
- [ ] Decide, with reasons, whether a given feature deserves a UseCase.
- [ ] Distinguish a UseCase from a ViewModel, a repository, and a data source.

**Next learning step:** Apply this layering to a real feature — for example, wiring a UseCase into a ViewModel with manual DI first, then with Hilt, as Phase 5 covers DI later.

## 10. Official references

- [Guide to app architecture](https://developer.android.com/topic/architecture) — responsibilities, layers, and architectural boundaries.
- [Add repository and Manual DI](https://developer.android.com/codelabs/basic-android-kotlin-compose-add-repository) — building a repository and wiring it without a DI framework.
- [ViewModel overview](https://developer.android.com/topic/libraries/architecture/viewmodel) — Android state holders and their lifetime, from Topic 1.

[Back to the course roadmap](../../README.md)
