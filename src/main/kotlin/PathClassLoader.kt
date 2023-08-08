import java.io.File
import java.net.URLClassLoader

class PathClassLoader(private val path: String): URLClassLoader(arrayOf(File(path).toURI().toURL())) {
override fun findClass(name: String?): Class<*> {
        return super.findClass(name)
    }
}