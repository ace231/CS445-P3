/**************************************************
 * file: Main3D.java
 * author: Alfredo Ceballos and Armando Sanabria
 * class: CS 445 - Computer Graphics
 * assignment: Quarter project
 * date last modified: 05/17/2017
 * purpose: main class for project. initializes 
 *          window and opengl
 ************************************************/
package cs445.project3;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

public class Main3D {

    private FPCameraController camera;
    private DisplayMode displayMode;

    private FloatBuffer lightPosition;
    private FloatBuffer whiteLight;

    /**
     *
     */
    public void start() {
        try {
            createWindow();
            initGL();
            camera = new FPCameraController(-30, -50, -30);
            gameLoop();
        } catch (Exception e) {
            System.out.println("An error occurred in the main class...");
            e.printStackTrace();
        }
    }

    /**
     *
     * @throws Exception
     */
    private void createWindow() throws Exception {
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();

        for (DisplayMode d1 : d) {
            if (d1.getWidth() == 640 && d1.getHeight() == 480 && d1.getBitsPerPixel() == 32) {
                displayMode = d1;
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("CS445 Final Project");
        Display.create();
    }

    /**
     *
     */
    private void initGL() {
        initLightArrays();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(100.0f, (float) displayMode.getWidth() / (float) displayMode.getHeight(), 0.1f, 300.0f);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);

    }

    private void initLightArrays() {
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();
        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();

    }

    /**
     *
     */
    public void gameLoop() {
        float dx = 0.0f, dy = 0.0f, dz = 0.0f, lastTime = 0.0f;
        long time = 0;
        float mouseSensitivity = 0.09f, movementSpeed = 0.35f;
        Mouse.setGrabbed(true);    //true means mouse is paralyzed

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
            camera.chunk.render();
            Display.update();
            Display.sync(60);
        }
        Display.destroy();
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Main3D thing = new Main3D();
        thing.start();
    }
}
