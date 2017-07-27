package com.app.dr1009.githubsearch

class Card(items: GithubJsonModel.Items) {
    val name = items.name ?: ""
    val description = items.description ?: ""
    val language = items.language ?: ""
    val url = items.htmlUrl ?: ""
    val star = items.stargazersCount.toString()
    val fork = items.forksCount.toString()
    val update = items.updatedAt ?: ""
}
