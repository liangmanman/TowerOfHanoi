import java.util.LinkedList;
import java.util.List;

/**
 * Created by liangmanman1 on 9/27/16.
 */
public class RecTowersOfHanoi implements TowersOfHanoi {
  private List<Move> solution;

  public RecTowersOfHanoi() {
    solution = new LinkedList<Move>();
  }

  @Override
  public void solve(int numDisks) {
    solution.clear();
    solve(numDisks,1,3,2);
  }

  @Override
  public List<Move> getMoves() {
    return solution;
  }


  private void solve(int numDisks,int from,int to,int inter) {
    if (numDisks>0) {
      solve(numDisks-1,from,inter,to);
      solution.add(new Move(from,to));
      solve(numDisks-1,inter,to,from);
    }
  }

}
