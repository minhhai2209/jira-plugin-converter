package hope.generator.utils;

import java.util.UUID;

import org.junit.Test;

public class KeyTest {

  @Test
  public void test() {
    String uuid = UUID.randomUUID().toString();
    System.out.println(uuid);
  }
}
