package matej.lamza.betshops.di.modules

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import matej.lamza.betshops.BuildConfig
import matej.lamza.betshops.data.api.BetshopAPI
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val networkModule = module {

    single {
        GsonBuilder()
            .setLenient()
            .create()
    }

    single<GsonConverterFactory> {
        GsonConverterFactory.create(get<Gson>())
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .addConverterFactory(get<GsonConverterFactory>())

            .build()
    }

    single { get<Retrofit>().create(BetshopAPI::class.java) }
}
