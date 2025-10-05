package ad.julian.elytra.core.api

import ad.julian.elytra.protocol.types.RunningServer

class ScalingRuleScript {
    var lambda: ((RunningServer) -> Int)? = null

    fun check(block: (server: RunningServer) -> Int) {
        lambda = block
    }
}

fun scalingRule(block: ScalingRuleScript.() -> Unit): ScalingRuleScript {
    val builder = ScalingRuleScript()
    builder.block()
    return builder
}