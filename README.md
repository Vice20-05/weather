# Időjárás-kliens – Swing + REST API interfész mögött (b-04)

A desktop weather app that gets real data from the free, no-key
[Open-Meteo](https://open-meteo.com/) API.

The point of the task: **the UI has no idea that HTTP exists.** The API call is
hidden behind the `IdojarasSzolgaltatas` interface (Dependency Inversion Principle).
It has three implementations:

- `OpenMeteoKliens`: the real one, using `java.net.http.HttpClient` + Gson
- `MockIdojaras`: fixed data, no internet, can throw an error on purpose
- `CacheeloIdojaras`: Decorator (stretch task) that remembers answers for 10 minutes

The concrete class is chosen **only in `Main`**. Nowhere else is there a `new OpenMeteoKliens()`.

## Requirements

- Java JDK 17 or newer
- No Maven install needed, the project has the Maven Wrapper (`./mvnw`)

## Project structure

```
weather/
├── pom.xml                     -> Maven project (Gson, JUnit 6, Mockito)
├── mvnw, .mvn/               -> Maven Wrapper
├── src/main/java/idojaras/
│   ├── Main.java               -> composition root, --mock / --hiba switches
│   ├── model/                  -> Idojaras (record), Varos (record), IdojarasKivetel
│   ├── szolgaltatas/           -> IdojarasSzolgaltatas, OpenMeteoKliens, MockIdojaras, CacheeloIdojaras
│   └── ui/                     -> IdojarasNezet, IdojarasController (SwingWorker), IdojarasAblak (JFrame)
├── src/test/java/idojaras/     -> 14 JUnit tests (JSON, mock, cache, controller with Mockito)
├── .vscode/launch.json        -> run configs: real / mock / error
├── docs/idojaras.puml          -> PlantUML class diagram
└── screenshots/                -> real mode, mock mode, error message
```

## How to run

```bash
./mvnw compile exec:java                          # real data from Open-Meteo
./mvnw compile exec:java -Dexec.args="--mock"     # mock, no internet needed
./mvnw compile exec:java -Dexec.args="--hiba"     # mock that always fails -> error dialog
```

**In VS Code:** open the `weather` folder. The Java extension recognises the `pom.xml`.
In **Run and Debug** (Ctrl+Shift+D) pick one of the ready-made launch
configurations from `.vscode/launch.json`: *Időjárás (valós)*, *Időjárás (mock)*
or *Időjárás (hiba)*.

## How to run the tests

```bash
./mvnw test
```

Or use the **Testing** panel in VS Code.

| Test class             | What it checks                                                      |
|------------------------|---------------------------------------------------------------------|
| `OpenMeteoKliensTest`  | JSON parsing from fixed strings, broken/missing data → `IdojarasKivetel`, URL format |
| `MockIdojarasTest`     | configured answer, error on purpose                                 |
| `CacheeloIdojarasTest` | second call comes from the cache (Mockito `verify(times(1))`)       |
| `IdojarasControllerTest` | Mockito: success shows data, error shows message, button disabled → enabled |

## Example API call

URL:

```
https://api.open-meteo.com/v1/forecast?latitude=46.54&longitude=24.56&current=temperature_2m,wind_speed_10m
```

Answer (real, 2026-10-28):

```json
{
  "latitude": 46.54601,
  "longitude": 24.56224,
  "generationtime_ms": 0.0376,
  "utc_offset_seconds": 0,
  "timezone": "GMT",
  "timezone_abbreviation": "GMT",
  "elevation": 330.0,
  "current_units": {
    "time": "iso8601",
    "interval": "seconds",
    "temperature_2m": "°C",
    "wind_speed_10m": "km/h"
  },
  "current": {
    "time": "2026-10-28T20:15",
    "interval": 900,
    "temperature_2m": 10.2,
    "wind_speed_10m": 2.0
  }
}
```

Only the `current` object is used: `temperature_2m`, `wind_speed_10m` and `time`.

## How the requirements are met

- **DIP:** `IdojarasController` gets an `IdojarasSzolgaltatas` and an `IdojarasNezet`
  (both interfaces) in its constructor. `IdojarasAblak` doesn't even know the service.
- **Error handling:** `IOException` (no network, timeout), HTTP status other than 200
  and broken JSON are all wrapped into `IdojarasKivetel`. The UI shows the message in a
  `JOptionPane`.
- **EDT is not blocked:** `frissit()` runs the call in `SwingWorker.doInBackground()`.
  The button is disabled ("Betöltés...") until `done()` turns it back on, which also
  happens after an error (`finally`).
- **Decimal point:** the URL is built with `Locale.US`, so it stays `46.54` and not
  `46,54` on a Hungarian system.

## Screenshots

- Real mode: `screenshots/real-mode.png`
- Mock mode: `screenshots/mock-mode.png`
- Error message: `screenshots/error-dialog.png`

## UML

`docs/idojaras.puml`: open it with the PlantUML extension (Alt+D for preview). It shows that
the `ui` package only points to the `IdojarasSzolgaltatas` interface, and only `Main`
has `<<create>>` arrows to the concrete classes.

## Not done (stretch)

- 7-day forecast chart
- OpenAPI (Swagger) description
