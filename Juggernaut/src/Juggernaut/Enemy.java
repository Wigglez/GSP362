/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Juggernaut;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author Wigglez
 */
public class Enemy {
    Main game;
    BulletAppState bulletAppState;
    Character Juggernaut;
    
    private  float currentHealth = 1;
    private float maxHealth = 1;
    
    private float experienceOnDeath = 0;
    private float scoreOnDeath = 0;
    
    private float incomingDamage = 0;
    
    private float movementSpeed = 0;
    
    private float dt = 0;
    private float prevTime = 0;
    
    Spatial enemyNinja;
    private CharacterControl enemy;
    
    Vector3f target;
    
    Spatial enemyDebug;
    
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false;
    private boolean right = false;
    
    private boolean isChasing = false;
    private boolean isMoving = false;
    
    Enemy() {
        
    }
    
    Enemy(Main gameRef, BulletAppState bulletAppStateRef, Vector3f spawnLocation, Vector3f spawnDirection) {
         this.game = gameRef;
        this.bulletAppState = bulletAppStateRef;
        //Load Ninja as filler for character model
        enemyNinja = game.getAssetManager().loadModel("Models/Elephant/Elephant.mesh.xml");
        enemyNinja.scale(0.04f, 0.04f, 0.04f);
       enemyNinja.setLocalTranslation(spawnLocation);

//        ninja.setLocalTranslation(new Vector3f(341, 300, 0));
        game.getRootNode().attachChild(enemyNinja);
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1f, 2f);
        enemy = new CharacterControl(capsuleShape, .05f);
        enemy.setFallSpeed(50);
        enemy.setGravity(120);        
        enemy.setPhysicsLocation(spawnLocation);
        enemy.setViewDirection(spawnDirection);
        enemy.setCollideWithGroups(2);
        enemy.setCollisionGroup(3);
        
        enemyDebug = enemy.createDebugShape(game.getAssetManager());
        
        enemyNinja.addControl(enemy);
        game.getRootNode().attachChild(enemyDebug);

        bulletAppState.getPhysicsSpace().add(enemy);
        
        movementSpeed = 0.1f;
        
        
    }
    
    void Update(float tpf, Vector3f playerPos) {
        // Movement
        walkDirection.set( 0, 0, 0);
        if(left) { 
            walkDirection.addLocal(Vector3f.UNIT_X.negate().multLocal(movementSpeed));
            enemy.setViewDirection(walkDirection.negate());
        }
        if(right) { 
            walkDirection.addLocal(Vector3f.UNIT_X.clone().multLocal(movementSpeed));
            enemy.setViewDirection(walkDirection.negate());
        }
        
        enemy.setWalkDirection(walkDirection);

        enemyDebug.setLocalTranslation(enemy.getPhysicsLocation());
        
        
        dt = game.getTimer().getTimeInSeconds() - prevTime;
        prevTime = game.getTimer().getTimeInSeconds();
        
         float distFromPlayer = playerPos.x - enemy.getPhysicsLocation().x;
        if( distFromPlayer > -15 && distFromPlayer < 0) {
           left = true;
        } else{
            left = false;
        }
        
        if(distFromPlayer > 0 && distFromPlayer < 15) {
           right = true;
        } else{
            right = false;
        }
        
        if(isChasing) {
            chasePlayer();
        }
    }
    
     public void moveTo(Vector3f target, float movementSpeed) {
        this.movementSpeed = movementSpeed;
        isMoving = true;
        lookAt(target);
    }
     
     public void lookAt(Vector3f target) {
        this.target = target;
    }
    
    public void chasePlayer() {
//       enemyNinja.moveTo(Juggernaut.ninja.getLocalTranslation(), movementSpeed);
       
    }
    
    public void onAction(String binding, boolean value, float tpf){
       
        if(binding.equals("Left")){
            left = value;
        } else if(binding.equals("Right")){
            right = value;
        } 
    }
    
    CharacterControl getControl(){
        return enemy;
    }
}
