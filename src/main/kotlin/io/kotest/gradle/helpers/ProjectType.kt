package io.kotest.gradle.helpers

import io.kotest.gradle.KotestExtension
import org.gradle.api.Project

enum class ProjectType {
   Multiplatform,
   Native,
   Js,
   Jvm
}

internal fun Project.kotest(): KotestExtension? {
   return when (val ext = extensions.findByName("kotest")) {
      is KotestExtension -> ext
      null -> null
      else -> throw IllegalStateException(
         "kotest is not of the correct type. " +
            "Expected ${KotestExtension::class.simpleName} but was ${ext::class.simpleName}"
      )
   }
}
