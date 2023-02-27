package org.saudigitus.emis.data.model

data class FilterData<T>(
    val data: List<T>?,
    val level: Int? = null,
    val key: String? = null,
    val objectType: String
)
