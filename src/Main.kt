
import kotlinx.coroutines.*

fun main() {

    val api = PolymarketApi()
    val observer = MarketObserver(api)
    val strategy = StrategyEngine(listOf(MomentumStrategy()))
    val risk = RiskManager()
    val executor = ExecutionEngine(api)

    val orchestrator = AgentOrchestrator(observer, strategy, risk, executor)

    println("Starting REAL trading agent...")

    CoroutineScope(Dispatchers.Default).launch {
        orchestrator.run()
    }

    Thread.sleep(Long.MAX_VALUE)
}
