package com.makhalibagas.meetsimpleapp

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

const val baseurl = "https://fcm.googleapis.com/fcm/"

val appModule = module {
    single { createWebService<FirebaseService>(baseurl) }
}

val repoModule = module {
    single { SendRepository(get()) }
}

val viewModelModule = module {
    viewModel { SendViewModel(get()) }
}

val myModule = listOf(
    appModule,
    repoModule,
    viewModelModule
)
