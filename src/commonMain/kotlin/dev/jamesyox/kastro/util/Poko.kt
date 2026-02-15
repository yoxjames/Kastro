package dev.jamesyox.kastro.util

/*
 * I added these to remove to the Poko annotations runtime dependency. For details on this see this discussion:
 * https://github.com/drewhamilton/Poko/issues/328
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
internal annotation class Poko {

    @Retention(AnnotationRetention.SOURCE)
    @Target(AnnotationTarget.PROPERTY)
    annotation class ReadArrayContent
}
