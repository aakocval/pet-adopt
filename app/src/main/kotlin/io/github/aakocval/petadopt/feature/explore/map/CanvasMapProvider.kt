package io.github.aakocval.petadopt.feature.explore.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme
import kotlinx.collections.immutable.ImmutableList
import javax.inject.Inject

/**
 * A stylized city drawn with Compose Canvas: land, a river and bay, parks, a road grid and pins.
 * Decorative geometry is laid out in fractions of the canvas, so it fits any size; pins are
 * projected from their coordinates into the same space.
 */
class CanvasMapProvider
    @Inject
    constructor() : MapProvider {
        @Composable
        override fun Map(
            pins: ImmutableList<MapPin>,
            selectedPinId: String?,
            onPinClick: (pinId: String?) -> Unit,
            modifier: Modifier,
        ) {
            val colors =
                MapColors(
                    land = MaterialTheme.colorScheme.surfaceVariant,
                    water = PetAdoptTheme.colors.tiles[WATER_TILE],
                    park = PetAdoptTheme.colors.tiles[PARK_TILE],
                    road = MaterialTheme.colorScheme.surface,
                    pin = MaterialTheme.colorScheme.primary,
                    pinBorder = MaterialTheme.colorScheme.surface,
                )
            val projection = remember(pins) { Projection.fitting(pins) }
            val hitRadius = with(LocalDensity.current) { HIT_RADIUS.toPx() }
            val description = "Map with ${pins.size} pets"

            Canvas(
                modifier =
                    modifier
                        .pointerInput(pins, projection) {
                            detectTapGestures { tap ->
                                val nearest =
                                    pins.minByOrNull {
                                        (
                                            projection.toOffset(
                                                it,
                                                size.toSize(),
                                            ) - tap
                                        ).getDistance()
                                    }
                                val hit =
                                    nearest?.takeIf {
                                        (projection.toOffset(it, size.toSize()) - tap).getDistance() <=
                                            hitRadius
                                    }
                                onPinClick(hit?.id) // tapping empty map clears the selection
                            }
                        }
                        // A Canvas is invisible to TalkBack: expose each pin as a custom action instead.
                        .semantics {
                            contentDescription = description
                            customActions =
                                pins.map { pin ->
                                    CustomAccessibilityAction(pin.label) { onPinClick(pin.id).let { true } }
                                }
                        },
            ) {
                drawCity(colors)
                pins.forEach { pin ->
                    drawPin(projection.toOffset(pin, size), selected = pin.id == selectedPinId, colors = colors)
                }
            }
        }

        private companion object {
            const val WATER_TILE = 2 // sky
            const val PARK_TILE = 1 // mint
            val HIT_RADIUS = 28.dp
        }
    }

private data class MapColors(
    val land: Color,
    val water: Color,
    val park: Color,
    val road: Color,
    val pin: Color,
    val pinBorder: Color,
)

/** Linear projection of the pins' bounding box (plus a margin) onto the canvas. Fine at city scale. */
private class Projection(
    private val minLat: Double,
    private val maxLat: Double,
    private val minLng: Double,
    private val maxLng: Double,
) {
    fun toOffset(
        pin: MapPin,
        size: Size,
    ): Offset {
        val x = (pin.location.longitude - minLng) / (maxLng - minLng)
        val y = (maxLat - pin.location.latitude) / (maxLat - minLat)
        return Offset((x * size.width).toFloat(), (y * size.height).toFloat())
    }

    companion object {
        private const val MARGIN = 0.25
        private const val MIN_SPAN = 0.01

        fun fitting(pins: List<MapPin>): Projection {
            if (pins.isEmpty()) return Projection(0.0, 1.0, 0.0, 1.0)
            val lats = pins.map { it.location.latitude }
            val lngs = pins.map { it.location.longitude }
            val latSpan = maxOf(lats.max() - lats.min(), MIN_SPAN)
            val lngSpan = maxOf(lngs.max() - lngs.min(), MIN_SPAN)
            return Projection(
                minLat = lats.min() - latSpan * MARGIN,
                maxLat = lats.max() + latSpan * MARGIN,
                minLng = lngs.min() - lngSpan * MARGIN,
                maxLng = lngs.max() + lngSpan * MARGIN,
            )
        }
    }
}

private fun androidx.compose.ui.unit.IntSize.toSize() = Size(width.toFloat(), height.toFloat())

@Suppress("MagicNumber") // map layout is expressed as fractions of the canvas
private fun DrawScope.drawCity(colors: MapColors) {
    val w = size.width
    val h = size.height
    drawRect(colors.land)

    // Bay along the right edge and a river winding into it.
    drawRect(colors.water, topLeft = Offset(w * 0.86f, 0f), size = Size(w * 0.14f, h))
    val river =
        Path().apply {
            moveTo(-10f, h * 0.18f)
            cubicTo(w * 0.30f, h * 0.10f, w * 0.40f, h * 0.55f, w * 0.62f, h * 0.48f)
            cubicTo(w * 0.75f, h * 0.44f, w * 0.80f, h * 0.70f, w * 0.90f, h * 0.68f)
        }
    drawPath(river, colors.water, style = Stroke(width = w * 0.05f, cap = StrokeCap.Round))

    // Parks.
    val corner = CornerRadius(w * 0.03f)
    drawRoundRect(colors.park, Offset(w * 0.08f, h * 0.62f), Size(w * 0.26f, h * 0.22f), corner)
    drawRoundRect(colors.park, Offset(w * 0.46f, h * 0.08f), Size(w * 0.18f, h * 0.16f), corner)
    drawRoundRect(colors.park, Offset(w * 0.55f, h * 0.74f), Size(w * 0.20f, h * 0.14f), corner)

    // Road grid and one diagonal avenue.
    val road = w * 0.018f
    listOf(0.30f, 0.56f, 0.82f).forEach { y ->
        drawLine(colors.road, Offset(0f, h * y), Offset(w * 0.86f, h * y), road)
    }
    listOf(0.20f, 0.42f, 0.70f).forEach { x -> drawLine(colors.road, Offset(w * x, 0f), Offset(w * x, h), road) }
    drawLine(colors.road, Offset(0f, h), Offset(w * 0.86f, h * 0.05f), road * 1.4f, cap = StrokeCap.Round)
}

@Suppress("MagicNumber") // pin shape proportions, relative to its radius
private fun DrawScope.drawPin(
    at: Offset,
    selected: Boolean,
    colors: MapColors,
) {
    val radius = (if (selected) 14.dp else 10.dp).toPx()
    val border = 3.dp.toPx()
    // Teardrop: a circle sitting on a point, so the tip marks the exact location.
    val tip =
        Path().apply {
            moveTo(at.x - radius * 0.6f, at.y - radius * 1.1f)
            lineTo(at.x, at.y)
            lineTo(at.x + radius * 0.6f, at.y - radius * 1.1f)
            close()
        }
    val center = Offset(at.x, at.y - radius * 1.6f)
    drawPath(tip, colors.pin)
    drawCircle(colors.pinBorder, radius + border, center)
    drawCircle(colors.pin, radius, center)
    if (selected) drawCircle(colors.pinBorder, radius * 0.4f, center)
}
