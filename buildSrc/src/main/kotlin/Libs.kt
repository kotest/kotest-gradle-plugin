object Libs {

   const val KotlinVersion = "1.4.32"
   const val GradlePluginPublishVersion = "0.14.0"

   object Kotest {
      private const val version = "4.4.3"
      const val shared = "io.kotest:kotest-assertions-shared:$version"
      const val assertions = "io.kotest:kotest-assertions-core:$version"
      const val junit5 = "io.kotest:kotest-runner-junit5:$version"
   }

}
