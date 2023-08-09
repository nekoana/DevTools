import java.io.File
import java.net.URLClassLoader

class PathClassLoader(vararg path: String): URLClassLoader(path.map { File(it).toURI().toURL() }.toTypedArray()) {
override fun findClass(name: String?): Class<*> {
        return super.findClass(name)
    }
}