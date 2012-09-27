package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;


public class Character  implements ActionListener{
    SimpleApplication game;
    BulletAppState bulletAppState;
    // HUD elements
    private static float currentHealth = 10;
    private static float currentArmor = 10;
    private static float currentEnergy = 10;

    private static float maxHealth = 100;
    private static float maxArmor = 100;
    private static float maxEnergy = 100;

    private static float currentLevel = 1;
    private static float currentExperience = 0;
    private static float maxExperience = 10;
    private static float currentScore = 0;
    private static float abiltyPoints =0;
    private static float attributePoints =0; 

    private static float incomingDamage = 0;
    
    private static float hoverEnergyCost = 0;
    
    private static float sprintEnergyCost = 0;
    private static float sprintSpeed =0;
    
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
    private static boolean sprintActive = false;
    private static boolean shieldActive = false;
    private static boolean superJumpActive = false;
    private static boolean hoverActive = false;
    
    private Weapon currentWeapon;
    private Weapon weaponSlot1;
    private Weapon weaponSlot2;
    private Weapon weaponSlot3;
    private float damageModifier = 1.0f;
    
    private StartScreen start;
    Spatial ninja;
    private CharacterControl player;

    Spatial playerDebug;
    
    private Vector3f walkDirection = new Vector3f();
    private static boolean left = false;
    private static boolean right = false;
    
    Character(){
        
    }


    Character(SimpleApplication gameRef, BulletAppState bulletAppStateRef){
        
        this.game = gameRef;
        this.bulletAppState = bulletAppStateRef;
        //Load Ninja as filler for character model
        ninja = game.getAssetManager().loadModel("Models/Ninja/Ninja.mesh.xml");
        ninja.scale(0.02f, 0.02f, 0.02f);
//        ninja.setLocalTranslation(new Vector3f(341, 300, 0));
        game.getRootNode().attachChild(ninja);
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1f, 2f);
        player = new CharacterControl(capsuleShape, .05f);
        player.setJumpSpeed(50);
        player.setFallSpeed(50);
        player.setGravity(120);        
        player.setPhysicsLocation(new Vector3f(1f,8f,1f));
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
    }
    
    void Update(float tpf){
        
        // Movement
        walkDirection.set( 0, 0, 0);
        if(left) { 
            walkDirection.addLocal(Vector3f.UNIT_X.negate().multLocal(0.4f));
            player.setViewDirection(walkDirection.negate());
        }
        if(right) { 
            walkDirection.addLocal(Vector3f.UNIT_X.clone().multLocal(0.4f));
            player.setViewDirection(walkDirection.negate());
        }
        
        player.setWalkDirection(walkDirection);
        
        // HUD updates
        //updateHealth();

        playerDebug.setLocalTranslation(player.getPhysicsLocation());
        
    }

    private void setUpKeys() {
        game.getInputManager().addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT),new KeyTrigger(KeyInput.KEY_A));
        game.getInputManager().addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT), new KeyTrigger(KeyInput.KEY_D));
        game.getInputManager().addMapping("Jump", new KeyTrigger(KeyInput.KEY_UP), new KeyTrigger(KeyInput.KEY_W));
        game.getInputManager().addMapping("Fire", new KeyTrigger(KeyInput.KEY_SPACE));
        game.getInputManager().addListener(this, "Left");
        game.getInputManager().addListener(this, "Right");
        game.getInputManager().addListener(this, "Jump");
        game.getInputManager().addListener(this, "Fire");
    }
    
    public void onAction(String binding, boolean value, float tpf){
        if(binding.equals("Left")){
            left = value;
        } else if(binding.equals("Right")){
            right = value;
        } else if(binding.equals("Jump")){
            player.jump();
        } else if(binding.equals("Fire")){
            fireWeapon();
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
        
        currentHealth = (currentHealth / maxHealth) * 100 ;

        return currentHealth;
        

    }

    public float armorPercentage() {
        
        currentArmor = (currentArmor / maxArmor) * 100;

        return currentArmor;

    }

    public float energyPercentage() {
        
        currentEnergy = (currentEnergy / maxEnergy) * 100;

        return currentEnergy;

    }
    
    public void fireWeapon(){
        currentWeapon.Fire(damageModifier, player.getPhysicsLocation(), game, bulletAppState);
    }
    
    public void upgradeDamageModifier(){
        damageModifier += .1f;
    }
    
    public void upgradeHealth(){
        maxHealth += 2;
        currentHealth = maxHealth;
    }
    
    public void upgradeArmor(){
        maxArmor += 1;
        currentArmor = maxArmor;
    }
    
    public void checkXP(){
        if(currentExperience > maxExperience){
            LevelUp();
            currentExperience = maxExperience - currentExperience;
            maxExperience = currentLevel * 10;
        }
    }
    
    // Timers in update for armor recharge
    // Ability (dash damage, sprint energy cost, sprint speed)
    // Hover (charactercontrol 6 fallspeed)
    // Sprint (charactercontrol 

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
    
}
