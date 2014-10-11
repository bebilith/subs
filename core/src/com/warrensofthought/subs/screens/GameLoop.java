package com.warrensofthought.subs.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.warrensofthought.subs.Subs;
import com.warrensofthought.subs.physics.GameObject;
import com.warrensofthought.subs.physics.util.GameObjectFactory;

import java.util.Random;


/**
 * Created by till on 08.10.14.
 */
public class GameLoop extends SubsScreen {

    final static short GROUND_FLAG = 1 << 8;
    final static short OBJECT_FLAG = 1 << 9;
    final static short ALL_FLAG = -1;
    PerspectiveCamera camera;
    CameraInputController camController;
    Model model;
    ModelBatch modelBatch;
    Environment environment;
    btCollisionConfiguration collisionConfig;
    btDispatcher dispatcher;
    Array<GameObject> instances;
    ArrayMap<String, GameObjectFactory> constructors;
    MyContactListener contactListener;
    btBroadphaseInterface broadphase;
    btCollisionWorld collisionWorld;
    private float spawnTimer;
    private Label label;
    private BitmapFont font;
    private Stage stage;
    private StringBuilder stringBuilder;
    private int visibleCount;
    private Vector3 position;
    private int selected = -1, selecting = -1;
    private Material selectionMaterial;
    private Material originalMaterial;

