package com.banana.hypermodes.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Shared bottom-layout geometry for fully floating Miuix navigation capsule and FAB.
 * Defines the vertical space structure at the bottom of main tab screens:
 * - System navigation bar inset (variable, from WindowInsets)
 * - Visual gap below capsule (16.dp margin)
 * - Capsule height (56.dp per Miuix FloatingNavigationBar default)
 * - Gap between content and capsule (12.dp breathing room)
 *
 * Used by MainTabsScreen (HyperModesApp.kt), AutomationsScreen, and page content
 * to eliminate the previous triple offset (Scaffold padding + navigationBarsPadding() + 80.dp).
 */
object BottomLayoutGeometry {
    /** Height of the floating navigation capsule itself */
    val capsuleHeight = 56.dp

    /** Visual gap between screen edge and capsule (negative = overlap with nav bar area) */
    val capsuleMargin = (-8).dp

    /** Breathing room between page content and the capsule */
    val contentGap = 4.dp

    /** FAB offset above the capsule top edge */
    val fabOffset = 8.dp

    /**
     * Total bottom padding for page content:
     * Ensures last item can scroll above the capsule with breathing room.
     * = capsuleHeight + contentGap (capsule itself + breathing space)
     * Note: Does NOT include navBar or capsuleMargin, as those are part of
     * the capsule's own offset from screen bottom, not content clearance.
     */
    @Composable
    fun contentBottomPadding(): PaddingValues {
        val bottomPadding = capsuleHeight + contentGap
        return PaddingValues(bottom = bottomPadding)
    }

    /**
     * Bottom offset for the floating navigation capsule:
     * system nav inset + capsuleMargin, with coerceAtLeast to prevent negative padding
     */
    @Composable
    fun capsuleBottomOffset(): Dp {
        val navBarInsets = WindowInsets.navigationBars.asPaddingValues()
        return (navBarInsets.calculateBottomPadding() + capsuleMargin).coerceAtLeast(0.dp)
    }

    /**
     * Bottom offset for the FAB:
     * system nav inset + capsuleMargin + capsuleHeight + fabOffset, coerced to non-negative
     */
    @Composable
    fun fabBottomOffset(): Dp {
        val navBarInsets = WindowInsets.navigationBars.asPaddingValues()
        return (navBarInsets.calculateBottomPadding() + capsuleMargin +
               capsuleHeight + fabOffset).coerceAtLeast(0.dp)
    }
}
