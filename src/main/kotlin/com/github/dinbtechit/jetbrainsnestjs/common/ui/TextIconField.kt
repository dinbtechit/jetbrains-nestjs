package com.github.dinbtechit.jetbrainsnestjs.common.ui

import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.Icon
import javax.swing.border.Border

/**
 * A custom text field with an icon displayed on the left side of the text field.
 *
 * @property icon The icon to display on the left side of the text field.
 */
class TextIconField(val icon: Icon) : JBTextField() {
    init {
        border = object : Border {
            private val insets = JBUI.insets(2, icon.iconWidth + 2, 2, 2)
            override fun paintBorder(c: Component?, g: Graphics?, x: Int, y: Int, width: Int, height: Int) {
                val g2d = g as Graphics2D
                g2d.translate(x + 5, y + (height - icon.iconHeight) / 2)
                icon.paintIcon(null, g, 0, 0)
                g2d.translate(-(x + 5), -(y + (height - icon.iconHeight) / 2))
            }

            override fun getBorderInsets(c: Component?) = insets
            override fun isBorderOpaque() = true
        }
    }
}