    public GameLoop(Subs subs) {
        super(subs);
        Bullet.init();

        position = new Vector3();

        font = new BitmapFont();
        label = new Label("", new Label.LabelStyle(font, Color.RED));
        stringBuilder = new StringBuilder();
        stage = new Stage();
        stage.addActor(label);
        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(3f, 7f, 10f);
        camera.lookAt(0, 4f, 0);
        camera.update();

        camController = new CameraInputController(camera);
        constructors = new ArrayMap<String, GameObjectFactory>(String.class, GameObjectFactory.class);
        Gdx.input.setInputProcessor(camController);

        ModelBuilder mb = new ModelBuilder();
        mb.begin();
        mb.node().id = "ground";
        mb.part("ground", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.RED)))
                .box(5f, 1f, 5f);
        mb.node().id = "sphere";
        mb.part("sphere", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.GREEN)))
                .sphere(1f, 1f, 1f, 10, 10);
        mb.node().id = "box";
        mb.part("box", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.BLUE)))
                .box(1f, 1f, 1f);
        mb.node().id = "cone";
        mb.part("cone", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.YELLOW)))
                .cone(1f, 2f, 1f, 10);
        mb.node().id = "capsule";
        mb.part("capsule", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.CYAN)))
                .capsule(0.5f, 2f, 10);
        mb.node().id = "cylinder";
        mb.part("cylinder", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.MAGENTA)))
                .cylinder(1f, 2f, 1f, 10);
        model = mb.end();

        constructors.put("ground", new GameObjectFactory(model, "ground", new btBoxShape(new Vector3(2.5f, 0.5f, 2.5f))));
        constructors.put("sphere", new GameObjectFactory(model, "sphere", new btSphereShape(0.5f)));
        constructors.put("box", new GameObjectFactory(model, "box", new btBoxShape(new Vector3(0.5f, 0.5f, 0.5f))));
        constructors.put("cone", new GameObjectFactory(model, "cone", new btConeShape(0.5f, 2f)));
        constructors.put("capsule", new GameObjectFactory(model, "capsule", new btCapsuleShape(.5f, 1f)));
        constructors.put("cylinder", new GameObjectFactory(model, "cylinder", new btCylinderShape(new Vector3(.5f, 1f, .5f))));
        instances = new Array<GameObject>();
        GameObject object = constructors.get("ground").construct();

        Random rand = new Random();
        int[][] heightmap = new int[100][100];

        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                int maxZ = rand.nextInt(3);
                heightmap[y][x] = maxZ;
            }
        }

        int total = 0;
        int culled = 0;
        for (int y = 0; y < 100; y++) {
            for (int x = 0; x < 100; x++) {
                for (int z = 0; z < heightmap[y][x]; z++) {
                    GameObject box = constructors.get("box").construct();
                    box.transform.trn(x, y, z);
                    instances.add(box);
                    boolean invisZ = z == 0 || z < heightmap[y][x] - 1;

                    boolean ym1 = y == 0 || heightmap[y - 1][x] != 0;
                    boolean yp1 = y == 99 || heightmap[y + 1][x] != 0;

                    boolean xm1 = x == 0 || heightmap[y][x - 1] != 0;
                    boolean xp1 = x == 99 || heightmap[y][x + 1] != 0;
                    total++;
                    if (invisZ && ym1 && yp1 && xm1 && xp1) {
                        box.visible = false;
                        culled++;
                    }
                }
            }
        }
        System.out.println(String.format("Total/Culled %s/%s", total, culled));

        collisionConfig = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfig);
        broadphase = new btDbvtBroadphase();
        contactListener = new MyContactListener();
        collisionWorld = new btCollisionWorld(dispatcher, broadphase, collisionConfig);
        collisionWorld.addCollisionObject(object.body, GROUND_FLAG, ALL_FLAG);

        Gdx.input.setInputProcessor(new InputMultiplexer(this, camController));
        selectionMaterial = new Material();
        selectionMaterial.set(ColorAttribute.createDiffuse(Color.ORANGE));
        originalMaterial = new Material();
    }


    @Override
    public void update(float delta) {

    }

    public void spawn() {
        GameObject obj = constructors.values[1 + MathUtils.random(constructors.size - 2)].construct();
        obj.moving = true;
        obj.transform.setFromEulerAngles(MathUtils.random(360f), MathUtils.random(360f), MathUtils.random(360f));
        obj.transform.trn(MathUtils.random(-2.5f, 2.5f), 9f, MathUtils.random(-2.5f, 2.5f));
        obj.body.setWorldTransform(obj.transform);
        obj.body.setUserValue(instances.size);
        obj.body.setCollisionFlags(obj.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
        instances.add(obj);
        collisionWorld.addCollisionObject(obj.body, OBJECT_FLAG, GROUND_FLAG);
    }

    private boolean isVisible(Camera camera, GameObject instance) {
        instance.transform.getTranslation(position);
        position.add(instance.center);
        return instance.visible && camera.frustum.boundsInFrustum(position, instance.dimensions);
    }

    @Override
    public void draw(float delta) {
        camController.update();

        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glCullFace(GL20.GL_BACK);

//        for (GameObject obj : instances) {
//            if (obj.moving) {
//                obj.transform.trn(0f, -delta, 0f);
//                obj.body.setWorldTransform(obj.transform);
//            }
//        }
//        collisionWorld.performDiscreteCollisionDetection();

//        if ((spawnTimer -= delta) < 0) {
//            spawn();
//            spawnTimer = 1.5f;
//        }

        modelBatch.begin(camera);
        visibleCount = 0;
        for (final GameObject modelInstance : instances) {
            if (isVisible(camera, modelInstance)) {
                modelBatch.render(modelInstance, environment);
                visibleCount++;
            }
        }
        stringBuilder.setLength(0);
        stringBuilder.append(" FPS: ").append(Gdx.graphics.getFramesPerSecond());
        stringBuilder.append(" Visible: ").append(visibleCount);
        stringBuilder.append(" Selected: ").append(selected);
        label.setText(stringBuilder);
//        modelBatch.render(instances, environment);
        modelBatch.end();
        stage.draw();
    }


    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        model.dispose();

        for (GameObject obj : instances) {
            obj.dispose();
        }
        instances.clear();

        for (GameObjectFactory factory : constructors.values()) {
            factory.dispose();
        }
        constructors.clear();

        collisionConfig.dispose();
        dispatcher.dispose();
        collisionWorld.dispose();
        broadphase.dispose();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        selecting = getObject(screenX, screenY);
        return selecting >= 0;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return selecting >= 0;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (selecting >= 0) {
            if (selecting == getObject(screenX, screenY))
                setSelected(selecting);
            selecting = -1;
            return true;
        }
        return false;
    }

    public void setSelected(int value) {
        if (selected == value) return;
        if (selected >= 0) {
            Material mat = instances.get(selected).materials.get(0);
            mat.clear();
            mat.set(originalMaterial);
        }
        selected = value;
        if (selected >= 0) {
            Material mat = instances.get(selected).materials.get(0);
            originalMaterial.clear();
            originalMaterial.set(mat);
            mat.clear();
            mat.set(selectionMaterial);
        }
    }

    public int getObject(int screenX, int screenY) {
        Ray ray = camera.getPickRay(screenX, screenY);

        int result = -1;
        float distance = -1;

        for (int i = 0; i < instances.size; ++i) {
            final GameObject instance = instances.get(i);
            if (instance.visible) {
                instance.transform.getTranslation(position);
                position.add(instance.center);

                float dist2 = ray.origin.dst2(position);
                if (distance >= 0f && dist2 > distance)
                    continue;

                if (Intersector.intersectRaySphere(ray, position, instance.radius, null)) {
                    result = i;
                    distance = dist2;
                }
            }
        }

        return result;
    }

    class MyContactListener extends ContactListener {
        @Override
        public boolean onContactAdded(int userValue0, int partId0, int index0, int userValue1, int partId1, int index1) {
            instances.get(userValue0).moving = false;
            instances.get(userValue1).moving = false;
            return true;
        }
    }
}
