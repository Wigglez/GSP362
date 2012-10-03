/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Juggernaut;

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
    Vector3f velocity;
    Vector3f direction;
    float dammage;
    
    static Sphere sphere;
    RigidBodyControl bulletPhys;
    
    Bullet(){
        
    }
    
    Bullet(Material bulletMat, float dammage, Vector3f pos, Vector3f dir, SimpleApplication game, BulletAppState bulletAppState){
        sphere = new Sphere(32, 32, .4f, true, false);
        Geometry bulletGeo = new Geometry("Bullet", sphere);
        game.getRootNode().attachChild(bulletGeo);
        Vector3f bulletOffset = new Vector3f(2*dir.x, 0, 0);
        bulletGeo.setLocalTranslation(pos.add(bulletOffset));
        bulletGeo.setMaterial(bulletMat);
        bulletPhys = new RigidBodyControl(.01f);
        bulletGeo.addControl(bulletPhys);
        
        bulletAppState.getPhysicsSpace().add(bulletPhys);
        
        bulletPhys.setGravity(Vector3f.ZERO);
        
        bulletPhys.setLinearVelocity(new Vector3f(35,0,0).mult(dir));
        velocity = bulletPhys.getLinearVelocity();
        this.dammage = dammage;
        
    }
    
    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public void setLifespan(float lifespan) {
        this.lifespan = lifespan;
    }

    

    public Vector3f getDirection() {
        return direction;
    }

    public float getLifespan() {
        return lifespan;
    }

    public Vector3f getVelocity() {
        return velocity;
    }
    
    
}
