package com.persons.finder.data

data class Person(
    val id: Long,
    val name: String,
    val jobTitle: String,
    val hobbies: List<String>,
    val bio: String,
    var location: Location? = null
)
