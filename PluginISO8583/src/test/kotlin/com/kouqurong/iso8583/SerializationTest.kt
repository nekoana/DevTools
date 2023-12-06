import com.kouqurong.iso8583.data.Attr
import com.typesafe.config.ConfigRenderOptions
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.hocon.Hocon
import kotlinx.serialization.hocon.encodeToConfig
import org.junit.Test

@Serializable
data class Person(val name: String, val age: Int, val address: String)

class SerializationTest {
    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun `test encode person`() {
        val person = Person("Alice", 12, "address")

        val config = Hocon.encodeToConfig(Person.serializer(), person)

        println(config.root().render())
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun `test encode persons`() {
        @Serializable
        data class PersonSerialize(val age: Int, val address: String)

        val persons = listOf(
            Person("Bob", 12, "address"),
            Person("Alice", 12, "address")
        ).associate {
            it.name to PersonSerialize(it.age, it.address)
        }

        val config = Hocon.encodeToConfig(persons)


        val ops = ConfigRenderOptions.defaults().setOriginComments(false)

        println(config.root().render(ops))

    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun `test seal class`() {
        val attr = mapOf("attr" to Attr.ASCII)

        val config = Hocon.encodeToConfig(attr)

        println(config.root().render())
    }
}