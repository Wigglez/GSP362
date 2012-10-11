package Juggernaut;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;


public class Character  implements ActionListener, PhysicsCollisionListener{
    Main game;
    BulletAppState bulletAppState;
    // HUD elements
    private static float currentHealth = 100;
    private static float currentArmor = 25;
    private static float currentEnergy = 100;

    private static float maxHealth = 100;
    private static float maxArmor = 25;
    private static float maxEnergy = 100;
    private static float Pickup = 20;
    private static float currentLevel = 1;
    private static float currentExperience = 0;
    private static float maxExperience = 10;
    private static float currentScore = 0;
    private static float abiltyPoints =0;
    private static float attributePoints =0; 

    private boolean damageTaken = false;
    private static float incomingDamage = 0;
    
    private static float hoverEnergyCost = 60f;
    
    private boolean sprintActive;
    private static float sprintEnergyCost = 35f;
    private static float sprintSpeed =0;
    
    private boolean dashActive;
    private static float dashEnergyCost;
    private static float dashDistance;
    private static float dashDamage;

    // Movement
    private static float movementSpeed = 0;
    private static float jumpHeight = 50;
    private static float superJumpHeight = 100;

    // Booleans
    private static boolean pickUpAdded = false;
    private static boolean isShooting = false;
    private static boolean shieldActive = false;
    private static boolean superJumpActive = false;
    private static boolean hoverActive = false;
    
    private Weapon currentWeapon;
    private Weapon weaponSlot1;
    private Weapon weaponSlot2;
    private Weapon weaponSlot3;
    private float damageModifier = 1.0f;
    private float fireDelay =0;
    private Vector<Bullet> bullets= new Vector<Bullet>();
    
    private float dt, prevTime =0;
    
    Spatial ninja;
    private CharacterControl player;

    Spatial playerDebug;
    
    private Vector3f walkDirection = new Vector3f();
    private static boolean left = false;
    private static boolean right = false;
    
    private static boolean isFiring = false;
    
    Character(){
        
    }


    Character(Main gameRef, BulletAppState bulletAppStateRef){
        
        this.game = gameRef;
        this.bulletAppState = bulletAppStateRef;
        bulletAppState.getPhysicsSpace().addCollisionListener(this);
        //Load Ninja as filler for character model
        ninja = game.getAssetManager().loadModel("Models/Ninja/Ninja.mesh.xml");
        ninja.setName("Player");
        ninja.scale(0.02f, 0.02f, 0.02f);
//        ninja.setLocalTranslation(new Vector3f(341, 300, 0));
        game.getRootNode().attachChild(ninja);
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1f, 2f);
        player = new CharacterControl(capsuleShape, .05f);
        player.setJumpSpeed(50);
        player.setFallSpeed(50);
        player.setGravity(120);        
        player.setViewDirection(new Vector3f(-1.0f, 0, 0));
        player.setCollideWithGroups(2);
        
        playerDebug = player.createDebugShape(game.getAssetManager());
        ninja.addControl(player);
        game.getRootNode().attachChild(playerDebug);

        bulletAppState.getPhysicsSpace().add(player);
      
        setUpKeys();
        
        weaponSlot1 = new Pistol(game);
        weaponSlot2 = new miniGun(game);
        weaponSlot3 = new laserRifle(game);
        currentWeapon = weaponSlot1;
        
