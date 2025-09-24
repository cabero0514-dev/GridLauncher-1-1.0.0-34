package tgo1014.gridlauncher.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import tgo1014.gridlauncher.domain.models.App
import tgo1014.gridlauncher.domain.models.TileSettings
import tgo1014.gridlauncher.ui.composables.TileLayout
import tgo1014.gridlauncher.ui.composables.sheets.SettingsBottomSheet
import tgo1014.gridlauncher.ui.composables.sheets.TileSettingsBottomSheet
import tgo1014.gridlauncher.ui.composables.sheets.LauncherOptionsBottomSheet
import tgo1014.gridlauncher.ui.models.GridItem
import tgo1014.gridlauncher.ui.models.SettingsEvent
import tgo1014.gridlauncher.ui.models.TileEvent
import tgo1014.gridlauncher.ui.theme.GridLauncherTheme
import tgo1014.gridlauncher.ui.theme.conditional
import tgo1014.gridlauncher.ui.theme.onOpenNotificationShade

@Composable
fun GridScreenScreen(
    state: HomeState,
    hazeState: HazeState = remember { HazeState() },
    onItemClicked: (item: GridItem) -> Unit = {},
    onItemLongClicked: (item: GridItem) -> Unit = {},
    onFooterClicked: () -> Unit = {},
    onOpenNotificationShade: () -> Unit = {},
    onTileEvent: (TileEvent) -> Unit = {},
    onSettingsEvent: (SettingsEvent) -> Unit = {},
    onAddWidgetRequested: () -> Unit = {},
) {
    var isOnTop by remember { mutableStateOf(true) }
    var isLauncherOptionsShowing by remember { mutableStateOf(false) }
    TileLayout(
        grid = state.grid,
        tileSettings = state.tileSettings,
        hazeState = hazeState,
        itemBeingEdited = state.itemBeingEdited,
        onBackgroundLongPress = { gx, gy -> isLauncherOptionsShowing = true },
    onItemLongClicked = onItemLongClicked,
    onItemDragStart = { itemId -> onTileEvent(TileEvent.OnTileDragStart(itemId)) },
    onItemDragEnd = { itemId, newX, newY -> onTileEvent(TileEvent.OnTileDragEnd(itemId, newX, newY)) },
        isOnTop = { isOnTop = it },
        onItemClicked = onItemClicked,
        contentPadding = if (state.itemBeingEdited == null) PaddingValues(0.dp) else PaddingValues(
            bottom = 200.dp
        ),
        modifier = Modifier
            .fillMaxSize()
            .onOpenNotificationShade(isOnTop, onOpenNotificationShade)
            // .background(Color.Black) TODO this line recreates old WP 7 style, need to add some options for this in the future
    )
    TileSettingsBottomSheet(
        isShowing = state.isEditMode,
        onTileEvent = onTileEvent,
    )
    SettingsBottomSheet(
        tileSettings = state.tileSettings,
        isShowing = state.isSettingsSheetShowing,
        onSettingsEvent = onSettingsEvent
    )
    LauncherOptionsBottomSheet(
        isShowing = isLauncherOptionsShowing,
        onClose = { isLauncherOptionsShowing = false },
        onOpenSettings = { onSettingsEvent(SettingsEvent.OnSettingsIconClicked) },
        onAddWidget = { onAddWidgetRequested() }
    )
}



@Composable
@Preview
private fun PreviewSmallTile() = GridLauncherTheme {
    GridScreenScreen(
        state = HomeState(
            grid = listOf(
                GridItem(app = App("はい"), width = 1),
            )
        ),
    )
}

@Composable
@Preview
private fun Preview() = GridLauncherTheme {
    GridScreenScreen(
        state = HomeState(
            grid = listOf(
                GridItem(app = App("وأصدقاؤك"), width = 2),
                GridItem(app = App("123"), width = 2, x = 2),
                GridItem(app = App("#1231"), width = 2, x = 4),
                GridItem(app = App("$$$$"), width = 2, y = 2),
                GridItem(app = App("FooBar"), width = 2, y = 2, x = 2),
                GridItem(app = App("Aaaa"), width = 2, y = 2, x = 4),
                GridItem(app = App("AAb"), width = 2, y = 4),
                GridItem(app = App("はい"), width = 2, y = 4, x = 2),
            )
        ),
    )
}