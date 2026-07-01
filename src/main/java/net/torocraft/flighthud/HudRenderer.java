package net.torocraft.flighthud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.torocraft.flighthud.components.AltitudeIndicator;
import net.torocraft.flighthud.components.ElytraHealthIndicator;
import net.torocraft.flighthud.components.FlightPathIndicator;
import net.torocraft.flighthud.components.HeadingIndicator;
import net.torocraft.flighthud.components.LocationIndicator;
import net.torocraft.flighthud.components.PitchIndicator;
import net.torocraft.flighthud.components.SpeedIndicator;
import net.torocraft.flighthud.config.SettingsConfig.DisplayMode;
import org.joml.Matrix3x2fStack;

public class HudRenderer extends HudComponent {

  private final Dimensions dim = new Dimensions();
  private final FlightComputer computer = new FlightComputer();
  private static final String FULL = DisplayMode.FULL.toString();
  private static final String MIN = DisplayMode.MIN.toString();

  private final HudComponent[] components =
      new HudComponent[] {new FlightPathIndicator(computer, dim), new LocationIndicator(dim),
          new HeadingIndicator(computer, dim), new SpeedIndicator(computer, dim),
          new AltitudeIndicator(computer, dim), new PitchIndicator(computer, dim),
          new ElytraHealthIndicator(computer, dim)};

  private void setupConfig(Minecraft client) {
    HudComponent.CONFIG = null;
    if (client.player.isFallFlying()) {
      if (FlightHud.CONFIG_SETTINGS.displayModeWhenFlying.equals(FULL)) {
        HudComponent.CONFIG = FlightHud.CONFIG_FULL;
      } else if (FlightHud.CONFIG_SETTINGS.displayModeWhenFlying.equals(MIN)) {
        HudComponent.CONFIG = FlightHud.CONFIG_MIN;
      }
    } else {
      if (FlightHud.CONFIG_SETTINGS.displayModeWhenNotFlying.equals(FULL)) {
        HudComponent.CONFIG = FlightHud.CONFIG_FULL;
      } else if (FlightHud.CONFIG_SETTINGS.displayModeWhenNotFlying.equals(MIN)) {
        HudComponent.CONFIG = FlightHud.CONFIG_MIN;
      }
    }
  }

  @Override
  public void render(GuiGraphicsExtractor g, float partial, Minecraft client) {
    setupConfig(client);

    if (HudComponent.CONFIG == null) {
      return;
    }

    try {
      Matrix3x2fStack m = g.pose();
      m.pushMatrix();

      if (HudComponent.CONFIG.scale != 1d) {
        float scale = 1 / (float) HudComponent.CONFIG.scale;
        m.scale(scale, scale);
      }

      computer.update(client, partial);
      dim.update(client);

      for (HudComponent component : components) {
        component.render(g, partial, client);
      }
      m.popMatrix();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
