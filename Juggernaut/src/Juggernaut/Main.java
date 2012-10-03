package Juggernaut;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.math.FastMath;

import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.TextBuilder;
import de.lessvoid.nifty.controls.button.builder.ButtonBuilder;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.render.image.ImageMode;
import de.lessvoid.nifty.render.image.ImageModeHelper;
import de.lessvoid.nifty.render.image.CompoundImageMode;
import de.lessvoid.nifty.screen.Screen;
import java.util.logging.Level;

public class Main extends SimpleApplication implements PhysicsCollisionListener {
    
    private StartScreen start;
    private Nifty nifty;
    
    private miniGun minigun;
    private Pistol pistol;
    private laserRifle laserRifle;
    
    
    private BulletAppState bulletAppState;  //Phyics manager
    private RigidBodyControl landscape;     //Phyics mesh for map
    
    Character Juggernaut;                   //Game Character

    Geometry elevator1;                     //Elevator 1 - Horizontal motion over lava pit
    RigidBodyControl elvtr1;
    Geometry elevator2;                     //Elevator 2 - Vertical motion to reach new area
    RigidBodyControl elvtr2;
    Geometry elevator3;                     //Elevator 3 - Vertical motion to reach new area
    RigidBodyControl elvtr3;
    
    
    CameraChunk []views = new CameraChunk[54];//holds all the different camera views
    CameraChunk currentView;                  //Stores the current view to update camPos and lookAt

    public static void main(String[] args) {
        
        java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);
        
        Main app = new Main();
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 720);
        app.setShowSettings(false); // splashscreen
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        setDisplayFps(true);
        setDisplayStatView(true);
        //Disable fly cam so it sits in fixed location
        flyCam.setEnabled(false);       
        
        //Creates the Physics state of the game
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.getPhysicsSpace().enableDebug(assetManager);  //Shows wireframes of models
        
        //Create the player character
        Juggernaut = new Character(this, bulletAppState);
        
        //Creates viewing boxes or Camera Chunks in the world
        setUpCameraBoxes();   
        
        //Load in the level
        Spatial map = assetManager.loadModel("Models/levelLayout - Update_cameraPos.obj");
        rootNode.attachChild(map);
        
        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
 //       mark_mat.setColor("Color", ColorRGBA.White);
 //       mark.setMaterial(mark_mat);
//        mark.setLocalTranslation(1f,1f,1f);
//        rootNode.attachChild(mark);
        
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape(map);
        landscape = new RigidBodyControl(sceneShape, 0);
        map.addControl(landscape);
        landscape.setCollisionGroup(1);         //Set collision group for the map
        landscape.removeCollideWithGroup(2);    //Prevent collision events between map and camera chunks(collision group 2)
        
        bulletAppState.getPhysicsSpace().add(landscape);
        
        //Elevator 1
        //Create Horizontal moving elevator
        elevator1 = new Geometry("Elevator1", new Box(4, 1, 5));
        elevator1.setLocalTranslation(304, 20, 0);
        elevator1.setMaterial(mark_mat);
        elvtr1 = new RigidBodyControl(10);
        elevator1.addControl(elvtr1);
        elvtr1.setFriction(1000.0f);
        elvtr1.setKinematic(true);
        bulletAppState.getPhysicsSpace().add(elvtr1);
        rootNode.attachChild(elevator1);
        
        //Elevator 2
        //Create Vertical moving elevator
        elevator2 = new Geometry("Elevator2", new Box(4, 1, 5));
        elevator2.setLocalTranslation(192, 208, 0);
        elevator2.setMaterial(mark_mat);
        elvtr2 = new RigidBodyControl(1000000000);  
        elevator2.addControl(elvtr2);
        elvtr2.setAngularDamping(100000.0f);
        elvtr2.setFriction(1000.0f);
