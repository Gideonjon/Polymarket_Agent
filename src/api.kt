
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.web3j.crypto.Credentials
import org.web3j.crypto.Sign
import org.web3j.utils.Numeric

class PolymarketApi {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    private val API_KEY = "019dbf8e-f58b-7c6b-90b9-dcd9718f5471"
    private val PRIVATE_KEY = "0xdcbd7cb365893ee5881d54ccfa9c00fab38fb8ad032dd73ed7f8c2b63a915d02"

    private val credentials = Credentials.create(PRIVATE_KEY)

    suspend fun getMarkets(): List<Market> {
        val raw: String = client.get("https://gamma-api.polymarket.com/markets").body()

        val json = Json.parseToJsonElement(raw).jsonArray

        return json.take(50).map {
            val obj = it.jsonObject
            val prices = obj["outcomePrices"]?.jsonArray?.mapNotNull {
                it.toString().replace(""", "").toDoubleOrNull()
            } ?: listOf(0.5)

            Market(
                id = obj["id"].toString().replace(""", ""),
                probability = prices.firstOrNull() ?: 0.5,
                volume = obj["volume"]?.toString()?.toDoubleOrNull() ?: 0.0,
                liquidity = obj["liquidity"]?.toString()?.toDoubleOrNull() ?: 0.0
            )
        }
    }

    suspend fun getOrderBook(marketId: String): OrderBook {
        return client.get("https://clob.polymarket.com/orderbook/$marketId").body()
    }

    suspend fun executeTrade(decision: TradeDecision) {

        val orderBook = getOrderBook(decision.marketId)

        val price = if (decision.action == "BUY") {
            orderBook.asks.firstOrNull()?.price ?: return
        } else {
            orderBook.bids.firstOrNull()?.price ?: return
        }

        val size = 5 * decision.confidence

        val payload = mapOf(
            "market" to decision.marketId,
            "side" to decision.action.lowercase(),
            "price" to price,
            "size" to size
        )

        val signature = signMessage(payload.toString())

        client.post("https://clob.polymarket.com/orders") {
            headers {
                append("Authorization", "Bearer $API_KEY")
                append("Signature", signature)
            }
            setBody(payload)
        }

        println("TRADE SENT: $payload")
    }

    private fun signMessage(message: String): String {
        val sig = Sign.signPrefixedMessage(
            message.toByteArray(),
            credentials.ecKeyPair
        )

        return Numeric.toHexString(sig.r) +
               Numeric.toHexString(sig.s) +
               Numeric.toHexString(sig.v)
    }
}
