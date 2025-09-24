package tgo1014.gridlauncher.ui.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.pointer.pointerInput
import kotlin.math.roundToInt
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMaxOfOrNull
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import eu.wewox.lazytable.LazyTable
import eu.wewox.lazytable.LazyTableItem
import eu.wewox.lazytable.LazyTableScrollDirection
import eu.wewox.lazytable.lazyTableDimensions
import tgo1014.gridlauncher.app.Constants.gridColumns
import tgo1014.gridlauncher.domain.models.App
import tgo1014.gridlauncher.domain.models.TileSettings
import tgo1014.gridlauncher.domain.usecases.wallpaper.AppHazeStyle
import tgo1014.gridlauncher.ui.models.GridItem
import tgo1014.gridlauncher.ui.theme.modifyIf
import tgo1014.gridlauncher.ui.theme.plus
import androidx.compose.ui.unit.IntOffset
import androidx.compose.foundation.layout.size
import android.util.Log


@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun TileLayout(
    grid: List<GridItem>,
    modifier: Modifier = Modifier,
    hazeState: HazeState = remember { HazeState() },
    columns: Int = gridColumns,
    tileSettings: TileSettings = TileSettings(),
    itemBeingEdited: GridItem? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onBackgroundLongPress: (gx: Int, gy: Int) -> Unit = { _, _ -> },
    isOnTop: (Boolean) -> Unit = {},
    onItemClicked: (item: GridItem) -> Unit = {},
    onItemLongClicked: (item: GridItem) -> Unit = {},
    onItemDragStart: (itemId: Int) -> Unit = {},
    onItemDrag: (itemId: Int, totalDragX: Float, totalDragY: Float) -> Unit = { _, _, _ -> },
    onItemDragEnd: (itemId: Int, newX: Int, newY: Int) -> Unit = { _, _, _ -> },
    footer: @Composable (Modifier) -> Unit = {},
)= BoxWithConstraints(modifier = modifier) {
    val padding = 4.dp
    val gridItemSize = (this.maxWidth - (padding * 2)) / columns
    var firstItemPosition: Float? by remember { mutableStateOf(null) }
    LaunchedEffect(grid.isEmpty()) {
        if (grid.isEmpty()) isOnTop(true)
    }

    // Draw drag preview above the grid with animated offset
    var dragPreview by remember { mutableStateOf<GridItem?>(null) }
    val scope = rememberCoroutineScope()
    // animated offsets in px
    val previewOffsetX = remember { Animatable(0f) }
    val previewOffsetY = remember { Animatable(0f) }
    dragPreview?.let { preview ->
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (previewOffsetX.value).toInt(),
                        y = (previewOffsetY.value).toInt()
                    )
                }
                .size(gridItemSize * preview.width, gridItemSize * preview.height)
                .padding(2.dp)
                .alpha(0.95f)
        ) {
            GridTile(
                item = preview,
                tileSettings = tileSettings,
                isEditMode = true,
                onItemClicked = {},
                onItemLongClicked = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
    val systemBars = WindowInsets.systemBars.asPaddingValues()
    var base by remember { mutableStateOf(PaddingValues()) }
    val configuration = LocalConfiguration.current
    LaunchedEffect(itemBeingEdited) {
        var basePadding = systemBars + PaddingValues(padding) + contentPadding
        if (itemBeingEdited != null) {
            basePadding += PaddingValues(bottom = configuration.screenHeightDp.dp / 2)
        }
        base = basePadding
    }
    val hazeChildModifier = Modifier
        .clip(RoundedCornerShape(tileSettings.cornerRadius))
        .hazeChild(
            state = hazeState,
            style = AppHazeStyle,
        )
    // ...existing code...
    // Grid lines overlay
    if (tileSettings.showGridLines) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cols = columns
            val maxRow = (grid.maxOfOrNull { it.y + it.height } ?: 0) + 1
            val cellW = size.width / cols
            val cellH = if (maxRow > 0) size.height / maxRow else size.height
            val lineColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.12f)
            for (i in 1 until cols) {
                val x = i * cellW
                drawLine(lineColor, Offset(x, 0f), Offset(x, size.height))
            }
            for (j in 1 until maxRow) {
                val y = j * cellH
                drawLine(lineColor, Offset(0f, y), Offset(size.width, y))
            }
        }
    }
    val density = LocalDensity.current
    val gridItemSizePx = with(density) { gridItemSize.toPx() }

    // background long-press handled at the BoxWithConstraints level (see modifier above)

    // pointer modifier for background long press on the grid area
    val pointerModifier = Modifier.pointerInput(Unit) {
        detectTapGestures(onLongPress = { offset ->
            val gx = kotlin.math.floor(offset.x / gridItemSizePx).toInt().coerceAtLeast(0)
            val gy = kotlin.math.floor(offset.y / gridItemSizePx).toInt().coerceAtLeast(0)
            Log.d("TileLayout", "Background long-press at gx=$gx gy=$gy")
            val occupied = grid.any { item ->
                val x1 = item.x
                val y1 = item.y
                val x2 = item.x + item.width - 1
                val y2 = item.y + item.height - 1
                gx in x1..x2 && gy in y1..y2
            }
            if (!occupied) onBackgroundLongPress(gx, gy)
        })
    }

    LazyTable(
        scrollDirection = LazyTableScrollDirection.VERTICAL,
        contentPadding = base,
        dimensions = lazyTableDimensions({ gridItemSize }, { gridItemSize }),
        modifier = pointerModifier,
    ) {
        items(
            items = grid,
            layoutInfo = { tile ->
                LazyTableItem(
                    column = tile.x,
                    row = tile.y,
                    columnsCount = tile.width,
                    rowsCount = tile.height
                )
            }
        ) {
            Box(modifier = Modifier.padding(2.dp)) {
                // Pass drag end events from GridTile; convert pixel delta to grid coordinates
                val density = androidx.compose.ui.platform.LocalDensity.current
                val gridItemSizePx = with(density) { gridItemSize.toPx() }
                GridTile(
                    item = it,
                    tileSettings = tileSettings,
                    isEditMode = it.id == itemBeingEdited?.id,
                    onItemClicked = onItemClicked,
                    onItemLongClicked = onItemLongClicked,
                    onDragStart = { id -> onItemDragStart(id) },
                    onDrag = { id, totalDragX, totalDragY ->
                            val dxCells = (totalDragX / gridItemSizePx).roundToInt()
                            val dyCells = (totalDragY / gridItemSizePx).roundToInt()
                            val newX = (it.x + dxCells).coerceAtLeast(0)
                            val newY = (it.y + dyCells).coerceAtLeast(0)
                            // set logical preview cell position
                            dragPreview = it.copy(x = newX, y = newY)
                            // animate preview pixel offset smoothly to the new cell
                            scope.launch {
                                val targetX = newX * gridItemSizePx
                                val targetY = newY * gridItemSizePx
                                previewOffsetX.animateTo(targetX, animationSpec = spring(stiffness = Spring.StiffnessMedium))
                                previewOffsetY.animateTo(targetY, animationSpec = spring(stiffness = Spring.StiffnessMedium))
                            }
                            onItemDrag(id, totalDragX, totalDragY)
                        },
                        onDragEnd = { itemId, totalDragX, totalDragY ->
                            val dxCells = (totalDragX / gridItemSizePx).roundToInt()
                            val dyCells = (totalDragY / gridItemSizePx).roundToInt()
                            val newX = (it.x + dxCells).coerceAtLeast(0)
                            val newY = (it.y + dyCells).coerceAtLeast(0)
                            // snap animation to final cell then commit
                            scope.launch {
                                val targetX = newX * gridItemSizePx
                                val targetY = newY * gridItemSizePx
                                previewOffsetX.animateTo(targetX, animationSpec = spring(stiffness = Spring.StiffnessLow))
                                previewOffsetY.animateTo(targetY, animationSpec = spring(stiffness = Spring.StiffnessLow))
                                // small delay to let animation finish visually
                                kotlinx.coroutines.delay(80)
                                onItemDragEnd(itemId, newX, newY)
                                dragPreview = null
                                // reset animatables to 0 for next drag
                                previewOffsetX.snapTo(0f)
                                previewOffsetY.snapTo(0f)
                            }
                        },
                    modifier = Modifier
                        .fillMaxSize()
                        .modifyIf(it == grid.firstOrNull()) {
                            onGloballyPositioned { coord ->
                                val y = coord.positionInWindow().y
                                if (firstItemPosition == null) {
                                    firstItemPosition = y
                                }
                                isOnTop(firstItemPosition == y)
                            }
                        }
                        .then(hazeChildModifier)
                )
            }
        }
        // Footer
        val maxY = grid.maxOfOrNull { it.y } ?: 0
        val rowOffset = grid.filter { it.y == maxY }
            .fastMaxOfOrNull { it.height }
            ?: 0
        items(
            count = 1,
            layoutInfo = {
                LazyTableItem(
                    column = 0,
                    row = maxY + rowOffset,
                    columnsCount = columns,
                    rowsCount = 1
                )
            },
            itemContent = { footer(hazeChildModifier) }
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    TileLayout(
        grid = listOf(
            GridItem(app = App(name = "FooBar 1"), width = 4, height = 2, x = 1, y = 0),
            GridItem(app = App(name = "FooBar 2"), width = 1, x = 1, y = 0),
            GridItem(app = App(name = "FooBar 3"), width = 4, x = 0, y = 1),
        ),
        // No footer in preview
    )
}

@Preview
@Composable
private fun PreviewScroll() {
    val items = List(4) {
        Box(
            Modifier
                .fillMaxSize()
                .border(1.dp, Color.Blue)
        )
    }
    TileLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        grid = List(items.size) { index ->
            GridItem(app = App(name = "FooBar 1"), width = 3, x = 0, y = index * 3)
        }
    )
}