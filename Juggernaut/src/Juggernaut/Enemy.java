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
    
    private static float currentHealth = 1;
    private static float maxHealth = 100;
    
    private static float experienceOnDeath = 0;
    private static float scoreOnDeath = 0;
    
    private static float incomingDamage = 0;
    
    private static float movementSpeed = 0;
    
    private float dt = 0;
    private float prevTime = 0;
    
    Spatial enemyNinja;
    private CharacterControl enemy;
    
    Spatial enemyDebug;
    
    private Vector3f walkDirection = new Vector3f();
    private static boolean left = false;
    private static boolean right = false;
    
    Enemy() {
        
    }
    
    Enemy(Main gameRef, BulletAppState bulletAppStateRef, Vector3f spawnLocation, Vector3f spawnDirection) {
         this.game = gameRef;
        this.bulletAppState = bulletAppStateRef;
        //Load Ninja as filler for character model
        enemyNinja = game.getAssetManager().loadModel("Models/Ninja/Ninja.mesh.xml");
        enemyNinja.scale(0.02f, 0.02f, 0.02f);
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
}
