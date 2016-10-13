import java.io.IOException;

/**
 * Created by liangmanman1 on 9/27/16
 */
public class HanoiAnimator {
  public static void main(String[] args) {
    //Schedule a job for the event-dispatching thread:
    //creating and showing this application's GUI.
    String input = args[0];
    try {
      int size = Integer.parseInt(input);
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          createAndShowGUI(size);
        }
      });
    }catch (IllegalArgumentException e) {
        System.out.println("Please input the number of disks in command line");
    }


  }

  private static void createAndShowGUI(int size) {
    JOGLFrame frame = new JOGLFrame("Hello JOGL Animator", size);
    HanoiController controller = new HanoiController(frame);
  }
}
