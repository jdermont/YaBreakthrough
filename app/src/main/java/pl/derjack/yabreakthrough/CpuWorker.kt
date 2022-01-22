package pl.derjack.yabreakthrough

import pl.derjack.yabreakthrough.cpu.Evaluator
import pl.derjack.yabreakthrough.cpu.Cpu
import pl.derjack.yabreakthrough.game.Game
import androidx.lifecycle.MutableLiveData
import pl.derjack.yabreakthrough.game.Action
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class CpuWorker(evaluator: Evaluator) {
    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    val cpu: Cpu = Cpu(evaluator)

    fun doWork(game: Game, actionData: MutableLiveData<Action>) {
        cpu.updateGameState(game)
        executor.execute(Runnable {
            val move = cpu.bestMove
            if (Thread.interrupted()) {
                return@Runnable
            }
            actionData.postValue(move.move)
        })
    }

    fun cancel() {
        cpu.cancel()
        executor.shutdownNow()
    }

    companion object {
        private const val SHORT_THINK = 250L
        private const val LONG_THINK = 750L
    }

}