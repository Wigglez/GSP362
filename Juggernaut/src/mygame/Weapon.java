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
    private float dammageIncrease;
    private float fireRateIncrease;
    private float increaseDammage = (float).5;
    protected Material bulletMaterial;
    
    public void setCurrentAmmo(int currentAmmo) {
        this.currentAmmo = currentAmmo;
    }

    public void setDammage(float dammage) {
        this.dammage = dammage + dammageIncrease;
    }

    public void setFireRate(float fireRate) {
        this.fireRate = fireRate + fireRateIncrease;
    }

    public void setMaxAmmo(int maxAmmo) {
        this.maxAmmo = maxAmmo;
    }

    public int getCurrentAmmo() {
        return currentAmmo;
    }

    public float getDammage() {
        return dammage + increaseDammage;
    }

    public float getFireRate() {
        return fireRate;
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

    public void setDammageIncrease(float dammageIncrease) {
        this.dammageIncrease = dammageIncrease;
    }

    public void setFireRateIncrease(float fireRateIncrease) {
        this.fireRateIncrease = fireRateIncrease;
    }

    public float getDammageIncrease() {
        return dammageIncrease;
    }

    public float getFireRateIncrease() {
        return fireRateIncrease;
    }
    
    public void Fire(float damageModifier, Vector3f pos, SimpleApplication game, BulletAppState bulletAppState ) {
        
        Bullet bullet = new Bullet(bulletMaterial, dammage * damageModifier, pos, game, bulletAppState);
        
    }
    
}
