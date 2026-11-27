# App-specific R8 rules.
#
# No keep rules are needed for our own code: kotlinx.serialization, Retrofit, Hilt, Coil and
# Navigation ship consumer rules for what they reflect on (@Serializable routes and DTOs, Retrofit
# interfaces, generated Hilt components). Verified by running the minified release build: Browse
# (Retrofit + JSON), Pet Detail (typed nav args), Explore and the adoption flow all work.
# Add a rule here only with a comment saying which crash it fixes.

# Defense in depth: strip any android.util.Log call (ours or a library's) from release builds.
# Our own logging is already a no-op in release (see the release source set).
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}
