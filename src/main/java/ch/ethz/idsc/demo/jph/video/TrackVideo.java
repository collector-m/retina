// code by jph
package ch.ethz.idsc.demo.jph.video;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Objects;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.core.mpc.ControlAndPredictionSteps;
import ch.ethz.idsc.gokart.gui.top.MPCPredictionRender;
import ch.ethz.idsc.gokart.gui.top.MPCPredictionSequenceRender;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.io.Mp4AnimationWriter;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ enum TrackVideo {
  ;
  public static void main(String[] args) throws Exception {
    System.out.print("building index...");
    NavigableMap<Scalar, ControlAndPredictionSteps> navigableMap = //
        ControlAndPredictionStepsIndex.build(TrackDrivingTables.SINGLETON);
    System.out.println("done");
    /** Read in some option values and their defaults. */
    final int snaps = 50; // fps
    final String string = TrackDrivingTables.SINGLETON.getParentFile().getName();
    final String filename = HomeDirectory.file(string + ".mp4").toString();
    // ---
    // File folder = new File("/media/datahaki/media/ethz/gokart/topic/track_red");
    File src = HomeDirectory.file("track_putty");
    List<TrackDriving> list = new LinkedList<>();
    int id = 0;
    // for (File csvFile : src.listFiles())
    File csvFile = new File("/home/datahaki/track_putty/" + string + ".csv");
    if (csvFile.isFile()) {
      // GokartLogInterface gokartLogInterface = GokartLogAdapter.of(file);
      // String title = file.getName();
      // File csvFile = new File(file);
      // if (csvFile.isFile())
      {
        TrackDriving trackDriving = new TrackDriving(Import.of(csvFile), id++);
        trackDriving.setDriver(csvFile.getName().startsWith("mh") ? "mh" : "tg");
        trackDriving.setExtrusion(true);
        // System.out.println(trackDriving.row(0));
        list.add(trackDriving);
      }
    }
    BufferedImage background = ImageIO.read(VideoBackground.IMAGE_FILE);
    final int max = list.stream().mapToInt(TrackDriving::maxIndex).max().getAsInt();
    BufferedImage bufferedImage = new BufferedImage( //
        VideoBackground.DIMENSION.width, //
        VideoBackground.DIMENSION.height, //
        BufferedImage.TYPE_3BYTE_BGR);
    Graphics2D graphics = bufferedImage.createGraphics();
    GraphicsUtil.setQualityHigh(graphics);
    // PathRender pathRender = new PathRender(new Color(115, 167, 115, 64),
    // new BasicStroke(6f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 10.0f }, 0.0f));
    // Tensor optimal = Import.of(new File(src, "opt/onelap.csv"));
    // RenderInterface ri = pathRender.setCurve(optimal, true);
    MPCPredictionSequenceRender mpcPredictionSequenceRender = new MPCPredictionSequenceRender(30);
    try (Mp4AnimationWriter mp4AnimationWriter = new Mp4AnimationWriter(filename, VideoBackground.DIMENSION, snaps)) {
      for (int index = 0; index < max; ++index) {
        System.out.println(index);
        Scalar time = list.get(0).timeFor(index);
        graphics.setColor(Color.WHITE);
        // graphics.fillRect(0, 0, dimension.width, dimension.height);
        graphics.drawImage(background, 0, 0, null);
        Tensor model2pixel = VideoBackground.MODEL2PIXEL;
        GeometricLayer geometricLayer = GeometricLayer.of(model2pixel);
        // ri.render(geometricLayer, graphics);
        if (true) {
          Entry<Scalar, ControlAndPredictionSteps> floorEntry = navigableMap.floorEntry(Quantity.of(time, SI.SECOND));
          if (Objects.nonNull(floorEntry)) {
            ControlAndPredictionSteps controlAndPredictionSteps = floorEntry.getValue();
            mpcPredictionSequenceRender.getControlAndPredictionSteps(controlAndPredictionSteps);
            mpcPredictionSequenceRender.render(geometricLayer, graphics);
            MPCPredictionRender mpcPredictionRender = new MPCPredictionRender();
            mpcPredictionRender.getControlAndPredictionSteps(controlAndPredictionSteps);
            mpcPredictionRender.render(geometricLayer, graphics);
          }
        }
        for (TrackDriving trackDriving : list) {
          trackDriving.setRenderIndex(index);
          trackDriving.render(geometricLayer, graphics);
        }
        graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 30));
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.drawString(String.format("time:%7s[s]", time.map(Round._3)), 0, 25);
        mp4AnimationWriter.append(bufferedImage);
        if (index == 100000)
          break;
      }
    }
    System.out.println("stopped.");
  }
}