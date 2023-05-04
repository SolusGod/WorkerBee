package com.app.workerbee.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.workerbee.R
import com.app.workerbee.activity.base.BaseActivity
import com.app.workerbee.adapter.ChannelAdapter
import com.app.workerbee.adapter.SavedChannelAdapter
import com.app.workerbee.api.APIConstants
import com.app.workerbee.api.ApiClient
import com.app.workerbee.callback.ChannelCallBack
import com.app.workerbee.databinding.ActivityChannelBinding
import com.app.workerbee.model.channels.ChannelModel
import com.app.workerbee.utility.AlertMessage
import com.app.workerbee.utility.Constant
import com.app.workerbee.utility.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class ChannelActivity : BaseActivity(), ChannelCallBack {

    private var total = 0
    private var search = ""
    private var isLoading = false
    private var visibleItemCount = 0
    private var pastVisiblePosition = 0
    private var nextPageToken: String? = null

    private var savedChannelList = ArrayList<ChannelModel>()
    private lateinit var savedChannelAdapter: SavedChannelAdapter

    private var channelList = ArrayList<ChannelModel>()
    private lateinit var searchChannelAdapter: ChannelAdapter

    private lateinit var binding: ActivityChannelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChannelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listStatus(4)
        savedChannelList = myPreferences!!.getChannelList(Constant.SAVED_CHANNEL, ChannelModel::class.java)
        savedChannelAdapter = SavedChannelAdapter(mContext, savedChannelList, this)
        binding.rvSavedChannel.adapter = savedChannelAdapter

        if (savedChannelList.isNotEmpty())
            binding.btnWatchVideos.visibility = View.VISIBLE

        binding.btnWatchVideos.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.isEmpty()) {
                    if (channelList.isNotEmpty()) {
                        val size = channelList.size
                        channelList.clear()
                        if (binding.rvSearchedChannels.adapter != null)
                            binding.rvSearchedChannels.adapter!!.notifyItemRangeRemoved(0, size)
                        listStatus(4)
                    }
                }
            }
        })

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (myPreferences!!.getBoolean(Constant.IS_NEW_USER)) {
                    finish()
                } else {
                    startActivity(Intent(mContext, HomeActivity::class.java))
                    finish()
                }
            }
        })

        binding.btnSearch.setOnClickListener {
            if (!TextUtils.isEmpty(binding.searchEdit.text.toString().trim())) {
                search = binding.searchEdit.text.toString().trim()
                listStatus(1)
                getSearchedChannels()
            }
        }

        binding.searchEdit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!TextUtils.isEmpty(binding.searchEdit.text.toString().trim())) {
                    search = binding.searchEdit.text.toString().trim()
                    listStatus(1)
                    getSearchedChannels()
                }
                true
            } else false
        }

        val gridLayoutManager = GridLayoutManager(mContext, 2)
        binding.rvSearchedChannels.layoutManager = gridLayoutManager

        binding.rvSearchedChannels.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isLoading = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                visibleItemCount = gridLayoutManager.childCount
                pastVisiblePosition = gridLayoutManager.findFirstCompletelyVisibleItemPosition()
                total = gridLayoutManager.itemCount

                if (isLoading && (visibleItemCount + pastVisiblePosition >= total)) {

                    Log.d("loadMore", "fired more, Current page: $nextPageToken")
                    isLoading = false
                    loadNextPageData()
                }
            }
        })
    }

    private fun loadNextPageData() {
        if (nextPageToken != null) {
            binding.bottomProgress.visibility = View.VISIBLE
            getSearchedChannels()
        }
    }

    private fun getSearchedChannels() {

        this.hideKeyboard()
        if (!Utils.isConnectedToInternet(mContext)) {
            AlertMessage.showMessage(mContext, R.string.no_internet_connection)
            return
        }
        Utils.checkAccessToken(mContext)

        val nextPage = if (nextPageToken == null)
            ""
        else
            "&pageToken=$nextPageToken"

        disposable = ApiClient.client.getSearchedChannels(
            APIConstants.search +
                    "part=snippet&type=channel&q=$search" +
                    "&regionCode=PK" +
                    nextPage +
                    "&maxResults=12&key=AIzaSyCIGxHMZCne2CRTCdKev7--K-3pHgADq18"
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                disposable?.dispose()

                binding.progress.visibility = View.GONE
                if (it.isSuccessful) {
                    val responseBody = it.body()

                    if (responseBody != null) {

                        binding.bottomProgress.visibility = View.GONE
                        nextPageToken = responseBody.nextPageToken

                        if (channelList.isEmpty()) {
                            channelList.addAll(responseBody.items)
                            searchChannelAdapter = ChannelAdapter(mContext, channelList, this)
                            binding.rvSearchedChannels.adapter = searchChannelAdapter
                        } else {
                            for (item in responseBody.items) {
                                if (!channelList.contains(item)) {
                                    channelList.add(item)
                                    binding.rvSearchedChannels.adapter?.notifyItemInserted(
                                        channelList.size - 1
                                    )
                                }
                            }
                        }

                        if (channelList.isEmpty())
                            listStatus(3)
                        else {
                            listStatus(2)
                        }

                    } else {
                        listStatus(3)
                        AlertMessage.showMessage(mContext, "Data loading failed")
                    }

                } else {
                    listStatus(3)
                    binding.progress.visibility = View.GONE
                    Utils.handleErrorMessage(it.errorBody()!!, mContext)
                }
            }, {
                listStatus(3)
                disposable?.dispose()
                binding.progress.visibility = View.GONE
                AlertMessage.showMessage(mContext, Utils.getErrorMessage(mContext, it))
            })
    }

    override fun onClickChannel(position: Int, obj: ChannelModel) {
        if (!savedChannelList.contains(obj)) {
            savedChannelList.add(0, obj)
            binding.rvSavedChannel.adapter!!.notifyItemInserted(0)
            binding.rvSavedChannel.smoothScrollToPosition(0)
            myPreferences!!.putChannelList(Constant.SAVED_CHANNEL, savedChannelList)
            myPreferences!!.putBoolean(Constant.IS_NEW_USER, false)
            binding.btnWatchVideos.visibility = View.VISIBLE
        }
    }

    override fun onRemoveChannel(position: Int) {
        savedChannelList.removeAt(position)
        binding.rvSavedChannel.adapter!!.notifyItemRemoved(position)
        myPreferences!!.putChannelList(Constant.SAVED_CHANNEL, savedChannelList)

        if (savedChannelList.isEmpty()) {
            binding.btnWatchVideos.visibility = View.GONE
            myPreferences!!.putBoolean(Constant.IS_NEW_USER, true)
        }
        else {
            binding.btnWatchVideos.visibility = View.VISIBLE
            myPreferences!!.putBoolean(Constant.IS_NEW_USER, false)
        }
    }

    private fun listStatus(i: Int) {
        when (i) {
            1 -> {
                binding.progress.visibility = View.VISIBLE
                binding.rvSearchedChannels.visibility = View.GONE
                binding.resultStatus.visibility = View.GONE
            }

            2 -> {
                binding.progress.visibility = View.GONE
                binding.rvSearchedChannels.visibility = View.VISIBLE
                binding.resultStatus.visibility = View.GONE
            }

            3 -> {
                binding.progress.visibility = View.GONE
                binding.rvSearchedChannels.visibility = View.GONE
                binding.resultStatus.text = getString(R.string.no_result_found)
                binding.resultStatus.visibility = View.VISIBLE
            }

            4 -> {
                binding.progress.visibility = View.GONE
                binding.rvSearchedChannels.visibility = View.GONE
                binding.resultStatus.text = getString(R.string.search_your_favorite_channels)
                binding.resultStatus.visibility = View.VISIBLE
            }
        }
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}