package com.warrensofthought.subs.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.warrensofthought.subs.Subs;


/**
 * Created by till on 08.10.14.
 */
public class GameLoop extends SubsScreen {

    PerspectiveCamera camera;
    CameraInputController camController;
    Model model;
    ModelBatch modelBatch;
    Array<ModelInstance> instances;
    Environment environment;
    ModelInstance ball;
    ModelInstance ground;


    public GameLoop(Subs subs) {
        super(subs);
        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(3f, 7f, 10f);
        camera.lookAt(0, 4f, 0);
        camera.update();

        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);

        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        mb.node().id = "ground";
        mb.part("box", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position |
                        VertexAttributes.Usage.Normal,
                new Material(ColorAttribute.createDiffuse(Color.RED))
        )
                .box(5f, 1f, 5f);
        mb.node().id = "ball";
        mb.part("sphere", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.GREEN)))
                .sphere(1f, 1f, 1f, 10, 10);
        model = mb.end();

        ground = new ModelInstance(model, "ground");
        ball = new ModelInstance(model, "ball");
        ball.transform.setToTranslation(0, 9f, 0);
        instances = new Array<ModelInstance>();
        instances.add(ground);
        instances.add(ball);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void draw(float delta) {
        camController.update();

        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(camera);
        modelBatch.render(instances, environment);
        modelBatch.end();
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        model.dispose();
    }
}
