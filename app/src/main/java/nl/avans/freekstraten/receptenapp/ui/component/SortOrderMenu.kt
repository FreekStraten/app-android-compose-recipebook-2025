package nl.avans.freekstraten.receptenapp.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import nl.avans.freekstraten.receptenapp.util.PreferencesManager

/**
 * A reusable dropdown menu for sorting options
 */
@Composable
fun SortOrderMenu(
    currentSortOrder: Int,
    onSortOrderSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = "Sorteren"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Niet sorteren") },
                onClick = {
                    onSortOrderSelected(PreferencesManager.SORT_NONE)
                    expanded = false
                },
                leadingIcon = if (currentSortOrder == PreferencesManager.SORT_NONE) {
                    { Text("✓") }
                } else null
            )

            DropdownMenuItem(
                text = { Text("Sorteren A-Z") },
                onClick = {
                    onSortOrderSelected(PreferencesManager.SORT_A_Z)
                    expanded = false
                },
                leadingIcon = if (currentSortOrder == PreferencesManager.SORT_A_Z) {
                    { Text("✓") }
                } else null
            )

            DropdownMenuItem(
                text = { Text("Sorteren Z-A") },
                onClick = {
                    onSortOrderSelected(PreferencesManager.SORT_Z_A)
                    expanded = false
                },
                leadingIcon = if (currentSortOrder == PreferencesManager.SORT_Z_A) {
                    { Text("✓") }
                } else null
            )
        }
    }
}