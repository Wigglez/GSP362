/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Juggernaut;

/**
 *
 * @author Jordon
 */

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;


public class StartScreen extends AbstractAppState implements ScreenController {
  
  
  public Nifty nifty;
//  private Main main;
  
  private Character Juggernaut;
 
  private miniGun minigun;
  private Pistol pistol;
  private laserRifle laserRifle;
  
  
  public Screen screen;
  private Application app;
  public float healthPercentage;
  public float armorPercentage;
  public float energyPercentage;

  
  String currentScreen;
  
  public Element healthBar;
  /** custom methods */ 
 
  public StartScreen() 
  { 
//      main = new Main();
      Juggernaut = new Character();
      
      minigun = new miniGun();
      pistol = new Pistol();
      laserRifle = new laserRifle();
      
      healthPercentage = Juggernaut.healthPercentage();
      armorPercentage = Juggernaut.armorPercentage();
      energyPercentage = Juggernaut.energyPercentage();
    /** Your custom constructor, can accept arguments */ 
  
  } 
 
  /** Nifty GUI ScreenControl methods */ 

  
 
  public void onStartScreen() { }
 
  public void onEndScreen() { }
 
  /** jME3 AppState methods */ 
  public void goToScreen(String nextScreen) 
  {
    nifty.gotoScreen(nextScreen);  // switch to another screen
    // start the game and do some more stuff...
   
  }
  public void TestHealth()
  {
      
    //Juggernaut.updateHealth();
//    healthPercentage = Juggernaut.healthPercentage();
    //healthPercentage = this.getHealthPercentage();
    this.UpdateHealth();
    Element niftyElement = nifty.getCurrentScreen().findElementByName("PlayerHealth");
    // swap old with new image
    niftyElement.setWidth((int)healthPercentage*2);
    
    //System.out.print(Juggernaut.healthPercentage() + "\n");
    System.out.print(this.healthPercentage + "\n");
      
  }
  public void TestArmor()
  {
      
    //Juggernaut.updateHealth();
//    healthPercentage = Juggernaut.healthPercentage();
    //healthPercentage = this.getHealthPercentage();
    this.UpdateArmor();
    Element niftyElement = nifty.getCurrentScreen().findElementByName("PlayerArmor");
    // swap old with new image
    niftyElement.setWidth((int)armorPercentage*2);
    
    //System.out.print(Juggernaut.healthPercentage() + "\n");
    System.out.print(this.armorPercentage + "\n");
      
  }
  public void TestEnergy()
  {
      
    //Juggernaut.updateHealth();
//    healthPercentage = Juggernaut.healthPercentage();
    //healthPercentage = this.getHealthPercentage();
    this.UpdateEnergy();
    Element niftyElement = nifty.getCurrentScreen().findElementByName("PlayerEnergy");
    // swap old with new image
    niftyElement.setWidth((int)energyPercentage*2);
    
    //System.out.print(Juggernaut.healthPercentage() + "\n");
    System.out.print(this.energyPercentage + "\n");
      
  }
  @Override
  public void bind(Nifty nifty, Screen screen) {
    this.nifty = nifty;
    this.screen = screen;
    
    
    //healthBar = nifty.getScreen("hud").findElementByName("playerHealth");
  }
  public float getHealthPercentage()
  {
      //healthPercentage = Juggernaut.healthPercentage();
      
      
      //System.out.print(healthPercentage + "\n\n");
      //main.HealthPercentage = healthPercentage;
      System.out.print(this.healthPercentage + "\n");
      //main.update();
      return healthPercentage;
      
  }
  public float UpdateHealth()
  {
      float increase = 10;
      if( healthPercentage < 100)
      {
      healthPercentage = healthPercentage + increase;
      }
      
      return healthPercentage;
  }
  public float UpdateArmor()
  {
      float increase = 10;
      if( armorPercentage < 100)
      {
      armorPercentage = armorPercentage + increase;
      }
      
      return armorPercentage;
  }
  public float UpdateEnergy()
  {
      float increase = 10;
      if( energyPercentage < 100)
      {
      energyPercentage = energyPercentage + increase;
      }
      
      return energyPercentage;
  }
  public float getArmorPercentage()
  {
      
      //main.HealthPercentage = healthPercentage;
      
      //main.update();
      return armorPercentage;
      
  }
  public float getEnergyPercentage()
  {
      
      //main.HealthPercentage = healthPercentage;
      
      //main.update();
      return energyPercentage;
      
  }
  public void weapon1Clicked()
  {
      
      NiftyImage img = nifty.getRenderEngine().createImage("Interface/Pistol.png", false);
      
      // find old image
      Element niftyImageElement = nifty.getCurrentScreen().findElementByName("CurrentWeapon");
      // swap old with new image
      niftyImageElement.getRenderer(ImageRenderer.class).setImage(img);
      
      Element niftyDamageElement = nifty.getCurrentScreen().findElementByName("WeaponDamageText");
      
      niftyDamageElement.getRenderer(TextRenderer.class).setText("Damage: " + pistol.dammage);
      
      Element niftyFireRateElement = nifty.getCurrentScreen().findElementByName("WeaponFireRateText");
      
      niftyFireRateElement.getRenderer(TextRenderer.class).setText("Fire Rate: " + pistol.fireRate);
      
      Element niftyAmmoElement = nifty.getCurrentScreen().findElementByName("Ammo");
      
      niftyAmmoElement.getRenderer(TextRenderer.class).setText("Ammo: " + pistol.currentAmmo);
      
      //System.out.print(screen.getScreenId().toString() + " Scren id \n");
  }
  public void weapon2Clicked()
  {
      NiftyImage img = nifty.getRenderEngine().createImage("Interface/miniGun.png", false);
      
      // find old image
      Element niftyElement = nifty.getCurrentScreen().findElementByName("CurrentWeapon");
      // swap old with new image
      niftyElement.getRenderer(ImageRenderer.class).setImage(img);
      
      Element niftyDamageElement = nifty.getCurrentScreen().findElementByName("WeaponDamageText");
      
      niftyDamageElement.getRenderer(TextRenderer.class).setText("Damage: " + minigun.dammage);
      
      Element niftyFireRateElement = nifty.getCurrentScreen().findElementByName("WeaponFireRateText");
      
      niftyFireRateElement.getRenderer(TextRenderer.class).setText("Fire Rate: " + minigun.fireRate);
      
      Element niftyAmmoElement = nifty.getCurrentScreen().findElementByName("Ammo");
      
      niftyAmmoElement.getRenderer(TextRenderer.class).setText("Ammo: " + minigun.currentAmmo);
      //System.out.print(screen.getScreenId().toString() + " Scren id \n");
      
  }
  public void weapon3Clicked()
  {
      NiftyImage img = nifty.getRenderEngine().createImage("Interface/Cannon.png", false);
      
      // find old image
      Element niftyElement = nifty.getCurrentScreen().findElementByName("CurrentWeapon");
      // swap old with new image
      niftyElement.getRenderer(ImageRenderer.class).setImage(img);
      
      Element niftyDamageElement = nifty.getCurrentScreen().findElementByName("WeaponDamageText");
      
      niftyDamageElement.getRenderer(TextRenderer.class).setText("Damage: " + laserRifle.dammage);
      
      Element niftyFireRateElement = nifty.getCurrentScreen().findElementByName("WeaponFireRateText");
      
      niftyFireRateElement.getRenderer(TextRenderer.class).setText("Fire Rate: " + laserRifle.fireRate);
      
      Element niftyAmmoElement = nifty.getCurrentScreen().findElementByName("Ammo");
      
      niftyAmmoElement.getRenderer(TextRenderer.class).setText("Ammo: " + laserRifle.currentAmmo);
      
      //System.out.print(screen.getScreenId().toString() + " Scren id \n");
  }
  public void DoNothing()
  {
      
  }
 
  public void startGame()
  {

  }
  public void quitGame() 
  {
      System.exit(0);
      nifty.exit();
    
  }
  
 
  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
    this.app=app;
    

  }

  @Override
  public void update(float tpf) { 
    /** jME update loop! */ 

    if (screen != null) 
    {
        
       
//      
    }
    
    
  } 

      
      
     
      

  public void updateHUD(float health,float armor,float energy, float Ammo){
         
        Element niftyElement = nifty.getCurrentScreen().findElementByName("PlayerHealth");
        if(niftyElement != null){
            // swap old with new image
            niftyElement.setWidth((int)health * 2);

             niftyElement = nifty.getCurrentScreen().findElementByName("PlayerArmor");
            // swap old with new image
            niftyElement.setWidth((int)armor * 2);

            niftyElement = nifty.getCurrentScreen().findElementByName("PlayerEnergy");
            // swap old with new image
            niftyElement.setWidth((int)energy * 2);
            
            Element niftyAmmoElement = nifty.getCurrentScreen().findElementByName("Ammo");
      
      niftyAmmoElement.getRenderer(TextRenderer.class).setText("Ammo: " + Ammo);
        }
    }
 
}
