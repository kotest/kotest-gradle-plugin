package io.kotest.gradle.assertions

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import org.gradle.api.Plugin
import org.gradle.api.Project

inline fun <reified T> Project.shouldHaveExtension() {
   project should haveExtension<T>()
}

inline fun <reified T : Plugin<*>> Project.shouldNotHaveExtension() {
   project shouldNot haveExtension<T>()
}

inline fun <reified T> haveExtension() = Matcher<Project> { project ->
   val extension = project.extensions.findByType(T::class.java)

   MatcherResult(
      extension != null,
      "Found no extension of type ${T::class.simpleName}",
      "Expected no extension of type ${T::class.simpleName}, but it exists"
   )
}
