package tgo1014.gridlauncher.ui.home

import tgo1014.gridlauncher.domain.models.App
import tgo1014.gridlauncher.domain.models.TileSettings
import tgo1014.gridlauncher.ui.models.GridItem

data class HomeState(
    val appList: List<App> = emptyList(),
    val grid: List<GridItem> = emptyList(),
    val goToHome: Boolean = false,
    val closeSearchFab: Boolean = false,
    val filterString: String = "",
    val itemBeingEdited: GridItem? = null,
    val tileSettings: TileSettings = TileSettings(),
    val isSettingsSheetShowing: Boolean = false,
) {
    val isEditMode get() = itemBeingEdited != null
}