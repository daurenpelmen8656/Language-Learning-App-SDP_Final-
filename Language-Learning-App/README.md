# Language Learning App

University design patterns final project built with Java 21 and Maven.

**Team:** Yelzhan, Dauren, Turar
**Group:** SE-2535

## Run

Run the test suite:

```bash
mvn test
```

Start `org.example.Main` from your IDE, or run these commands from the project directory:

```bash
mvn compile
java -cp target/classes org.example.Main
```

Then open [http://localhost:8080](http://localhost:8080). Stop the server with `Ctrl+C`.

## How to run the web UI

`org.example.Main` delegates to `org.example.web.WebMain`. The server uses the JDK `HttpServer` and serves the offline HTML, CSS, and JavaScript from `src/main/resources/web`.

### Web API

- `GET /api/lesson` returns the lesson, vocabulary, decorated cards, and exercise data.
- `POST /api/answer` accepts `{"word":"apple","correct":true}` and returns review timing, progress, and unlocked achievements. The session is saved after each answer.
- `GET /api/progress` returns the current session totals and due word count.
- `POST /api/strategy` accepts `{"name":"leitner"}`, `{"name":"sm2"}`, or `{"name":"simple"}`.
- `GET /api/events` returns the latest 30 pattern events.

## Architecture

`WebMain` assembles the application dependencies and starts the HTTP server. The HTML, CSS, and JavaScript frontend calls the Java API; `LessonService` returns lesson data built with dictionary, decorator, factory, and builder components. `ReviewService` applies the selected strategy, updates `UserProgress`, and publishes answer events. The `UserSession` singleton implements `UserProgress` and owns the active session state.

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
