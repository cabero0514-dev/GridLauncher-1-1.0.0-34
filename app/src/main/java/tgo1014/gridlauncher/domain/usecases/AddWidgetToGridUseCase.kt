package tgo1014.gridlauncher.domain.usecases

import tgo1014.gridlauncher.domain.AppsManager
import tgo1014.gridlauncher.ui.models.GridItem
import javax.inject.Inject

class AddWidgetToGridUseCase @Inject constructor(
    private val appsManager: AppsManager,
) {
    suspend operator fun invoke(widget: GridItem) {
        val current = appsManager.homeGridFlow.firstOrNull().orEmpty().toMutableList()
        // Give new id if not present
        val id = if (widget.id >= 0) widget.id else (current.maxOfOrNull { it.id } ?: 0) + 1
        val item = widget.copy(id = id)
        current.add(item)
        appsManager.setGrid(current)
    }
}
