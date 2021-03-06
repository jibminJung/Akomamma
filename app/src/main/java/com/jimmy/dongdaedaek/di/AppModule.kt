package com.jimmy.dongdaedaek.di

import android.app.Activity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.jimmy.dongdaedaek.BuildConfig
import com.jimmy.dongdaedaek.data.api.*
import com.jimmy.dongdaedaek.data.preference.PreferenceManager
import com.jimmy.dongdaedaek.data.preference.SharedPreferenceManager
import com.jimmy.dongdaedaek.data.repository.*
import com.jimmy.dongdaedaek.domain.model.Store
import com.jimmy.dongdaedaek.domain.usecase.*
import com.jimmy.dongdaedaek.presentation.addStore.AddStoreContract
import com.jimmy.dongdaedaek.presentation.addStore.AddStoreFragment
import com.jimmy.dongdaedaek.presentation.addStore.AddStorePresenter
import com.jimmy.dongdaedaek.presentation.map.MapPageContract
import com.jimmy.dongdaedaek.presentation.map.MapPageFragment
import com.jimmy.dongdaedaek.presentation.map.MapPagePresenter
import com.jimmy.dongdaedaek.presentation.mypage.MyPageContract
import com.jimmy.dongdaedaek.presentation.mypage.MyPageFragment
import com.jimmy.dongdaedaek.presentation.mypage.MyPagePresenter
import com.jimmy.dongdaedaek.presentation.newexplore.ExploreViewModel
import com.jimmy.dongdaedaek.presentation.selectLocation.SelectLocationContract
import com.jimmy.dongdaedaek.presentation.selectLocation.SelectLocationFragment
import com.jimmy.dongdaedaek.presentation.selectLocation.SelectLocationPresenter
import com.jimmy.dongdaedaek.presentation.storeinformation.StoreInformationContract
import com.jimmy.dongdaedaek.presentation.storeinformation.StoreInformationFragment
import com.jimmy.dongdaedaek.presentation.storeinformation.StoreInformationPresenter
import com.jimmy.dongdaedaek.presentation.wishstorelist.WishStoreListContract
import com.jimmy.dongdaedaek.presentation.wishstorelist.WishStoreListFragment
import com.jimmy.dongdaedaek.presentation.wishstorelist.WishStoreListPresenter
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


val appModule = module {
    single { Dispatchers.IO }
    single { Firebase.firestore }
    single { Firebase.storage }
    single { Firebase.auth }

}
val dataModule = module {
    single<StoreApi> { StoreApiImpl(get()) }
    single<ReviewApi> { ReviewApiImpl(get()) }

    single { androidContext().getSharedPreferences("preference", Activity.MODE_PRIVATE) }
    single<PreferenceManager> { SharedPreferenceManager(get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get()) }

    single<StoreRepository> { StoreRepositoryImpl(get(), get()) }
    single<ReviewRepository> { ReviewRepositoryImpl(get(), get()) }
    single { TmapRepository(get(), get()) }
    single { CategoryRepository(get(), get()) }
    single { ImageRepository(get(), get(),androidContext()) }
    single {WishStoreRepository(get(),get())}

    // Api
    single {
        OkHttpClient()
            .newBuilder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()
    }
    single<TmapApi> {
        Retrofit.Builder().baseUrl(Url.TMAP_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
            .create()
    }

}

val domainModule = module {
    factory { GetStoresUseCase(get()) }
    factory { GetReviewUseCase(get()) }
    factory { UploadReviewUseCase(get(), get()) }
    factory { RequestLoginUseCase(get(), get(), get()) }
    factory { CheckLinkAndLoginUseCase(get(), get(), get()) }
    factory { GetCurrentUserEmail(get(), get()) }
    factory { LogoutUseCase(get(), get()) }
    factory { FindLocationUseCase(get()) }
    factory { RegisterStoreUseCase(get(), get()) }
    factory { UploadPhotosUseCase(get()) }
    factory { GetRecentReviewUseCase(get()) }
    factory { GetStoreByIdUseCase(get()) }
    factory { GetFilteredStoreUseCase(get()) }
    factory { RegisterWishStoreUseCase(get(),get()) }
    factory { DeleteWishStoreUseCase(get(),get()) }
    factory { CheckUserWishStoreUseCase(get(),get()) }
    factory { LoadWishStoreListUseCase(get(),get()) }
    factory { RegisterCategoryUseCase(get()) }
}
val presenterModule = module {
    viewModel { ExploreViewModel(get(),get(),get(),get()) }

    scope<StoreInformationFragment> {
        scoped<StoreInformationContract.Presenter> { (store: Store) ->
            StoreInformationPresenter(store, getSource(), get(), get(), get(), get(),get(),get(),get())
        }
    }
    scope<MyPageFragment> {
        scoped<MyPageContract.Presenter> { MyPagePresenter(getSource(), get(), get(), get()) }
    }
    scope<MapPageFragment> {
        scoped<MapPageContract.Presenter> { MapPagePresenter(getSource(),get(),get(),get()) }
    }

    scope<AddStoreFragment> {
        scoped<AddStoreContract.Presenter> { AddStorePresenter(getSource(), get(), get(),get()) }
    }

    scope<SelectLocationFragment> {
        scoped<SelectLocationContract.Presenter> { SelectLocationPresenter(getSource(), get()) }
    }
    scope<WishStoreListFragment> {
        scoped<WishStoreListContract.Presenter> { WishStoreListPresenter(getSource(), get(),get()) }
    }
}