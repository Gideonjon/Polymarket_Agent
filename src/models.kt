
import kotlinx.serialization.Serializable

@Serializable
data class Market(
    val id: String,
    val probability: Double,
    val volume: Double,
    val liquidity: Double
)

data class TradeDecision(
    val marketId: String,
    val action: String,
    val confidence: Double
)

@Serializable
data class OrderBook(
    val bids: List<OrderLevel>,
    val asks: List<OrderLevel>
)

@Serializable
data class OrderLevel(
    val price: Double,
    val size: Double
)
