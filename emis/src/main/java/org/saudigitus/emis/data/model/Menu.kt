package org.saudigitus.emis.data.model

data class Menu(
    val img: Int,
    val uid: String = "",
    val section: String = "",
    val title: String,
    val objectType: String = ""
)
