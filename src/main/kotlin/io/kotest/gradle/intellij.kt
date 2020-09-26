package io.kotest.gradle

/**
 * Attempts to detect if we are running from intellij.
 */
fun isIntellij(): Boolean = try {
   Class.forName("com.intellij.rt.execution.CommandLineWrapper")
   true
} catch (t: Throwable) {
   false
}