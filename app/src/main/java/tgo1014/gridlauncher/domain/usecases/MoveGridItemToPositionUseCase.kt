package tgo1014.gridlauncher.domain.usecases

import kotlinx.coroutines.flow.firstOrNull
import tgo1014.gridlauncher.app.Constants
import tgo1014.gridlauncher.domain.AppsManager
import tgo1014.gridlauncher.ui.models.GridItem
import javax.inject.Inject

class MoveGridItemToPositionUseCase @Inject constructor(
    private val appsManager: AppsManager
) {
    suspend operator fun invoke(itemId: Int, requestedX: Int, requestedY: Int) = runCatching {
        val currentGrid = appsManager.homeGridFlow.firstOrNull().orEmpty().toMutableList()
        val index = currentGrid.indexOfFirst { it.id == itemId }
        if (index == -1) return@runCatching
        val item = currentGrid[index]

        // Helper to test overlap
        fun overlaps(a: GridItem, b: GridItem): Boolean {
            val ax1 = a.x
            val ay1 = a.y
            val ax2 = a.x + a.width - 1
            val ay2 = a.y + a.height - 1
            val bx1 = b.x
            val by1 = b.y
            val bx2 = b.x + b.width - 1
            val by2 = b.y + b.height - 1
            return !(ax2 < bx1 || bx2 < ax1 || ay2 < by1 || by2 < ay1)
        }

        // Test requested position first
        val candidate = item.copy(x = requestedX, y = requestedY)
        val others = currentGrid.filterNot { it.id == itemId }
        val collides = others.any { overlaps(it, candidate) }
        var finalItem = candidate

        if (collides) {
            // Find first free slot scanning rows top-to-bottom, left-to-right
            val maxRow = (currentGrid.maxOfOrNull { it.y + it.height } ?: 0) + 20
            run loop@{
                for (y in 0..maxRow) {
                    for (x in 0..(Constants.gridColumns - item.width)) {
                        val c = item.copy(x = x, y = y)
                        if (others.none { overlaps(it, c) }) {
                            finalItem = c
                            return@loop
                        }
                    }
                }
            }
        }

        currentGrid[index] = finalItem
        appsManager.setGrid(currentGrid)
    }
}
