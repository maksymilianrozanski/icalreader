package io.github.maksymilianrozanski.icalreader.data

class ApiUtils() {

    val BASE_URL: String = "https://example.com/"

    fun getAPIService(): APIService {
        return RetrofitClient.getClient(BASE_URL).create(APIService::class.java)
    }
}