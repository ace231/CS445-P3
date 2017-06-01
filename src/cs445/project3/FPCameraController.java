/**************************************************
 * file: FPCameraController.java
 * author: Alfredo Ceballos and Armando Sanabria
 * class: CS 445 - Computer Graphics
 * assignment: Quarter project
 * date last modified: 05/29/2017
 * purpose: controller for first person camera. handles
 *          changes coming from user and renders view
 *          test
 *************************************************/
package cs445.project3;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.opengl.GL11.*;

public class FPCameraController {

    private Vector3f position = null;
    private Vector3f lPosition = null;

//    FPCameraController camera = new FPCameraController(0, 0, 0);

    private float yaw = 0.0f;
    private float pitch = 0.0f;
    
    Chunk chunk;
    private Vector3Float me;

    /**
     *
     * @param x
     * @param y
     * @param z
     */
    public FPCameraController(float x, float y, float z) {
        position = new Vector3f(x, y, z);
        lPosition = new Vector3f(x, y, z);
        lPosition.x = 0f;
        lPosition.y = 15f;
        lPosition.z = 0f;
        chunk = new Chunk(0, 0, 0);
    }

    /**
     *
     * @return
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     *
     * @param position
     */
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    /**
     *
     * @return
     */
    public Vector3f getlPosition() {
        return lPosition;
    }

    /**
     *
     * @param lPosition
     */
    public void setlPosition(Vector3f lPosition) {
        this.lPosition = lPosition;
    }

    /**
     *
     * @param amt
     */
    public void yaw(float amt) {
        yaw += amt;
    }

    /**
     *
     * @param amt
     */
    public void pitch(float amt) {
        pitch -= amt;
    }

    /**
     *
     * @param dist
     */
    public void walkForward(float dist) {
        float xOffset = dist * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = dist * (float) Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;

    }

    /**
     *
     * @param dist
     */
    public void walkBackwards(float dist) {
        float xOffset = dist * (float) Math.sin(Math.toRadians(yaw));
        float zOffset = dist * (float) Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
    }

    /**
     *
     * @param dist
     */
    public void strafeLeft(float dist) {
        float xOffset = dist * (float) Math.sin(Math.toRadians(yaw - 90));
        float zOffset = dist * (float) Math.cos(Math.toRadians(yaw - 90));
        position.x -= xOffset;
        position.z += zOffset;
    }

    /**
     *
     * @param dist
     */
    public void strafeRight(float dist) {
        float xOffset = dist * (float) Math.sin(Math.toRadians(yaw - 90));
        float zOffset = dist * (float) Math.cos(Math.toRadians(yaw - 90));
        position.x += xOffset;
        position.z -= zOffset;
    }

    /**
     *
     * @param dist
     */
    public void moveUp(float dist) {
        position.y -= dist;
    }

    /**
     *
     * @param dist
     */
    public void moveDown(float dist) {
        position.y += dist;
    }

    /**
     *
     */
    public void lookThrough() {
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        glTranslatef(position.x, position.y, position.z);

        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(20.0f).put(50.0f).put(30).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    

    /**
     * 
     * @param movementSpeed
     * @param i
     * @return 
     */
    public boolean collision(float movementSpeed, int i) {
        return true;
    }

}
