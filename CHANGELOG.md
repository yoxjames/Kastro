## Version: 0.2.0 (Pending)
* **NON PASSIVE**: Renamed `LunarIllumination.angle` to `LunarIllumination.illuminationAngle`. Too many things that are different were called `angle` which is what led to the bug above.
* Made kotlinx-datetime an api dependency
* Bumped Kotlin version to 1.9.22
* Fixed bug where `LunarPosition.phase` was inaccurate. It was based on the `LunarIllumination.angle` instead of `LunarIllumination.phase` as it should be.
* Added GoldenHourDuskEnd and GoldenHourDawnEnd to be consistent with `BlueHourDuskEnd` and `BlueHourDawnEnd`. The golden hour events were conspicuously missing.
* Spelling and grammar corrections in API docs.
* Fixed bug where `SolarEventSequence.limit` was not respected in certain situations and the resulting sequence could contain values beyond the specified limit.

## Version: 0.1.0
Initial Release