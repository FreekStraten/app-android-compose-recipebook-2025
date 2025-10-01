package nl.avans.freekstraten.receptenapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import nl.avans.freekstraten.receptenapp.R

// Bestaande typografie (laat ik staan)
val AppTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )
)

// Lokale font-family voor je logo (Lobster Two)
private val BrandScriptFamily = FontFamily(
    Font(R.font.lobstertwo_regular,     weight = FontWeight.Normal, style = FontStyle.Normal),
    Font(R.font.lobstertwo_bolditalic,  weight = FontWeight.Bold,   style = FontStyle.Italic)
)

// Extension-stijl: MaterialTheme.typography.brandTitle
val Typography.brandTitle: TextStyle
    @Composable get() = TextStyle(
        fontFamily    = BrandScriptFamily,
        fontWeight    = FontWeight.Bold,
        fontStyle     = FontStyle.Italic,
        fontSize      = 22.sp,
        lineHeight    = 28.sp,
        letterSpacing = 0.sp
    )
