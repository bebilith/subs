package com.warrensofthought.subs;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.warrensofthought.subs.screens.GameLoop;
import com.warrensofthought.subs.screens.MainMenu;
import com.warrensofthought.subs.screens.SubsScreen;

public class Subs extends Game {

    @Override
    public void create() {
        setScreen(new MainMenu(this));
    }

    @Override
    public void render() {
        SubsScreen currentScreen = getScreen();
        // update the screen
        currentScreen.render(Gdx.graphics.getDeltaTime());

        // When the screen is done we change to the
        // next screen. Ideally the screen transitions are handled
        // in the screen itself or in a proper state machine.
        if (currentScreen.isDone()) {
            if (currentScreen instanceof MainMenu) {
                setScreen(new GameLoop(this));
            }
            // dispose the resources of the current screen
            currentScreen.dispose();
        }
    }

    /**
     * For this game each of our screens is an instance of InvadersScreen.
     *
     * @return the currently active {@link SubsScreen}.
     */
    @Override
    public SubsScreen getScreen() {
        return (SubsScreen) super.getScreen();
    }
}
