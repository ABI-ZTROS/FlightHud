package net.torocraft.flighthud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.torocraft.flighthud.config.HudConfig;
import org.joml.Matrix3x2fStack;

public abstract class HudComponent {

  public abstract void render(GuiGraphicsExtractor g, float partial, Minecraft client);

  public static HudConfig CONFIG;

  protected int i(double d) {
    return (int) Math.round(d);
  }

  protected void drawPointer(GuiGraphicsExtractor g, float x, float y, float rot) {
    Matrix3x2fStack m = g.pose();
    m.pushMatrix();
    m.translate(x, y);
    m.rotate((float) Math.toRadians(rot + 45));
    drawVerticalLine(g, 0, 0, 5);
    drawHorizontalLine(g, 0, 5, 0);
    m.popMatrix();
  }

  protected float wrapHeading(float degrees) {
    degrees = degrees % 360;
    while (degrees < 0) {
      degrees += 360;
    }
    return degrees;
  }

  protected void drawFont(Minecraft mc, GuiGraphicsExtractor g, String s, float x, float y) {
    drawFont(mc, g, s, x, y, CONFIG.color);
  }

  protected void drawFont(Minecraft mc, GuiGraphicsExtractor g, String s, float x, float y,
      int color) {
    g.text(mc.font, s, Math.round(x), Math.round(y), color, false);
  }

  protected void drawRightAlignedFont(Minecraft mc, GuiGraphicsExtractor g, String s, float x,
      float y) {
    int w = mc.font.width(s);
    drawFont(mc, g, s, x - w, y);
  }

  protected void drawBox(GuiGraphicsExtractor g, float x, float y, float w, float h) {
    drawHorizontalLine(g, x, x + w, y);
    drawHorizontalLine(g, x, x + w, y + h);
    drawVerticalLine(g, x, y, y + h);
    drawVerticalLine(g, x + w, y, y + h);
  }

  protected void drawHorizontalLineDashed(GuiGraphicsExtractor g, float x1, float x2, float y,
      int dashCount) {
    float width = x2 - x1;
    int segmentCount = dashCount * 2 - 1;
    float dashSize = width / segmentCount;
    for (int i = 0; i < segmentCount; i++) {
      if (i % 2 != 0) {
        continue;
      }
      float dx1 = i * dashSize + x1;
      float dx2;
      if (i == segmentCount - 1) {
        dx2 = x2;
      } else {
        dx2 = ((i + 1) * dashSize) + x1;
      }
      drawHorizontalLine(g, dx1, dx2, y);
    }
  }

  protected void drawHorizontalLine(GuiGraphicsExtractor g, float x1, float x2, float y) {
    drawHorizontalLine(g, x1, x2, y, CONFIG.color);
  }

  protected void drawHorizontalLine(GuiGraphicsExtractor g, float x1, float x2, float y,
      int color) {
    if (x2 < x1) {
      float i = x1;
      x1 = x2;
      x2 = i;
    }
    fill(g, x1 - CONFIG.halfThickness, y - CONFIG.halfThickness, x2 + CONFIG.halfThickness,
        y + CONFIG.halfThickness, color);
  }

  protected void drawVerticalLine(GuiGraphicsExtractor g, float x, float y1, float y2) {
    drawVerticalLine(g, x, y1, y2, CONFIG.color);
  }

  protected void drawVerticalLine(GuiGraphicsExtractor g, float x, float y1, float y2,
      int color) {
    if (y2 < y1) {
      float i = y1;
      y1 = y2;
      y2 = i;
    }
    fill(g, x - CONFIG.halfThickness, y1 + CONFIG.halfThickness, x + CONFIG.halfThickness,
        y2 - CONFIG.halfThickness, color);
  }

  protected void fill(GuiGraphicsExtractor g, float x1, float y1, float x2, float y2) {
    fill(g, x1, y1, x2, y2, CONFIG.color);
  }

  protected void fill(GuiGraphicsExtractor g, float x1, float y1, float x2, float y2,
      int color) {
    int ix1 = Math.round(Math.min(x1, x2));
    int iy1 = Math.round(Math.min(y1, y2));
    int ix2 = Math.round(Math.max(x1, x2));
    int iy2 = Math.round(Math.max(y1, y2));
    g.fill(ix1, iy1, ix2, iy2, color);
  }
}
