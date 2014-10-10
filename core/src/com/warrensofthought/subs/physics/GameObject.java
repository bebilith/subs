package com.warrensofthought.subs.physics;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by till on 09.10.14.
 */
public class GameObject extends ModelInstance implements Disposable {

    private final static BoundingBox bounds = new BoundingBox();
    public final btCollisionObject body;
    public final Vector3 center = new Vector3();
    public final Vector3 dimensions = new Vector3();
    public boolean moving;
    public boolean visible = true;

    public GameObject(Model model, String node, btCollisionShape shape, boolean mergeTransform) {
        super(model, node, mergeTransform);
        body = new btCollisionObject();
        body.setCollisionShape(shape);
        calculateBoundingBox(bounds);
        center.set(bounds.getCenter());
        dimensions.set(bounds.getDimensions());
    }

    @Override
    public void dispose() {
        body.dispose();
    }
}
