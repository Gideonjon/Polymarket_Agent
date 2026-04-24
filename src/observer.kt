
class MarketObserver(private val api: PolymarketApi) {

    suspend fun observe(): List<Market> {
        return api.getMarkets()
    }
}
