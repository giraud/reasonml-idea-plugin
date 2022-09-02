package jpsplugin.com.reason;

public class Interrupted {
  private Interrupted() {
  }

  public static void sleep(int timeToWait) {
    try {
      Thread.sleep(timeToWait);
    } catch (InterruptedException e) {
      // Do nothing
    }
  }
}
