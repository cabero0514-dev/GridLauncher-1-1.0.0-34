package tgo1014.gridlauncher.domain.usecases

import kotlinx.coroutines.flow.first
import tgo1014.gridlauncher.domain.AppsManager
import tgo1014.gridlauncher.ui.models.GridItem
import javax.inject.Inject

class AddWidgetToGridUseCase @Inject constructor(
    private val appsManager: AppsManager,
) {
    suspend operator fun invoke(widget: GridItem) {
        // Collect the current grid from the Flow (suspend)
        val current = appsManager.homeGridFlow.first().toMutableList()
        // Give new id if not present. If grid is empty, start IDs at 0.
        val id = if (widget.id >= 0) widget.id else (current.maxOfOrNull { it.id } ?: -1) + 1
        val item = widget.copy(id = id)
        current.add(item)
        appsManager.setGrid(current)
    }
}
