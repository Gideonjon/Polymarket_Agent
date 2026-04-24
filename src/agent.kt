
import kotlinx.coroutines.*

class AgentOrchestrator(
    private val observer: MarketObserver,
    private val strategy: StrategyEngine,
    private val riskManager: RiskManager,
    private val executor: ExecutionEngine
) {

    suspend fun run() = coroutineScope {
        while (isActive) {
            try {
                val markets = observer.observe()
                val decisions = strategy.evaluate(markets)
                val approved = riskManager.filter(decisions)

                executor.execute(approved)

            } catch (e: Exception) {
                println("ERROR: ${e.message}")
            }

            delay(30000)
        }
    }
}
