
interface Strategy {
    fun evaluate(markets: List<Market>): List<TradeDecision>
}

class MomentumStrategy : Strategy {

    override fun evaluate(markets: List<Market>): List<TradeDecision> {
        return markets.mapNotNull {
            if (it.probability > 0.65 && it.volume > 1000) {
                TradeDecision(it.id, "BUY", 0.7)
            } else null
        }
    }
}

class StrategyEngine(private val strategies: List<Strategy>) {

    fun evaluate(markets: List<Market>): List<TradeDecision> {
        return strategies.flatMap { it.evaluate(markets) }
    }
}
