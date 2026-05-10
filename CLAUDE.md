# Sketchy

Android Jetpack Compose creative coding playground. Each screen is an isolated generative art demo.

## Build Commands

```bash
./gradlew assembleDebug   # build debug APK
./gradlew test            # run JVM unit tests (no emulator needed)
./gradlew lint            # static analysis (baseline tracked in app/lint-baseline.xml)
```

## Module Structure

```
app      → main application, navigation graph, all screen composables
sketch   → Sketch/SketchWithCache composables, math utilities, FPS, screenshot capture
style    → Material3 theme, colours, spacing constants, shared UI components
shaders  → GLSL RuntimeShader definitions (Android 13+)
```

Dependency order: `app → sketch, style, shaders` | `sketch → style`

## Adding a New Sketch

1. Create a `@Composable` function in `app/src/main/kotlin/.../screens/` (or a subdirectory).
2. Wrap drawing logic in `Sketch { time -> ... }` or `SketchWithCache { time -> ... }` from the `:sketch` module.
3. Register it in `app/src/main/kotlin/.../Screens.kt`:
   - Simple screen: `DestinationScreen(label = "My Sketch") { MySketch() }`
   - Grouped screen: add to an existing `NestedNavScreen` list, or create a new one with `nestedContent()`

## Key Abstractions

### `Sketch {}` — `sketch/src/main/.../Sketch.kt`
Animation-loop composable. Drives a `time: Float` value via `AnimationState` each frame.
```kotlin
Sketch(modifier = Modifier.fillMaxSize()) { time ->
    drawCircle(color = Color.White, radius = 100f * sin(time))
}
```
`SketchWithCache {}` is the cache-optimised variant using `drawWithCache` — prefer it when setup work (path allocation, etc.) can be separated from per-frame drawing.

### Math helpers — `sketch/src/main/.../Utils.kt`
| Function | Description |
|----------|-------------|
| `norm(value, min, max)` | Normalise to [0, 1] |
| `lerp(norm, min, max)` | Linear interpolate from normalised value |
| `map(value, srcMin, srcMax, dstMin, dstMax)` | Remap between ranges |
| `Int/Float.mapTo(srcMin, srcMax, dstMin, dstMax)` | Extension shorthand for `map` |
| `Random.nextFloat(min, max)` | Random float in [min, max) |
| `Offset.distanceTo(other)` | Euclidean distance between two offsets |
| `PI`, `TWO_PI`, `HALF_PI` | Float trig constants |

## Navigation Architecture

`Screens.kt` builds the `NavHost` graph. Two screen types:
- `DestinationScreen(label) { Composable }` — leaf screen, navigates directly
- `NestedNavScreen(label, screens)` — category screen backed by a nested nav graph

## Lint Baseline

`app/lint-baseline.xml` records 26 pre-existing issues. Running `./gradlew lint` is expected to pass against this baseline — do not suppress new violations, fix them instead.
