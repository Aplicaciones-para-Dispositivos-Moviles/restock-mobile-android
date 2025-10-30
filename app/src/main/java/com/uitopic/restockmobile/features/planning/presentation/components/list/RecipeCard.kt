package com.uitopic.restockmobile.features.planning.presentation.components.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.uitopic.restockmobile.features.planning.data.remote.models.RecipeDto

@Composable
fun RecipeCard(
    recipe: RecipeDto,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RecipeImage(
                imageUrl = recipe.imageUrl,
                contentDescription = recipe.name
            )

            RecipeInfo(
                name = recipe.name ?: "",
                description = recipe.description ?: "",
                price = recipe.price ?: 0.0,
                suppliesCount = recipe.supplies?.size ?: 0,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RecipeImage(
    imageUrl: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun RecipeInfo(
    name: String,
    description: String,
    price: Double,
    suppliesCount: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.height(80.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            PriceTag(price = price)
            SuppliesCount(count = suppliesCount)
        }
    }
}

@Composable
private fun PriceTag(
    price: Double,
    modifier: Modifier = Modifier
) {
    Text(
        text = "S/ %.2f".format(price),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
private fun SuppliesCount(
    count: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier
    ) {
        Text(
            text = "$count supplies",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
