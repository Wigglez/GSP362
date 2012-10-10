/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Juggernaut;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
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
    private Quaternion rotation;
    
    private float dt = 0;
    private float prevTime = 0;
    
    Spatial enemyElephant;
    private RigidBodyControl enemy;
    
    Spatial enemyDebug;
    
    private Vector3f walkDirection = new Vector3f();
    private boolean walkLeft = false;
    private boolean walkRight = false;
    
    private boolean isChasing = false;
    private boolean isMoving = false;
   
    public static final Quaternion YAW090   = new Quaternion().fromAngleAxis(FastMath.PI/2,   new Vector3f(0,1,0));
    public static final Quaternion YAW270   = new Quaternion().fromAngleAxis(FastMath.PI*3/2, new Vector3f(0,1,0));
    Enemy() {
        
    }
    
    Enemy(Main gameRef, BulletAppState bulletAppStateRef, Vector3f spawnLocation, Vector3f spawnDirection) {
         this.game = gameRef;
        this.bulletAppState = bulletAppStateRef;
        //Load Ninja as filler for character model
        enemyElephant = game.getAssetManager().loadModel("Models/Elephant/Elephant.mesh.xml");
        enemyElephant.scale(0.04f, 0.04f, 0.04f);
       enemyElephant.setLocalTranslation(spawnLocation);
       enemyElephant.setLocalRotation(YAW090);
//        ninja.setLocalTranslation(new Vector3f(341, 300, 0));
        game.getRootNode().attachChild(enemyElephant);
        CylinderCollisionShape cylinderShape = new CylinderCollisionShape(new Vector3f(2,2,2), 1);
        enemy = new RigidBodyControl(cylinderShape, .05f);
        
//        enemy.setFallSpeed(50);
//        enemy.setGravity(120);        
        enemy.setPhysicsLocation(spawnLocation);
        enemy.setCollideWithGroups(2);
        enemy.setCollisionGroup(3);
        
        enemyDebug = enemy.createDebugShape(game.getAssetManager());
        
        enemyElephant.addControl(enemy);
        game.getRootNode().attachChild(enemyDebug);

        bulletAppState.getPhysicsSpace().add(enemy);
        
        movementSpeed = 10.f;
        rotation = YAW090;
        enemy.setAngularDamping(1);
        
    }
    
    void Update(float tpf, Vector3f playerPos) {
        // Movement
        walkDirection.set( 0, 0, 0);
        if(walkLeft) { 
            enemy.setLinearVelocity(Vector3f.UNIT_X.negate().multLocal(movementSpeed));
            enemy.setPhysicsRotation(YAW090);
            rotation = YAW090;
//            walkDirection.addLocal(Vector3f.UNIT_X.negate().multLocal(movementSpeed));
//            enemy.setViewDirection(walkDirection.negate());
        } else if(walkRight) { 
            enemy.setLinearVelocity(Vector3f.UNIT_X.clone().multLocal(movementSpeed));
            enemy.setPhysicsRotation(YAW270);
            rotation = YAW270;
//            walkDirection.addLocal(Vector3f.UNIT_X.clone().multLocal(movementSpeed));
//            enemy.setViewDirection(walkDirection.negate());
        } 
        
        enemy.setPhysicsLocation(new Vector3f(enemy.getPhysicsLocation().x, enemy.getPhysicsLocation().y, 0));
//        enemy.setWalkDirection(walkDirection);

        enemyDebug.setLocalTranslation(enemy.getPhysicsLocation());
        
        
        dt = game.getTimer().getTimeInSeconds() - prevTime;
        prevTime = game.getTimer().getTimeInSeconds();
        
        
       // Aggro
		float distFromPlayerX = playerPos.x - enemy.getPhysicsLocation().x;
		float distFromPlayerY = playerPos.y - enemy.getPhysicsLocation().y;

		if (distFromPlayerY > -7 && distFromPlayerY < 7) {
			if (distFromPlayerX > -15 && distFromPlayerX < 0) {
				walkLeft = true;
			} else {
				walkLeft = false;
			}

			if (distFromPlayerX > 0 && distFromPlayerX < 15) {
				walkRight = true;
			} else {
				walkRight = false;
			}
		}
        
    }
    
    public void onAction(String binding, boolean value, float tpf){
       
        if(binding.equals("Left")){
            walkLeft = value;
        } else if(binding.equals("Right")){
            walkRight = value;
        } 
    }
    
    RigidBodyControl getControl(){
        return enemy;
    }
}
