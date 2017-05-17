/**************************************************
 * file: Main3d.java
 * author: Alfredo Ceballos and Armando Sanabria
 * class: CS 445 - Computer Graphics
 * assignment: Quarter project
 * date last modified: 5/8/2017
 * purpose: main class for project. initializes window
 *          and opengl
 **************************************************/
package cs445.project3;

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
        
        for (int i = 0; i < d.length; i++) {
            if (d[i].getWidth() == 640 && d[i].getHeight() == 480 && d[i].getBitsPerPixel() == 32) {
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("CS445 PLACEHOLDER TEXT LOL");
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
        glEnableClientState (GL_TEXTURE_COORD_ARRAY); 
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
