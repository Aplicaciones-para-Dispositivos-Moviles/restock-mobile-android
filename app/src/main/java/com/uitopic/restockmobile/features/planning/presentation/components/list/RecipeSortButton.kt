package com.uitopic.restockmobile.features.planning.presentation.components.list

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun RecipeSortButton(
    isSortedByPriceDesc: Boolean,
    onToggleSort: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggleSort,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isSortedByPriceDesc) Icons.Default.KeyboardArrowDown
            else Icons.Default.KeyboardArrowUp,
            contentDescription = "Sort by price",
            tint = if (isSortedByPriceDesc) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface
        )
    }
}
