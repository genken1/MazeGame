package com.mygdx.game.System;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.mygdx.game.Component.ModelComponent;

public class RenderSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;//Immutable - list of constants objects
    private ModelBatch batch;
    private Environment environment;

    public RenderSystem(ModelBatch batch, Environment environment) {
        this.batch = batch;
        this.environment = environment;
    }

    public void addedToEngine(Engine e) {
        // Grabs all entities with desired components
        entities = e.getEntitiesFor(Family.all(ModelComponent.class).get());
    }

    public void update(float delta) {
        for (int i = 0; i < entities.size(); i++) {
            ModelComponent mod = entities.get(i).getComponent(ModelComponent.class);
            batch.render(mod.instance, environment);
        }
    }
}