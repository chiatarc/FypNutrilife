package com.example.assignme.DataClass

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun rememberWidowInfo():WindowInfo{
    val configuration = LocalConfiguration.current
    return WindowInfo(
        screenWidthInfo =  when{
            configuration.screenWidthDp<600->WindowInfo.WindowType.Compact
            configuration.screenHeightDp<840-> WindowInfo.WindowType.Medium
            else-> WindowInfo.WindowType.Expanded
        },
        screenHeightInfo = when{
            configuration.screenWidthDp<480-> WindowInfo.WindowType.Compact
            configuration.screenHeightDp<900-> WindowInfo.WindowType.Medium
            else -> WindowInfo.WindowType.Expanded
        },
        screenWidth = configuration.screenWidthDp.dp,
        screeHeight = configuration.screenHeightDp.dp
    )
}

data class WindowInfo(
    val screenWidthInfo: WindowType,
    val screenHeightInfo: WindowType,
    val screenWidth: Dp,
    val screeHeight: Dp
){
    sealed class WindowType{
        object Compact: WindowType()
        object Medium: WindowType()
        object Expanded: WindowType()
    }
}