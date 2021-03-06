package com.mygdx.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.mygdx.game.Component.CharacterComponent;
import com.mygdx.game.Screen.WinScreen;
import com.mygdx.game.System.BulletSystem;
// import com.badlogic.gdx.graphics.OrthographicCamera;

import com.mygdx.game.System.PlayerSystem;
import com.mygdx.game.System.RenderSystem;

public class GameWorld {
    private static final boolean debug = true;
    private DebugDrawer debugDrawer;
    private static final float FOV = 67F;
    private ModelBatch modelBatch;
    private Environment environment;
    private PerspectiveCamera perspectiveCamera;
    private ModelLoader loader = new ObjLoader();
    private Engine engine;
    private Entity character;
    public BulletSystem bulletSystem;
    int poloz = (int)(Math.random()*4);

    Model maze = loader.loadModel(Gdx.files.internal("labirinettt.obj"));
    Model mazeBackGround = loader.loadModel(Gdx.files.internal("PlaneBackGround4.obj"));
    Model modelBackGround = loader.loadModel(Gdx.files.internal("SphereBackGround.obj"));

    private void setDebug(){
        if(debug){
            debugDrawer = new DebugDrawer();
            debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
        }
    }

    public GameWorld(WinScreen winScreen) {
        Bullet.init();
        initEnvironment();
        initModelBatch();
        initPersCamera();
        addSystems(winScreen);
        addEntities();
        setDebug();
    }

    private void initEnvironment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 2.f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }

    private void initPersCamera() {
        perspectiveCamera = new PerspectiveCamera(FOV, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        perspectiveCamera.position.set(0f,30f,0f);
        perspectiveCamera.rotate(90,0,2,0);
        perspectiveCamera.lookAt(0f,0f,0f);
        perspectiveCamera.near =0.1f;
        perspectiveCamera.far =300f;
        CameraInputController camController = new CameraInputController(perspectiveCamera);
        Gdx.input.setInputProcessor(camController);
        perspectiveCamera.update();
    }

    private void initModelBatch() {
        modelBatch = new ModelBatch();
    }

    private void addEntities() {
        createGround();
        createPlaneBackGround();
        if(poloz==0)
        createPlayer(6.75f, 1.3f, 6.75f);
        else if(poloz==1)
        createPlayer(-6.75f, 1.3f, 6.75f);
        else if(poloz==2)
        createPlayer(6.75f, 1.3f, -6.75f);
        else if(poloz==3)
        createPlayer(-6.75f, 1.3f, -6.75f);
        createBackGround();

    }

    private void createPlayer(float x, float y, float z) {
        character = EntityFactory.createPlayer(bulletSystem, x, y, z);
        engine.addEntity(character);
    }


    private void createGround() {
        engine.addEntity(EntityFactory.createStaticEntity(maze,0,0,0));
    }
    private void createPlaneBackGround() {
        engine.addEntity(EntityFactory.createStaticEntity(mazeBackGround,0,-30,0));
    }
    private void createBackGround(){
        engine.addEntity(EntityFactory.createBackGround(modelBackGround,0,0,0));
    }

    private void addSystems(WinScreen winScreen) {
        engine = new Engine();
        engine.addSystem(new RenderSystem(modelBatch, environment));
        engine.addSystem(bulletSystem = new BulletSystem());
        engine.addSystem(new PlayerSystem(this, perspectiveCamera, winScreen));
    }

    public void render(float delta) {
        renderWorld(delta);

        bulletSystem.collisionWorld.setDebugDrawer(debugDrawer);
        //debugDrawer.begin(perspectiveCamera);
        //bulletSystem.collisionWorld.debugDrawWorld();
        //debugDrawer.end();
    }


    protected void renderWorld(float delta) {
        modelBatch.begin(perspectiveCamera);
        engine.update(delta);///////
        modelBatch.end();
    }

    public void resize(int width, int height) {
        perspectiveCamera.viewportHeight = height;
        perspectiveCamera.viewportWidth = width;
    }

    public void dispose() {
        bulletSystem.collisionWorld.removeAction(character.getComponent(CharacterComponent.class).characterController);
        bulletSystem.collisionWorld.removeCollisionObject(character.getComponent(CharacterComponent.class).ghostObject);
        bulletSystem.dispose();

        bulletSystem = null;

        modelBatch.dispose();

        modelBatch = null;
        character.getComponent(CharacterComponent.class).characterController.dispose();
        character.getComponent(CharacterComponent.class).ghostObject.dispose();
        character.getComponent(CharacterComponent.class).ghostShape.dispose();
    }

    public void remove(Entity entity) {
        engine.removeEntity(entity);
        bulletSystem.removeBody(entity);
    }
}