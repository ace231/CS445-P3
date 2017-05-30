/**************************************************
 * file: Main3D.java
 * author: Alfredo Ceballos and Armando Sanabria
 * class: CS 445 - Computer Graphics
 * assignment: Quarter project
 * date last modified: 05/17/2017
 * purpose: main class for project. initializes window
 *          and opengl
 ************************************************* */
package cs445.project3;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

/**
 *
 * @author Alfredo & Armando
 */
public class Main3D {

    private final FPCameraController fp = new FPCameraController(0.0f, 0.0f, 0.0f);
    private DisplayMode displayMode;

    private FloatBuffer whiteLight;
    private FloatBuffer lightPosition;
    private FloatBuffer ambientLight;

    /**
     *
     */
    public void start() {
        try {
            createWindow();
            initGL();
            fp.gameLoop();
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
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        GLU.gluPerspective(100.0f,
                (float) displayMode.getWidth() / (float) displayMode.getHeight(), 0.1f, 300.0f);
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        glEnableClientState(GL_VERTEX_ARRAY);
        glEnableClientState(GL_COLOR_ARRAY);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        
        initLightArrays();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);
        glLight(GL_LIGHT0, GL_AMBIENT, ambientLight);
        glEnable(GL_LIGHTING);
        glEnable(GL_LIGHT0);
    }

    private void initLightArrays() {
        lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(0.0f).put(0.0f).put(0.0f).put(1.0f).flip();

        whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();

        ambientLight = BufferUtils.createFloatBuffer(4);
        ambientLight.put(0.5f).put(0.5f).put(0.5f).put(0.0f).flip();

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
