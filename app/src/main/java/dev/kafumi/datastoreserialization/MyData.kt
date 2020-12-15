package dev.kafumi.datastoreserialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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