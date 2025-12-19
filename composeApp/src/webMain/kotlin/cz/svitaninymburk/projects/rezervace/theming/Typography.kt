package cz.svitaninymburk.projects.rezervace.theming

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import rezervace.composeapp.generated.resources.Nunito_Black
import rezervace.composeapp.generated.resources.Nunito_Bold
import rezervace.composeapp.generated.resources.Nunito_ExtraBold
import rezervace.composeapp.generated.resources.Nunito_ExtraLight
import rezervace.composeapp.generated.resources.Nunito_Light
import rezervace.composeapp.generated.resources.Nunito_Medium
import rezervace.composeapp.generated.resources.Nunito_Regular
import rezervace.composeapp.generated.resources.Nunito_SemiBold
import rezervace.composeapp.generated.resources.Res


@Composable fun AppTypography(): Typography {
    val nunito = FontFamily(
        Font(Res.font.Nunito_ExtraLight, FontWeight.ExtraLight),
        Font(Res.font.Nunito_Light, FontWeight.Light),
        Font(Res.font.Nunito_Medium, FontWeight.Medium),
        Font(Res.font.Nunito_Regular, FontWeight.Normal),
        Font(Res.font.Nunito_SemiBold, FontWeight.SemiBold),
        Font(Res.font.Nunito_Bold, FontWeight.Bold),
        Font(Res.font.Nunito_Black, FontWeight.Black),
        Font(Res.font.Nunito_ExtraBold, FontWeight.ExtraBold),
    )
    return Typography(
        titleLarge = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.1.sp
        ),
        titleSmall = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = nunito,
            fontWeight = FontWeight.Light,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),
    )
}
