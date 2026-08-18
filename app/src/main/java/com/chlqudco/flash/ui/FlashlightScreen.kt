package com.chlqudco.flash.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chlqudco.flash.FlashlightStatus
import com.chlqudco.flash.FlashlightUiState
import com.chlqudco.flash.R
import com.chlqudco.flash.ads.AdMobBanner
import com.chlqudco.flash.ui.theme.FlashTheme
import com.chlqudco.flash.ui.theme.Night
import com.chlqudco.flash.ui.theme.NightElevated
import com.chlqudco.flash.ui.theme.NightSurface
import com.chlqudco.flash.ui.theme.TextMuted
import com.chlqudco.flash.ui.theme.TextPrimary
import com.chlqudco.flash.ui.theme.TorchYellow

@Composable
internal fun FlashlightScreen(
    state: FlashlightUiState,
    onToggle: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOn = state.status == FlashlightStatus.ON
    val canToggle = state.status != FlashlightStatus.NO_FLASH &&
        state.status != FlashlightStatus.UNAVAILABLE
    val backgroundTop by animateColorAsState(
        targetValue = if (isOn) Color(0xFF24200F) else Night,
        label = "screenBackground"
    )
    val title = stringResource(
        when (state.status) {
            FlashlightStatus.ON -> R.string.flashlight_on_title
            FlashlightStatus.OFF -> R.string.flashlight_off_title
            FlashlightStatus.UNAVAILABLE -> R.string.flashlight_unavailable_title
            FlashlightStatus.NO_FLASH -> R.string.flashlight_missing_title
            FlashlightStatus.PERMISSION_DENIED -> R.string.flashlight_permission_title
            FlashlightStatus.ERROR -> R.string.flashlight_error_title
        }
    )
    val description = stringResource(
        when (state.status) {
            FlashlightStatus.ON -> R.string.flashlight_on_description
            FlashlightStatus.OFF -> R.string.flashlight_off_description
            FlashlightStatus.UNAVAILABLE -> R.string.flashlight_unavailable_description
            FlashlightStatus.NO_FLASH -> R.string.flashlight_missing_description
            FlashlightStatus.PERMISSION_DENIED -> R.string.flashlight_permission_description
            FlashlightStatus.ERROR -> R.string.flashlight_error_description
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(backgroundTop, Night, Color(0xFF05090D))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            FlashlightHeader(isOn = isOn)
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                val compact = maxHeight < 420.dp
                val buttonSize = if (compact) 156.dp else 212.dp
                val verticalSpacing = if (compact) 18.dp else 34.dp

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = if (compact) 22.sp else 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(verticalSpacing))
                    PowerButton(
                        isOn = isOn,
                        enabled = canToggle,
                        size = buttonSize,
                        onToggle = onToggle
                    )
                    Spacer(modifier = Modifier.size(verticalSpacing))
                    Text(
                        text = description,
                        color = TextMuted,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center
                    )
                    if (state.showSettingsAction) {
                        TextButton(onClick = onOpenSettings) {
                            Text(
                                text = stringResource(R.string.open_app_settings),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
            AdMobBanner(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun FlashlightHeader(isOn: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(TorchYellow.copy(alpha = 0.14f), RoundedCornerShape(13.dp)),
            contentAlignment = Alignment.Center
        ) {
            FlashBolt(modifier = Modifier.size(23.dp))
        }
        Text(
            text = stringResource(R.string.app_name),
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 12.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.background(
                color = if (isOn) {
                    TorchYellow.copy(alpha = 0.14f)
                } else {
                    NightElevated
                },
                shape = RoundedCornerShape(50)
            ).padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(if (isOn) TorchYellow else TextMuted, CircleShape)
            )
            Text(
                text = stringResource(
                    if (isOn) R.string.flashlight_on_status else R.string.flashlight_off_status
                ),
                color = if (isOn) TorchYellow else TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 7.dp)
            )
        }
    }
}

@Composable
private fun PowerButton(
    isOn: Boolean,
    enabled: Boolean,
    size: androidx.compose.ui.unit.Dp,
    onToggle: () -> Unit
) {
    val buttonColor by animateColorAsState(
        targetValue = when {
            isOn -> TorchYellow
            enabled -> NightSurface
            else -> NightSurface.copy(alpha = 0.55f)
        },
        label = "powerButtonColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isOn) TorchYellow.copy(alpha = 0.7f) else Color(0xFF2D3B47),
        label = "powerButtonBorder"
    )
    val elevation by animateDpAsState(
        targetValue = if (isOn) 24.dp else 8.dp,
        label = "powerButtonElevation"
    )
    val controlDescription = stringResource(
        if (isOn) R.string.flashlight_turn_off else R.string.flashlight_turn_on
    )
    val currentStateDescription = stringResource(
        if (isOn) R.string.flashlight_on_status else R.string.flashlight_off_status
    )

    Box(
        modifier = Modifier
            .size(size + 28.dp)
            .background(
                if (isOn) TorchYellow.copy(alpha = 0.08f) else Color.Transparent,
                CircleShape
            )
            .border(
                BorderStroke(
                    width = 1.dp,
                    color = if (isOn) TorchYellow.copy(alpha = 0.18f) else Color(0xFF17232D)
                ),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .shadow(elevation = elevation, shape = CircleShape)
                .background(buttonColor, CircleShape)
                .border(BorderStroke(1.dp, borderColor), CircleShape)
                .semantics {
                    contentDescription = controlDescription
                    stateDescription = currentStateDescription
                }
                .toggleable(
                    value = isOn,
                    enabled = enabled,
                    role = Role.Switch,
                    onValueChange = { onToggle() }
                ),
            contentAlignment = Alignment.Center
        ) {
            PowerGlyph(
                color = if (isOn) Night else TextPrimary.copy(alpha = if (enabled) 1f else 0.35f),
                modifier = Modifier.size(size * 0.4f)
            )
        }
    }
}

@Composable
private fun PowerGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val strokeWidth = size.minDimension * 0.1f
        drawArc(
            color = color,
            startAngle = -42f,
            sweepAngle = 264f,
            useCenter = false,
            topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f + size.height * 0.08f),
            size = Size(size.width - strokeWidth, size.height - strokeWidth),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        drawLine(
            color = color,
            start = Offset(size.width / 2f, 0f),
            end = Offset(size.width / 2f, size.height * 0.48f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun FlashBolt(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(size.width * 0.58f, 0f)
            lineTo(size.width * 0.12f, size.height * 0.58f)
            lineTo(size.width * 0.45f, size.height * 0.58f)
            lineTo(size.width * 0.34f, size.height)
            lineTo(size.width * 0.9f, size.height * 0.4f)
            lineTo(size.width * 0.58f, size.height * 0.4f)
            close()
        }
        drawPath(path = path, color = TorchYellow)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF081018)
@Composable
private fun FlashlightScreenPreview() {
    FlashTheme {
        FlashlightScreen(
            state = FlashlightUiState(status = FlashlightStatus.OFF),
            onToggle = {},
            onOpenSettings = {}
        )
    }
}
