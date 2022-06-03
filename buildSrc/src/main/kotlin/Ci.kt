object Ci {

   // this is the version
   private const val baseVersion = "0.4"

   private val githubBuildNumber = System.getenv("GITHUB_RUN_NUMBER")

   private val snapshotVersion = when (githubBuildNumber) {
      null -> "$baseVersion-LOCAL"
      else -> "$baseVersion.${githubBuildNumber}-SNAPSHOT"
   }

   private val releaseVersion = "${baseVersion}.${githubBuildNumber}"

   val isRelease = githubBuildNumber != null
   val publishVersion = if (isRelease) releaseVersion else snapshotVersion
}
