package com.app.dr1009.githubsearch

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.app.dr1009.githubsearch.databinding.ActivityMainBinding
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request


class MainActivity : AppCompatActivity() {

    enum class State {
        NeverFetched,
        Fetching,
        SuccessFetch,
        FailureFetch
    }

    interface StateObserver {
        fun update(state: State)
    }

    inner class NeverFetchedState : StateObserver {
        override fun update(state: State) {
            if (state != State.NeverFetched) {
                return
            }
            Log.d(TAG, "NeverFetchedState")
            mBinding.buttonSearch.isEnabled = true
        }
    }

    inner class FetchingState : StateObserver {
        override fun update(state: State) {
            if (state != State.Fetching) {
                return
            }
            Log.d(TAG, "FetchingState")
            mBinding.buttonSearch.isEnabled = false
            mCardList.clear()
            mBinding.recycler.adapter.notifyDataSetChanged()
            mBinding.executePendingBindings()
        }
    }

    inner class SuccessFetchState : StateObserver {
        override fun update(state: State) {
            if (state != State.SuccessFetch) {
                return
            }
            if(mCardList.isEmpty()){
                this@MainActivity.runOnUiThread({
                    Toast.makeText(this@MainActivity, "No Result", Toast.LENGTH_SHORT).show()
                })
            }
            Log.d(TAG, "SuccessFetchState")
            mBinding.buttonSearch.isEnabled = true
            mBinding.executePendingBindings()
        }
    }

    inner class FailureFetchState : StateObserver {
        override fun update(state: State) {
            if (state != State.FailureFetch) {
                return
            }
            Log.d(TAG, "FailureFetchState")
            mBinding.buttonSearch.isEnabled = true
            this@MainActivity.runOnUiThread({
                Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_SHORT).show()
            })
        }
    }

    private val TAG = "MainActivity"
    private val OKHTTP_CLIENT = OkHttpClient.Builder().build()

    private lateinit var mBinding: ActivityMainBinding
    private val mCardList = mutableListOf<Card>()
    private val mAdapter = RecyclerAdapter(mCardList)
    var mStateObserverList = ArrayList<StateObserver>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

        mBinding.buttonSearch.setOnClickListener { _ -> onClickSearch() }
        mBinding.params = SearchParams()

        mBinding.recycler.adapter = mAdapter

        val autoCompleteAdapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.language))
        mBinding.editLang.setAdapter(autoCompleteAdapter)

        initStateObserver()
    }

    fun initStateObserver() {
        mStateObserverList.add(NeverFetchedState())
        mStateObserverList.add(FetchingState())
        mStateObserverList.add(SuccessFetchState())
        mStateObserverList.add(FailureFetchState())
    }

    fun onClickSearch() {
        notifyUpdateState(State.Fetching)

        val url = resources.getString(R.string.base_url) + mBinding.params.searchUrl
        Observable
                .create<GithubJsonModel> {
                    val request = Request.Builder()
                            .url(url)
                            .get()
                            .build()
                    val response = OKHTTP_CLIENT.newCall(request).execute()
                    val model = Gson().fromJson(response.body()?.string(), GithubJsonModel::class.java)
                    it.onNext(model)
                    it.onComplete()
                }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ model ->
                    model.items.forEachIndexed { index, item ->
                        val card = Card(item)
                        mCardList.add(card)
                        mBinding.recycler.adapter.notifyItemInserted(index)
                    }
                }, { _ ->
                    notifyUpdateState(State.FailureFetch)
                }, {
                    notifyUpdateState(State.SuccessFetch)
                })
    }

    fun notifyUpdateState(state: State) {
        mBinding.state = state
        for (observer in mStateObserverList) {
            observer.update(state)
        }
    }

    fun onClickLegal(view: View) {
        val context = view.context
        val intent = Intent(context, LegalActivity::class.java)
        context.startActivity(intent)
    }
}
