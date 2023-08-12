annotation class IdRes

object R {
  object id {
    @IdRes const val APP_NAME = 1

    val map = buildMap { put(1, "DevTools") }
  }
}

fun stringResource(@IdRes id: Int): String {
  return R.id.map[id] ?: throw IllegalArgumentException("Resource id $id not found")
}
