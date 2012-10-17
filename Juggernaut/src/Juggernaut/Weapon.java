/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Juggernaut;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import java.util.Vector;

/**
 *
 * @author Vince
 */
public abstract class Weapon {
    protected float damage;
    protected float fireRate;
    protected int maxAmmo;
    protected int currentAmmo;
    
    protected Material bulletMaterial;
    
    public void setCurrentAmmo(int currentAmmo) {
        this.currentAmmo = currentAmmo;
        if(currentAmmo > maxAmmo){
            currentAmmo = maxAmmo;
        }
    }

  

    public void setMaxAmmo(int maxAmmo) {
        this.maxAmmo = maxAmmo;
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }

    public float getDamage() {
        return damage;
    }

    public float getFireRate() {
        return fireRate;
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }
    
    public Bullet Fire(float damageModifier, Vector3f pos, Vector3f dir, Main game, BulletAppState bulletAppState) {
        
        Bullet bullet = new Bullet(bulletMaterial, damage * damageModifier, pos, dir, game, bulletAppState);
        if(currentAmmo > 0){
            currentAmmo--;
        }
       return bullet;
    }
    
}
