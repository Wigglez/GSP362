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
public class Pistol extends Weapon{

 
    Pistol()
    {
        dammage = 1;
        fireRate = .75f;
        maxAmmo = 0;
        currentAmmo = 0;  
    }
    Pistol(SimpleApplication gameRef){
        dammage = .75f;
        fireRate = .25f;
        maxAmmo = 0;
        currentAmmo = 0;
       
        bulletMaterial = new Material(gameRef.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        bulletMaterial.setColor("Color", ColorRGBA.Green);
    }

}
    
