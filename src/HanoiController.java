import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by liangmanman1 on 9/27/16.
 */
public class HanoiController {

  private JOGLFrame frame;
  private KeyListener kbd;

  /**
   * the constructor for HanoiController
   * @param frame
   */
  public HanoiController(JOGLFrame frame) {
    this.frame = frame;
    configure();
    frame.resetFocus();
    frame.setVisible(true);
  }

  /**
   * the setting of keyEvent
   */
  private void configure() {
    Map<Integer, Runnable> keysTyped = new HashMap<>();
    Map<Integer, Runnable> keysPressed = new HashMap<>();
    Map<Integer, Runnable> keysReleased = new HashMap<>();

    keysPressed.put(KeyEvent.VK_SPACE, () -> {
      this.frame.nextStep();
    });

    keysPressed.put(KeyEvent.VK_A, ()-> {
      this.frame.AutoMode();
    });

    keysPressed.put(KeyEvent.VK_D,()-> {
      this.frame.KeyBoardMode();
    });

    KeyboardHandler kbd = new KeyboardHandler();
    kbd.setKeysTyped(keysTyped);
    kbd.setKeysPressed(keysPressed);
    kbd.setKeysReleased(keysReleased);
    this.kbd = kbd;


    frame.addKeyListenerHelper(kbd);

  }
}
