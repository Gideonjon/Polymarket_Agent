
class RiskManager {

    fun filter(decisions: List<TradeDecision>): List<TradeDecision> {
        return decisions.filter { it.confidence > 0.6 }
    }
}