//        elvtr2.setKinematic(true);
        bulletAppState.getPhysicsSpace().add(elvtr2);
        rootNode.attachChild(elevator2);
                
        
        //Add lights to see the models
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        rootNode.addLight(sun);
        
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White);
        rootNode.addLight(al);
        
        this.CreateHUD();

    }

    @Override
    public void simpleUpdate(float tpf) {
        
        //Update player
        Juggernaut.Update(tpf);

        //Move the horizontal elevator back and forth
        elevator1.setLocalTranslation( 304 + 20*FastMath.cos(timer.getTimeInSeconds()), 20, 0);
        elvtr1.setPhysicsLocation(elevator1.getLocalTranslation()); 

        //Move Vertical elevator up and down
        elvtr2.setLinearVelocity(new Vector3f(0, 15*FastMath.cos(timer.getTimeInSeconds()), 0));
        elvtr2.setPhysicsRotation(Matrix3f.IDENTITY);
        
        //Step through all views and test if player is in that space
        //If true, set current view to this view
        for(int i = 0; i < views.length; i++){            
            if(views[i].testForPlayer(Juggernaut.getControl())){
                currentView = views[i];
            }
                
        }   
        
        cam.setLocation(currentView.CamPosition());
        cam.lookAt(currentView.CamLookAt(), Vector3f.UNIT_Y);
        

    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }


    
    public void collision(PhysicsCollisionEvent event) {
        //For actual physics collisions
        //Maybe used for elevators/Moving platforms
    }

    private void setUpCameraBoxes() {
        

        //Create all viewing boxes and place them throughout map
        currentView = new CameraChunk(new Vector3f(10, 0, 0), rootNode, bulletAppState);
        views[0] = currentView;
        views[1] = new CameraChunk(new Vector3f(70, 0, 0), rootNode, bulletAppState);
        views[2] = new CameraChunk(new Vector3f(130, 0, 0), rootNode, bulletAppState);
        views[3] = new CameraChunk(new Vector3f(190, 0, 0), rootNode, bulletAppState);
        views[4] = new CameraChunk(new Vector3f(200, 24,  0), rootNode, bulletAppState);
        views[5] = new CameraChunk(new Vector3f(260, 24,  0), rootNode, bulletAppState);
        views[6] = new CameraChunk(new Vector3f(320, 24, 0), rootNode, bulletAppState);
        views[7] = new CameraChunk(new Vector3f(380, 24, 0), rootNode, bulletAppState);
        views[8] = new CameraChunk(new Vector3f(440, 32, 0), rootNode, bulletAppState);
        views[9] = new CameraChunk(new Vector3f(500, 48, 0), rootNode, bulletAppState);
        views[10] = new CameraChunk(new Vector3f(510, 73, 0), rootNode, bulletAppState);
        views[11] = new CameraChunk(new Vector3f(450, 73, 0), rootNode, bulletAppState);
        views[12] = new CameraChunk(new Vector3f(394, 92, 0), rootNode, bulletAppState);
        views[13] = new CameraChunk(new Vector3f(345, 107, 0), rootNode, bulletAppState);
        views[14] = new CameraChunk(new Vector3f(334, 125, 0), rootNode, bulletAppState);
        views[15] = new CameraChunk(new Vector3f(304, 129, 0), rootNode, bulletAppState);
        views[16] = new CameraChunk(new Vector3f(274, 125, 0), rootNode, bulletAppState);
        views[17] = new CameraChunk(new Vector3f(254, 107, 0), rootNode, bulletAppState);
        views[18] = new CameraChunk(new Vector3f(211, 95, 0), rootNode, bulletAppState);
        views[19] = new CameraChunk(new Vector3f(192, 72, 0), rootNode, bulletAppState);
        views[20] = new CameraChunk(new Vector3f(192, 48, 0), rootNode, bulletAppState);
        views[21] = new CameraChunk(new Vector3f(192, 114, 0), rootNode, bulletAppState);
        views[22] = new CameraChunk(new Vector3f(192, 138, 0), rootNode, bulletAppState);
        views[23] = new CameraChunk(new Vector3f(192, 162, 0), rootNode, bulletAppState);
        views[24] = new CameraChunk(new Vector3f(192, 186, 0), rootNode, bulletAppState);
        views[25] = new CameraChunk(new Vector3f(192, 210, 0), rootNode, bulletAppState);
        views[26] = new CameraChunk(new Vector3f(192, 234, 0), rootNode, bulletAppState);
        views[27] = new CameraChunk(new Vector3f(132, 169, 0), rootNode, bulletAppState);
        views[28] = new CameraChunk(new Vector3f(90, 145, 0), rootNode, bulletAppState);
        views[29] = new CameraChunk(new Vector3f(40, 124, 0), rootNode, bulletAppState);
        views[30] = new CameraChunk(new Vector3f(-20, 124, 0), rootNode, bulletAppState);
        views[31] = new CameraChunk(new Vector3f(-80, 124, 0), rootNode, bulletAppState);
        views[32] = new CameraChunk(new Vector3f(-140, 124, 0), rootNode, bulletAppState);
        views[33] = new CameraChunk(new Vector3f(-200, 124, 0), rootNode, bulletAppState);
        views[34] = new CameraChunk(new Vector3f(-260, 124,  0), rootNode, bulletAppState);
        views[35] = new CameraChunk(new Vector3f(-182, 100,  0), rootNode, bulletAppState);
        views[36] = new CameraChunk(new Vector3f(-182, 76, 0), rootNode, bulletAppState);
        views[37] = new CameraChunk(new Vector3f(-122, 76, 0), rootNode, bulletAppState);
        views[38] = new CameraChunk(new Vector3f(-82, 148, 0), rootNode, bulletAppState);
        views[39] = new CameraChunk(new Vector3f(-82, 172, 0), rootNode, bulletAppState);
        views[40] = new CameraChunk(new Vector3f(-82, 196, 0), rootNode, bulletAppState);
        views[41] = new CameraChunk(new Vector3f(-142, 196, 0), rootNode, bulletAppState);
        views[42] = new CameraChunk(new Vector3f(232, 245, 0), rootNode, bulletAppState);
        views[43] = new CameraChunk(new Vector3f(292, 245, 0), rootNode, bulletAppState);
        views[44] = new CameraChunk(new Vector3f(352, 245,  0), rootNode, bulletAppState);
        views[45] = new CameraChunk(new Vector3f(281, 269,  0), rootNode, bulletAppState);
        views[46] = new CameraChunk(new Vector3f(281, 293, 0), rootNode, bulletAppState);
        views[47] = new CameraChunk(new Vector3f(281, 317, 0), rootNode, bulletAppState);
        views[48] = new CameraChunk(new Vector3f(341, 300, 0), rootNode, bulletAppState);
        views[49] = new CameraChunk(new Vector3f(381, 300, 0), rootNode, bulletAppState);
        views[50] = new CameraChunk(new Vector3f(381, 280, 0), rootNode, bulletAppState);
        views[51] = new CameraChunk(new Vector3f(221, 323, 0), rootNode, bulletAppState);
        views[52] = new CameraChunk(new Vector3f(341, 323, 0), rootNode, bulletAppState);
        views[53] = new CameraChunk(new Vector3f(401, 323, 0), rootNode, bulletAppState);

        
    }
    public void CreateHUD()
    {
           
            start = new StartScreen();
            stateManager.attach(start);

            minigun = new miniGun(this);
            pistol = new Pistol(this);
            laserRifle = new laserRifle(this);
            


            NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(
                    assetManager, inputManager, audioRenderer, guiViewPort);
            nifty = niftyDisplay.getNifty();
            guiViewPort.addProcessor(niftyDisplay);


            nifty.loadStyleFile("nifty-default-styles.xml");
            nifty.loadControlFile("nifty-default-controls.xml");

            // Start<screen>
            nifty.addScreen("start", new ScreenBuilder("start") {{
            controller(new Juggernaut.StartScreen());

            layer(new LayerBuilder("background") {{
                childLayoutCenter();
                backgroundColor("#000f");

                // <!-- ... -->
                image(new ImageBuilder() {{
                    filename("Interface/juggernaut.png");
                }});

            }});

            ///////////////////////////////////////////////////////////
            //Start Screen
            //////////////////////////////////////////////////////////
            layer(new LayerBuilder("foreground") {{
                    childLayoutVertical();
                    backgroundColor("#0000");

                // panel added
                panel(new PanelBuilder("panel_One") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#f008");
                    height("25%");
                    width("100%");

    //                text(new TextBuilder() {{
    //                    text("Start Screen");
    //                    font("Interface/Fonts/Default.fnt");
    //                    wrap(true);
    //                    height("100%");
    //                    width("100%");
    //                 }});
                }});

                panel(new PanelBuilder("panel_Two") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#0f08");
                    height("25%");
                    width("100%");

                    control(new ButtonBuilder("StartButton", "Start") {{
                          alignCenter();
                          valignBottom();
                          height("25%");
                          width("25%");
                          visibleToMouse(true);
                          interactOnClick("goToScreen(hud)");
                          //interactOnClick("startGame()");

                        }});

                }});
                panel(new PanelBuilder("panel_Three") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#0f09");
                    height("25%");
                    width("100%");

                    control(new ButtonBuilder("OptionButton", "Options") {{
                      alignCenter();
                      valignCenter();
                      height("25%");
                      width("25%");
                      visibleToMouse(true);
                      interactOnClick("goToScreen(OptionsScreen)");

                        }});

                }});

                panel(new PanelBuilder("panel_Four") {{
                    childLayoutCenter();
                    alignCenter();
                   //backgroundColor("#00f8");
                    height("25%");
                    width("100%");


                    control(new ButtonBuilder("QuitButton", "Quit") {{
                      alignCenter();
                      valignTop();
                      height("25%");
                      width("25%");
                      visibleToMouse(true);
                      interactOnClick("quitGame()");
                        }});

                    }}); // panel added
                }});

            }}.build(nifty));

            //////////////////////////////////////////////////////////
            // HUD</screen>
            //////////////////////////////////////////////////////////
            nifty.addScreen("hud", new ScreenBuilder("hud") {{
            controller(new Juggernaut.StartScreen());

            layer(new LayerBuilder("background") {{
                childLayoutCenter();
                //backgroundColor("#0000");
                // <!-- ... -->
                image(new ImageBuilder() {{
                    filename("Interface/Hud_Background.png");
                }});
            }});

            layer(new LayerBuilder("foreground") {{
                childLayoutVertical();
                //backgroundColor("#0000");

                // panel added

                panel(new PanelBuilder("panel_One") {{
                    childLayoutHorizontal();
                    alignCenter();
                    //backgroundColor("#f008");
                    height("25%");
                    width("100%");
                    visibleToMouse(false);
                        panel(new PanelBuilder("Portrait_panel") {{
                        childLayoutVertical();
                        alignLeft();
                        //valignTop();
                        //backgroundColor("#0f05");
                        height("100%");
                        width("10%");
                        visibleToMouse(false);

                         panel(new PanelBuilder("Portrait") {{
                            childLayoutVertical();
    //                        alignLeft();
    //                        valignTop();
                            //backgroundColor("#0f05");
                            height("105");
                            width("128");
                            visibleToMouse(false);
                            image(new ImageBuilder() 
                                    {{
                                        filename("Interface/NO.jpg");
                                        valignCenter();
                                        alignCenter();
                                        height("100%");
                                        width("100%");
                                    }});

                            }});


                        }});

                        panel(new PanelBuilder("Bar_panel") {{
                            childLayoutVertical();
                            alignLeft();
                            //valignTop();
                            //backgroundColor("#f005");
                            height("100%");
                            width("200");
                            visibleToMouse(false);

                            panel(new PanelBuilder("Health_panel") {{
                                childLayoutHorizontal();
                                alignLeft();
                                //valignTop();
                                //backgroundColor("#00f8");
                                height("35");
                                width("100%");
                                visibleToMouse(false);


                                    image(new ImageBuilder() 
                                    {{
                                        id("PlayerHealth");
                                        filename("Interface/HealthBar.png");
                                        valignCenter();
                                        alignCenter();
                                        height("100%");
                                        width(start.getHealthPercentage() + "%");
                                    }});


                            }});

                            panel(new PanelBuilder("Armor_panel") {{
                                childLayoutHorizontal();
                                alignLeft();
                                //valignTop();
                                //backgroundColor("#00f7");
                                height("35");
                                width("100%");
                                visibleToMouse(false);

                                    image(new ImageBuilder() 
                                    {{
                                        id("PlayerArmor");
                                        filename("Interface/ArmorBar.png");
                                        valignCenter();
                                        alignCenter();
                                        height("100%");
                                        width(start.getEnergyPercentage() + "%");
                                    }});



                            }});

                            panel(new PanelBuilder("Energy_panel") {{
                                childLayoutHorizontal();
                                alignLeft();
                                //valignTop();
                                //backgroundColor("#00f6");
                                height("35");
                                width("100%");
                                visibleToMouse(false);


                                    image(new ImageBuilder() 
                                    {{
                                        id("PlayerEnergy");
                                        filename("Interface/EnergyBar.png");
                                        valignCenter();
                                        alignCenter();
                                        height("100%");
                                        width(start.getEnergyPercentage() + "%");
                                    }});



                            }});
                            

                        }});
                        panel(new PanelBuilder("Remaining_Top") {{
                            
                            childLayoutVertical();
                            //alignRight();
                            //valignTop();
                            //backgroundColor("#f005");
                            height("100%");
                            width("*");
                            visibleToMouse(false);
                            
                            panel(new PanelBuilder("Score_panel") {{
                                childLayoutHorizontal();
                                alignRight();
                                valignTop();
                                //backgroundColor("#00f6");
                                height("35");
                                width("200");
                                visibleToMouse(false);
                                
                                text(new TextBuilder() {{
                                    id("score");
                                    childLayoutHorizontal();
                                    alignRight();
                                    text("Score");
                                    font("Interface/Fonts/Default.fnt");
                                    //wrap(true);
                                    height("100%");
                                    width("100%");
                                }});
                            }});
                            
                            panel(new PanelBuilder("Key_panel") {{
                                childLayoutHorizontal();
                                alignRight();
                                valignTop();
                                //backgroundColor("#00f8");
                                height("50");
                                width("50");
                                visibleToMouse(false);
                                
                                image(new ImageBuilder() 
                                {{
                                        filename("Interface/Key.png");
                                        valignCenter();
                                        alignCenter();
                                        height("100%");
                                        width("100%");
                                }});
                            }});
                        }});
                }});

                panel(new PanelBuilder("panel_Two") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#0f08");
                    height("25%");
                    width("100%");
                    visibleToMouse(false);
                    
//                    control(new ButtonBuilder("TestHealthButton", "Test Health") {{
//                      alignCenter();
//                      valignBottom();
//                      height("25%");
//                      width("25%");
//                      visibleToMouse(true);
//                      interactOnClick("TestHealth()");
//                        }});
                    



                }});
                panel(new PanelBuilder("panel_Three") {{
                    childLayoutVertical();
                    alignCenter();
                    //backgroundColor("#0f09");
                    height("24%");
                    width("100%");
                    visibleToMouse(false);
                     
//                     control(new ButtonBuilder("TestArmorButton", "Test Armor") {{
//                      alignCenter();
//                      valignTop();
//                      height("25%");
//                      width("25%");
//                      visibleToMouse(true);
//                      interactOnClick("TestArmor()");
//                        }});
//                     
//                     control(new ButtonBuilder("TestEnergyButton", "Test Energy") {{
//                      alignCenter();
//                      valignBottom();
//                      height("25%");
//                      width("25%");
//                      visibleToMouse(true);
//                      interactOnClick("TestEnergy()");
//                        }});
                    
                    


                }});

                panel(new PanelBuilder("Experience_Panel") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#00f8");
                    height("2.5%");
                    width("100%");
                    visibleToMouse(false);

                    }}); // panel added
                panel(new PanelBuilder("top_spacer_Panel") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#0f06");
                    height("2.8%");
                    width("100%");
                    visibleToMouse(false);

                    }}); // panel added
                panel(new PanelBuilder("panel_Five") {{
                    childLayoutHorizontal();
                    alignCenter();
                    //backgroundColor("#00f5");
                    height("17.9%");
                    width("100%");
                    visibleToMouse(false);

                    panel(new PanelBuilder("Current_Weapon_panel") {{
                        childLayoutHorizontal();
                        alignLeft();
                        //valignTop();
                        //backgroundColor("#f003");
                        height("100%");
                        width("25%");
                        visibleToMouse(false);

                        panel(new PanelBuilder("weapon_spacer_Panel") {{
                            childLayoutCenter();
                            alignCenter();
                            //backgroundColor("#0f06");
                            height("100%");
                            width("20");
                            visibleToMouse(false);

                        }}); // panel added

                        panel(new PanelBuilder("Weapon_panel") {{
                            childLayoutCenter();
                            alignCenter();
                            //valignTop();
                            //backgroundColor("#00f6");
                            height("100%");
                            width("150");
                            visibleToMouse(false);


                                image(new ImageBuilder() 
                                {{
                                    filename("Interface/Pistol.png");
                                    valignCenter();
                                    alignCenter();
                                    height("100%");
                                    width("100%");
                                    id("CurrentWeapon");
                                }});

                            }});
                         panel(new PanelBuilder("Weapon_Stats_panel") {{
                            childLayoutVertical();
                            alignRight();
                            //valignTop();
                            //backgroundColor("#00f8");
                            height("100%");
                            width("130");
                            visibleToMouse(false);
                            
                            
                            panel(new PanelBuilder("Current_Weapon_Text") {{
                                childLayoutCenter();
                                //valignBottom();
                                //valignTop();
                                alignLeft();
                                //backgroundColor("#0f08");
                                height("20%");
                                width("100%");
                                visibleToMouse(false);
                                
                                text(new TextBuilder() {{
                                    id("CurrentWeaponText");
                                    childLayoutCenter();
                                    alignLeft();
                                    text("CurrentWeapon");
                                    font("Interface/Fonts/Default.fnt");
                                    //wrap(true);
                                    height("100%");
                                    width("100%");
                                }});

                            }});
                            
                            panel(new PanelBuilder("Weapon_Damage_Text") {{
                                childLayoutHorizontal();
                                //valignBottom();
                                //valignTop();
                                alignLeft();
                                //backgroundColor("#0f06");
                                height("20%");
                                width("100%");
                                visibleToMouse(false);
                                
                                text(new TextBuilder() {{
                                    id("WeaponDamageText");
                                    childLayoutHorizontal();
                                    alignLeft();
                                    text("Damage: " + pistol.dammage);
                                    font("Interface/Fonts/Default.fnt");
                                    //wrap(true);
                                    height("100%");
                                    width("100%");
                                }});

                            }});
                            panel(new PanelBuilder("Weapon_Fire_Rate_Text") {{
                                childLayoutCenter();
                                //valignBottom();
                                //valignTop();
                                //backgroundColor("#0f04");
                                height("20%");
                                width("100%");
                                visibleToMouse(false);
                                
                                text(new TextBuilder() {{
                                    id("WeaponFireRateText");
                                    childLayoutCenter();
                                    alignLeft();
                                    text("Fire Rate: " + pistol.fireRate);
                                    font("Interface/Fonts/Default.fnt");
                                    //wrap(true);
                                    height("100%");
                                    width("100%");
                                }});

                            }}); 

                            panel(new PanelBuilder("Weapon_Ammo_Text") {{
                                childLayoutCenter();
                                //valignBottom();
                                //valignTop();
                                //backgroundColor("#f008");
                                height("20%");
                                width("100%");
                                visibleToMouse(false);
                                
                                text(new TextBuilder() {{
                                    id("Ammo");
                                    childLayoutHorizontal();
                                    alignRight();
                                    text("Ammo: " + pistol.currentAmmo);
                                    font("Interface/Fonts/Default.fnt");
                                    //wrap(true);
                                    height("100%");
                                    width("100%");
                                }});

                                }});

                            }});

                        }});

                    panel(new PanelBuilder("Inventory1_panel") {{
                        childLayoutVertical();
                        alignLeft();
                        //valignTop();
                        //backgroundColor("#0f04");
                        height("100%");
                        width("15%");
                        visibleToMouse(false);
                         panel(new PanelBuilder("Pistol_panel") {{
                            childLayoutCenter();
                            alignCenter();
                            //valignTop();
                            //backgroundColor("#f004");
                            height("100%");
                            width("80%");
                            interactOnClick("weapon1Clicked()");

                            image(new ImageBuilder() 
                                {{
                                    filename("Interface/Pistol.png");
                                    valignCenter();
                                    alignCenter();
                                    height("129");
                                    width("150");
                                    id("Pistol");

                                }});
                         }});
                        }});
                    panel(new PanelBuilder("Inventory2_panel") {{
                        childLayoutVertical();
                        alignLeft();
                        //valignTop();
                        //backgroundColor("#f005");
                        height("100%");
                        width("15%");

                         panel(new PanelBuilder("MiniGun_panel") {{

                            childLayoutCenter();
                            alignCenter();
                            //valignTop();
                            //backgroundColor("#f004");
                            height("100%");
                            width("80%");
                            interactOnClick("weapon2Clicked()");

                            image(new ImageBuilder() 
                                {{
                                    filename("Interface/miniGun.png");
                                    valignCenter();
                                    alignCenter();
                                    height("129");
                                    width("150");
                                    id("MiniGun");
                                }});

                        }});
                        }});
                    panel(new PanelBuilder("Inventory3_panel") {{
                        childLayoutVertical();
                        alignLeft();
                        //valignTop();
                        //backgroundColor("#f006");
                        height("100%");
                        width("15%");

                        panel(new PanelBuilder("Cannon_panel") {{
                            childLayoutCenter();
                            alignCenter();
                            //valignTop();
                            //backgroundColor("#f004");
                            height("100%");
                            width("80%");
                            interactOnClick("weapon3Clicked()");


                            image(new ImageBuilder() 
                                {{
                                    filename("Interface/Cannon.png");
                                    valignCenter();
                                    alignCenter();
                                    height("129");
                                    width("150");
                                    id("Cannon");
                                }});
                        }});
                        }});
                    panel(new PanelBuilder("Skill1_panel") {{
                        childLayoutVertical();
                        alignLeft();
                        //valignTop();
                        //backgroundColor("#f007");
                        height("100%");
                        width("15%");
                        visibleToMouse(false);

                        image(new ImageBuilder() 
                            {{
                                filename("Interface/running-man.png");
                                valignCenter();
                                alignCenter();
                                height("129");
                                width("150");

                            }});

                        }});
                    panel(new PanelBuilder("Skill2_panel") {{
                        childLayoutVertical();
                        alignLeft();
                        //valignTop();
                        //backgroundColor("#f008");
                        height("100%");
                        width("15%");
                        visibleToMouse(false);

                        image(new ImageBuilder() 
                            {{
                                filename("Interface/JetPack.png");
                                valignCenter();
                                alignCenter();
                                height("129");
                                width("150");
                            }});
                        }});

                    }}); // panel added
                    panel(new PanelBuilder("bottom_spacer_Panel") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#0f06");
                    height("2.8%");
                    width("100%");
                    visibleToMouse(false);

                    }}); // panel added
                }});

        }}.build(nifty));


            ////////////////////////////////////////////////////////
            // Options</screen>
            ////////////////////////////////////////////////////////

            nifty.addScreen("OptionsScreen", new ScreenBuilder("OptionsScreen") {{
            controller(new Juggernaut.StartScreen());

            layer(new LayerBuilder("background") {{
                childLayoutCenter();
                backgroundColor("#000f");
                // <!-- ... -->
                image(new ImageBuilder() {{
                    filename("Interface/options.png");
                }});
            }});

            layer(new LayerBuilder("foreground") {{
                childLayoutVertical();
                backgroundColor("#0000");

                 panel(new PanelBuilder("panel_One") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#f008");
                    height("25%");
                    width("100%");

    //                text(new TextBuilder() {{
    //                    text("Options");
    //                    font("Interface/Fonts/Default.fnt");
    //                    wrap(true);
    //                    height("100%");
    //                    width("100%");
    //                 }});
                }});

                panel(new PanelBuilder("panel_Two") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#0f08");
                    height("25%");
                    width("100%");

                    control(new ButtonBuilder("GuideButton", "Guide") {{
                          alignCenter();
                          valignBottom();
                          height("25%");
                          width("25%");
                          visibleToMouse(true);
                          interactOnClick("goToScreen(GuideScreen)");
                        }});

                }});
                panel(new PanelBuilder("panel_Three") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#0f09");
                    height("25%");
                    width("100%");

                    control(new ButtonBuilder("ControlsButton", "Controls") {{
                      alignCenter();
                      valignCenter();
                      height("25%");
                      width("25%");
                      visibleToMouse(true);
                      interactOnClick("goToScreen(ControlsScreen)");
                        }});

                }});

                panel(new PanelBuilder("panel_Four") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#00f8");
                    height("25%");
                    width("100%");


                    control(new ButtonBuilder("BackButton", "Back") {{
                      alignCenter();
                      valignTop();
                      height("25%");
                      width("25%");
                      visibleToMouse(true);
                      interactOnClick("goToScreen(start)");
                        }});

                }}); // panel added
            }});
        }}.build(nifty));


            ////////////////////////////////////////////////////////
            //Controls</screen>
            ////////////////////////////////////////////////////////

            nifty.addScreen("ControlsScreen", new ScreenBuilder("ControlsScreen") {{
            controller(new Juggernaut.StartScreen());

            layer(new LayerBuilder("background") {{
                childLayoutCenter();
                backgroundColor("#000f");
                // <!-- ... -->
                image(new ImageBuilder() {{
                    filename("Interface/controls.png");
                }});
            }});

            layer(new LayerBuilder("foreground") {{
                childLayoutVertical();
                backgroundColor("#0000");

                 panel(new PanelBuilder("panel_One") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#f008");
                    height("25%");
                    width("100%");

    //                text(new TextBuilder() {{
    //                    text("Controls");
    //                    font("Interface/Fonts/Default.fnt");
    //                    wrap(true);
    //                    height("100%");
    //                    width("100%");
    //                 }});
                }});

                panel(new PanelBuilder("panel_Two") {{
                    childLayoutCenter();
                    alignCenter();
                   // backgroundColor("#0f08");
                    height("50%");
                    width("100%");

                    text(new TextBuilder() {{
                        text("W/Up Arrow - Jump \n"+
                             "A/Left Arrow - Move Left \n"+
                             "S/Down Arrow - Crouch \n"+
                             "D/Right Arrow - Move Right \n"+
                             "Spacebar - Shoot \n"+
                             "Shift/Control - Active Abilities \n");
                        font("Interface/Fonts/Default.fnt");
                        //wrap(true);
                        height("100%");
                        width("100%");
                    }});


                }});


                panel(new PanelBuilder("panel_Three") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#00f8");
                    height("25%");
                    width("100%");


                    control(new ButtonBuilder("BackButton", "Back") {{
                      alignCenter();
                      valignTop();
                      height("25%");
                      width("25%");
                      visibleToMouse(true);

                      interactOnClick("goToScreen(OptionsScreen)");
                        }});

                }}); // panel added
            }});
        }}.build(nifty));

            ////////////////////////////////////////////////////////
            //Guide</screen>
            ////////////////////////////////////////////////////////

            nifty.addScreen("GuideScreen", new ScreenBuilder("GuideScreen") {{
            controller(new Juggernaut.StartScreen());

            layer(new LayerBuilder("background") {{
                childLayoutCenter();
                backgroundColor("#000f");
                // <!-- ... -->
                image(new ImageBuilder() {{
                    filename("Interface/guide.png");
                }});
            }});

            layer(new LayerBuilder("foreground") {{
                childLayoutVertical();
                backgroundColor("#0000");

                 panel(new PanelBuilder("panel_One") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#f008");
                    height("25%");
                    width("100%");

    //                text(new TextBuilder() {{
    //                    text("Guide");
    //                    font("Interface/Fonts/Default.fnt");
    //                    wrap(true);
    //                    height("100%");
    //                    width("100%");
    //                 }});
                }});

                panel(new PanelBuilder("panel_Two") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#0f08");
                    height("50%");
                    width("100%");

                    text(new TextBuilder() {{
                        text("You are a super soldier from the year 2015 and are unfrozen to save the world "
                                + "from the overwhelming mass of aliens in the year 3027. "
                                + "Your goal is to exterminate the aliens by using various weapons, skills, "
                                + "and upgrades found along the way. During the campaign, you will "
                                + "encounter different enemies that will drop ammo and health pickups to assist you with your survivability. ");
                        font("Interface/Fonts/Default.fnt");
                        wrap(true);
                        height("100%");
                        width("100%");
                    }});


                }});


                panel(new PanelBuilder("panel_Three") {{
                    childLayoutCenter();
                    alignCenter();
                    //backgroundColor("#00f8");
                    height("25%");
                    width("100%");


                    control(new ButtonBuilder("BackButton", "Back") {{
                      alignCenter();
                      valignTop();
                      height("25%");
                      width("25%");
                      visibleToMouse(true);

                      interactOnClick("goToScreen(OptionsScreen)");
                        }});

                }}); // panel added
            }});
        }}.build(nifty));

        flyCam.setEnabled(false);
        nifty.gotoScreen("start"); // start the screen
        //nifty.setDebugOptionPanelColors(true);


    }
    public StartScreen getHud(){
        return start;
    }
    public Nifty getNifty(){
        return nifty;
    }
}
