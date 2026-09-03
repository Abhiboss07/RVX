# Keep the NotificationListenerService and its manifest-declared entry points.
-keep class com.rvx.companion.media.MediaObserverService { *; }
-keep class com.rvx.companion.edge.EdgeLightingService { *; }
# Glyph controllers.
-keep class com.rvx.companion.glyph.** { *; }
# Nothing Glyph Matrix SDK (vendored .aar) — keep the whole package + AIDL stubs.
-keep class com.nothing.ketchum.** { *; }
-keep class com.nothing.thirdparty.** { *; }
-keepattributes *Annotation*
