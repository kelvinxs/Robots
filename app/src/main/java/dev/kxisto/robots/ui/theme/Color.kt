package dev.kxisto.robots.ui.theme

import androidx.compose.ui.graphics.Color

sealed class AppColors {
    companion object {
        val BackgroundLight = Color(0xFFEAEFF1)
        val BackgroundDark = Color(0xFF151618)
        val PrimaryLight = Color(0xFF007BD8)
        val PrimaryDark = Color(0xFF006CBE)
        val PrimaryVariantLight = Color(0xFF103185)
        val PrimaryVariantDark = Color(0xFF103185)
        val SecondaryLight = Color(0xFF0091FF)
        val SecondaryDark = Color(0xFF0091FF)

        val PlayerColor1 = Color(0xFF1A5AFF)
        val PlayerTrailColor1 = Color(0xFF779DFF)
        val PlayerColor2 = Color(0xFFFF2763)
        val PlayerTrailColor2 = Color(0xFFFF85A7)
        val PlayerColor3 = Color(0xFF70FF34)
        val PlayerTrailColor3 = Color(0xFFC7FDB0)
        val PlayerColor4 = Color(0xFF7E1391)
        val PlayerTrailColor4 = Color(0xFFB07DB9)

        val GameTargetColor = Color(0xFFFFEB3B)
        val GameUnknownAreaColor = Color(0xFF99A1A5)
        val GameAreaBorderColor = Color(0x80000000)
    }
}