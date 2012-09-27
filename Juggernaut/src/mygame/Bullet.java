/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author josh
 */


public class Bullet {
    float lifespan;
    float velocity;
    Vector3f direction;
    float dammage;
    
    static Sphere sphere;
    RigidBodyControl bulletPhys;
    
    Bullet(){
        
    }
    
    Bullet(Material bulletMat, float dammage, Vector3f pos, SimpleApplication game, BulletAppState bulletAppState){
        sphere = new Sphere(32, 32, .4f, true, false);
        Geometry bulletGeo = new Geometry("Bullet", sphere);
        game.getRootNode().attachChild(bulletGeo);
        bulletGeo.setLocalTranslation(pos.add(2, 0, 0));
        bulletGeo.setMaterial(bulletMat);
        bulletPhys = new RigidBodyControl(.01f);
        bulletGeo.addControl(bulletPhys);
        
        bulletAppState.getPhysicsSpace().add(bulletPhys);
        
        bulletPhys.setGravity(Vector3f.ZERO);
        
        bulletPhys.setLinearVelocity(new Vector3f(25,0,0));
        
        this.dammage = dammage;
        
    }
    
    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public void setLifespan(float lifespan) {
        this.lifespan = lifespan;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public float getLifespan() {
        return lifespan;
    }

    public float getVelocity() {
        return velocity;
    }
    
    
}
