package com.app.dr1009.githubsearch

import android.content.Intent
import android.net.Uri
import android.view.View

object CardPresenter {

    @JvmStatic
    fun onClickPublic(view: View, url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        view.context.startActivity(intent)
    }
}
