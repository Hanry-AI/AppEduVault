package com.example.eduvault.core.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Phân giải chuỗi văn bản Markdown đơn giản sang AnnotatedString để hiển thị đẹp mắt trong Compose.
 * Hỗ trợ tiêu đề (#, ##, ###), in đậm (**), in nghiêng (*) và danh sách bullet points.
 */
fun parseMarkdownToAnnotatedString(markdown: String): AnnotatedString = buildAnnotatedString {
    val lines = markdown.split("\n")
    lines.forEachIndexed { index, line ->
        var processedLine = line.trim()
            .replace("\\Omega", "Ω")
            .replace("\\omega", "ω")
            .replace("\\alpha", "α")
            .replace("\\beta", "β")
            .replace("\\gamma", "γ")
            .replace("\\delta", "δ")
            .replace("\\pi", "π")
            .replace("\\sigma", "σ")
            .replace("\\mu", "μ")
            .replace("\\lambda", "λ")
            .replace("\\theta", "θ")
            .replace("\\phi", "φ")
            .replace("\\epsilon", "ε")
            .replace("\\rho", "ρ")
            .replace("\\Delta", "Δ")
            .replace("\\Sigma", "Σ")
        
        // Tiêu đề (Headings)
        var isHeading = false
        var headingSize = 13.sp
        if (processedLine.startsWith("### ")) {
            processedLine = processedLine.removePrefix("### ")
            isHeading = true
            headingSize = 15.sp
        } else if (processedLine.startsWith("## ")) {
            processedLine = processedLine.removePrefix("## ")
            isHeading = true
            headingSize = 16.sp
        } else if (processedLine.startsWith("# ")) {
            processedLine = processedLine.removePrefix("# ")
            isHeading = true
            headingSize = 18.sp
        }
        
        // Danh sách bullet points
        var isBullet = false
        if (processedLine.startsWith("* ") || processedLine.startsWith("- ") || processedLine.startsWith("• ")) {
            processedLine = processedLine.substring(2)
            isBullet = true
        }

        if (isBullet) {
            append("•  ")
        }

        if (isHeading) {
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = headingSize))
        }

        var i = 0
        while (i < processedLine.length) {
            if (processedLine.startsWith("**", i)) {
                val closingIdx = processedLine.indexOf("**", i + 2)
                if (closingIdx != -1) {
                    val boldText = processedLine.substring(i + 2, closingIdx)
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(boldText)
                    pop()
                    i = closingIdx + 2
                } else {
                    append(processedLine[i].toString())
                    i++
                }
            } else if (processedLine.startsWith("*", i) && !processedLine.startsWith("**", i)) {
                val closingIdx = processedLine.indexOf("*", i + 1)
                if (closingIdx != -1) {
                    val italicText = processedLine.substring(i + 1, closingIdx)
                    pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                    append(italicText)
                    pop()
                    i = closingIdx + 1
                } else {
                    append(processedLine[i].toString())
                    i++
                }
            } else if (processedLine.startsWith("`", i)) {
                val closingIdx = processedLine.indexOf("`", i + 1)
                if (closingIdx != -1) {
                    val codeText = processedLine.substring(i + 1, closingIdx)
                    pushStyle(SpanStyle(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        background = androidx.compose.ui.graphics.Color(0x1A808080)
                    ))
                    append(codeText)
                    pop()
                    i = closingIdx + 1
                } else {
                    append(processedLine[i].toString())
                    i++
                }
            } else if (processedLine.startsWith("$", i)) {
                val closingIdx = processedLine.indexOf("$", i + 1)
                if (closingIdx != -1) {
                    val mathContent = processedLine.substring(i + 1, closingIdx)
                    appendMathText(mathContent, 13f)
                    i = closingIdx + 1
                } else {
                    append(processedLine[i].toString())
                    i++
                }
            } else {
                append(processedLine[i].toString())
                i++
            }
        }
        
        if (isHeading) {
            pop()
        }

        if (index < lines.size - 1) {
            append("\n")
        }
    }
}

fun AnnotatedString.Builder.appendMathText(mathText: String, baseFontSizeSp: Float) {
    var text = mathText
        .replace(Regex("""\\frac\{([^}]+)\}\{([^}]+)\}""")) { match ->
            val num = match.groupValues[1]
            val den = match.groupValues[2]
            if (num.length > 5 || den.length > 5) "($num)/($den)" else "$num/$den"
        }
        .replace("\\times", " × ")
        .replace("\\le", " ≤ ")
        .replace("\\ge", " ≥ ")
        .replace("\\approx", " ≈ ")
        .replace("\\ne", " ≠ ")
        .replace("\\pm", " ± ")
        .replace("\\infty", " ∞ ")
        .replace("\\dots", "...")
        .replace("\\alpha", "α")
        .replace("\\beta", "β")
        .replace("\\gamma", "γ")
        .replace("\\delta", "δ")
        .replace("\\pi", "π")
        .replace("\\sigma", "σ")
        .replace("\\mu", "μ")
        .replace("\\lambda", "λ")
        .replace("\\theta", "θ")
        .replace("\\omega", "ω")
        .replace("\\phi", "φ")
        .replace("\\epsilon", "ε")
        .replace("\\rho", "ρ")
        .replace("\\Delta", "Δ")
        .replace("\\Omega", "Ω")
        .replace("\\Sigma", "Σ")
    
    text = text.trim()
    val start = length
    var i = 0
    val len = text.length
    while (i < len) {
        when {
            text[i] == '_' -> {
                i++
                if (i < len) {
                    val subChar = if (text[i] == '{') {
                        val close = text.indexOf('}', i)
                        if (close != -1) {
                            val content = text.substring(i + 1, close)
                            i = close
                            content
                        } else {
                            text[i].toString()
                        }
                    } else {
                        text[i].toString()
                    }
                    val subStart = length
                    append(subChar)
                    addStyle(
                        style = SpanStyle(
                            baselineShift = androidx.compose.ui.text.style.BaselineShift.Subscript,
                            fontSize = (baseFontSizeSp * 0.75f).sp,
                            fontStyle = FontStyle.Italic
                        ),
                        start = subStart,
                        end = length
                    )
                }
            }
            text[i] == '^' -> {
                i++
                if (i < len) {
                    val superChar = if (text[i] == '{') {
                        val close = text.indexOf('}', i)
                        if (close != -1) {
                            val content = text.substring(i + 1, close)
                            i = close
                            content
                        } else {
                            text[i].toString()
                        }
                    } else {
                        text[i].toString()
                    }
                    val superStart = length
                    append(superChar)
                    addStyle(
                        style = SpanStyle(
                            baselineShift = androidx.compose.ui.text.style.BaselineShift.Superscript,
                            fontSize = (baseFontSizeSp * 0.75f).sp,
                            fontStyle = FontStyle.Italic
                        ),
                        start = superStart,
                        end = length
                    )
                }
            }
            else -> {
                val isLetter = text[i].isLetter()
                val charStart = length
                append(text[i])
                if (isLetter) {
                    addStyle(
                        style = SpanStyle(fontStyle = FontStyle.Italic),
                        start = charStart,
                        end = length
                    )
                }
            }
        }
        i++
    }
    
    addStyle(
        style = SpanStyle(
            fontWeight = FontWeight.Medium
        ),
        start = start,
        end = length
    )
}
