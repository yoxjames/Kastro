# Kastro
Kastro is a pure Kotlin library for calculating information about the Moon and Sun (Luna and Sol). What makes Kastro 
special is the lazily evaluated `Sequence` based implementation for calculating the times of astronomical events. This 
allows the user to use the data in powerful ways. Kastro builds on the work of Richard Körber (shred) in his library 
commons-suncalc. Much of the math in Kastro comes from commons-suncalc but was ported to common Kotlin. For a pure Java 
API be sure to check his project out https://github.com/shred/commons-suncalc!

## Note on Accuracy
Like commons-suncalc, this library strives on getting "close enough" without using a ton of computational resources. 
Most calculations should be accurate to within a minute. Moon phase events (Full Moon, New Moon, etc) may be off by as 
much as five minutes.

## Getting Started
This project is deployed on Maven Central. Coordinates are as follows:

groupId: `dev.jamesyox`

artifactId: `kastro`

version: `0.1.0`

This project depends on kotlinx-datetime. So you will need to add that as a dependency to use Kastro. This will be corrected in the next release of Kastro where kotlinx-datetime will be made an api dependency.

If you use Gradle you should be able to simply add:
```kotlin
implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
implementation("dev.jamesyox:kastro:0.1.0")
```
to your dependencies and be able to use the library.

## Cookbook
Kastro leverages Kotlin Sequences to lazily evaluate the algorithms contained within. This means that you can determine
what time the sun will rise tomorrow, or every sunrise time for the next 10 years with a single call. I think a few
examples will make it clear how powerful this approach is. You can use all the power of Kotlin's Sequences (stdlib) to
answer all sorts of questions. See `ReadmeExamples.kt` for a compiled full text version of all of these.

### What time does the sun set next?
Simple use case that uses `Sequence`'s `first()` method. Start can be any `Instant` in the past or future. 
`requestedSolarEvents` is an optional field that can make calculations more efficient however you are free
to use any `Sequence` methods to achieve the same results. 

> :warning: **first() will throw if your sequence is empty!** Kastro can return empty sequences. For example if you set 
> a limit for 1 minute there may not be any solar events in that timeframe, thus an empty `Sequence` would be returned!

```kotlin
val nextSunset = SolarEventSequence(
    start = clock.now(),
    latitude = latitude,
    longitude = longitude,
    requestedSolarEvents = listOf(SolarEvent.Sunset) // Not required but makes calculations more efficient
).first() // This example is safe, but first() can throw on empty sequences!
```

### When is solar noon on the 13th of December?
Here we take an arbitrary date and find solar noon.
```kotlin
val solarNoon = SolarEventSequence(
    start = LocalDate(2023, 12, 13).atStartOfDayIn(timeZone),
    latitude = latitude,
    longitude = longitude,
    requestedSolarEvents = listOf(SolarEvent.Noon)
).first() // This example is safe, but first() can throw on empty sequences!
```

### Show me a full week of sunrise and sunset times.
```kotlin
val fullWeek = SolarEventSequence(
    start = clock.now(),
    latitude = latitude,
    longitude = longitude,
    requestedSolarEvents = listOf(SolarEvent.Sunrise, SolarEvent.Sunset),
    limit = 7.days
).toList()
```

### When does the next Golden Hour begin and end?
There are two Golden Hours, one at dusk while the sun is setting, and the other at dawn while the sun is rising. These
are represented by `SolarEvent.GoldenHourDusk` and `SolarEvent.GoldenHourDawn`. So you could get the next Golden Hour
with the following code:
```kotlin
val goldenHour = SolarEventSequence(
    start = clock.now(),
    latitude = latitude,
    longitude = longitude,
    requestedSolarEvents = listOf(SolarEvent.GoldenHourDusk, SolarEvent.GoldenHourDusk)
).firstOrNull()
```
`SolarEvent.GoldenHourDusk` and `SolarEvent.GoldenHourDawn` are not added by default and must be included in
`requestedSolarEvents` if you want that information.

### What is the state of the Sun right now?
```kotlin
val sunState = clock.now().calculateSolarState(
    latitude = latitude,
    longitude = longitude
)
```
For any given instant you can see the state of the sun including information like altitude and azimuth.

