package com.example.ui.dialogs

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun WhatIfSimulationDialog(
    onDismiss: () -> Unit,
    onRunScenario: (scenarioPrompt: String) -> Unit
) {
    var customScenario by remember { mutableStateOf("") }

    val presetScenarios = listOf(
        "⚡ 100x nárůst uživatelů" to "Co se stane se systémem, databází a náklady, pokud službu začne používat 100x více lidí za den?",
        "💥 Výpadek cloudu & offline provoz" to "Co když cloudové API vypadne na 12 hodin? Jak zajistit integritu a kontinuitu provozu?",
        "🏴‍☠️ Agresivní globální konkurent" to "Co když zítra velký hráč (např. Google, Meta) spustí stejnou funkci zdarma?",
        "🔒 Přísná legislativa & únik dat" to "Jak systém obstojí při bezpečnostním auditu a přísných regulacích ochrany dat?",
        "💸 Nulový marketingový budget" to "Jak dosáhnout virálního růstu a adopce pouze prostřednictvím samotného produktu bez placené reklamy?"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SlateCard,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = NeonCoral,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Simulace 'What If' Scénářů",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Otestujte odolnost projektu v krizových podmínkách. AI navrhne zranitelná místa i mitigační větve do mapy.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Presets list
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presetScenarios) { (title, prompt) ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SlateCardElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCoral.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onRunScenario("Krizový What-If Scénář: $title. Otázka: $prompt. Analyzuj dopady a přidej do mapy zranitelnosti (RISK) i mitigaci (SOLUTION).")
                                }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCoral
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = prompt,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Custom scenario input
                OutlinedTextField(
                    value = customScenario,
                    onValueChange = { customScenario = it },
                    label = { Text("Vlastní krizový scénář", color = TextSecondary) },
                    placeholder = { Text("Např. Co když dodavatel HW zkrachuje?", color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCoral,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            ElevatedButton(
                onClick = {
                    if (customScenario.isNotBlank()) {
                        onRunScenario("Vlastní What-If Scénář: $customScenario. Analyzuj dopady na mapu a doplň rizika a řešení.")
                    }
                },
                enabled = customScenario.isNotBlank(),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = NeonCoral,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_run_custom_scenario")
            ) {
                Text("Nasimulovat", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
            ) {
                Text("Zavřít")
            }
        }
    )
}
