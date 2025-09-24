package tgo1014.gridlauncher.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.input.pointer.pointerInput
import tgo1014.gridlauncher.ui.theme.detectDragGestures
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import tgo1014.gridlauncher.domain.models.App
import tgo1014.gridlauncher.domain.models.TileSettings
import tgo1014.gridlauncher.ui.models.GridItem
import tgo1014.gridlauncher.ui.theme.AsyncImage
import tgo1014.gridlauncher.ui.theme.GridLauncherTheme
import tgo1014.gridlauncher.ui.theme.flipRandomly
import tgo1014.gridlauncher.ui.theme.isPreview
import tgo1014.gridlauncher.ui.theme.modifyIf
import tgo1014.gridlauncher.ui.theme.tileEditMode

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GridTile(
    item: GridItem,
    modifier: Modifier = Modifier,
    tileSettings: TileSettings = TileSettings(),
    isEditMode: Boolean = false,
    onItemClicked: (item: GridItem) -> Unit = {},
    onItemLongClicked: (item: GridItem) -> Unit = {},
    onDragStart: (itemId: Int) -> Unit = {},
    onDrag: (itemId: Int, totalDragX: Float, totalDragY: Float) -> Unit = { _, _, _ -> },
    onDragEnd: (itemId: Int, totalDragX: Float, totalDragY: Float) -> Unit = { _, _, _ -> },
) {
    val shape = RoundedCornerShape(tileSettings.cornerRadius)
    val app = item.app
    // Implement basic drag detection. We accumulate total drag and call onDragEnd when released.
    var totalDragX = 0f
    var totalDragY = 0f
    val haptic = LocalHapticFeedback.current
    Box(
        modifier = Modifier
            .modifyIf(!isEditMode && tileSettings.isTileFlipEnabled) {
                flipRandomly()
            }
            .tileEditMode(isEditMode)
            .clip(shape)
            .pointerInput(item.id) {
                detectDragGestures(
                    onDragStart = {
                        totalDragX = 0f; totalDragY = 0f
                        // haptic feedback on drag start
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onDragStart(item.id)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDragX += dragAmount.x
                        totalDragY += dragAmount.y
                        onDrag(item.id, totalDragX, totalDragY)
                    },
                    onDragEnd = {
                        // haptic feedback on drag end
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onDragEnd(item.id, totalDragX, totalDragY)
                    },
                    onDragCancel = {
                        // ignore
                    }
                )
            }
            .combinedClickable(
                onClick = { onItemClicked(item) },
                onLongClick = { onItemLongClicked(item) }
            )
            .then(modifier)
    ) {
        if (!tileSettings.isTransparencyEnabled) {
            AsyncImage(
                model = app.icon.bgFile,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxSize()
            )
        }
        val onContainer = MaterialTheme.colorScheme.onPrimaryContainer
        val contentColor = contentColorFor(MaterialTheme.colorScheme.primaryContainer)
        val textColor = remember {
            when {
                tileSettings.isTransparencyEnabled -> contentColor
                app.icon.bgFile == null -> onContainer
                app.icon.isLightBackground -> Color.Black
                else -> Color.White
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            val iconModifier = Modifier.align(Alignment.Center)
            if (isPreview) {
                Image(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = iconModifier,
                )
            } else {
                AsyncImage(
                    model = item.app.icon.iconFile,
                    contentDescription = null,
                    modifier = iconModifier,
                )
            }
            if (item.width > 1 && item.height > 1 && !tileSettings.isAppLabelsHidden) {
                Text(
                    text = item.app.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = textColor,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(8.dp)
                        .padding(horizontal = (tileSettings.cornerRadius * 0.5).dp),
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
private fun PreviewEdit() = GridLauncherTheme {
    val gridCellSize = 100.dp
    val item = GridItem(app = App("Foobar"), width = 1)
    Box(
        Modifier
            .height(gridCellSize * item.height)
            .width(gridCellSize * item.width)
    ) {
        GridTile(item = item, isEditMode = true)
    }
}

@Composable
@Preview(wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE)
private fun PreviewSmallSquare() = GridLauncherTheme {
    val gridCellSize = 100.dp
    val item = GridItem(app = App("Foobar"), width = 1)
    Box(
        Modifier
            .height(gridCellSize * item.height)
            .width(gridCellSize * item.width)
    ) {
        GridTile(item = item)
    }

}

@Composable
@Preview(wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE)
private fun PreviewMediumSquare() = GridLauncherTheme {
    val gridCellSize = 100.dp
    val item = GridItem(app = App("Foobar"), width = 2)
    Box(
        Modifier
            .height(gridCellSize * item.height)
            .width(gridCellSize * item.width)
    ) {
        GridTile(item = item)
    }
}

@Composable
@Preview(wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE)
private fun PreviewLargeRectangle() = GridLauncherTheme {
    val gridCellSize = 100.dp
    val item = GridItem(app = App("Foobar"), width = 4, height = 2)
    Box(
        Modifier
            .height(gridCellSize * item.height)
            .width(gridCellSize * item.width)
    ) {
        GridTile(item = item)
    }
}