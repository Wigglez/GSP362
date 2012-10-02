/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;

/**
 *
 * @author josh
 */
public class laserRifle extends Weapon{
    laserRifle()
    {
        dammage = 6;
        fireRate = 1.5f;
        maxAmmo = 20;
        currentAmmo = 0;
    }
    laserRifle(SimpleApplication gameRef){
        dammage = 6;
        fireRate = 1.5f;
        maxAmmo = 20;
        currentAmmo = 0;
        
        bulletMaterial = new Material(gameRef.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        bulletMaterial.setColor("Color", ColorRGBA.Red);
    
    }
    
    
}
