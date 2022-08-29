package io.kotest.gradle.workarounds

import org.gradle.process.ExecResult
import org.gradle.process.internal.ExecAction
import org.gradle.process.internal.JavaExecAction

/**
 * Bridging interface to provide a common [execute] method for both [JavaExecAction] and [ExecAction]
 */
sealed interface UnifyingExecAction {
   fun execute(): ExecResult

   class Java(private val execAction: JavaExecAction) : UnifyingExecAction {
      override fun execute(): ExecResult =
         execAction.execute()
   }

   class Default(private val execAction: ExecAction) : UnifyingExecAction {
      override fun execute(): ExecResult =
         execAction.execute()
   }
}
