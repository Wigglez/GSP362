/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

/**
 *
 * @author josh
 */
public class laserRifle {
    float dammage = 6;
    float fireRate = (float) 0.5;
    int maxAmmo = 20;
    int currentAmmo = 0;
    float dammageIncrease;
    float fireRateIncrease;
    
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
        return dammage;
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
   
    public void Fire() {
        
        Bullet bullet = new Bullet();
        
    }
    
}
