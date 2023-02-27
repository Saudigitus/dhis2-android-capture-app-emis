package org.saudigitus.emis.data.model

import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.Serializable

@Serializable
data class Value(
    @JsonProperty("key")
    val key: String? = null,
    @JsonProperty("value")
    val value: String? = null
)