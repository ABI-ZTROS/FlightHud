package net.torocraft.flighthud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.platform.InputConstants;
import net.torocraft.flighthud.config.HudConfig;
import net.torocraft.flighthud.config.SettingsConfig;
import net.torocraft.flighthud.config.loader.ConfigLoader;
import org.lwjgl.glfw.GLFW;

public class FlightHud implements ClientModInitializer {
  public static final String MODID = "flighthud";

  public static SettingsConfig CONFIG_SETTINGS = new SettingsConfig();
  public static HudConfig CONFIG_MIN = new HudConfig();
  public static HudConfig CONFIG_FULL = new HudConfig();

  public static ConfigLoader<SettingsConfig> CONFIG_LOADER_SETTINGS = new ConfigLoader<>(
      new SettingsConfig(), FlightHud.MODID + ".settings.json",
      config -> FlightHud.CONFIG_SETTINGS = config);

  public static ConfigLoader<HudConfig> CONFIG_LOADER_FULL = new ConfigLoader<>(
      new HudConfig(), FlightHud.MODID + ".full.json",
      config -> FlightHud.CONFIG_FULL = config);

  public static ConfigLoader<HudConfig> CONFIG_LOADER_MIN = new ConfigLoader<>(
      HudConfig.getDefaultMinSettings(), FlightHud.MODID + ".min.json",
      config -> FlightHud.CONFIG_MIN = config);

  private static final HudRenderer hudRenderer = new HudRenderer();

  private static KeyMapping keyMapping;

  @Override
  public void onInitializeClient() {
    CONFIG_LOADER_SETTINGS.load();
    CONFIG_LOADER_FULL.load();
    CONFIG_LOADER_MIN.load();
    setupKeyCode();
    setupCommand();
    setupHudRendering();
  }

  private static void setupHudRendering() {
    HudElementRegistry.attachElementBefore(
        VanillaHudElements.CHAT,
        Identifier.fromNamespaceAndPath(MODID, "flight_hud"),
        (graphics, deltaTracker) -> {
          float partial = deltaTracker.getGameTimeDeltaPartialTick(false);
          hudRenderer.render(graphics, partial, Minecraft.getInstance());
        });
  }

  private static void setupKeyCode() {
    keyMapping = new KeyMapping("key.flighthud.toggleDisplayMode",
        InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_GRAVE_ACCENT,
        "category.flighthud.toggleDisplayMode");

    KeyBindingHelper.registerKeyBinding(keyMapping);

    ClientTickEvents.END_CLIENT_TICK.register(client -> {
      while (keyMapping.wasPressed()) {
        CONFIG_SETTINGS.toggleDisplayMode();
      }
    });
  }

  private static void setupCommand() {
    ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
      dispatcher.register(ClientCommandManager.literal("flighthud")
          .then(ClientCommandManager.literal("toggle").executes(new SwitchDisplayModeCommand())));
    });
  }
}
