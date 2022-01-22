package pl.derjack.yabreakthrough

import android.util.Log
import androidx.lifecycle.ViewModel
import pl.derjack.yabreakthrough.game.Game
import pl.derjack.yabreakthrough.CpuWorker
import pl.derjack.yabreakthrough.SingleLiveEvent
import androidx.lifecycle.MutableLiveData
import pl.derjack.yabreakthrough.game.Action

class GameViewModel : ViewModel() {
    var game: Game? = null
    var cpuWorker: CpuWorker? = null
    val lastAction: SingleLiveEvent<Action> by lazy { SingleLiveEvent() }

    override fun onCleared() {
        Log.d("GameViewModel", "onCleared")
    }
}