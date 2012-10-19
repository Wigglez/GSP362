/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Juggernaut;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
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
public class Enemy implements PhysicsCollisionListener{
    private Main game;
    private BulletAppState bulletAppState;
    Character Juggernaut;
    
    private  float currentHealth = 6;
    private float maxHealth = 3;
    
    private float experienceOnDeath = 1;
    private float scoreOnDeath = 10;
    
    private float Damage = 3;
    
    private float movementSpeed = 0;
    
    Spatial enemyElephant;
    private RigidBodyControl enemy;
    
    Spatial enemyDebug;
    
    private boolean damageTaken = false;
    private static float incomingDamage = 0;
    
    private Vector3f walkDirection = new Vector3f();
    private boolean walkLeft = false;
    private boolean walkRight = false;
    
    private boolean isChasing = false;
    private boolean isMoving = false;
   
    public static final Quaternion YAW090   = new Quaternion().fromAngleAxis(FastMath.PI/2,   new Vector3f(0,1,0));
    public static final Quaternion YAW270   = new Quaternion().fromAngleAxis(FastMath.PI*3/2, new Vector3f(0,1,0));
    Enemy() {
        
    }
    
    Enemy(Character player, Main gameRef, BulletAppState bulletAppStateRef, Vector3f spawnLocation, Vector3f spawnDirection) {
        this.Juggernaut = player; 
        this.game = gameRef;
        this.bulletAppState = bulletAppStateRef;
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
        //Load Ninja as filler for character model
        enemyElephant = game.getAssetManager().loadModel("Models/enemy.obj");
        enemyElephant.setName("Enemy");
        enemyElephant.scale(1.25f, 1.25f, 1.25f);
       enemyElephant.setLocalTranslation(spawnLocation);
       if(spawnDirection.x == 1.0f){
           enemyElephant.setLocalRotation(YAW090);
       }else{
           enemyElephant.setLocalRotation(YAW270);
       }
       
//        ninja.setLocalTranslation(new Vector3f(341, 300, 0));
        game.getRootNode().attachChild(enemyElephant);
        CylinderCollisionShape cylinderShape = new CylinderCollisionShape(new Vector3f(2,3.5f,2), 1);
        enemy = new RigidBodyControl(cylinderShape, .05f);
        
//        enemy.setFallSpeed(50);
//        enemy.setGravity(120);        
        enemy.setPhysicsLocation(spawnLocation);
        enemy.setCollideWithGroups(2);
        enemy.setCollisionGroup(3);
        
        //enemyDebug = enemy.createDebugShape(game.getAssetManager());
        
        enemyElephant.addControl(enemy);
        //game.getRootNode().attachChild(enemyDebug);

        bulletAppState.getPhysicsSpace().add(enemy);
        
        movementSpeed = 10.f;
        enemy.setAngularDamping(1);
        
    }
    
    void Update(float dt, Vector3f playerPos) {
        // Movement
        walkDirection.set( 0, 0, 0);
        if(walkLeft) { 
//            enemy.setLinearVelocity(new Vector3f(-1, -9.8f,0).mult(movementSpeed));
            enemy.setLinearVelocity(Vector3f.UNIT_X.negate().multLocal(movementSpeed).add(new Vector3f(0, -15f, 0)));
            enemy.setPhysicsRotation(YAW090);
//            walkDirection.addLocal(Vector3f.UNIT_X.negate().multLocal(movementSpeed));
//            enemy.setViewDirection(walkDirection.negate());
        } else if(walkRight) { 
            enemy.setLinearVelocity(Vector3f.UNIT_X.clone().multLocal(movementSpeed).add(new Vector3f(0, -15f, 0)));
            enemy.setPhysicsRotation(YAW270);
//            walkDirection.addLocal(Vector3f.UNIT_X.clone().multLocal(movementSpeed));
//            enemy.setViewDirection(walkDirection.negate());
        } 
        
        enemy.setPhysicsLocation(new Vector3f(enemy.getPhysicsLocation().x, enemy.getPhysicsLocation().y, 0));
//        enemy.setWalkDirection(walkDirection);

        //enemyDebug.setLocalTranslation(enemy.getPhysicsLocation());
     
        
        
       // Aggro
        float distFromPlayerX = playerPos.x - enemy.getPhysicsLocation().x;
        float distFromPlayerY = playerPos.y - enemy.getPhysicsLocation().y;

        if (distFromPlayerY > -7 && distFromPlayerY < 7) {
            if (distFromPlayerX > -50 && distFromPlayerX < 0) {
                    walkLeft = true;
                    walkRight = false;
            } 

            if (distFromPlayerX > 0 && distFromPlayerX < 50) {
                    walkRight = true;
                    walkLeft = false;
            } 
        }else {
            walkLeft = false;
            walkRight = false;
        }
        
        if(damageTaken){
            currentHealth -= incomingDamage;
            damageTaken = false;
        }
        
        if(currentHealth <= 0){
            Die();
        }
        
    }
    
    public Spatial getSpatial(){
        return enemyElephant;
    }
    
    RigidBodyControl getControl(){
        return enemy;
    }
    
    public float getDamage(){
        return Damage;
    }
    
    public void Die(){
        Juggernaut.enemiesDead += 1;
        Juggernaut.addExperience(experienceOnDeath);
        Juggernaut.addScore(scoreOnDeath);
        
        //game.getRootNode().detachChild(enemyDebug);
        game.getRootNode().detachChild(enemyElephant);
        bulletAppState.getPhysicsSpace().remove(enemy);
        System.out.print(Juggernaut.enemiesDead + "\n");
    }
    
    public boolean isDead(){
        return (currentHealth <= 0);
    }
    
    public void collision(PhysicsCollisionEvent event) {
        if(event.getNodeA().getName().equals("Player") && event.getNodeB().equals(this.enemyElephant)){
            experienceOnDeath = 0;
            scoreOnDeath = 0;
            
            incomingDamage = currentHealth;
            damageTaken = true;
        } else if(event.getNodeB().getName().equals("Bullet") && event.getNodeA().equals(this.enemyElephant)){
            experienceOnDeath = 1;
            scoreOnDeath = 10;
            
            damageTaken = true;
            incomingDamage = Juggernaut.DamageOutput();
        }
        
        
    }
}
