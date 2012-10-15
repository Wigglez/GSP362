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
 * @author Vince
 */
public class Boss implements PhysicsCollisionListener{
    private Main game;
    private BulletAppState bulletAppState;
    Character Juggernaut;
    
    private  float currentHealth = 6;
    private float maxHealth = 3;
    
    private float experienceOnDeath = 1;
    private float scoreOnDeath = 10;
    
    private float Damage = 3;
    
    Spatial bossAlien;
    private RigidBodyControl boss;
    
    Spatial bossDebug;
    
    private boolean damageTaken = false;
    private static float incomingDamage = 0;
    
    private boolean attackPlayer = false;
    private float attackDelay = 1.0f;
    private boolean isEnraged = false;
    private float shotsFired = 0;
    
    

   
    public static final Quaternion YAW090   = new Quaternion().fromAngleAxis(FastMath.PI/2,   new Vector3f(0,1,0));
    public static final Quaternion YAW270   = new Quaternion().fromAngleAxis(FastMath.PI*3/2, new Vector3f(0,1,0));
    
    Boss(){

    }

    Boss(Character player, Main gameRef, BulletAppState bulletAppStateRef, Vector3f spawnLocation, Vector3f spawnDirection) {

        this.Juggernaut = player; 
        this.game = gameRef;
        this.bulletAppState = bulletAppStateRef;
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
        bossAlien = game.getAssetManager().loadModel("Models/Elephant/Elephant.mesh.xml");
        bossAlien.setName("Boss");
        bossAlien.scale(0.04f, 0.04f, 0.04f);
       bossAlien.setLocalTranslation(spawnLocation);
       if(spawnDirection.x == 1.0f){
           bossAlien.setLocalRotation(YAW090);
       }else{
           bossAlien.setLocalRotation(YAW270);
       }

       game.getRootNode().attachChild(bossAlien);
        CylinderCollisionShape cylinderShape = new CylinderCollisionShape(new Vector3f(2,2,2), 1);
        boss = new RigidBodyControl(cylinderShape, .05f);


        boss.setPhysicsLocation(spawnLocation);
        boss.setCollideWithGroups(2);
        boss.setCollisionGroup(3);

        bossDebug = boss.createDebugShape(game.getAssetManager());

        bossAlien.addControl(boss);
        game.getRootNode().attachChild(bossDebug);

        bulletAppState.getPhysicsSpace().add(boss);

        boss.setAngularDamping(1);
        
    }

    public void Update(float dt, Vector3f playerPos){

        boss.setPhysicsLocation(new Vector3f(boss.getPhysicsLocation().x, boss.getPhysicsLocation().y, 0));

        bossDebug.setLocalTranslation(boss.getPhysicsLocation());

        float distFromPlayerX = playerPos.x - boss.getPhysicsLocation().x;
        float distFromPlayerY = playerPos.y - boss.getPhysicsLocation().y;

        if (distFromPlayerY > -7 && distFromPlayerY < 15) {
            if (distFromPlayerX > -60 && distFromPlayerX < 0) {
                attackPlayer = true;
            } 

        } else{
                currentHealth += 1 * dt;
                if(currentHealth > maxHealth){
                    currentHealth = maxHealth;
                }
        }
        
        if(damageTaken){
            currentHealth -= incomingDamage;
            damageTaken = false;
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

    public void collision(PhysicsCollisionEvent event) {
    //        if(event.getNodeA().getName().equals("Player") && event.getNodeB().equals(this.enemyElephant)){
    //            experienceOnDeath = 0;
    //            scoreOnDeath = 0;
    //            
    //            incomingDamage = currentHealth;
    //            damageTaken = true;
    //        } else if(event.getNodeB().getName().equals("Bullet") && event.getNodeA().equals(this.enemyElephant)){
    //            experienceOnDeath = 1;
    //            scoreOnDeath = 10;
    //            
    //            damageTaken = true;
    //            incomingDamage = Juggernaut.DamageOutput();
    //        }


    }

}