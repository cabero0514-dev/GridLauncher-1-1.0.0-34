package tgo1014.gridlauncher.ui.composables.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LauncherOptionsBottomSheet(
    isShowing: Boolean,
    onClose: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onAddWidget: () -> Unit = {},
) {
    val state = rememberModalBottomSheetState()
    LaunchedEffect(isShowing) {
        if (isShowing) state.show() else state.hide()
    }
    if (isShowing) {
        ModalBottomSheet(onDismissRequest = onClose, sheetState = state) {
            Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onOpenSettings(); onClose() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Launcher settings")
                }
                Button(onClick = { onAddWidget(); onClose() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Add widget")
                }
            }
        }
    }
}