        movementSpeed = 0.4f;
        
    }
    
    void Update(float tpf){
        // Movement
        walkDirection.set( 0, 0, 0);
        if(left) { 
            walkDirection.addLocal(Vector3f.UNIT_X.negate().multLocal(movementSpeed));
            player.setViewDirection(walkDirection.negate());
        }
        if(right) { 
            walkDirection.addLocal(Vector3f.UNIT_X.clone().multLocal(movementSpeed));
            player.setViewDirection(walkDirection.negate());
        }
        
        player.setWalkDirection(walkDirection);
        player.setPhysicsLocation(new Vector3f(player.getPhysicsLocation().x, player.getPhysicsLocation().y, 0));


        playerDebug.setLocalTranslation(player.getPhysicsLocation());
        
        
        dt = game.getTimer().getTimeInSeconds() - prevTime;
        prevTime = game.getTimer().getTimeInSeconds();
        
        fireDelay += dt;
        
        if(isFiring){
            if(fireDelay > currentWeapon.getFireRate()){
                fireWeapon();
                fireDelay = 0;
            }            
        }
        
        
        for(int bulletItr = 0; bulletItr < bullets.size(); bulletItr++){
            Bullet testBullet = bullets.get(bulletItr);
            if(testBullet.LifeTime(dt) ){
                testBullet.delete();
                bullets.remove(bulletItr);
            }

        }
         
        
        if(hoverActive && currentEnergy > 0){
            sprintActive = false;
            hover(dt);
        } else {
            player.setFallSpeed(50);
        }
        
        if(sprintActive && currentEnergy > 0){
            sprint(dt);
        } else {
            movementSpeed = 0.4f;
        }
        
        if(currentEnergy < maxEnergy){
            if(!sprintActive && !hoverActive)
                currentEnergy += 10f * dt;
        }
        
        if(damageTaken){
            if(currentArmor > 0){
                currentArmor -= incomingDamage;
            }else{
                currentHealth -= incomingDamage;
            }
            
            damageTaken = false;
        }
        
//        System.out.print(currentHealth +"\n");
        game.getHud().bind(game.getNifty(), game.getHud().screen);
        game.getHud().updateHUD(healthPercentage(), armorPercentage(), energyPercentage());
    }

    private void setUpKeys() {
        game.getInputManager().addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT),new KeyTrigger(KeyInput.KEY_A));
        game.getInputManager().addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT), new KeyTrigger(KeyInput.KEY_D));
        game.getInputManager().addMapping("Jump", new KeyTrigger(KeyInput.KEY_UP), new KeyTrigger(KeyInput.KEY_W));
        game.getInputManager().addMapping("Fire", new KeyTrigger(KeyInput.KEY_SPACE));
        game.getInputManager().addMapping("Weapon1", new KeyTrigger(KeyInput.KEY_1), new KeyTrigger(KeyInput.KEY_NUMPAD1));
        game.getInputManager().addMapping("Weapon2", new KeyTrigger(KeyInput.KEY_2), new KeyTrigger(KeyInput.KEY_NUMPAD2));
        game.getInputManager().addMapping("Weapon3", new KeyTrigger(KeyInput.KEY_3), new KeyTrigger(KeyInput.KEY_NUMPAD3));
        game.getInputManager().addMapping("Sprint", new KeyTrigger(KeyInput.KEY_RCONTROL));
        game.getInputManager().addMapping("Hover", new KeyTrigger(KeyInput.KEY_RSHIFT));
        game.getInputManager().addListener(this, "Left");
        game.getInputManager().addListener(this, "Right");
        game.getInputManager().addListener(this, "Jump");
        game.getInputManager().addListener(this, "Fire");
        game.getInputManager().addListener(this, "Weapon1");
        game.getInputManager().addListener(this, "Weapon2");
        game.getInputManager().addListener(this, "Weapon3");
        game.getInputManager().addListener(this, "Sprint");
        game.getInputManager().addListener(this, "Hover");
    }
    
    public void onAction(String binding, boolean value, float tpf){
        game.getHud().bind(game.getNifty(), game.getHud().screen);
        if(binding.equals("Left")){
            left = value;
        } else if(binding.equals("Right")){
            right = value;
        } else if(binding.equals("Jump")){
            player.jump();
        } else if(binding.equals("Fire")){
            isFiring = value; 
        } else if(binding.equals("Weapon1")){
            switchWeapon(1);
            game.getHud().weapon1Clicked();
        }else if(binding.equals("Weapon2")){
            switchWeapon(2);
            game.getHud().weapon2Clicked();
        }else if(binding.equals("Weapon3")){
            switchWeapon(3);
            game.getHud().weapon3Clicked();
        } else if(binding.equals("Sprint")){
            sprintActive = value;
        } else if(binding.equals("Hover")){
            hoverActive = value;
        }
        
         
    }
    
    CharacterControl getControl(){
        return player;
    }
    
    public float updateHealth() {

        if (incomingDamage > 0 && currentArmor > 0) {
            currentHealth = updateHealth();
        } else if  (incomingDamage > 0 && currentArmor == 0) {
            currentHealth = currentHealth - incomingDamage;
        }
       

        return currentHealth;

    }
    
    public float updateArmor() {
        
        if(incomingDamage > 0 && currentArmor > 0) {
            currentArmor = currentArmor - incomingDamage;
        } 
        
        return currentArmor;
    }

    public float healthPercentage() {
        
        return (currentHealth / maxHealth) * 100 ;
        

    }

    public float armorPercentage() {
        
        return (currentArmor / maxArmor) * 100;

    }

    public float energyPercentage() {
  
        return (currentEnergy / maxEnergy) * 100;

    }
    
    public void fireWeapon(){
        bullets.add(currentWeapon.Fire(damageModifier, player.getPhysicsLocation(), player.getViewDirection().normalize().negate(), game, bulletAppState));
    }
    
    public void upgradeDamageModifier(){
        damageModifier += .1f;
    }
    
    public void upgradeHealth(){
        maxHealth += 25;
        currentHealth = maxHealth;
    }
    
    public void upgradeArmor(){
        maxArmor += 25;
        currentArmor = maxArmor;
    }
    
    public void checkXP(){
        if(currentExperience > maxExperience){
            LevelUp();
            currentExperience = maxExperience - currentExperience;
            maxExperience = currentLevel * 10;
        }
    }

    private void LevelUp() {
        currentLevel += 1;
        attributePoints += 2;
        if(currentLevel % 2 != 0){
            abiltyPoints += 1;
        }
        currentHealth = maxHealth;
        currentArmor = maxArmor;
        currentEnergy = maxEnergy;
        
    }
    
    private void switchWeapon(int weaponSlot){
        if(weaponSlot == 1){
            currentWeapon = weaponSlot1;
        } else if(weaponSlot == 2){
            currentWeapon = weaponSlot2;
        } else if(weaponSlot == 3){
            currentWeapon = weaponSlot3;
        }
    }
    
    private void sprint(float dt){
        movementSpeed = 0.7f;
        currentEnergy -= sprintEnergyCost * dt;
    }
    
    private void hover(float dt){
        player.setFallSpeed(6);
        currentEnergy -= hoverEnergyCost * dt;
    }
    
    public Vector3f getPosition(){
        return player.getPhysicsLocation();
    }
    
    public Weapon EquippedWeapon(){
        return currentWeapon;
    }
    
    public float DamageOutput(){
        return currentWeapon.getDammage() * damageModifier;
    }
    public void healthPickup(float pickup){
        currentHealth += pickup;
    }
    
    public float getPickup(){
        return Pickup;
    }
    public void collision(PhysicsCollisionEvent event) {
        if(event.getNodeA().getName().equals("Player") && event.getNodeB().getName().equals("Enemy")){
            incomingDamage = 25;
            damageTaken = true;
            
        }
    }
}
