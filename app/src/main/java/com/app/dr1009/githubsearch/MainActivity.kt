package com.app.dr1009.githubsearch

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.app.dr1009.githubsearch.databinding.ActivityMainBinding
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val BASE_URL = "https://api.github.com/search/repositories?"
    private val OKHTTP_CLIENT = OkHttpClient.Builder().build()

    private lateinit var mBinding: ActivityMainBinding
    private val mData = mutableListOf<Card>()
    private val mAdapter = RecyclerAdapter(mData)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

        mBinding.buttonSearch.setOnClickListener { _ -> onClickSearch(mBinding.params.pramsUrl) }
        mBinding.params = SearchParams()

        mBinding.recycler.adapter = mAdapter
    }

    fun onClickSearch(urlParams: String) {
        mBinding.buttonSearch.isEnabled = false
        mData.clear()
        mBinding.recycler.adapter.notifyDataSetChanged()
        mBinding.executePendingBindings()

        val url = BASE_URL + urlParams
        Observable
                .create<GithubJsonModel> {
                    val request = Request.Builder()
                            .url(url)
                            .get()
                            .build()
                    val response = OKHTTP_CLIENT.newCall(request).execute()
                    val model = Gson().fromJson(response.body()?.string(), GithubJsonModel::class.java)
                    it.onNext(model)
                }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it.items.forEach { item ->
                        val card = Card(item)
                        mData.add(card)
                    }
                    mBinding.recycler.adapter.notifyDataSetChanged()
                    mBinding.buttonSearch.isEnabled = true
                    mBinding.executePendingBindings()
                }, { e ->
                    Log.d(TAG, "error", e)
                    mBinding.buttonSearch.isEnabled = true
                })
    }
}
