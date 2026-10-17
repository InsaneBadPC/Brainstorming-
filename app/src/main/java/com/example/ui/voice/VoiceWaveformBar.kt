package com.example.ui.voice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.voice.VoiceState

@Composable
fun VoiceWaveformBar(
    voiceState: VoiceState,
    transcription: String,
    audioRms: Float,
    isAiProcessing: Boolean,
    onMicClick: () -> Unit,
    onStopSpeaking: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "voice_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(650),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val micButtonColor by animateColorAsState(
        targetValue = when (voiceState) {
            VoiceState.LISTENING -> NeonCyan
            VoiceState.SPEAKING -> NeonEmerald
            VoiceState.PROCESSING -> NeonPurple
            else -> SlateCardElevated
        },
        label = "mic_color"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        // Floating transcription bubble when listening or speaking
        AnimatedVisibility(
            visible = (voiceState == VoiceState.LISTENING || voiceState == VoiceState.SPEAKING || isAiProcessing) && transcription.isNotBlank(),
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 }
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SlateCardElevated.copy(alpha = 0.95f),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .padding(bottom = 12.dp, start = 24.dp, end = 24.dp)
                    .fillMaxWidth(0.9f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    if (voiceState == VoiceState.SPEAKING) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "AI Mluví",
                            tint = NeonEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(NeonCyan, CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = transcription,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 17.sp
                        ),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Main Floating Voice Control Capsule
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = SlateCardElevated.copy(alpha = 0.94f),
            shadowElevation = 10.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (voiceState != VoiceState.IDLE) micButtonColor.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
            ),
            modifier = Modifier
                .shadow(
                    elevation = if (voiceState != VoiceState.IDLE) 12.dp else 4.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = micButtonColor
                )
                .clip(RoundedCornerShape(32.dp))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Interactive Mic Icon
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(46.dp)
                        .scale(if (voiceState == VoiceState.LISTENING) pulseScale else 1f)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    micButtonColor.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                        .border(
                            width = if (voiceState == VoiceState.LISTENING) 2.dp else 1.dp,
                            color = micButtonColor,
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .clickable { onMicClick() }
                        .testTag("btn_voice_mic")
                ) {
                    if (isAiProcessing) {
                        CircularProgressIndicator(
                            color = NeonPurple,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = when (voiceState) {
                                VoiceState.SPEAKING -> Icons.Default.VolumeUp
                                else -> Icons.Default.Mic
                            },
                            contentDescription = "Hlasový režim",
                            tint = if (voiceState != VoiceState.IDLE) micButtonColor else TextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Waveform or Status Message
                Column(
                    modifier = Modifier.clickable { onMicClick() }
                ) {
                    when (voiceState) {
                        VoiceState.LISTENING -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Poslouchám...",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NeonCyan
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                // Live animated waveform bars
                                AudioWaveformVisualizer(rms = audioRms)
                            }
                        }
                        VoiceState.SPEAKING -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "AI Odpovídá...",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NeonEmerald
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                AudioWaveformVisualizer(rms = 0.7f)
                            }
                        }
                        VoiceState.PROCESSING -> {
                            Text(
                                text = "Zpracovávám nápad...",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = NeonPurple
                            )
                        }
                        else -> {
                            Text(
                                text = "Klepněte a řekněte nápad",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                            Text(
                                text = "AI nápad rozpracuje do mapy",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                // Stop speaking button if currently reading aloud
                if (voiceState == VoiceState.SPEAKING) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onStopSpeaking,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                            .testTag("btn_stop_speaking")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Zastavit řeč",
                            tint = TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AudioWaveformVisualizer(rms: Float, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = modifier
    ) {
        val barCount = 5
        for (i in 0 until barCount) {
            val variance = when (i) {
                0 -> 0.4f
                1 -> 0.8f
                2 -> 1.0f
                3 -> 0.7f
                else -> 0.5f
            }
            val heightDp = (6 + (rms * 16f * variance)).coerceIn(4f, 22f).dp

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(heightDp)
                    .background(NeonCyan, RoundedCornerShape(2.dp))
            )
        }
    }
}
