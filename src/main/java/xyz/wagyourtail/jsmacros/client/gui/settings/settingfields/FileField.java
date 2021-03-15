package xyz.wagyourtail.jsmacros.client.gui.settings.settingfields;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ChatComponentText;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.overlays.FileChooser;
import xyz.wagyourtail.jsmacros.client.gui.screens.BaseScreen;
import xyz.wagyourtail.jsmacros.client.gui.settings.settingcontainer.AbstractSettingContainer;
import xyz.wagyourtail.jsmacros.client.gui.settings.SettingsOverlay;
import xyz.wagyourtail.jsmacros.core.Core;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class FileField extends AbstractSettingField<String> {
    
    public FileField(int x, int y, int width, FontRenderer textRenderer, AbstractSettingContainer parent, SettingsOverlay.SettingField<String> field) {
        super(x, y, width, textRenderer.FONT_HEIGHT + 2, textRenderer, parent, field);
    }
    
    public static File getTopLevel(SettingsOverlay.SettingField<?> setting) {
        for (String option : setting.option.type().options()) {
            if (option.startsWith("topLevel=")) {
                switch (option.replace("topLevel=", "")) {
                    case "MC":
                        return Minecraft.getMinecraft().mcDataDir;
                    case "CONFIG":
                        return Core.instance.config.configFolder;
                    case "MACRO":
                    default:
                        return Core.instance.config.macroFolder;
                }
            }
        }
        //default
        return Core.instance.config.macroFolder;
    }
    
    @Override
    public void init() {
        super.init();
        try {
            this.addButton(new Button(x + width / 2, y, width / 2, height, textRenderer, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentText(setting.get()), (btn) -> {
                try {
                    parent.openOverlay(new FileChooser(parent.x, parent.y, parent.width, parent.height, textRenderer, getTopLevel(setting), new File(getTopLevel(setting), setting.get()), getFirstOverlayParent(), (file) -> {
                        try {
                            setting.set("." + file.getAbsolutePath().substring(getTopLevel(setting).getAbsolutePath().length()).replaceAll("\\\\", "/"));
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }, file -> {}));
                } catch (InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void setPos(int x, int y, int width, int height) {
        super.setPos(x, y, width, height);
        for (GuiButton btn : buttons) {
            btn.yPosition = y;
        }
    }
    
    
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        textRenderer.drawString(BaseScreen.trimmed(textRenderer, settingName.getFormattedText(), width / 2), x, y + 1, 0xFFFFFF);
    }
    
}
