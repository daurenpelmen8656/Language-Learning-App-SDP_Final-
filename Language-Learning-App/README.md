# Language Learning App

University design patterns final project built with Java 21 and Maven.

**Team:** Yelzhan, Dauren, Turar
**Group:** SE-2535

## Run

Run the test suite:

```bash
mvn test
```

Run `org.example.Main` from your IDE. The application includes an optional JavaFX splash screen and timed answer dialogs.

## How to run the web UI

Run `org.example.web.WebMain` from your IDE, then open [http://localhost:8080](http://localhost:8080). The server uses the JDK `HttpServer` and serves the offline HTML, CSS, and JavaScript from `src/main/resources/web`.

### Web API

- `GET /api/lesson` returns the lesson, vocabulary, decorated cards, and exercise data.
- `POST /api/answer` accepts `{"word":"apple","correct":true}` and returns review timing, progress, and unlocked achievements. The session is saved after each answer.
- `GET /api/progress` returns the current session totals and due word count.
- `POST /api/strategy` accepts `{"name":"leitner"}`, `{"name":"sm2"}`, or `{"name":"simple"}`.
- `GET /api/events` returns the latest 30 pattern events.

## Architecture

`Main` asks `AppConfig` to assemble the application dependencies. `ConsoleUi` handles console interaction and delegates lesson preparation, review, and progress requests to the thin `LearningFacade`. `LessonService` uses dictionary, decorator, factory, and builder components to return lesson data. `ReviewService` uses the selected strategy, updates the `UserProgress` abstraction, and publishes answer events. The `UserSession` singleton implements `UserProgress` and owns the active session state.

## Patterns and authors

| Pattern | Classes | Author |
|---|---|---|
| Builder | `LessonBuilder`, `Lesson` | Yelzhan |
| Factory Method | `ExerciseFactory`, `Exercise` and implementations | Yelzhan |
| Adapter | `DictionaryAdapter`, `DictionaryService`, `ExternalDictionaryApi` | Turar |
| Decorator | `WordCard` and decorators | Turar |
| Strategy | `ReviewStrategy`, `LeitnerStrategy`, `Sm2Strategy`, `SimpleIntervalStrategy` | Dauren |
| Facade | `LearningFacade`, `LessonService`, `ReviewService` | Dauren |
| Observer | `ProgressPublisher`, `ProgressEvent`, observers | Project team |
| Singleton | `UserSession`, `UserProgress`, `WordProgress` | Project team |
