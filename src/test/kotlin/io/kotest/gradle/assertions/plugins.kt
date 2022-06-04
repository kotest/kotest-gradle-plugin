package io.kotest.gradle.assertions

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import org.gradle.api.Plugin
import org.gradle.api.Project

inline fun <reified T : Plugin<*>> Project.shouldHavePlugin() {
   project should havePlugin<T>()
}

inline fun <reified T : Plugin<*>> Project.shouldNotHavePlugin() {
   project shouldNot havePlugin<T>()
}

inline fun <reified T : Plugin<*>> havePlugin() = Matcher<Project> { project ->
   val plugins = project.plugins.withType(T::class.java)

   MatcherResult(
      plugins.isNotEmpty(),
      "Found no plugins of type ${T::class.simpleName}",
      "Expected no plugin of type ${T::class.simpleName}, but it exists"
   )
}
