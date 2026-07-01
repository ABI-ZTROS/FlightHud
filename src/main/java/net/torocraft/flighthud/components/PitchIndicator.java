package net.torocraft.flighthud.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.FlightComputer;
import net.torocraft.flighthud.HudComponent;

public class PitchIndicator extends HudComponent {
  private final Dimensions dim;
  private final FlightComputer computer;
  private final PitchIndicatorData pitchData = new PitchIndicatorData();

  public PitchIndicator(FlightComputer computer, Dimensions dim) {
    this.computer = computer;
    this.dim = dim;
  }

  @Override
  public void render(GuiGraphicsExtractor g, float partial, Minecraft mc) {
    pitchData.update(dim);

    float horizonOffset = computer.pitch * dim.degreesPerPixel;
    float yHorizon = dim.yMid + horizonOffset;

    float a = dim.yMid;
    float b = dim.xMid;

    float roll = computer.roll * (CONFIG.pitchLadder_reverseRoll ? -1 : 1);

    if (CONFIG.pitchLadder_showRoll) {
      g.pose().pushMatrix();
      g.pose().translate(b, a);
      g.pose().rotate((float) Math.toRadians(roll));
      g.pose().translate(-b, -a);
    }

    if (CONFIG.pitchLadder_showLadder) {
      drawLadder(mc, g, yHorizon);
    }

    drawReferenceMark(mc, g, yHorizon, CONFIG.pitchLadder_optimumClimbAngle);
    drawReferenceMark(mc, g, yHorizon, CONFIG.pitchLadder_optimumGlideAngle);

    if (CONFIG.pitchLadder_showHorizon) {
      pitchData.l1 -= pitchData.margin;
      pitchData.r2 += pitchData.margin;
      drawDegreeBar(mc, g, 0, yHorizon);
    }

    if (CONFIG.pitchLadder_showRoll) {
      g.pose().popMatrix();
    }
  }

  private void drawLadder(Minecraft mc, GuiGraphicsExtractor g, float yHorizon) {
    int degreesPerBar = CONFIG.pitchLadder_degreesPerBar;

    if (degreesPerBar < 1) {
      degreesPerBar = 20;
    }

    for (int i = degreesPerBar; i <= 90; i = i + degreesPerBar) {
      float offset = dim.degreesPerPixel * i;
      drawDegreeBar(mc, g, -i, yHorizon + offset);
      drawDegreeBar(mc, g, i, yHorizon - offset);
    }

  }

  private void drawReferenceMark(Minecraft mc, GuiGraphicsExtractor g, float yHorizon,
      float degrees) {
    if (degrees == 0) {
      return;
    }

    float y = (-degrees * dim.degreesPerPixel) + yHorizon;

    if (y < dim.tFrame || y > dim.bFrame) {
      return;
    }

    float width = (pitchData.l2 - pitchData.l1) * 0.45f;
    float l1 = pitchData.l2 - width;
    float r2 = pitchData.r1 + width;

    drawHorizontalLineDashed(g, l1, pitchData.l2, y, 3);
    drawHorizontalLineDashed(g, pitchData.r1, r2, y, 3);
  }

  private void drawDegreeBar(Minecraft mc, GuiGraphicsExtractor g, float degree, float y) {

    if (y < dim.tFrame || y > dim.bFrame) {
      return;
    }

    int dashes = degree < 0 ? 4 : 1;

    drawHorizontalLineDashed(g, pitchData.l1, pitchData.l2, y, dashes);
    drawHorizontalLineDashed(g, pitchData.r1, pitchData.r2, y, dashes);

    int sideTickHeight = degree >= 0 ? 5 : -5;
    drawVerticalLine(g, pitchData.l1, y, y + sideTickHeight);
    drawVerticalLine(g, pitchData.r2, y, y + sideTickHeight);

    int fontVerticalOffset = degree >= 0 ? 0 : 6;

    drawFont(mc, g, String.format("%d", i(Math.abs(degree))), pitchData.r2 + 6,
        (float) y - fontVerticalOffset);

    drawFont(mc, g, String.format("%d", i(Math.abs(degree))), pitchData.l1 - 17,
        (float) y - fontVerticalOffset);
  }

  private static class PitchIndicatorData {
    public float width;
    public float mid;
    public float margin;
    public float sideWidth;
    public float l1;
    public float l2;
    public float r1;
    public float r2;

    public void update(Dimensions dim) {
      width = i(dim.wScreen / 3);
      float left = width;

      mid = i((width / 2) + left);
      margin = i(width * 0.3d);
      l1 = left + margin;
      l2 = mid - 7;
      sideWidth = l2 - l1;
      r1 = mid + 8;
      r2 = r1 + sideWidth;
    }

    private int i(double d) {
      return (int) Math.round(d);
    }
  }

}
