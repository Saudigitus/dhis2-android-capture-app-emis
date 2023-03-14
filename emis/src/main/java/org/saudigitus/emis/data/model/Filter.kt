package org.saudigitus.emis.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.Serializable

@Serializable
data class Filter(
    @JsonProperty("key")
    val key: String? = "",
    @JsonProperty("label")
    val label: String? = "",
    @JsonProperty("objectType")
    val objectType: String? = "",
    @JsonProperty("values")
    val values: List<Value>? = listOf()
)
