package com.mustafakoceerr.thememanager.sample

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.mustafakoceerr.thememanager.ThemeMode
import com.mustafakoceerr.thememanager.ThemeSettings
import com.mustafakoceerr.thememanager.compose.ManagedTheme
import com.mustafakoceerr.thememanager.isDynamicColorAvailable

@Composable
fun ThemeScreen(
    settings: ThemeSettings,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onDynamicColorChanged: (Boolean) -> Unit,
    dynamicColorAvailable: Boolean = isDynamicColorAvailable,
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Text(stringResource(R.string.title), style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))
            Column(Modifier.selectableGroup()) {
                ThemeMode.entries.forEach { mode ->
                    ThemeModeOption(
                        label = mode.labelRes,
                        selected = settings.mode == mode,
                        onClick = { onThemeModeSelected(mode) },
                    )
                }
            }
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            DynamicColorOption(
                checked = settings.useDynamicColor,
                enabled = dynamicColorAvailable,
                onCheckedChange = onDynamicColorChanged,
            )
            Spacer(Modifier.height(24.dp))
            ColorPreview()
        }
    }
}

@get:StringRes
private val ThemeMode.labelRes: Int
    get() = when (this) {
        ThemeMode.SYSTEM -> R.string.theme_mode_system
        ThemeMode.LIGHT -> R.string.theme_mode_light
        ThemeMode.DARK -> R.string.theme_mode_dark
    }

@Composable
private fun ThemeModeOption(@StringRes label: Int, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Spacer(Modifier.width(8.dp))
        Text(stringResource(label), style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun DynamicColorOption(checked: Boolean, enabled: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = checked, enabled = enabled, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(stringResource(R.string.dynamic_color), style = MaterialTheme.typography.bodyLarge)
            Text(
                text = stringResource(
                    if (enabled) R.string.dynamic_color_description else R.string.dynamic_color_unavailable,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(Modifier.width(16.dp))
        Switch(checked = checked, onCheckedChange = null, enabled = enabled)
    }
}

@Composable
private fun ColorPreview() {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(stringResource(R.string.preview_title), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(stringResource(R.string.preview_body), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {}) { Text(stringResource(R.string.preview_filled)) }
                FilledTonalButton(onClick = {}) { Text(stringResource(R.string.preview_tonal)) }
                OutlinedButton(onClick = {}) { Text(stringResource(R.string.preview_outlined)) }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ThemeScreenPreview() {
    val settings = ThemeSettings()
    ManagedTheme(settings, SampleLightColors, SampleDarkColors) {
        ThemeScreen(settings = settings, onThemeModeSelected = {}, onDynamicColorChanged = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun ThemeScreenDarkUnavailablePreview() {
    val settings = ThemeSettings(mode = ThemeMode.DARK)
    ManagedTheme(settings, SampleLightColors, SampleDarkColors) {
        ThemeScreen(
            settings = settings,
            onThemeModeSelected = {},
            onDynamicColorChanged = {},
            dynamicColorAvailable = false,
        )
    }
}
