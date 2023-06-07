package com.freqwency.promotr.view.account.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.ExperimentalCoroutinesApi;

@HiltViewModel
class AccountViewModel @Inject constructor(application:Application): AndroidViewModel(application) {

}