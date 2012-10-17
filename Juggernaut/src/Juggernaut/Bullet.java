/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Juggernaut;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import java.util.Vector;

/**
 *
 * @author josh
 */


public class Bullet implements PhysicsCollisionListener {
    Main game;
    BulletAppState bulletAppState;
    float lifespan;
    float lifeTime;
    Vector3f velocity;
    Vector3f direction;
    float damage;
    
    static Sphere sphere;
    Geometry bulletGeo;
    RigidBodyControl bulletPhys;
    
    Bullet(){
        
    }
    
    Bullet(Material bulletMat, float damage, Vector3f pos, Vector3f dir, Main gameRef, BulletAppState bulletAppStateRef){
        game = gameRef;
        bulletAppState = bulletAppStateRef;
        sphere = new Sphere(32, 32, .4f, true, false);
        bulletGeo = new Geometry("Bullet", sphere);
        game.getRootNode().attachChild(bulletGeo);
        Vector3f bulletOffset = new Vector3f(2*dir.x, 0, 0);
        bulletGeo.setLocalTranslation(pos.add(bulletOffset));
        bulletGeo.setMaterial(bulletMat);
        bulletPhys = new RigidBodyControl(.01f);
        bulletGeo.addControl(bulletPhys);
        
        bulletAppState.getPhysicsSpace().add(bulletPhys);
        bulletAppState.getPhysicsSpace().addCollisionListener(this);

        bulletPhys.setGravity(Vector3f.ZERO);
        
        bulletPhys.setLinearVelocity(new Vector3f(35,0,0).mult(dir));
        velocity = bulletPhys.getLinearVelocity();
        this.damage = damage;
        
        lifeTime = 0;
        lifespan = 1.5f;
        
        
    }
    
    public boolean LifeTime(float dt){
        lifeTime += dt;
        
        if(lifeTime > lifespan){
            return true;
        } else{        
            return false;
        }
    }
    
    public void delete(){
        game.getRootNode().detachChild(bulletGeo);
        
        bulletAppState.getPhysicsSpace().remove(bulletPhys);
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
    
     public void collision(PhysicsCollisionEvent event) {
         if(event.getNodeB().equals(this.bulletGeo) && event.getNodeA().getName().equals("Enemy")
          || event.getNodeA().equals(this.bulletGeo) && event.getNodeB().getName().equals("LevleGeo")
          || event.getNodeB().equals(this.bulletGeo) && event.getNodeA().getName().equals("Health")){
            delete();
        }
        
        
    }
    
}
