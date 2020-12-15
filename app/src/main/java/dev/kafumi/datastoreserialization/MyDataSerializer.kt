package dev.kafumi.datastoreserialization

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@OptIn(ExperimentalSerializationApi::class)
class MyDataSerializer(
    private val stringFormat: StringFormat = Json,
) : Serializer<MyData> {
    override val defaultValue: MyData
        get() = MyData()

    override fun readFrom(input: InputStream): MyData {
        try {
            val bytes = input.readBytes()
            val string = bytes.decodeToString()
            return stringFormat.decodeFromString(string)
        } catch (e: SerializationException) {
            throw CorruptionException("Cannot read stored data", e)
        }
    }

    override fun writeTo(t: MyData, output: OutputStream) {
        val string = stringFormat.encodeToString(t)
        val bytes = string.encodeToByteArray()
        output.write(bytes)
    }
}
