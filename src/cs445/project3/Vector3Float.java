/**************************************************
 * file: Vector3dFloat.java
 * author: Alfredo Ceballos and Armando Sanabria
 * class: CS 445 - Computer Graphics
 * assignment: Quarter project
 * date last modified: 5/6/2017
 * purpose: Vector class to hold information about camera
 *          position
 **************************************************/
package cs445.project3;

/**
 *
 * @author Alfredo
 */
public class Vector3Float {

    public float x, y, z;

    public Vector3Float(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }
}
