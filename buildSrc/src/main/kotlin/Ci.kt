object Ci {

   // this is the version
   private const val baseVersion = "0.1.0"

   private val githubBuildNumber = System.getenv("GITHUB_RUN_NUMBER")

   private val snapshotVersion = when (githubBuildNumber) {
      null -> "$baseVersion-LOCAL"
      else -> "$baseVersion.${githubBuildNumber}-SNAPSHOT"
   }

   private val releaseVersion = System.getenv("RELEASE_VERSION")

   val isRelease = releaseVersion != null
   val publishVersion = releaseVersion ?: snapshotVersion
}
