package io.kotest.gradle

import io.kotest.gradle.Constants.KOTEST_EXTENSION_NAME
import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class KotestExtension @Inject constructor(project: Project) {
   private val objects = project.objects
   val tags: Property<String> = objects.property(String::class.java)
}

internal fun Project.kotest(): KotestExtension? {
   return when (val ext = extensions.findByName(KOTEST_EXTENSION_NAME)) {
      is KotestExtension -> ext
      null -> null
      else -> error(
         "kotest is not of the correct type. " +
            "Expected ${KotestExtension::class.simpleName} but was ${ext::class.simpleName}"
      )
   }
}
