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
  private Main main;
  
  private Character Juggernaut;
 
  private miniGun minigun;
  private Pistol pistol;
  private laserRifle laserRifle;
  
  
  public Screen screen;
  private Application app;
  public float healthPercentage;
  public float armorPercentage;
  public float energyPercentage;
  public float experiencePercentage;
  public float score;
  public int miniGunAmmo;
  
  String currentScreen;
  
  public Element healthBar;
  private Element Storepopup;
  private Element StoreTabpopup;
  private Element Skillspopup;
  private Element Attributespopup;
  private Element Abilitiespopup;
  /** custom methods */ 
 
  public StartScreen() 
  { 
      main = new Main();
      Juggernaut = new Character();
      
      minigun = new miniGun();
      pistol = new Pistol();
      laserRifle = new laserRifle();
      
      healthPercentage = Juggernaut.healthPercentage();
      armorPercentage = Juggernaut.armorPercentage();
      energyPercentage = Juggernaut.energyPercentage();
      experiencePercentage = Juggernaut.expPercentage();
      score = Juggernaut.getScore();
      
      miniGunAmmo = minigun.currentAmmo;
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
  public float getExperiencePercentage()
  {
      System.out.print(experiencePercentage + "\n");
      
      return experiencePercentage;
  }
  public float getScore()
  {

      return score;
  }
  public int getMiniGunAmmo()
  {

      return miniGunAmmo;
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
  ///////////////////////////////////////////////////////////////////////////
  //Create/Show/Close main Store Pop up
  ///////////////////////////////////////////////////////////////////////////
  public void CreateStorePopup()
  {
    main.registerStorePopup(nifty);
    Storepopup = nifty.createPopup("storePopup");
    //System.out.print(" Create Store Called \n");
  }
  public void ShowStorePopup()
  {
    //isRunning = false;
    this.CreateStorePopup();
    nifty.showPopup(nifty.getCurrentScreen(), Storepopup.getId(), null);
    //System.out.print(" Show Store Called \n");
    
     
  }
  public void CloseStorePopup()
  {
    //isRunning = true;
    nifty.closePopup(Storepopup.getId());
    
  }
  ////////////////////////////////////////////////////////////////////////////
  //Create/Show/Close Store Tab popup
  ////////////////////////////////////////////////////////////////////////////
  public void CreateStoreTabPopup()
  {
    main.registerStoreTab(nifty);
    StoreTabpopup = nifty.createPopup("storeTabPopup");
    //System.out.print(" Create Store Tab Called \n");
  }
   public void ShowStoreTabPopup()
  {
    this.CloseStorePopup();
    //System.out.print(" Close Store Called in Show Store Tab \n");
    this.CreateStoreTabPopup();
    nifty.showPopup(nifty.getCurrentScreen(), StoreTabpopup.getId(), null);
    //System.out.print(" Show Store Tab Called \n");
  }
   
  public void CloseStoreTabPopup()
  {
    nifty.closePopup(StoreTabpopup.getId());
  }
  
  /////////////////////////////////////////////////////////////////////////////
  //Create/Show/Close Skills popup
  /////////////////////////////////////////////////////////////////////////////
  public void CreateSkillsPopup()
  {
    main.registerSkillsTab(nifty);
    Skillspopup = nifty.createPopup("SkillsPopup");
    //System.out.print(" Create Skills popup Called \n");
  }
   public void ShowSkillsPopup()
  {
    //isRunning = false;
    this.CreateSkillsPopup();
    nifty.showPopup(nifty.getCurrentScreen(), Skillspopup.getId(), null);
    //System.out.print(" Show Skills popup Called \n");
    //System.out.print(this.isRunning + "\n");
  }
   
  public void CloseSkillsPopup()
  {
    //isRunning = true;
    nifty.closePopup(Skillspopup.getId());
  }
  
  /////////////////////////////////////////////////////////////////////////////
  //Create/Show/Close Attributes popup
  /////////////////////////////////////////////////////////////////////////////
  public void CreateAttributesPopup()
  {
    
    main.registerAttributesTab(nifty);
    Attributespopup = nifty.createPopup("AttributesPopup");
    //System.out.print(" Create  Attributes popup Called \n");
  }
   public void ShowAttributesPopup()
  {
    this.CloseSkillsPopup();
    this.CreateAttributesPopup();
    nifty.showPopup(nifty.getCurrentScreen(), Attributespopup.getId(), null);
    //System.out.print(" Show  Attributes popup Called \n");
  }
   
  public void CloseAttributesPopup()
  {
    nifty.closePopup(Attributespopup.getId());
    this.ShowSkillsPopup();
  }
  /////////////////////////////////////////////////////////////////////////////
  //Create/Show/Close Attributes popup
  /////////////////////////////////////////////////////////////////////////////
  public void CreateAbilitiesPopup()
  {
    main.registerAbilitiesTab(nifty);
    Abilitiespopup = nifty.createPopup("AbilitiesPopup");
    //System.out.print(" Create  Abilities popup Called \n");
  }
   public void ShowAbilitiesPopup()
  {
    
    this.CloseSkillsPopup();
    this.CreateAbilitiesPopup();
    nifty.showPopup(nifty.getCurrentScreen(), Abilitiespopup.getId(), null);
    //System.out.print(" Show  Attributes popup Called \n");
  }
   
  public void CloseAbilitiesPopup()
  {
    nifty.closePopup(Abilitiespopup.getId());
    this.ShowSkillsPopup();
  }
  
  
  public void BuyHealth()
  {
      
      if(Character.currentScore < 10)
      {
           Element niftyElement = nifty.getCurrentScreen().findElementByName("buyHealthButton");
           niftyElement.disable();
      }
      else
      {
          if(Character.currentHealth >= Character.maxHealth)
          {
              Element niftyElement = nifty.getCurrentScreen().findElementByName("buyHealthButton");
              niftyElement.disable();

          }
          else
          {
              Juggernaut.buyHealth();

              Element niftyCurrentHealthElement = nifty.getCurrentScreen().findElementByName("Current_Health");

              niftyCurrentHealthElement.getRenderer(TextRenderer.class).setText((int)Character.currentHealth + "");
          }
      }
      
  }
  public void BuyMiniGunAmmo()
  {
       if(Character.currentScore < 30)
      {
           Element niftyElement = nifty.getCurrentScreen().findElementByName("Buy_MiniGun_Ammo_Button");
           niftyElement.disable();
      }
      else
      {
          if(Juggernaut.WeaponSlot2().getCurrentAmmo() >= Juggernaut.WeaponSlot2().getMaxAmmo())
          {
              Element niftyElement = nifty.getCurrentScreen().findElementByName("Buy_MiniGun_Ammo_Button");
              niftyElement.disable();

          }
          else
          {
              Juggernaut.buyMiniGunAmmo();
              
              Element niftyCurrentHealthElement = nifty.getCurrentScreen().findElementByName("MiniGun_Current_Ammo");

              niftyCurrentHealthElement.getRenderer(TextRenderer.class).setText(Juggernaut.WeaponSlot2().currentAmmo + "");
          }
      }
      
  }
  public void BuyLaserRifleAmmo()
  {
      if(Character.currentScore < 50)
      {
           Element niftyElement = nifty.getCurrentScreen().findElementByName("Buy_LaserGun_Ammo_Button");
           niftyElement.disable();
      }
      else
      {
          if(Juggernaut.WeaponSlot3().getCurrentAmmo() >= Juggernaut.WeaponSlot3().getMaxAmmo())
          {
              Element niftyElement = nifty.getCurrentScreen().findElementByName("Buy_LaserGun_Ammo_Button");
              niftyElement.disable();

          }
          else
          {
              Juggernaut.buyLaserRifleAmmo();
              
              Element niftyCurrentHealthElement = nifty.getCurrentScreen().findElementByName("LaserGun_Current_Ammo");

              niftyCurrentHealthElement.getRenderer(TextRenderer.class).setText(Juggernaut.WeaponSlot3().currentAmmo + "");
          }
      }
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

      
      
     
      

  public void updateHUD(float health,float armor,float energy, float Ammo, float experience, float score){
         
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
            
            niftyElement = nifty.getCurrentScreen().findElementByName("PlayerExperience");
            
            niftyElement.setWidth((int)experience * 13);
            
            Element niftyAmmoElement = nifty.getCurrentScreen().findElementByName("Ammo");
      
            niftyAmmoElement.getRenderer(TextRenderer.class).setText("Ammo: " + Ammo);
            
            niftyElement = nifty.getCurrentScreen().findElementByName("score");
      
            niftyElement.getRenderer(TextRenderer.class).setText("" + score);
        }
    }
 
}
