package com.warrensofthought.subs.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.warrensofthought.subs.Subs;

/**
 * Created by till on 08.10.14.
 */
public class MainMenu extends SubsScreen {

    boolean isDone;
    SpriteBatch batch;
    Texture img;

    public MainMenu(Subs subs) {
        super(subs);
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
        isDone = false;
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void draw(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(img, 0, 0);
        batch.end();
    }

    @Override
    public boolean isDone() {
        return isDone;
    }
}
