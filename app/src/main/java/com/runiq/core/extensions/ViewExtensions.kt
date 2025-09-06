package com.runiq.core.extensions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Hide the soft keyboard
 */
fun View.hideKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

/**
 * Show the soft keyboard
 */
fun View.showKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

/**
 * Conditional modifier application
 */
inline fun Modifier.conditional(
    condition: Boolean,
    ifTrue: Modifier.() -> Modifier,
    ifFalse: Modifier.() -> Modifier = { this }
): Modifier = if (condition) {
    then(ifTrue(Modifier))
} else {
    then(ifFalse(Modifier))
}

/**
 * Apply modifier only if condition is true
 */
inline fun Modifier.applyIf(
    condition: Boolean,
    modifier: Modifier.() -> Modifier
): Modifier = if (condition) {
    then(modifier(Modifier))
} else {
    this
}

/**
 * Clickable with ripple effect and proper semantics
 */
fun Modifier.clickableWithRipple(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
): Modifier = composed {
    clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(),
        onClick = onClick
    )
}

/**
 * Clickable without ripple effect
 */
fun Modifier.clickableNoRipple(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
): Modifier = composed {
    clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        role = role,
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}

/**
 * Apply alpha based on enabled state
 */
fun Modifier.enabledAlpha(enabled: Boolean): Modifier {
    return alpha(if (enabled) 1f else 0.6f)
}

/**
 * Apply different modifiers based on boolean condition
 */
fun Modifier.ifElse(
    condition: Boolean,
    ifTrueModifier: Modifier,
    ifFalseModifier: Modifier = Modifier
): Modifier = if (condition) {
    then(ifTrueModifier)
} else {
    then(ifFalseModifier)
}

/**
 * Add padding only if condition is true
 */
fun Modifier.paddingIf(
    condition: Boolean,
    all: Dp = 0.dp,
    horizontal: Dp = 0.dp,
    vertical: Dp = 0.dp,
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp
): Modifier = applyIf(condition) {
    when {
        all > 0.dp -> padding(all)
        horizontal > 0.dp || vertical > 0.dp -> padding(
            horizontal = horizontal,
            vertical = vertical
        )
        else -> padding(
            start = start,
            top = top,
            end = end,
            bottom = bottom
        )
    }
}

/**
 * Shimmer effect for loading states
 */
fun Modifier.shimmer(
    enabled: Boolean = true
): Modifier = composed {
    if (enabled) {
        // TODO: Implement shimmer effect when needed
        alpha(0.5f)
    } else {
        this
    }
}

/**
 * Bounce animation on click
 */
fun Modifier.bounceClick(
    onClick: () -> Unit
): Modifier = composed {
    // TODO: Implement bounce animation when needed
    clickableWithRipple(onClick = onClick)
}

/**
 * Context extension for Compose
 */
@Composable
fun rememberContext(): Context = LocalContext.current