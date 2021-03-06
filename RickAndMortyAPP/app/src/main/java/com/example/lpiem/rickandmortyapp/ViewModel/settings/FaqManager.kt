package com.example.lpiem.rickandmortyapp.ViewModel.settings

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.lpiem.rickandmortyapp.Model.ResponsesFromAPI.FAQ
import com.example.lpiem.rickandmortyapp.Util.SingletonHolder

class FaqManager internal constructor(internal val context: Context) {

    var closeFaqLiveData = MutableLiveData<Fragment>()
    var faqLoaderLiveData = MutableLiveData<List<FAQ>>()

    companion object : SingletonHolder<FaqManager, Context>(::FaqManager)

}