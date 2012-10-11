/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Juggernaut;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.List;

/**
 *
 * @author Wigglez
 */
public class Lava {
    private GhostControl ghost;     //The physical area that the box exists in, not visible or tangebale tho
                                    //used for testing collisions
    private Vector3f position;      //Position of box
    
    Lava() {
    }
    
    Lava(Vector3f position, Vector3f boxSize, Node rootNode,BulletAppState bulletAppState ){
       
        this.position = position;
        
        //Creates box that will test collision
        ghost = new GhostControl(new BoxCollisionShape(boxSize));
       
        //Create Node 
        Node view = new Node("view");                //Create Node for physics manager
        view.setLocalTranslation(this.position);    //Place node in correct world position
        view.addControl(ghost);                     //Attach ghost control to node
//        rootNode.attachChild(view);                 //attach veiw node to game world
        bulletAppState.getPhysicsSpace().add(ghost);//Lets the physics manager see ghost box
        ghost.setCollisionGroup(2);                 //Add this box to collision group 2
        ghost.removeCollideWithGroup(1);            //Prevent colliding with world geometry
       
    }
    
    Vector3f getPosition(){
        return position;
    }

     boolean testForPlayer(CharacterControl player)
    {
        //Get list of all objects withing this chunk
        List objects = ghost.getOverlappingObjects();
        
        //Check list to see if the player is within it
        //If they are return true
        if(objects.contains(player)){
            
            return true;
        }
            
        
        return false;       
        
    }
}
