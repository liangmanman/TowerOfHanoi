import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.*;
import util.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * Created by liangmanman1 on 9/27/16.
 *
 * The View class is the "controller" of all our OpenGL stuff. It cleanly
 * encapsulates all our OpenGL functionality from the rest of Java GUI, managed
 * by the JOGLFrame class.
 */
public class View{
  private int WINDOW_WIDTH, WINDOW_HEIGHT;
  ShaderProgram program;
  private Matrix4f proj, modelview;
  private ObjectInstance obj;
  private ShaderLocationsVault shaderLocations;
  private int time;
  private int size;
  private List<FloatBuffer> colors;
  private FloatBuffer color;
  private Vector4f  motionLeft, motionRight;
  private List<Vector4f> centers;
  private List<Move> moves;
  private List<Float> scales;
  private boolean isMoving;
  private int[] disks;
  private boolean isAuto;


  public View(int size) {
    proj = new Matrix4f();
    modelview = new Matrix4f();
    proj.identity();

    obj = null;
    shaderLocations = null;
    time = 0;
    this.size = size;
    colors = new ArrayList<>();
    centers = new ArrayList<>();
    scales = new ArrayList<>();
    motionLeft = new Vector4f(-1.0f, 0.0f, 0, 0); // last 0 for acceleration
    motionRight = new Vector4f(1.0f, 0.0f, 0, 0);
    this.isMoving = false;
    this.isAuto = false;
  }

  public void init(GLAutoDrawable gla) throws Exception {
    GL3 gl = (GL3) gla.getGL().getGL3();

    //compile and make our shader program.
    // Look at the ShaderProgram class for details on how this is done
    program = new ShaderProgram();
    program.createProgram(gl, "shaders/default.vert", "shaders/default.frag");
    shaderLocations = program.getAllShaderVariables(gl);
    Random random = new Random();

    //draw rectangles using many random colors
    for (int i = 0; i < this.size; i++) {
      colors.add(FloatBuffer.wrap(new float[]{random.nextFloat(), random.nextFloat(),
        random.nextFloat(), 0}));
    }
    //using hanoi solve how many times it need to move, and how to move
    TowersOfHanoi hanoi = new RecTowersOfHanoi();
    hanoi.solve(this.size);
    moves = hanoi.getMoves();
    int frame = moves.size();

    // set in each frame which disk should move
    this.setDisks();

    // set initial center of each rectangle
    this.resetCenter();

    //center of rectangle should be different in each frame
    if (time >1) {
      for (int i = 1; i <= time-1; i++) {
        // at this frame should move to which tower
        Move prevMove = moves.get(i-1);
        int towerNumber = prevMove.getTo();
        int nowPositionX;
        // need to move to middle
        if (towerNumber == 2 ) {
          nowPositionX = 0;
        }
        // need to move to left
        else if (towerNumber == 1) {
          nowPositionX = -100;
        }
        // need to move to right
        else {
          nowPositionX = 100;
        }

        centers.get(disks[i]).x = nowPositionX;
      }

    }


    // set initial scales of each rectangle
    scales.add(1.0f);
    for (int i = 1; i < this.size; i++) {
      float prevScale = scales.get(i-1);
      float thisScale = prevScale * 0.9f;
      scales.add(thisScale);
    }

    List<Vector4f> positions = new ArrayList<Vector4f>();
    positions.add(new Vector4f(-30.0f, -5.0f, 0, 1.0f));
    positions.add(new Vector4f(30.0f, -5.0f, 0, 1.0f));
    positions.add(new Vector4f(30.0f, 5.0f, 0, 1.0f));
    positions.add(new Vector4f(-30.0f, 5.0f, 0, 1.0f));

    //set up vertex attributes (in this case we have only position and color)
    List<IVertexData> vertexData = new ArrayList<IVertexData>();
    VertexAttribProducer producer = new VertexAttribProducer();
    for (int i = 0; i < positions.size(); i++) {
      IVertexData v = producer.produce();
      v.setData("position", new float[]{positions.get(i).x,
        positions.get(i).y,
        positions.get(i).z,
        positions.get(i).w});
      vertexData.add(v);
    }

    List<Integer> indices = new ArrayList<Integer>();
    indices.add(0);
    indices.add(1);
    indices.add(2);

    indices.add(0);
    indices.add(2);
    indices.add(3);

    //now we create a polygon mesh object
    PolygonMesh mesh;
    mesh = new PolygonMesh();
    mesh.setVertexData(vertexData);
    mesh.setPrimitives(indices);

    mesh.setPrimitiveType(GL.GL_TRIANGLES);
    mesh.setPrimitiveSize(3);

    Map<String, String> shaderToVertexAttribute = new HashMap<String, String>();

    //currently there are two per-vertex attributes: position and color
    shaderToVertexAttribute.put("vPosition", "position");

    obj = new ObjectInstance(gl, program, shaderLocations,
      shaderToVertexAttribute, mesh, "triangles");

    this.isMoving = false;

  }


