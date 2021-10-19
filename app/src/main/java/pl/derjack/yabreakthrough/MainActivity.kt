package pl.derjack.yabreakthrough

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import pl.derjack.yabreakthrough.game.Game
import pl.derjack.yabreakthrough.views.BoardView

const val TAG = "MainActivity"
class MainActivity : AppCompatActivity(), BoardView.BoardListener {

    val game = Game()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boardView.setBoardListener(this)
        boardView.setGame(game)
    }

    override fun clicked(rowFrom: Int, colFrom: Int, rowTo: Int, colTo: Int) {
        Log.d(TAG,"clicked $rowFrom $colFrom $rowTo $colTo")
        game.makeMove(rowFrom, colFrom, rowTo, colTo)
    }

    override fun onAnimationStarted() {
        Log.d(TAG,"onAnimationStarted")
    }

    override fun onAnimationFinished() {
        Log.d(TAG,"onAnimationFinished")
    }
}