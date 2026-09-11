# News Bubbles

Android app showing news headlines grouped into animated category bubbles.
Built with **Clean Architecture**, Hilt, Coroutines, and Jetpack Compose.

---

## Setup

1. Get a free key at https://newsapi.org/register.
2. Add it to `local.properties` (create if missing):
   ```
   NEWS_API_KEY=your_actual_key_here
   ```
   `local.properties` is gitignored; the key is injected into `BuildConfig.NEWS_API_KEY` at compile time.

---

## Architecture

Clean Architecture, three layers with a strict dependency rule: **Presentation → Domain ← Data**.

| Layer | What lives here | Depends on |
|---|---|---|
| **Domain** | `Article`, `NewsCategory`, `NewsRepository` interface, `GetArticlesUseCase` | Nothing outside itself |
| **Data** | `ArticleDto`/`NewsResponseDto`, `NewsApiService`, `ArticleMapper`, `NewsRepositoryImpl` | Domain interfaces & entities |
| **Presentation** | ViewModels, Compose screens, `BubbleUiModel`, `BubblePhysics`, navigation, theme | Domain entities & use cases |
| **DI** | Hilt modules (`NetworkModule`, `RepositoryModule`) | All layers (wires them together) |

**Notable choices:**
- ViewModel calls `GetArticlesUseCase`, never `NewsRepository` directly.
- `ArticleMapper` converts DTOs → domain `Article` at the data/domain boundary — presentation never sees a DTO.
- `BubbleUiModel` keeps UI concerns (color, icon) out of the domain `NewsCategory`.
- `RepositoryModule` `@Binds` `NewsRepository` → `NewsRepositoryImpl`; domain never references the impl class.

---

## Third-party libraries

| Library | Why |
|---|---|
| **Retrofit + OkHttp** | No built-in HTTP/REST client in Android |
| **Coil** | Async image loading for `urlToImage`; no built-in Compose equivalent |

Bubble physics is hand-rolled (~60 lines, `BubblePhysics.kt`) — no physics library needed.
