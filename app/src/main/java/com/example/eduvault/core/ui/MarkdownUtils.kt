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
