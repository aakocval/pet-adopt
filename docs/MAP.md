# Map (Explore tab)

The Explore tab draws its map through one small interface, so the map technology can change
without touching the screen, its ViewModel or its tests.

```kotlin
interface MapProvider {
    @Composable
    fun Map(
        pins: ImmutableList<MapPin>,   // id, GeoPoint, accessibility label
        selectedPinId: String?,
        onPinClick: (pinId: String?) -> Unit,  // null = tap on empty map
        modifier: Modifier,
    )
}
```

- `feature/explore/map/MapProvider.kt`: the interface, `MapPin` and `LocalMapProvider`
- `feature/explore/map/CanvasMapProvider.kt`: the default implementation
- `di/MapModule.kt`: the Hilt binding (the only line that changes when you swap maps)
- `MainActivity` provides the bound instance through `LocalMapProvider`

## Default: a Canvas mock map

`CanvasMapProvider` draws a stylized city with Compose `Canvas`: land, a river and bay, parks, a
road grid, and teardrop pins. Pins are projected from their coordinates (a linear fit of the
pins' bounding box, which is accurate enough at city scale). Taps hit-test the nearest pin within
28dp; each pin is also exposed to TalkBack as a custom action, because a Canvas is otherwise
invisible to screen readers.

Why a mock: no API key, no billing account, nothing to configure. The repo is free to clone and run.

## Swapping in Google Maps Compose

1. Add `com.google.maps.android:maps-compose` and put your key in `local.properties`
   (git-ignored), read it via `manifestPlaceholders`, and restrict the key to this app's package
   and signing certificate in Google Cloud Console.
2. Implement the interface:

   ```kotlin
   class GoogleMapProvider @Inject constructor() : MapProvider {
       @Composable
       override fun Map(pins: ImmutableList<MapPin>, selectedPinId: String?, onPinClick: (String?) -> Unit, modifier: Modifier) {
           GoogleMap(modifier = modifier, onMapClick = { onPinClick(null) }) {
               pins.forEach { pin ->
                   Marker(
                       state = rememberMarkerState(key = pin.id, position = LatLng(pin.location.latitude, pin.location.longitude)),
                       title = pin.label,
                       onClick = { onPinClick(pin.id); true },
                   )
               }
           }
       }
   }
   ```

3. Change the binding in `MapModule` to `GoogleMapProvider`. Nothing else changes.

## Swapping in MapLibre (open source, no Google account)

Same steps with `org.maplibre.gl:android-sdk` (or a MapLibre Compose wrapper) and a tile source
you're allowed to use (self-hosted tiles or a provider with a free tier). Draw pins as a symbol
layer from a GeoJSON source built from `pins`, and map feature taps back to `onPinClick(id)`.

## Things to keep when swapping

- Keep `onPinClick(null)` for taps on empty map: the ViewModel uses it to hide the pet card.
- Keep an accessible way to reach each pin (marker titles / content descriptions).
- Certificate pinning (M7) covers the photo hosts; a real map adds tile hosts to review.
