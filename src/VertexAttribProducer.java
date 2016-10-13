/**
 * Created by liangmanman1 on 9/27/16.
 */
public class VertexAttribProducer implements util.VertexProducer<VertexAttrib> {
  @Override
  public VertexAttrib produce() {
    return new VertexAttrib();
  }
}
