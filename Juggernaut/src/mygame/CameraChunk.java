
package mygame;

import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Node;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.GhostControl;
import com.jme3.math.Vector3f;
import java.util.List;

/**
 *
 * @author Vince
 */
public class CameraChunk {
    private GhostControl ghost;     //The physical area that the box exists in, not visible or tangebale tho
                                    //used for testing collisions
    private Vector3f position;      //Position of box
    private Vector3f camLookAt;     //Camera look at when in this Chunk
    private Vector3f camPosition;   //Camera position when in this Chunk
    private Vector3f camSetBack = new Vector3f( 0, 0, 50); //Uniform set back for camPosition in relation to position

    CameraChunk() {
    }
    
    ////////////////////////////////////////////////////////////////////////////
    //Consturctor
    //Parameters: Vector3f Position - the location of the chunk
    //            Node rootNode - to attach the chunk to the game world
    //            BulletAppState bulletAppState - to place the chunk in physics manager
    //
    ////////////////////////////////////////////////////////////////////////////
    CameraChunk(Vector3f position, Node rootNode,BulletAppState bulletAppState ){
       
        this.position = position;
        
        //Creates box that will test collision
        ghost = new GhostControl(new BoxCollisionShape( new Vector3f(30,12,5)));
       
        //Create Node 
        Node view = new Node("view");                //Create Node for physics manager
        view.setLocalTranslation(this.position);    //Place node in correct world position
        view.addControl(ghost);                     //Attach ghost control to node
        rootNode.attachChild(view);                 //attach veiw node to game world
        bulletAppState.getPhysicsSpace().add(ghost);//Lets the physics manager see ghost box
        ghost.setCollisionGroup(2);                 //Add this box to collision group 2
        ghost.removeCollideWithGroup(1);            //Prevent colliding with world geometry
        
        
        //Sets the camera Look at position and 
        //the camera position for this camera chunk
        camLookAt = this.position.add(new Vector3f(0, -4, 0));
        camPosition = this.position.add(camSetBack); 
    }
    
    ////////////////////////////////////////////////////////////////////////////
    //getPosition()
    //Parameters: None
    //
    //Return: type Vector3f
    //        returns position of chunk
    ////////////////////////////////////////////////////////////////////////////
    Vector3f getPosition(){
        return position;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    //CamLookAt()
    //Parameters: None
    //
    //Return: type Vector3f
    //        returns camLookAt
    ////////////////////////////////////////////////////////////////////////////
    Vector3f CamLookAt(){
        return camLookAt;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    //CamPosition()
    //Parameters: None
    //
    //Return: type Vector3f
    //        returns camPosition
    ////////////////////////////////////////////////////////////////////////////
    Vector3f CamPosition(){
        return camPosition;
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    //testForPlayer()
    //Parameters: CharacterControl player - the character control of the player
    //                                      to test against the volume the chunk's
    //                                      box takes up
    //
    //Return: type Boolean
    //        returns true if player is within this chunk
    //        returns fals otherwise
    ////////////////////////////////////////////////////////////////////////////
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