  public void draw(GLAutoDrawable gla) {
    GL3 gl = gla.getGL().getGL3();
    FloatBuffer fb16 = Buffers.newDirectFloatBuffer(16);

    //set the background color to be black
    gl.glClearColor(1, 1, 1, 0);
    //clear the background
    gl.glClear(gl.GL_COLOR_BUFFER_BIT);
    //enable the shader program
    program.enable(gl);


    modelview = new Matrix4f();

    /**
     * if autoMode, flying disks one by one
     */
    if (this.isAuto) {
      if (!this.isMoving) {
        this.isMoving = true;
        if (time != moves.size()) {
          time = time +1;
        }
        else {
          this.resetCenter();
          time = 0;
        }
      }

    }

    /**
     * draw the first tower
     */
    modelview = modelview.translate(new Vector3f(-100.0f, 0.0f, 0));
    modelview = modelview.rotate((float)Math.toRadians(90), 0, 0, 1);
    modelview = modelview.scale(3.0f, .2f, 1);
    gl.glUniformMatrix4fv(
      shaderLocations.getLocation("projection"),
      1, false, proj.get(fb16));
    gl.glUniformMatrix4fv(
      shaderLocations.getLocation("modelview"),
      1, false, modelview.get(fb16));
    // first color should be tower's color: black
    color = FloatBuffer.wrap(new float[]{0, 0, 0, 0});
    gl.glUniform4fv(
      shaderLocations.getLocation("vColor")
      , 1, color);
    obj.draw(gla);
    /**
     * draw the rest two towers
     */
    for (int i = 0; i < 2; i++) {
      modelview = modelview.translate(new Vector3f(0.0f, -500.0f, 0));
      gl.glUniformMatrix4fv(
        shaderLocations.getLocation("projection"),
        1, false, proj.get(fb16));
      gl.glUniformMatrix4fv(
        shaderLocations.getLocation("modelview"),
        1, false, modelview.get(fb16));
      // first color should be tower's color: black
      color = FloatBuffer.wrap(new float[]{0, 0, 0, 0});
      gl.glUniform4fv(
        shaderLocations.getLocation("vColor")
        , 1, color);
      obj.draw(gla);
    }


    /**
     * draw the disks
     */
    modelview = modelview.translate(new Vector3f(0.0f, 1000.0f, 0));
    modelview = modelview.scale(0.333333f, 5f, 1);
    modelview = modelview.rotate((float)Math.toRadians(-90), 0, 0, 1);

    for (int i=0; i < this.size; i++) {
      Vector4f thisCenter = centers.get(i);
      modelview =
        new Matrix4f().translate(thisCenter.x, thisCenter.y, 0);
      modelview = modelview.scale(scales.get(i), 1, 1);

      gl.glUniformMatrix4fv(
        shaderLocations.getLocation("projection"),
        1, false, proj.get(fb16));
      gl.glUniformMatrix4fv(
        shaderLocations.getLocation("modelview"),
        1, false, modelview.get(fb16));
      color = colors.get(i);
      gl.glUniform4fv(
        shaderLocations.getLocation("vColor")
        , 1, color);
      obj.draw(gla);
    }
    /**
     * flying disk
     */
    if (time > 0) {
      Move newMove = moves.get(time-1);
      int newTowerNumberTo = newMove.getTo();
      int newTowerNumberFrom = newMove.getFrom();
      int newPositionX;
      if (newTowerNumberTo == 2) {
        newPositionX = 0;
      }
      else if (newTowerNumberTo == 1) {
        newPositionX = -100;
      }
      else {
        newPositionX = 100;
      }

      Vector4f motion;
      if (newTowerNumberFrom < newTowerNumberTo) {
        motion = motionRight;
      }
      else{
        motion = motionLeft;
      }
      if (centers.get(disks[time]).x != newPositionX) {
        centers.get(disks[time]).add(motion);
      }
      else {
        this.isMoving = false;
      }
    }

    gl.glFlush();
    //disable the program
    program.disable(gl);
  }

  //this method is called from the JOGLFrame class, every time the window resizes
  public void reshape(GLAutoDrawable gla, int x, int y, int width, int height) {
    GL gl = gla.getGL();
    WINDOW_WIDTH = width;
    WINDOW_HEIGHT = height;

    gl.glViewport(0, 0, width, height);

    proj = new Matrix4f().ortho2D(-150, 150, -150, 150);

  }

  public void dispose(GLAutoDrawable gla) {
    obj.cleanup(gla);
  }

  /**
   * call this to check if is animating
   * if is animating, do nothing
   * else start animating
   */
  public void nextStep() {
    if (!this.isMoving) {
      this.isMoving = true;
      if (time != moves.size()) {
        time = time +1;
      }
      else {
        this.resetCenter();
        time = 0;
      }
    }

  }

  /**
   * call this after going through every frame
   * set each center back to left again
   */
  public void resetCenter() {
    centers.clear();
    // set initial center of each rectangle
    centers.add(new Vector4f(-100, -85, 0,1));
    for (int i = 1; i < this.size; i++) {
      Vector4f newCenter = centers.get(i-1);
      Vector4f newCenter2 = new Vector4f(0, 10.5f, 0, 1).add(newCenter);
      centers.add(newCenter2);
    }
    this.isMoving = false;
  }

  /**
   * Setting each frame which disk need to move
   */
  public void setDisks() {
    int sizeOfDisk = this.moves.size()+1;
    this.disks = new int[sizeOfDisk];
    this.disks[0] = 0;
    for (int i =1; i < disks.length; i++) {
      if (i%2!=0) {
        disks[i] = size-1;
      }
      else {
        for (int j=size-1; j >=1; j--) {
          if (i%Math.pow(2, j)==0) {
            disks[i] = size-1-j;
            j = 0;
          }
        }
      }
    }
  }

  /**
   * change a parameter to set view as auto mode
   */
  public void AutoMode() {
    if (!this.isAuto) {
      this.isAuto = true;
      this.time = 0;
      this.resetCenter();
    }
  }

  /**
   * change the parameter to set view as key control mode
   */
  public void KeyBoardMode() {
    this.isAuto = false;
    this.time = 0;
    this.resetCenter();
  }

}
