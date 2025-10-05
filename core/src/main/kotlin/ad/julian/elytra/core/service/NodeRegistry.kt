package ad.julian.elytra.core.service

import ad.julian.elytra.core.model.RunningNode
import org.springframework.stereotype.Service

@Service
class NodeRegistry {
    val nodes = mutableMapOf<String, RunningNode>()

    fun registerNode(node: RunningNode) {
        nodes[node.id] = node
    }

    fun getNode(id: String): RunningNode? {
        return nodes[id]
    }
}