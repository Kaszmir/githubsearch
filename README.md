# GitHub Search Autocomplete

A reusable Android autocomplete component that searches GitHub users and                                                                                                                                                                         
repositories in parallel, sorts results alphabetically, and opens the
selected item in the browser.

Single-module Android app — Compose, Hilt, Retrofit, kotlinx.serialization,                                                                                                                                                                      
coroutines + Flow.

## How to run

Open in Android Studio and run the app

No configuration required — the app calls the public GitHub API anonymously                                                                                                                                                                      
(60 req/h rate limit).

## Architecture

Clean-architecture flavoured, single-feature module:

```
feature/autocomplete/
├── data/           # Retrofit API, DTOs, repositories, error mapper
├── domain/         # Repository contracts, SearchUseCase, SearchError model
└── presentation/   # AutoCompleteWidget, ViewModel, State, Action, UiEffect
```

