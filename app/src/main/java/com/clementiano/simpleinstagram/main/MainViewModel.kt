package com.clementiano.simpleinstagram.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.clementiano.simpleinstagram.data.MeResponse
import com.clementiano.simpleinstagram.data.MediaData
import com.clementiano.simpleinstagram.data.MediaResponse
import com.clementiano.simpleinstagram.data.PreferenceStore
import com.clementiano.simpleinstagram.network.RetrofitClient
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subjects.PublishSubject

class MainViewModel(private val preferenceStore: PreferenceStore) : ViewModel() {

    private val graphApi = RetrofitClient.getGraphApiInterface()
    private val profileApi = RetrofitClient.getPlainApiInterface()

    private val accessToken = preferenceStore.auth

    val username = MutableLiveData<String>()
    val fullname = MutableLiveData<String>()
    val bio = MutableLiveData<String>()
    val postsNo = MutableLiveData<Int>()
    val _followers = MutableLiveData<Int>()
    val _following = MutableLiveData<Int>()
    var mediaItems = ArrayList<MediaData>()
    var profilePicUrl = MutableLiveData<String>()

    val mediaCount = Transformations.map(postsNo) { it.toString() }
    val followers = Transformations.map(_followers) { it.toString() }
    val following = Transformations.map(_following) { it.toString() }

    val mediaFetched = PublishSubject.create<Boolean>()

    init {
        postsNo.value = 0
        _followers.value = 0
        _following.value = 0
        fullname.value = preferenceStore.fullname
    }

    fun loadProfileData() {
        Single.merge(getProfile(), fetchMediaList())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
            }, {
                it.printStackTrace()
            })
    }

    fun getProfile(): Single<MeResponse> {
        if (accessToken == null) return Single.error(Throwable("You must pass an accessToken"))
        return graphApi.getProfile(accessToken)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess {
                username.value = it.username
                postsNo.value = it.media_count
                preferenceStore.username = username.value

                getProfileDetails()
            }
    }

    private fun getProfileDetails() {
        if (username.value != null) {
            profileApi.getProfileDetails(username.value!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d(TAG, "Profile returned: ${it.graphql.user.full_name}")
                    it.graphql.user.apply {
                        fullname.value = full_name
                        bio.value = biography
                        preferenceStore.fullname = fullname.value
                        _following.value = edge_follow.count
                        _followers.value = edge_followed_by.count
                        profilePicUrl.value = profile_pic_url_hd
                    }

                }, {
                    it.printStackTrace()
                })
        }
    }

    private fun fetchMediaList(): Single<MediaResponse> {
        return if (accessToken != null)
            graphApi.getMediaList(accessToken)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    mediaItems.clear()
                    mediaItems.addAll(it.data)
                    mediaFetched.onNext(true)
                }
        else Single.error(Throwable("You must specify an access token"))
    }

    companion object {
        private val TAG: String? = MainViewModel::class.java.simpleName
    }
}