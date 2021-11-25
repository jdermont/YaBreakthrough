package pl.derjack.yabreakthrough;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import pl.derjack.yabreakthrough.game.Action;
import pl.derjack.yabreakthrough.game.Game;

public class GameViewModel extends ViewModel {

    @Nullable
    public Game game;
    @Nullable
    public CpuWorker cpuWorker;
    @Nullable
    private SingleLiveEvent<Action> lastAction;

    public MutableLiveData<Action> getLastAction() {
        if (lastAction == null) {
            lastAction = new SingleLiveEvent<>();
        }
        return lastAction;
    }

    @Override
    protected void onCleared() {
        Log.d("GameViewModel","onCleared");
    }
}
