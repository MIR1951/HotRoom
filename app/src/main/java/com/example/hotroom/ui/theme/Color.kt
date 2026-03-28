package com.example.hotroom.ui.theme

import androidx.compose.ui.graphics.Color

// Emerald Canopy (Organic Tailwind) Colors
val Primary = Color(0xFF006E1C)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFF4CAF50)
val OnPrimaryContainer = Color(0xFF003C0B)

val Secondary = Color(0xFF7A5649)
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFFFDCDBA) 
val OnSecondaryContainer = Color(0xFF795548)

val Tertiary = Color(0xFF0061A4)
val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFF33A0FE)
val OnTertiaryContainer = Color(0xFF00355D)

val Error = Color(0xFFBA1A1A)
val OnError = Color(0xFFFFFFFF)
val ErrorContainer = Color(0xFFFFDAD6)
val OnErrorContainer = Color(0xFF93000A)

val Background = Color(0xFFF8FAF8)
val OnBackground = Color(0xFF191C1B)

val Surface = Color(0xFFF8FAF8)
val OnSurface = Color(0xFF191C1B)
val SurfaceVariant = Color(0xFFE1E3E1)
val OnSurfaceVariant = Color(0xFF3F4A3C)

val Outline = Color(0xFF6F7A6B)
val OutlineVariant = Color(0xFFBECAB9)

// Surface Containers (Tailwind custom scale)
val SurfaceContainerLowest = Color(0xFFFFFFFF)
val SurfaceContainerLow = Color(0xFFF2F4F2)
val SurfaceContainer = Color(0xFFECEEEC)
val SurfaceContainerHigh = Color(0xFFE6E9E7)
val SurfaceContainerHighest = Color(0xFFE1E3E1)

// Semantic health indicators (Keep for logic)
val HealthExcellent = Color(0xFF006E1C) 
val HealthGood = Color(0xFF4CAF50)
val HealthWarning = Color(0xFF7A5649) // Using secondary earth tone for warning
val HealthCritical = Color(0xFFBA1A1A)

// Backward compatibility mappings for older screens
val GreenPrimary = Primary
val GreenPrimaryDark = Color(0xFF005313) 
val GreenPrimaryLight = Color(0xFF94F990) 
val GreenPrimaryContainer = PrimaryContainer
val GreenOnPrimary = OnPrimary

val BackgroundLight = Background
val SurfaceLight = Surface
val SurfaceVariantLight = SurfaceVariant

val TextPrimary = OnBackground
val TextSecondary = Outline
val TextOnGreen = OnPrimary

val AccentOrange = Secondary // mapped to earth tones
val ErrorRed = Error
val InfoBlue = Tertiary
val SuccessGreen = Primary

// Glassmorphism fallback
val GlassBorder = OutlineVariant
val GlassSurface = SurfaceContainerLowest