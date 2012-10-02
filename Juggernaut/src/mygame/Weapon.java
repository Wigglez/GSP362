/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;

/**
 *
 * @author Vince
 */
public abstract class Weapon {
    protected float dammage;
    protected float fireRate;
    protected int maxAmmo;
    protected int currentAmmo;
    
    protected Material bulletMaterial;
    
    public void setCurrentAmmo(int currentAmmo) {
        this.currentAmmo = currentAmmo;
    }

  

    public void setMaxAmmo(int maxAmmo) {
        this.maxAmmo = maxAmmo;
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }

    public float getDammage() {
        return dammage;
    }

    public float getFireRate() {
        return fireRate;
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }
    
    public void Fire(float damageModifier, Vector3f pos, Vector3f dir, SimpleApplication game, BulletAppState bulletAppState ) {
        
        Bullet bullet = new Bullet(bulletMaterial, dammage * damageModifier, pos, dir, game, bulletAppState);
        
    }
    
}
