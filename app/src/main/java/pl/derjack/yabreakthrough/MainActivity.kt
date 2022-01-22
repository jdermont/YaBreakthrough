package pl.derjack.yabreakthrough

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import pl.derjack.yabreakthrough.cpu.BetterEvaluator
import pl.derjack.yabreakthrough.game.Action
import pl.derjack.yabreakthrough.game.Board
import pl.derjack.yabreakthrough.game.Game
import pl.derjack.yabreakthrough.views.BoardView

class MainActivity : AppCompatActivity(), BoardView.BoardListener, View.OnClickListener {
    private val gameModel: GameViewModel by viewModels()
    private val actionObserver: Observer<Action> = Observer { action ->
        val x = action.toCoords()
        boardView.clickAndAnimate(x[0], x[1], x[2], x[3])
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gameModel.lastAction.observe(this, actionObserver)
        setupViews()
        refreshViews()
    }

    override fun clicked(rowFrom: Int, colFrom: Int, rowTo: Int, colTo: Int) {
        Log.d(TAG,"clicked $rowFrom $colFrom $rowTo $colTo")
        gameModel.game?.makeMove(rowFrom, colFrom, rowTo, colTo)
    }

    override fun onAnimationStarted() {
        Log.d(TAG,"onAnimationStarted")
    }

    override fun onAnimationFinished() {
        Log.d(TAG,"onAnimationFinished")
        gameModel.game?.run {
            if (this.currentPlayer == gameModel.cpuWorker?.cpu?.player) {
                gameModel.cpuWorker?.doWork(this, gameModel.lastAction)
            }
        }
    }

    private fun setupViews() {
        boardView.setBoardListener(this)
        startBtn.setOnClickListener(this)
    }

    private fun refreshViews() {
        boardView.setGame(gameModel.game)
        startBtn.isEnabled = gameModel.game?.isOver ?: true
    }

    override fun onClick(v: View?) {
        startGame()
    }

    private fun startGame() {
        gameModel.cpuWorker?.cancel()
        gameModel.game = Game()
        gameModel.cpuWorker = CpuWorker(BetterEvaluator())
        gameModel.cpuWorker?.cpu?.player = Board.TWO
        boardView.setGame(gameModel.game)
    }

    companion object {
        const val TAG = "MainActivity"
    }
}