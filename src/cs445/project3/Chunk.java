/**************************************************
 * file: Chunk.java
 * author: Alfredo Ceballos and Armando Sanabria
 * class: CS 445 - Computer Graphics
 * assignment: Quarter project
 * date last modified: 05/17/2017
 * purpose: main class for project. initializes window
 *          and opengl
 **************************************************/
package cs445.project3;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/**
 *
 * @author Armando Sanabria <asanabria@cpp.edu>
 */
public class Chunk {

    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    static final float PERSITANCE_MIN = 0.18f;
    static final float PERSITANCE_MAX = 0.40f;

    private final Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int VBOTextureHandle;
    private final int StartX;
    private final int StartY;
    private final int StartZ;
    private final Random r;
    
    private Texture texture;

    public Chunk(int startX, int startY, int startZ) {
        try {
            texture = TextureLoader.getTexture("PNG", 
                    ResourceLoader.getResourceAsStream("src/cs445/project3/terrain.png"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.print("no texture file");
        }
        
        r = new Random(System.currentTimeMillis());
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int i = 0; i < CHUNK_SIZE; i++) {
                for (int j = 0; j < CHUNK_SIZE; j++) {
                    if (r.nextFloat() > 0.8f) {
                        Blocks[x][i][j] = new Block(Block.BlockType.BlockType_Grass);
                    } else if (r.nextFloat() > 0.6f) {
                        Blocks[x][i][j] = new Block(Block.BlockType.BlockType_Dirt);
                    } else if (r.nextFloat() > 0.4f) {
                        Blocks[x][i][j] = new Block(Block.BlockType.BlockType_Water);
                    } else if (r.nextFloat() > 0.3f) {
                        Blocks[x][i][j] = new Block(Block.BlockType.BlockType_Sand);
                    } else if (r.nextFloat() > 0.1f) {
                        Blocks[x][i][j] = new Block(Block.BlockType.BlockType_Stone);
                    } else {
                        Blocks[x][i][j] = new Block(Block.BlockType.BlockType_Bedrock);
                    }

                }
            }

        }

        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();

        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);

    }

    public void render() {
        glPushMatrix();
//        glPushMatrix();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);

        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glColorPointer(3, GL_FLOAT, 0, 0L);

        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2, GL_FLOAT, 0, 0L);

        glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }

    public void rebuildMesh(float startX, float startY, float startZ) {
        Random rand = new Random();
        int seed = (int) (50 * rand.nextFloat());
        float persitance = 0;

        while (persitance < PERSITANCE_MIN) {
            persitance = (PERSITANCE_MAX) * rand.nextFloat();
        }

        SimplexNoise noise = new SimplexNoise(CHUNK_SIZE, persitance, seed);

        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();

        FloatBuffer VertexPositionData
                = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData
                = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData
                = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);

        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int z = 0; z < CHUNK_SIZE; z++) {
                for (int y = 0; y < CHUNK_SIZE; y++) {
                    int height = (int) (startY + Math.abs((int) (CHUNK_SIZE * noise.getNoise((int) x, (int) z))));
                    if (y >= height) {
                        break;
                    }

                    VertexPositionData.put(createCube(
                            (float) (startX + x * CUBE_LENGTH),
                            (float) (y * CUBE_LENGTH + (int) (CHUNK_SIZE * .8)),
                            (float) (startZ + z * CUBE_LENGTH)));

                    VertexColorData.put(createCubeVertexCol(getCubeColor(
                            Blocks[(int) x][(int) y][(int) z])));

                    VertexTextureData.put(createTexCube((float) 0,
                            (float) 0, Blocks[(int) (x)][(int) (y)][(int) (z)]));
                }
            }
        }

        VertexTextureData.flip();
        VertexColorData.flip();
        VertexPositionData.flip();

        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

    }

    public static float[] createTexCube(float x, float y, Block block) {
        float offset = (1024f / 16) / 1024f;

        switch (block.getID()) {
            case 0: //grass
                return new float[]{
                    // TOP!
                    x + offset * 3, y + offset * 10,
                    x + offset * 2, y + offset * 10,
                    x + offset * 2, y + offset * 9,
                    x + offset * 3, y + offset * 9,
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // FRONT QUAD
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // BACK QUAD
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    // LEFT QUAD
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // RIGHT QUAD
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1
                };

            case 1: //sand
                return new float[]{
                    // TOP!
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // FRONT QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // BACK QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // LEFT QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2,
                    // RIGHT QUAD
                    x + offset * 2, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 2,
                    x + offset * 2, y + offset * 2
                };
            case 2: //water
                return new float[]{
                    // TOP!
                    x + offset * 2, y + offset * 11,
                    x + offset * 3, y + offset * 11,
                    x + offset * 3, y + offset * 12,
                    x + offset * 2, y + offset * 12,
                    // BOTTOM QUAD(DOWN=+Y
                    x + offset * 2, y + offset * 11,
                    x + offset * 3, y + offset * 11,
                    x + offset * 3, y + offset * 12,
                    x + offset * 2, y + offset * 12,
                    // FRONT QUAD
                    x + offset * 2, y + offset * 11,
                    x + offset * 3, y + offset * 11,
                    x + offset * 3, y + offset * 12,
                    x + offset * 2, y + offset * 12,
                    // BACK QUAD
                    x + offset * 2, y + offset * 11,
                    x + offset * 3, y + offset * 11,
                    x + offset * 3, y + offset * 12,
                    x + offset * 2, y + offset * 12,
                    // LEFT QUAD
                    x + offset * 2, y + offset * 11,
                    x + offset * 3, y + offset * 11,
                    x + offset * 3, y + offset * 12,
                    x + offset * 2, y + offset * 12,
                    // RIGHT QUAD
                    x + offset * 2, y + offset * 11,
                    x + offset * 3, y + offset * 11,
                    x + offset * 3, y + offset * 12,
                    x + offset * 2, y + offset * 12,};
            case 3: // dirt
                return new float[]{
                    // TOP!
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // FRONT QUAD
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // BACK QUAD
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // LEFT QUAD
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    // RIGHT QUAD
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,};
            case 4: //stone
                return new float[]{
                    // TOP!
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // FRONT QUAD
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // BACK QUAD
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // LEFT QUAD
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,
                    // RIGHT QUAD
                    x + offset * 1, y + offset * 0,
                    x + offset * 2, y + offset * 0,
                    x + offset * 2, y + offset * 1,
                    x + offset * 1, y + offset * 1,};
            case 5: //bedrock
                return new float[]{
                    // TOP!
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // FRONT QUAD
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // BACK QUAD
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // LEFT QUAD
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,
                    // RIGHT QUAD
                    x + offset * 1, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 2,
                    x + offset * 1, y + offset * 2,};
            default:    //grass is default
                return new float[]{
                    // TOP!
                    x + offset * 3, y + offset * 10,
                    x + offset * 2, y + offset * 10,
                    x + offset * 2, y + offset * 9,
                    x + offset * 3, y + offset * 9,
                    // BOTTOM QUAD(DOWN=+Y)
                    x + offset * 3, y + offset * 1,
                    x + offset * 2, y + offset * 1,
                    x + offset * 2, y + offset * 0,
                    x + offset * 3, y + offset * 0,
                    // FRONT QUAD
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // BACK QUAD
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    // LEFT QUAD
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1,
                    // RIGHT QUAD
                    x + offset * 3, y + offset * 0,
                    x + offset * 4, y + offset * 0,
                    x + offset * 4, y + offset * 1,
                    x + offset * 3, y + offset * 1
                };
        }
    }

    /**
     *
     * @param cubeColorArray
     * @return
     */
    private float[] createCubeVertexCol(float[] cubeColorArray) {
        float[] cubeColors = new float[cubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = cubeColorArray[i
                    % cubeColorArray.length];
        }
        return cubeColors;
    }

    /**
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[]{
            // TOP QUAD
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,
            // BOTTOM QUAD
            x + offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z,
            x + offset, y - offset, z,
            // FRONT QUAD
            x + offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            // BACK QUAD
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            // LEFT QUAD
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - CUBE_LENGTH,
            // RIGHT QUAD
            x + offset, y + offset, z,
            x + offset, y + offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z};

    }

    /**
     *
     * @param block
     * @return
     */
    private float[] getCubeColor(Block block) {
        return new float[]{1, 1, 1};
    }
}
