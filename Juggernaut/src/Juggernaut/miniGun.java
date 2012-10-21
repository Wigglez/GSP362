/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Juggernaut;

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
     damage = 0.5f;
     fireRate = .125f;
     maxAmmo = 300;
     currentAmmo = 300;
    }
    miniGun(SimpleApplication gameRef)
    {
     damage = 0.5f;
     fireRate = .1f;
     maxAmmo = 300;
     currentAmmo = 300;
     
     bulletMaterial = new Material(gameRef.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
     bulletMaterial.setColor("Color", ColorRGBA.Yellow);
    }
    
}
