package com.warrensofthought.subs.screens;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.warrensofthought.subs.Subs;

/**
 * Created by till on 08.10.14.
 */
public abstract class SubsScreen extends InputAdapter implements Screen {

    protected Subs subs;

    public SubsScreen(Subs subs) {
        this.subs = subs;
    }

    /**
     * Called when the screen should update itself, e.g. continue a simulation etc.
     */
    public abstract void update(float delta);

    /**
     * Called when a screen should render itself
     */
    public abstract void draw(float delta);

    /**
     * Called by GdxSubs to check whether the screen is done.
     *
     * @return true when the screen is done, false otherwise
     */
    public abstract boolean isDone();

    @Override
    public void render(float delta) {
        update(delta);
        draw(delta);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
