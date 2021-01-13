# Jetpack DataStore sample with Kotlin Seirialization

A sample Android app to demonstrate [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) with [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization) instead of Protocol Buffers.

## Typed DataStore doesn't require protocol buffers

Although [Android Developers documents](https://developer.android.com/topic/libraries/architecture/datastore) explain that we need to define data scheme by usign [protocol buffers](https://developers.google.com/protocol-buffers), it's actually not required. We can use any data serialization method, e.g. [Kotlin Serialization](https://github.com/Kotlin/kotlinx.serialization) or [Moshi](https://github.com/square/moshi), with [Proto DataStore](https://developer.android.com/topic/libraries/architecture/datastore#proto-datastore) (or sometimes it's called Typed DataStore).

DataStore is a library to serialize data object to binary and store it on storage. Data conversion between data object and binary is abstracted by [`Serializer`](https://developer.android.com/reference/kotlin/androidx/datastore/core/Serializer) interface. App can provide arbitrary implementation of `Serializer` when instantiating `DataStore`.

[Android Developers](https://developer.android.com/topic/libraries/architecture/datastore#proto-datastore) explains how to implement `Serializer` with protocol buffers. However, `Serializer` and `DataStore` doesn't depend on protocol buffers. We can implement `Serializer` interface with another serialization method and can utilize Typed DataStore without protocol buffers.

## How to implement `Serializer` with Kotlin Serialization

[`MyDataSerializer.kt`](app/src/main/java/dev/kafumi/datastoreserialization/MyDataSerializer.kt) is an example implementation of `Serializer` with Kotlin Serialization.

```kotlin
class MyDataSerializer(
    private val stringFormat: StringFormat = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    },
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
```

Here is the definition of the corresponding data class [`MyData`](https://github.com/kafumi/android-datastore-serialization-sample/blob/main/app/src/main/java/dev/kafumi/datastoreserialization/MyData.kt).

```kotlin
@Serializable
data class MyData(
    val myBooleanValue: Boolean = false,
    val myStringValue: String? = null,
    val myEnumValue: MyEnum = MyEnum.FOO,
) {
    @Serializable
    enum class MyEnum {
        @SerialName("FOO")
        FOO,

        @SerialName("BAR")
        BAR,

        @SerialName("BAZ")
        BAZ,
    }
}
```

## How to use DataStore with `MyDataSerializer`

App can create `DataStore` with `MyDataSerializer` and use it to persist data on storage.

```kotlin
    private val dataStore = context.createDataStore(
        fileName = "my_data.json",
        serializer = MyDataSerializer(),
    )
```

See [Android Developers guide](https://developer.android.com/topic/libraries/architecture/datastore#proto-create) about how to read from and write to DataStore.

The DataStore will generate and store JSON file `files/datastore/my_data.json` on app's storage like this.

```json
{"myBooleanValue":true,"myStringValue":"Sample string","myEnumValue":"BAR"}
```