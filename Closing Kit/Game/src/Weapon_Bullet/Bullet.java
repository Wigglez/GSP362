/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Vector3f;

/**
 *
 * @author josh
 */


public class Bullet {
    float lifespan;
    float velocity;
    Vector3f direction;

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
