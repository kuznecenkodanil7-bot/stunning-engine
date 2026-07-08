package com.yourname.baritone.gui;

import com.yourname.baritone.settings.Settings;
import com.yourname.baritone.settings.SettingsLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public final class SettingsScreen extends Screen {
    private final Screen parent;
    private final Settings settings;

    public SettingsScreen(Screen parent, Settings settings) {
        super(Text.translatable("baritone.settings"));
        this.parent = parent;
        this.settings = settings;
    }

    @Override
    protected void init() {
        int center = width / 2;
        int y = height / 4;
        addDrawableChild(ButtonWidget.builder(label("Render path", settings.renderPath), button -> {
            settings.renderPath = !settings.renderPath;
            button.setMessage(label("Render path", settings.renderPath));
            SettingsLoader.save(settings);
        }).dimensions(center - 100, y, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(label("Render goal", settings.renderGoal), button -> {
            settings.renderGoal = !settings.renderGoal;
            button.setMessage(label("Render goal", settings.renderGoal));
            SettingsLoader.save(settings);
        }).dimensions(center - 100, y + 24, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(label("Allow break", settings.allowBreak), button -> {
            settings.allowBreak = !settings.allowBreak;
            button.setMessage(label("Allow break", settings.allowBreak));
            SettingsLoader.save(settings);
        }).dimensions(center - 100, y + 48, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(label("Allow place", settings.allowPlace), button -> {
            settings.allowPlace = !settings.allowPlace;
            button.setMessage(label("Allow place", settings.allowPlace));
            SettingsLoader.save(settings);
        }).dimensions(center - 100, y + 72, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), button -> close()).dimensions(center - 100, y + 112, 200, 20).build());
    }

    private Text label(String name, boolean value) {
        return Text.literal(name + ": " + (value ? "ON" : "OFF"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 30, 0xFFFFAA00);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        if (client != null) {
            client.setScreen(parent);
        }
    }
}
