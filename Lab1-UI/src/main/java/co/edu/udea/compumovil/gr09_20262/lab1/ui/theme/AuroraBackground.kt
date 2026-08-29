package co.edu.udea.compumovil.gr09_20262.lab1.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Fondo estilo "aurora": un degradado a pantalla completa con varias manchas de
 * color difusas por encima. El [content] se dibuja sobre el fondo.
 *
 * Nota: [Modifier.blur] usa RenderEffect y solo difumina en Android 12 (API 31)+;
 * en versiones anteriores las manchas se ven un poco más marcadas pero el efecto
 * de degradado radial hacia transparente se mantiene.
 */
@Composable
fun AuroraBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val dark = isSystemInDarkTheme()
    val gradient = if (dark) AuroraDarkGradient else AuroraLightGradient
    val blobAlpha = if (dark) 0.40f else 0.22f

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.linearGradient(gradient)),
    ) {
        AuroraBlob(AuroraTeal, blobAlpha, 320.dp, Alignment.TopStart, (-90).dp, (-70).dp)
        AuroraBlob(AuroraViolet, blobAlpha, 360.dp, Alignment.TopEnd, 110.dp, 30.dp)
        AuroraBlob(AuroraBlue, blobAlpha, 380.dp, Alignment.BottomStart, (-70).dp, 90.dp)
        AuroraBlob(AuroraPink, blobAlpha * 0.8f, 300.dp, Alignment.BottomEnd, 80.dp, 60.dp)
        content()
    }
}

@Composable
private fun BoxScope.AuroraBlob(
    color: Color,
    alpha: Float,
    size: Dp,
    alignment: Alignment,
    offsetX: Dp,
    offsetY: Dp,
) {
    Box(
        modifier = Modifier
            .align(alignment)
            .offset(x = offsetX, y = offsetY)
            .size(size)
            .blur(90.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(color.copy(alpha = alpha), Color.Transparent),
                ),
                shape = CircleShape,
            ),
    )
}
