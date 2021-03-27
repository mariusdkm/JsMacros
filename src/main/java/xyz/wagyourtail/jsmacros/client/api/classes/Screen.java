package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.text.LiteralText;

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
public class Screen extends net.minecraft.client.gui.screen.Screen {
    private final int bgStyle;
    
    
    public Screen(String title, boolean dirt) {
        super(new LiteralText(title));
        this.bgStyle = dirt ? 0 : 1;
    }
    
    @Override
    public void render(int mouseX, int mouseY, float delta) {
        if (bgStyle == 0) this.renderDirtBackground(0);
        else if (bgStyle == 1) this.renderBackground(0);
        
        drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 20, 0xFFFFFF);
        
        super.render(mouseX, mouseY, delta);
    }
}
