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
public class miniGun extends Weapon{
     
    miniGun()
    {
     dammage = 0.5f;
     fireRate = 4;
     maxAmmo = 100;
     currentAmmo = 0;
    }
    miniGun(SimpleApplication gameRef)
    {
     dammage = 0.5f;
     fireRate = 4;
     maxAmmo = 100;
     currentAmmo = 0;
     
     bulletMaterial = new Material(gameRef.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
     bulletMaterial.setColor("Color", ColorRGBA.Yellow);
    }
    
}
