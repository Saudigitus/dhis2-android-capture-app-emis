package org.saudigitus.emis.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun TextAttributeView(
    attribute: String,
    attributeValue: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.Start)
    ) {
        Text(
            modifier = Modifier
                .wrapContentWidth(Alignment.Start, false)
                .fillMaxWidth(.2f),
            text = attribute,
            softWrap = true,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = attributeValue,
            softWrap = true,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
