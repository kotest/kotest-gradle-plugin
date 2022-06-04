import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import org.gradle.api.Project
import org.gradle.api.Task

inline fun <reified T : Task> Project.shouldHaveTask(withName: String? = null) {
   project should haveTask<T>(withName)
}

inline fun <reified T : Task> Project.shouldNotHaveTask(withName: String? = null) {
   project shouldNot haveTask<T>(withName)
}

inline fun <reified T : Task> haveTask(withName: String? = null) = Matcher<Project> { project ->
   val tasks = project.tasks.withType(T::class.java)

   if (tasks.isEmpty()) MatcherResult(
      false,
      "Found no tasks of type ${T::class.simpleName}",
      "Expected "
   )
   else if (withName != null) MatcherResult(
      tasks.findByName(withName) != null,
      "Found tasks of correct type, but none with the name $withName",
      "Expected not to find a task with $withName, but it was found."
   )
   else MatcherResult(true, "", "")
}
