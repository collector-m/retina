// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.retina.util.data.Word;

public enum LinmotPutHelper {
  ;
  /* package */ static final Word CMD_HOME = Word.createShort("HOME", (short) 0x083f);
  public static final Word CMD_OPERATION = Word.createShort("OPERATION", (short) 0x003f);
  /* package */ static final Word CMD_ERR_ACK = Word.createShort("ERR_ACK", (short) 0x00bf);
  private static final Word CMD_OFF_MODE = Word.createShort("OFF_MODE", (short) 0x003e);
  public static final Word MC_POSITION = Word.createShort("POSITION", (short) 0x0900);
  /* package */ static final Word MC_ZEROS = Word.createShort("ZEROS", (short) 0x0000);
  // ---
  public static final List<Word> COMMANDS = Arrays.asList( //
      CMD_HOME, CMD_OPERATION, CMD_ERR_ACK, CMD_OFF_MODE);
  public static final List<Word> HEADER = Arrays.asList( //
      MC_POSITION, MC_ZEROS); //
  // ---
  /** all magic numbers are justified through experimentation */
  public static final int TARGETPOS_MIN = -500;
  public static final int TARGETPOS_MAX = -48;
  public static final int TARGETPOS_INIT = -50;
  // ---
  public static final int MAXVELOCITY_MIN = 0;
  public static final int MAXVELOCITY_MAX = 1000;
  public static final int MAXVELOCITY_INIT = 1000;
  // ---
  public static final int ACCELERATION_MIN = 0;
  public static final int ACCELERATION_MAX = 5000;
  public static final int ACCELERATION_INIT = 500;
  // ---
  public static final int DECELERATION_MIN = 0;
  public static final int DECELERATION_MAX = 5000;
  public static final int DECELERATION_INIT = 500;
  // ---
  /** off-mode event is used as fallback control and when
   * human driver takes over control of the break by foot */
  public static final LinmotPutEvent OFF_MODE_EVENT = new LinmotPutEvent( //
      LinmotPutHelper.CMD_OFF_MODE, //
      LinmotPutHelper.MC_ZEROS);

  /** @param value in the unit interval
   * @return */
  public static LinmotPutEvent operationToRelativePosition(double value) {
    return operationToPosition((short) // TODO this formula is slightly off, but that does not affect safety
    Math.min(Math.max(TARGETPOS_MIN, (TARGETPOS_MIN * value + TARGETPOS_INIT)), TARGETPOS_MAX));
  }

  public static LinmotPutEvent operationToPosition(short pos) {
    final LinmotPutEvent linmotPutEvent = new LinmotPutEvent( //
        LinmotPutHelper.CMD_OPERATION, //
        LinmotPutHelper.MC_POSITION);
    linmotPutEvent.target_position = pos;
    linmotPutEvent.max_velocity = MAXVELOCITY_INIT;
    linmotPutEvent.acceleration = ACCELERATION_INIT;
    linmotPutEvent.deceleration = DECELERATION_INIT;
    return linmotPutEvent;
  }

  public static Word findControlWord(short value) {
    return Word.findShort(COMMANDS, value);
  }

  public static Word findHeaderWord(short value) {
    return Word.findShort(HEADER, value);
  }
}