## Version: 0.2.0 (Pending)
### New Features
* Added iOS/Mac targets. This can now be used in KMP projects targeting those platforms!
* Added GoldenHourDuskEnd and GoldenHourDawnEnd to be consistent with `BlueHourDuskEnd` and `BlueHourDawnEnd`. The golden hour events were conspicuously missing.

### Non-Passive
* Renamed `LunarIllumination.angle` to `LunarIllumination.illuminationAngle`. Too many things that are different were called `angle` which is what led to bugs.
* Renamed `TwilightPhase` to `SolarPhase` and made it so `TwilightPhases` represent _actual_ twilight phases rather than things like `SolarPhase.Day` or `SolarPhase.Night`. This was misleading.
* Broke `SolarPhase` into distinct Dawn and Dusk phases. This really simplified  a lot of things including documentation. Also, this means that `SolarState.solarPhase` returns the correct phase based on whether it's dawn or dusk.
* Did the same thing as above with `LightState` and `LightPhase`.

### Bugfixes
* Made kotlinx-datetime an api dependency
* Fixed bug where `LunarPosition.phase` was inaccurate. It was based on the `LunarIllumination.angle` instead of `LunarIllumination.phase` as it should be.
* Spelling and grammar corrections in API docs.
* Fixed bug where `SolarEventSequence.limit` was not respected in certain situations and the resulting sequence could contain values beyond the specified limit.

### Other
* Bumped Kotlin version to 1.9.22
* Adjust coroutine readme example to use `Duration.INFINITE` as the intended functionality is to run "forever" when technically the limit defaults to `365.days`

## Version: 0.1.0
Initial Release