### Show me sunrise times for every Tuesday for a year.
```kotlin
val tuesdaySunrises = SolarEventSequence(
    start = clock.now(),
    latitude = latitude,
    longitude = longitude,
    requestedSolarEvents = listOf(SolarEvent.Sunrise),
    limit = 365.days // Omitting leap year shenanigans
).filter {
    it.time.toLocalDateTime(timeZone).dayOfWeek == DayOfWeek.TUESDAY 
}.toList()
```
This simply makes use of `Sequence`'s `filter` function. You can use any of Kotlin's sequence operators to work with the
data though.

### Does the sun set in the next hour?
Sometimes an empty result is logically relevant.

```kotlin
val doesItSet = SolarEventSequence(
    start = clock.now(),
    latitude = latitude,
    longitude = longitude,
    requestedSolarEvents = listOf(SolarEvent.Sunrise),
    limit = 1.hours
).any() // Returns true if anything is in the sequence
```

### When is the next moonrise?
Lunar horizon events can be requested with `LunarHorizonEventSequence`
```kotlin
val nextMoonrise = LunarHorizonEventSequence(
    start = clock.now(),
    latitude = latitude,
    longitude = longitude,
    requestedHorizonEvents = listOf(LunarEvent.HorizonEvent.Moonrise)
).first()
```

### When is the next full moon?
Lunar phase events can be requested with `LunarPhaseSequence`. Note that this sequence does not require location as moon
phases are the same across planet Earth.
```kotlin
val nextFullMoon = LunarPhaseSequence(
    start = clock.now(),
    requestedLunarPhases = listOf(LunarEvent.PhaseEvent.FullMoon)
).first()
```

### Show me a list of Moonrise, moonsets, and moon phases for the next 30 days
If we wanted to combine the results of `LunarHorizonEventSequence` and `LunarPhaseSequence` use `LunarEventSequence`.
```kotlin
val moonList = LunarEventSequence(
    start = clock.now(),
    latitude = latitude,
    longitude = longitude,
    requestedLunarEvents = LunarEvent.all, // Show us everything!
    limit = 30.days
).toList()
```


### Do any full moons happen on Fridays this year
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

Please note you will need to add [kotlinx-coroutines](https://github.com/Kotlin/kotlinx.coroutines) as a dependency to do this. Kastro strives to include as few 
dependencies as possible (really just [kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime))
```kotlin
SolarEventSequence(
    start = clock.now(),
    latitude = latitude,
    longitude = longitude,
    requestedSolarEvents = listOf(SolarEvent.Sunset)
).asFlow()
    .onEach { delay(it.time) - clock.now() }
    .collect { doSomething() } // Whatever you dream up!
```

## Contributing
Pull requests are welcome. Feel free to fork and open a PR. Prior to opening your PR ensure that tests pass and detekt 
static analysis also passes

```bash
./gradlew allTest detektAll
```
Should return successfully

You may run into issues depending on your host os. At the very least ensure that

```bash
./gradlew jvmTest detektAll
```

returns successfully.

This project is still alpha so API changes are possible. However, if they can be avoided then we should strive for that. 
Run `./gradlew apiCheck` to see if your changes maintain binary compatibility! Enhancements to the overall shape of the
API are welcome though as this has not yet reached the `1.0` milestone.

## Future Work
I ran into some difficulties getting the height offset calculation to work correctly for `SolarEventSequence`. I hope to 
eventually resolve that but didn't think it should block an alpha release. Something I definitely want for `1.0`.

I would also like to add additional KMP targets. I don't own any Apple products, so I cannot build any Apple targets. 
I hope to eventually have a solution to that if there is demand. I am also curious to look into WASM and any other KMP
targets added in the future.

I am also curious to potentially make the library usable for other languages like Javascript. This library is a Kotlin Multiplatform project, but it would be cool to also have it be on npm for use in Javascript/Typescript projects or even be a Swift package (SPM) for use on iOS/Apple targets. There are some challenges to doing that (such as how the exposed API could be adapted to better fit those languages) but I plan to actively look into this as it's something I am generally curious about.

## References
* commons-suncalc (2023), Richard Körber, https://github.com/shred/commons-suncalc
* "Astronomy on the Personal Computer", 4th edition, by Oliver Montenbruck and Thomas Pfleger
* "Astronomical Algorithms" by Jean Meeus