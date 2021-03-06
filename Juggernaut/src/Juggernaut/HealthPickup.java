/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Juggernaut;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 *
 * @author josh
 */
public class HealthPickup implements PhysicsCollisionListener{
    Main game;
    BulletAppState bulletAppState;
    Spatial healthPickup;
    private CharacterControl HealthPickup;        
    Spatial healthDebug;
    private boolean Pickup = false;
    private Character Juggernaut;
    boolean isActive = true;
    
    float increase;
    float currentHealth;
    HealthPickup(){
        
    }
    
   public void HealthPickup(float currentHealth){
       // Juggernaut = new Character();
      //game.startScreen.TestHealth();
      //game.getHud().healthPercentage = healthPercentage;
       
      //Juggernaut.getCurrentHealth();
      //float increase = 20;
      increase = 20;
      if( Juggernaut.healthPercentage() < 100)
      {
      currentHealth +=  increase;
      }
      System.out.print(Juggernaut.updateHealth() + "\n\n");
    
      //Juggernaut.setCurrentHealth(currentHealth);
      //return game.getHud().healthPercentage;
     
    // return Juggernaut.updateHealth();  
        
//        healthIncrease = 20;
//        currentHealth = this.Juggernaut.healthPercentage();
//        
//       if( currentHealth < 100){
//     
//           currentHealth = (currentHealth + healthIncrease);
//      }
//      
//      return this.Juggernaut.updateHealth();
    }

     HealthPickup(Character player,Main gameRef, BulletAppState bulletAppStateRef, Vector3f spawnLocation) {
        this.Juggernaut = player;
        this.game = gameRef;
        this.bulletAppState = bulletAppStateRef;
        bulletAppState.getPhysicsSpace().addCollisionListener(this);

 
        healthPickup = game.getAssetManager().loadModel("Models/Health.obj");
        healthPickup.setName("Health");
         Material mark_mat = new Material(game.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
       
         
        healthPickup.setLocalScale(.5f,.5f,.5f);
         healthPickup.setLocalTranslation(spawnLocation);

        game.getRootNode().attachChild(healthPickup);
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1f, 2f);
        HealthPickup = new CharacterControl(capsuleShape, .05f);
        HealthPickup.setFallSpeed(50);
        HealthPickup.setGravity(120);        
        HealthPickup.setPhysicsLocation(spawnLocation);
        HealthPickup.setCollideWithGroups(1);
        HealthPickup.setCollisionGroup(4);
      
        //healthDebug = HealthPickup.createDebugShape(game.getAssetManager());
        
        healthPickup.addControl(HealthPickup);
        //game.getRootNode().attachChild(healthDebug);

        bulletAppState.getPhysicsSpace().add(HealthPickup);
             
    }
     
      public void collision(PhysicsCollisionEvent event) {
        if(event.getNodeA().getName().equals("Player") && event.getNodeB().equals(this.healthPickup)){
            if(Juggernaut.healthPercentage() < 100){
            this.Despawn();
            Juggernaut.healthPickup(increase);
            }
            
        }
      }
       public void Despawn(){
        //game.getRootNode().detachChild(healthDebug);
        game.getRootNode().detachChild(healthPickup);
        bulletAppState.getPhysicsSpace().remove(HealthPickup);
        isActive = false;
    
    }
     
     void Update(float dt, Vector3f playerPos) {
         if(isActive){
        
//             healthDebug.setLocalTranslation(HealthPickup.getPhysicsLocation());

            float distFromPlayerY = playerPos.y - HealthPickup.getPhysicsLocation().y;

            float distFromPlayer = playerPos.x - HealthPickup.getPhysicsLocation().x;
            HealthPickup.setPhysicsLocation(new Vector3f(HealthPickup.getPhysicsLocation().x, HealthPickup.getPhysicsLocation().y, 0));

            float pickup = 25;

            if (distFromPlayerY > -3 && distFromPlayerY < 3) {

                if( distFromPlayer > -1 && distFromPlayer < 1) {
                    if(Juggernaut.healthPercentage() < 100){
                        this.Despawn();

                       Pickup = true;
                    } 
                } else{
                   Pickup = false;
                }

                if(Pickup){
                   game.healthPickupSound.play();
                   Juggernaut.healthPickup(pickup);
                   Pickup = false;
                   //this.HealthPickup(currentHealth);

                }
            }
         }
     }
}
