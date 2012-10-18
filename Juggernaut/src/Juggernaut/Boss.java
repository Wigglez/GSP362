package Juggernaut;


import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.Vector;
/**
 *
 * @author Vince
 */
public class Boss implements PhysicsCollisionListener{
    private Main game;
    private BulletAppState bulletAppState;
    Character Juggernaut;
    
    private  float currentHealth = 30;
    private float maxHealth = 30;
    
    private float experienceOnDeath = 50;
    private float scoreOnDeath = 100;
    
    private float Damage = 30;
    Spatial bossAlien;
    private RigidBodyControl boss;
    
    Spatial bossDebug;
    
    private boolean damageTaken = false;
    private static float incomingDamage = 0;
    
    private boolean attackPlayer = false;
    private float attackDelay = 0f;
    private float attackSpeed = 1.0f;
    private boolean isEnraged = false;
    private float shotsFired = 0;
    
    private Material bulletMaterial;
    private Vector<Bullet> bullets= new Vector<Bullet>();

   
    public static final Quaternion YAW090   = new Quaternion().fromAngleAxis(FastMath.PI/2,   new Vector3f(0,1,0));
    public static final Quaternion YAW270   = new Quaternion().fromAngleAxis(FastMath.PI*3/2, new Vector3f(0,1,0));
    
    Boss(){

    }

    Boss(Character player, Main gameRef, BulletAppState bulletAppStateRef, Vector3f spawnLocation, Vector3f spawnDirection) {

        this.Juggernaut = player; 
        this.game = gameRef;
        this.bulletAppState = bulletAppStateRef;
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
        bossAlien = game.getAssetManager().loadModel("Models/Hydralisk.OBJ");
        bossAlien.setName("Boss");
        bossAlien.scale(1.75f, 1.75f, 1.75f);
       bossAlien.setLocalTranslation(spawnLocation);
       if(spawnDirection.x == 1.0f){
           bossAlien.setLocalRotation(YAW090);
       }else{
           bossAlien.setLocalRotation(YAW270);
       }

       game.getRootNode().attachChild(bossAlien);
        CylinderCollisionShape cylinderShape = new CylinderCollisionShape(new Vector3f(2,8,2), 1);
        boss = new RigidBodyControl(cylinderShape, .05f);


        boss.setPhysicsLocation(spawnLocation);
        boss.setCollideWithGroups(2);
        boss.setCollisionGroup(3);

        bossDebug = boss.createDebugShape(game.getAssetManager());

        bossAlien.addControl(boss);
        game.getRootNode().attachChild(bossDebug);

        bulletAppState.getPhysicsSpace().add(boss);

        boss.setAngularDamping(1);
        
        bulletMaterial = new Material(gameRef.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        bulletMaterial.setColor("Color", ColorRGBA.Orange);
        
    }

    public void Update(float dt, Vector3f playerPos){

         boss.setPhysicsLocation(new Vector3f(425, 320, 0));

        bossDebug.setLocalTranslation(boss.getPhysicsLocation());

        float distFromPlayerX = playerPos.x - boss.getPhysicsLocation().x;
        float distFromPlayerY = playerPos.y - boss.getPhysicsLocation().y;

        if (distFromPlayerY > -7 && distFromPlayerY < 15) {
            
            if (distFromPlayerX > -60 && distFromPlayerX < 0) {
                attackPlayer = true;
            } else{
                attackPlayer = false;
            }

        } else{
                currentHealth += 1 * dt;
                if(currentHealth > maxHealth){
                    currentHealth = maxHealth;
                }
        }
        
        
        
        attackDelay += dt;
        if( attackPlayer && attackDelay > attackSpeed){
            
            Vector3f toPlayer = playerPos.subtractLocal(boss.getPhysicsLocation());
            Bullet b = new Bullet(bulletMaterial, Damage, boss.getPhysicsLocation(), toPlayer.normalizeLocal(), game, bulletAppState );
            bullets.add(b);
            attackDelay = 0;
            
        }
        
        if(damageTaken){
            currentHealth -= incomingDamage;
            damageTaken = false;
        }
        
        if(currentHealth <= maxHealth/2.0f){
            attackSpeed = .5f;
            Damage = 50.0f;
        } else{
            attackSpeed = 1.0f;
            Damage = 30.0f;
        }
        
        for(int bulletItr = 0; bulletItr < bullets.size(); bulletItr++){
            Bullet testBullet = bullets.get(bulletItr);
            if(testBullet.LifeTime(dt) ){
                testBullet.delete();
                bullets.remove(bulletItr);
            }

        }
        
        if(currentHealth <= 0){
            Die();
        }

    }

    public Spatial getSpatial(){
            return bossAlien;
    }

    RigidBodyControl getControl(){
        return boss;
    }

    public float getDamage(){
        return Damage;
    }
    
    public void Die(){
        Juggernaut.addExperience(experienceOnDeath);
        Juggernaut.addScore(scoreOnDeath);
        
        game.getRootNode().detachChild(bossDebug);
        game.getRootNode().detachChild(bossAlien);
        bulletAppState.getPhysicsSpace().remove(boss);
    }
    
    public boolean isDead(){
        return (currentHealth <= 0);
    }

    public void collision(PhysicsCollisionEvent event) {
            if(event.getNodeB().getName().equals("Bullet") && event.getNodeA().equals(bossAlien)){
                damageTaken = true;
                incomingDamage = Juggernaut.DamageOutput();
            }


    }

}