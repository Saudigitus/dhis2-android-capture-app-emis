package org.saudigitus.emis.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.Serializable
import org.saudigitus.emis.utils.Mapper.translateJsonToObject

@Serializable
data class AppConfig(
    @JsonProperty("filters")
    val filters: List<Filter>? = listOf(),
    @JsonProperty("linelist")
    val linelist: List<Linelist>? = listOf(),
    @JsonProperty("programs")
    val programs: String? = ""
) {
    private fun toJson(): String = translateJsonToObject().writeValueAsString(this)

    companion object {
        fun fromJson(json: String): AppConfig = translateJsonToObject()
            .readValue(json, AppConfig::class.java)
    }

    override fun toString(): String {
        return toJson()
    }
}
