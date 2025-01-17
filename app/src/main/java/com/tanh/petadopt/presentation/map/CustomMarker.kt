package com.tanh.petadopt.presentation.map

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mapbox.maps.extension.style.expressions.dsl.generated.color
import com.tanh.petadopt.domain.model.Pet

@Composable
fun CustomMarker(
    modifier: Modifier = Modifier,
    pet: Pet
) {

    Box(
        modifier = modifier
    ) {
        AsyncImage(
            model = pet.photoUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(
                    CircleShape
                )
                .align(Alignment.Center)
        )

    }

}


