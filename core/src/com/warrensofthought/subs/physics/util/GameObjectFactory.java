package com.warrensofthought.subs.physics.util;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.Disposable;
import com.warrensofthought.subs.physics.GameObject;

/**
 * Created by till on 09.10.14.
 */
public class GameObjectFactory implements Disposable {

    public final Model model;
    public final String node;
    public final btCollisionShape shape;

    public GameObjectFactory(Model model, String node, btCollisionShape shape) {
        this.model = model;
        this.node = node;
        this.shape = shape;
    }

    public GameObject construct() {
        return new GameObject(model, node, shape, true);
    }

    @Override
    public void dispose() {
        shape.dispose();
    }

}
