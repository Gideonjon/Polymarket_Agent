
import kotlinx.coroutines.*

class ExecutionEngine(private val api: PolymarketApi) {

    suspend fun execute(decisions: List<TradeDecision>) = coroutineScope {
        decisions.map {
            launch {
                try {
                    api.executeTrade(it)
                } catch (e: Exception) {
                    println("Trade failed: ${e.message}")
                }
            }
        }.joinAll()
    }
}
