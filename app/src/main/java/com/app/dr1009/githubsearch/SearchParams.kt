package com.app.dr1009.githubsearch

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.text.TextUtils

class SearchParams : BaseObservable() {
    @get:Bindable
    var query = ""
        set(query) {
            field = query
            notifyPropertyChanged(com.app.dr1009.githubsearch.BR.query)
        }
    @get:Bindable
    var language = ""
        set(language) {
            field = language
            notifyPropertyChanged(com.app.dr1009.githubsearch.BR.language)
        }

    val pramsUrl: String
        @Bindable
        get() {
            return "q=" + query + if (!TextUtils.isEmpty(language)) {
                "+language:" + language
            } else {
                ""
            }
        }
}
