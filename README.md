# Kastro
![Maven Central Version](https://img.shields.io/maven-central/v/dev.jamesyox/kastro)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[![Kotlin](https://img.shields.io/badge/kotlin-1.9.23-blue.svg?logo=kotlin)](http://kotlinlang.org)

Kastro is a Kotlin multiplatform library for calculating astronomical events for the Moon and Sun (Luna and Sol). What makes Kastro special is the lazily evaluated `Sequence`-based implementation, which allows you to use the data in powerful ways. Kastro builds on the work of Richard Körber (shred) in his library commons-suncalc. Much of the math in Kastro comes from commons-suncalc but was ported to common Kotlin. 


> [!NOTE] 
> For a pure Java API be sure to check out [Richard Körber's project](https://github.com/shred/commons-suncalc)!.

## Accuracy
Like commons-suncalc, this library strives on getting "close enough" without using a lot of computational resources. 
Most calculations should be accurate to within a minute. Moon phase events may be off by as much as five minutes.

# Getting Started
This project is deployed on Maven Central. Coordinates are as follows:

groupId: `dev.jamesyox`

artifactId: `kastro`

version: `0.2.0`

## Gradle
If you use Gradle you should be able to add the following to your dependencies to use Kastro:
```kotlin
implementation("dev.jamesyox:kastro:0.2.0")
```

## Solar Phases

You can calculate the following solar phases:

| Phase | Description |
|-----:|-----------|
|     Astronomical twlight| During dawn, the sun is increaing in brightness and transitioning from night toward nautical twilight. During dusk, the sun is decreasing in brightness and is transitioning from nautical twilight toward night|
|     Blue hour| Happens during both dawn and dusk, when the sun is below the horizon, causing the light to look mostly blue.|
|     Civil twilight| During dawn, the sun is increasing in brightness and is transitioning from nautical twilight toward day. During dusk, the sun is decreasing in brightness and transitioning from day toward nautical dusk|
|     Day| The sun is above the horizon|
|     Golden hour| Happens during both dawn and dusk, when the sun is close to the horizon, causing the light to look golden. Ideal time to take pictures. |
|     Nadir| Opposite of noon. The sun is at its lowest point below the horizon|
|     Nautical twilight| During dawn, the sun is increasing in brightness and is transitioning from night toward civil twilight. During dusk, the sun is decreasing in brightness and is transitioning from civil twilight toward astronomical twilight|
|     Night| The sun is below the horizon|
|     Noon| Opposite of nadir. The sun is at its highest point above the horizon|
|     Sunrise| The sun's top edge first rises above the horizon|
|     Sunset| The sun's top edge completely disappears below the horizon|

## Lunar Phases

You can calculate the following lunar phases:

| Phase | Description |
|-----:|---------------|
|     First quarter| Moon is increasing in brightness and is transitioning toward full moon|
|     Full moon| Moon is fully illuminated|
|     Last quarter| Moon is decreasing in brightness and is transitioning toward new moon|
|     New moon| Moon is not illuminated|
|     Waning crescent| The moon is decreasing in brightness and is transitioning between last quarter and new moon|
|     Waning gibbous| The moon is decreasing in brightness and is transitioning between full moon and last quarter|
|     Waxing crescent| The moon is increasing in brightness and is transitioning between new moon and first quarter|
|     Waxing gibbous| The moon is increasing in brightness and is transitioning between first quarter and full moon|

## Examples

Kastro leverages Kotlin Sequences to lazily evaluate the algorithms contained within so you can determine anything in a single call, from what time the sun will rise tomorrow, to every sunrise time for the next 10 years. with a single call.

See `ReadmeExamples.kt` for a compiled full text version of all of the examples.

> [!WARNING]
> **first() will throw if your sequence is empty!** Kastro can return empty sequences. For example if you set a limit for 1 minute there may not be any solar events in that timeframe and an empty `Sequence` will be returned.

### What time does the sun set next in Denver, CO?
This use case uses `Sequence`'s `first()` method. Start can be any `Instant` in the past or future. 
`requestedSolarEvents` is an optional field that can make calculations more efficient however you are free
to use any `Sequence` methods to achieve the same results. 

```kotlin
val nextSunset = SolarEventSequence(
    start = clock.now(),
    latitude = 39.7348,
    longitude = -104.9653,
    requestedSolarEvents = listOf(SolarEvent.Sunset) // Not required but makes calculations more efficient
).first() // This example is safe, but first() can throw on empty sequences!
```

### When is solar noon on December 31st, 2023 in Denver, CO?
```kotlin
val solarNoon = SolarEventSequence(
    start = LocalDate(2023, 12, 31).atStartOfDayIn(timeZone),
    latitude = 39.7348,
    longitude = -104.9653,
    requestedSolarEvents = listOf(SolarEvent.Noon)
).first() // This example is safe, but first() can throw on empty sequences!
```

### When are the next sunrise and sunset events in the next week in Denver, CO?
```kotlin
val fullWeek = SolarEventSequence(
    start = clock.now(),
    latitude = 39.7348,
    longitude = -104.9653,
    requestedSolarEvents = listOf(SolarEvent.Sunrise, SolarEvent.Sunset),
    limit = 7.days
).toList()
```

### When does the next Golden Hour begin and end in Denver, CO?
```kotlin
val goldenHour = SolarEventSequence(
    start = clock.now(),
    latitude = 39.7348,
    longitude = -104.9653,
    requestedSolarEvents = listOf(SolarEvent.GoldenHourDusk, SolarEvent.GoldenHourDusk)
).firstOrNull()
```

> [!NOTE] 
>`SolarEvent.GoldenHourDusk` and `SolarEvent.GoldenHourDawn` are not added by default and must be included in `requestedSolarEvents` if you want that information.

### What is the state of the Sun right now in Denver, CO?
```kotlin
val sunState = clock.now().calculateSolarState(
    latitude = 39.7348,
    longitude = -104.9653,
)
```
### What are the sunrise times for every Tuesday in the next year in Denver, CO?
```kotlin
val tuesdaySunrises = SolarEventSequence(
    start = clock.now(),
    latitude = 39.7348,
    longitude = -104.9653,
    requestedSolarEvents = listOf(SolarEvent.Sunrise),
    limit = 365.days // Omitting leap year shenanigans
).filter {
    it.time.toLocalDateTime(timeZone).dayOfWeek == DayOfWeek.TUESDAY 
}.toList()
```
### Does the sun set in the next hour in Denver, CO?
```kotlin
val doesItSet = SolarEventSequence(
    start = clock.now(),
    latitude = 39.7348,
    longitude = -104.9653,
    requestedSolarEvents = listOf(SolarEvent.Sunrise),
    limit = 1.hours
).any() // Returns true if anything is in the sequence
```

### When is the next moonrise in Denver, CO?
```kotlin
val nextMoonrise = LunarHorizonEventSequence(
    start = clock.now(),
    latitude = 39.7348,
    longitude = -104.9653,
    requestedHorizonEvents = listOf(LunarEvent.HorizonEvent.Moonrise)
).first()
```

### When is the next full moon?
```kotlin
val nextFullMoon = LunarPhaseSequence(
    start = clock.now(),
    requestedLunarPhases = listOf(LunarEvent.PhaseEvent.FullMoon)
).first()
```

>[!NOTE]
> This example does not require location because moon phases are the same across Earth.

### What are the next moonrises, moonsets, and moon phases for the next 30 days in Denver, CO?
```kotlin
val moonList = LunarEventSequence(
    start = clock.now(),
    latitude = 39.7348,
    longitude = -104.9653,
    requestedLunarEvents = LunarEvent.all, // Show us everything!
    limit = 30.days
).toList()
```
### Do any full moons happen on Fridays this year?
```kotlin
val fridayFullMoon = LunarPhaseSequence(
    start = clock.now(),
    requestedLunarPhases = listOf(LunarEvent.PhaseEvent.FullMoon),
    limit = 365.days // Omitting leap year shenanigans
).filter { 
    it.time.toLocalDateTime(timeZone).dayOfWeek == DayOfWeek.FRIDAY 
}.any()
```

### Execute some code on every sunset forever
Kastro guarantees all sequences are ordered by time. This means that events closer to the `start` time come before later
events. This means that if you wanted to execute some code on each sunset (maybe to turn off your lights?) the following
would work. 

Please note you will need to add [kotlinx-coroutines](https://github.com/Kotlin/kotlinx.coroutines) as a dependency to do this. Kastro strives to include as few dependencies as possible (really just [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime))
```kotlin
SolarEventSequence(
    start = clock.now(),
    latitude = latitude,
    longitude = longitude,
    requestedSolarEvents = listOf(SolarEvent.Sunset),
    limit = Duration.INFINITE
).asFlow()
    .onEach { delay(it.time) - clock.now() }
    .collect { doSomething() } // The world is your oyster!
```

## Contributing
Pull requests are welcome. Feel free to fork and open a PR. Beforeopening a PR, make sure that both tests detekt static analysis pass.

```bash
./gradlew allTest detektAll
```
Should return successfully

You may run into issues depending on your host OS. At the very least ensure that the following returns successfully:

```bash
./gradlew jvmTest detektAll
```

This project is still alpha so API changes are possible, but we strive for no breaking changes.
Run `./gradlew apiCheck` to see if your changes maintain binary compatibility! 
Enhancements to the overall shape of the API are welcome though as this has not yet reached the `1.0` milestone.

## Future Work
I ran into some difficulties getting the height offset calculation to work correctly for `SolarEventSequence`. I hope to eventually resolve that but didn't think it should block an alpha release. It's something I want for the future `1.0` release.

I would also like to add additional KMP targets. I recently added various Apple targets. I am currently looking into WASM targets next.

I am also curious to potentially make the library usable for other languages like Javascript. This library is a Kotlin Multiplatform project, but it would be cool to also have it be on npm for use in Javascript/Typescript projects or even be a Swift package (SPM) for use on iOS/Apple targets. There are some challenges to doing that (such as how the exposed API could be adapted to better fit those languages) but I plan to actively look into this as it's something I am generally curious about.

# References
* “Blue Hour – Magic Hour.” Timeanddate.com, 2019, www.timeanddate.com/astronomy/blue-hour.html.
* “Golden Hour – When Sunlight Turns Magical.” Www.timeanddate.com, www.timeanddate.com/astronomy/golden-hour.html.
* Körber, Richard. “Shred/Commons-Suncalc.” GitHub, 16 Jan. 2024, github.com/shred/commons-suncalc.
* Meeus, Jean. Astronomical Algorithms. Richmond, Va., Willmann-Bell, 1998.‌
* Montenbruck, Oliver, and Thomas Pfleger. Astronomy on the Personal Computer. Springer, 14 Mar. 2013.
* NASA. “Moon Phases | Phases, Eclipses & Supermoons.” Moon: NASA Science, https://moon.nasa.gov/moon-in-motion/phases-eclipses-supermoons/moon-phases/
* US Department of Commerce, NOAA. “Definitions of Twilight.” www.weather.gov, www.weather.gov/fsd/twilight.

‌
