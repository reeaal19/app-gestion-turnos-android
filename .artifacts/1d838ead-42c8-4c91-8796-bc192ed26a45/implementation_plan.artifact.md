# Fix KSP "unexpected jvm signature V" error

The error `unexpected jvm signature V` is a known issue when using Room with Kotlin 2.0.0 and KSP2. It occurs because Room 2.6.1 doesn't fully support the new KSP2 engine when processing `suspend` functions that return `Unit`.

## Proposed Changes

### Build Configuration

#### [MODIFY] [gradle.properties](file:///C:/Users/Real/AndroidStudioProjects/CalendarioValorian/gradle.properties)
Add `ksp.useKSP2=false` to force KSP to use the stable V1 engine, which is compatible with Room 2.6.1.

### Data Layer (Optional but Recommended)

#### [MODIFY] [TurnoDao.kt](file:///C:/Users/Real/AndroidStudioProjects/CalendarioValorian/app/src/main/java/com/example/calendariovalorian/data/TurnoDao.kt)
Update `guardarTurnoCalendario` to return `Long` instead of `Unit` as a defensive measure.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:kspDebugKotlin` to verify the build error is resolved.
- Run a full build: `./gradlew assembleDebug`.
