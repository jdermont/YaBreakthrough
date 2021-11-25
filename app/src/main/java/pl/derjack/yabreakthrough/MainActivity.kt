package pl.derjack.yabreakthrough

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import pl.derjack.yabreakthrough.game.Action
import pl.derjack.yabreakthrough.views.BoardView

const val TAG = "MainActivity"
class MainActivity : AppCompatActivity(), BoardView.BoardListener {
    private val gameModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionObserver: Observer<Action> = Observer { action ->
            val x = action.toCoords()
            boardView.clickAndAnimate(x[0], x[1], x[2], x[3])
        }

        gameModel.lastAction.observe(this, actionObserver)
        boardView.setBoardListener(this)
        boardView.setGame(gameModel.game)
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
    }
}