package io.kotest.gradle

import org.gradle.api.Project
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class KotestExtension @Inject constructor(project: Project) {

   private val objects = project.objects

   val tags: Property<String> = objects.property(String::class.java)
}