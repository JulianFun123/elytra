package ad.julian.elytra.core.model

import ad.julian.elytra.core.service.WebSocketService

data class RunningNode(
    val id: String,
    val session: WebSocketService.WrappedSession
)