/**************************************************
 * file: FPCameraController.java
 * author: Alfredo Ceballos and Armando Sanabria
 * class: CS 445 - Computer Graphics
 * assignment: Quarter project
 * date last modified: 5/8/2017
 * purpose: controller for first person camera. handles
 *          changes coming from user and renders view
 test
 **************************************************/
package cs445.project3;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;

/**
 *
 * @author Alfredo
 */
public class FPCameraController {

    private Vector3f position = null;
    private Vector3f lPosition = null;
    private float yaw = 0.0f;
    private float pitch = 0.0f;
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
    }

    /**
     *
     */
    public void gameLoop() {
        FPCameraController camera = new FPCameraController(0, 0, 0);
        createBlock(0, 0, -10);
        float dx = 0.0f, dy = 0.0f, dz = 0.0f, lastTime = 0.0f;
        long time = 0;
        float mouseSensitivity = 0.09f, movementSpeed = 0.35f;
        Mouse.setGrabbed(false);    //true means mouse is paralyzed

        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            time = Sys.getTime();
            lastTime = time;
            dx = Mouse.getDX();
            dy = Mouse.getDY();
            camera.yaw(dx * mouseSensitivity);
            camera.pitch(dy * mouseSensitivity);

            if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
                camera.walkForward(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
                camera.walkBackwards(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
                camera.strafeLeft(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
                camera.strafeRight(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                camera.moveUp(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
                camera.moveDown(movementSpeed);
            }

            glLoadIdentity();
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            render();
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }

    /**
     *
     */
    private void render() {
        try {
           glBegin(GL_QUADS);
            // Renders top of cube
            glColor3f(0.0f, 0.0f, 1.0f);
            glVertex3f( 1.0f, 1.0f,-1.0f);
            glVertex3f(-1.0f, 1.0f,-1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f( 1.0f, 1.0f, 1.0f); 


            // Renders bottom of cube
            glColor3f(0.0f, 1.0f, 0.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(1.0f, -1.0f, -1.0f);

            // Front of cube
            glColor3f(1.0f, 0.0f, 0.0f);
            glVertex3f(1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);

            // Back of cube
            glColor3f(1.0f, 1.0f, 0.0f);
            glVertex3f(1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(1.0f, 1.0f, -1.0f);

            // Left of cube
            glColor3f(1.0f, 0.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, -1.0f);
            glVertex3f(-1.0f, -1.0f, 1.0f);

            // Right of cube
            glColor3f(0.0f, 1.0f, 1.0f);
            glVertex3f(1.0f, 1.0f, -1.0f);
            glVertex3f(1.0f, 1.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);
            glVertex3f(1.0f, -1.0f, 1.0f);
            glEnd();
        } catch (Exception e) {
            System.out.println("So an error occurred...");
            e.printStackTrace();
        }
    }

    private void createBlock(int i, int i0, int i1) {
        Block[][][] b;
        b = new Block[90][90][90];

        int x, y, z;

        x = i;
        y = i0;
        z = i1;

        rebuildMesh(x, y, z);
    }

    private void rebuildMesh(int x, int y, int z) {
        FloatBuffer VertexPositionData
                = BufferUtils.createFloatBuffer((90 * 90 * 90) * 6 * 12);
        VertexPositionData.put(createCube((float) (x * 2),
                (float) (y * 2 + (int) (90 * -.256)), (float) (x * 2) - (90)));

    }

    private float createCube(float x, float y, float z) {
        return 0;
    }
}

