package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;

/**
 * just go look at {@link xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen IScreen}
 * since all the methods are done through a mixin...
 * 
 * @author Wagyourtail
 * 
 * @since 1.0.5
 * 
 * @see xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen
 */
public class Screen extends GuiScreen {
    private final int bgStyle;
    private ChatComponentText title;
    
    public Screen(String title, boolean dirt) {
        new ChatComponentText(title);
        this.bgStyle = dirt ? 0 : 1;
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float delta) {
        if (bgStyle == 0) this.drawBackground(0);
        else if (bgStyle == 1) this.drawDefaultBackground();
        
        drawCenteredString(this.fontRendererObj, this.title.getFormattedText(), this.width / 2, 20, 0xFFFFFF);
        
        super.drawScreen(mouseX, mouseY, delta);
    }
}
