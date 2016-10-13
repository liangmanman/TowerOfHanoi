/**
 * Created by liangmanman1 on 9/27/16.
 */

import java.util.List;

/**
 * The interface of a towers of hanoi solver.
 * The intended use is to create an object, call solve and then call export.
 * Calling solve will compute a fresh solution, erasing solutions computed
 * earlier
 */
public interface TowersOfHanoi {

  /**
   * Compute a solution to the towers of hanoi problem with 3 towers and the
   * given number of disks
   * @param numDisks the number of disks to transfer from tower 1 to 3
   */
  void solve(int numDisks);

  List<Move> getMoves();

}
