/*
 * Copyright 2017 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.indicators;

import org.terasology.entitySystem.entity.EntityManager;
import org.terasology.entitySystem.entity.EntityRef;
import org.terasology.entitySystem.entity.lifecycleEvents.BeforeRemoveComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnAddedComponent;
import org.terasology.entitySystem.entity.lifecycleEvents.OnChangedComponent;
import org.terasology.entitySystem.event.ReceiveEvent;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterMode;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.logic.location.Location;
import org.terasology.logic.location.LocationComponent;
import org.terasology.math.geom.Quat4f;
import org.terasology.math.geom.Vector3f;
import org.terasology.registry.In;
import org.terasology.rendering.assets.texture.TextureRegionAsset;
import org.terasology.rendering.iconmesh.IconMeshFactory;
import org.terasology.rendering.logic.MeshComponent;
import org.terasology.utilities.Assets;

/**
 * Created by nikhil on 15/5/17.
 */
@RegisterSystem(RegisterMode.AUTHORITY)
public class IndicatorAuthoritySystem  extends BaseComponentSystem {
    private static final float INDICATOR_SCALE = 0.5f;
    @In
    private EntityManager entityManager;

    private MeshComponent getIndicatorMeshComponent(IndicatorComponent indicator) {
            MeshComponent meshComponent = null;
            meshComponent = new MeshComponent();
            meshComponent.material = Assets.getMaterial("engine:droppedItem").get();
            TextureRegionAsset<?> textureRegionAsset = Assets.getTextureRegion(indicator.icon).orElse(null);
            if (textureRegionAsset != null) {
                meshComponent.mesh = IconMeshFactory.getIconMesh(textureRegionAsset);
            }
            return meshComponent;
    }

    private LocationComponent getIndicatorLocation(LocationComponent loc) {
        Vector3f pos = new Vector3f(loc.getWorldPosition());
        //TODO should center indicator above head of entity
        pos.addY(1.2f);
        LocationComponent indicatorLocation = new LocationComponent(pos);
        indicatorLocation.setWorldRotation(loc.getWorldRotation());
        indicatorLocation.setWorldScale(0.1f);
        return indicatorLocation;
    }

    private EntityRef getIndicatorEntity(IndicatorComponent indicator,
                                         LocationComponent loc, EntityRef entity) {
        return entityManager.create(getIndicatorMeshComponent(indicator),  getIndicatorLocation(loc));
    }

    @ReceiveEvent
    public void onIndicatorAdded(OnAddedComponent event, EntityRef entity, IndicatorComponent indicator) {
        if(!entity.hasComponent(LocationComponent.class))
            return;
        LocationComponent loc = entity.getComponent(LocationComponent.class);
        indicator.entity = getIndicatorEntity(indicator, loc, entity);
        //TODO ideally the location of the indicator must be childed to that of the entity's, but it doesn't render
        //Location.attachChild(entity, indicator.entity, new Vector3f(0, 1.2f, 0), new Quat4f(), .1f);
        entity.saveComponent(indicator);
    }

    @ReceiveEvent
    public  void BeforeIndicatorRemoved(BeforeRemoveComponent event, EntityRef entity, IndicatorComponent indicator) {
        //Location.removeChild(entity, indicator.entity);
        indicator.entity.destroy();
    }

    @ReceiveEvent
    public void onEntityLocationChange(OnChangedComponent event, EntityRef entity, LocationComponent loc) {
        if(!entity.hasComponent(IndicatorComponent.class))
            return;

        IndicatorComponent indicator = entity.getComponent(IndicatorComponent.class);
        indicator.entity.saveComponent(getIndicatorLocation(loc));
    }

    @ReceiveEvent
    public void OnDisplayIndicatorEvent(DisplayIndicatorEvent displayIndicatorEvent, EntityRef entity) {
        if(entity.hasComponent(IndicatorComponent.class))
            entity.removeComponent(IndicatorComponent.class);
        entity.addComponent(displayIndicatorEvent.indicatorComponent);
    }

}
