import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;

/**
 * Created by liangmanman1 on 9/27/16.
 */
public class KeyboardHandler implements KeyListener {
  private Map<Integer, Runnable> keysTyped;
  private Map<Integer, Runnable> keysPressed;
  private Map<Integer, Runnable> keysReleased;

  /**
   * Empty default constructor
   */
  public KeyboardHandler() {


  }

  /**
   * Set the map for typed events
   *
   * @param map a map of all the key typed events
   */
  public void setKeysTyped(Map<Integer, Runnable> map) {
    this.keysTyped = map;
  }

  /**
   * Set the map for pressed events
   *
   * @param map a map of all the key pressed events
   */
  public void setKeysPressed(Map<Integer, Runnable> map) {
    this.keysPressed = map;
  }

  /**
   * Set the map for released events
   *
   * @param map a map of all the key released events
   */
  public void setKeysReleased(Map<Integer, Runnable> map) {
    this.keysReleased = map;
  }

  @Override
  public void keyTyped(KeyEvent e) {
    if (keysTyped.containsKey(e.getKeyCode()))
      keysTyped.get(e.getKeyCode()).run();
  }

  @Override
  public void keyPressed(KeyEvent e) {
    if (keysPressed.containsKey(e.getKeyCode()))
      keysPressed.get(e.getKeyCode()).run();
  }

  @Override
  public void keyReleased(KeyEvent e) {
    if (keysReleased.containsKey(e.getKeyCode()))
      keysReleased.get(e.getKeyCode()).run();
  }


}

