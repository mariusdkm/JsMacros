package xyz.wagyourtail.jsmacros.client.gui.screens;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import org.lwjgl.input.Keyboard;
import xyz.wagyourtail.jsmacros.client.gui.containers.RunningThreadContainer;
import xyz.wagyourtail.jsmacros.client.gui.elements.Button;
import xyz.wagyourtail.jsmacros.client.gui.elements.Scrollbar;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptThreadWrapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CancelScreen extends BaseScreen {
    private int topScroll;
    private Scrollbar s;
    private final List<RunningThreadContainer> running = new ArrayList<>();
    public CancelScreen(GuiScreen parent) {
        super(new ChatComponentText("Cancel"), parent);
    }

    @Override
    public void initGui() {
        super.initGui();
        topScroll = 10;
        running.clear();
        s = this.addButton(new Scrollbar(width - 12, 5, 8, height-10, 0, 0xFF000000, 0xFFFFFFFF, 1, this::onScrollbar));
        
        this.addButton(new Button(0, this.height - 12, this.width / 12, 12, fontRendererObj, 0, 0xFF000000, 0x7FFFFFFF, 0xFFFFFF, new ChatComponentTranslation("jsmacros.back"), (btn) -> this.onClose()));
    }

    public void addContainer(ScriptThreadWrapper t) {
        running.add(new RunningThreadContainer(10, topScroll + running.size() * 15, width - 26, 13, fontRendererObj, this, t));
        running.sort(new RTCSort());
        s.setScrollPages(running.size() * 15 / (double)(height - 20));
    }

    public void removeContainer(RunningThreadContainer t) {
        for (GuiButton b : t.getButtons()) {
            buttonList.remove(b);
        }
        running.remove(t);
        s.setScrollPages(running.size() * 15 / (double)(height - 20));
        updatePos();
    }

    private void onScrollbar(double page) {
        topScroll = 10 - (int) (page * (height - 20));
        updatePos();
    }

    public void updatePos() {
        for (int i = 0; i < running.size(); ++i) {
            if (topScroll + i * 15 < 10 || topScroll + i * 15 > height - 10) running.get(i).setVisible(false);
            else {
                running.get(i).setVisible(true);
                running.get(i).setPos(10, topScroll + i * 15, width - 26, 13);
            }
        }
    }
    
    @Override
    public boolean mouseScrolled(int mouseX, int mouseY, int amount) {
        s.mouseDragged(mouseX, mouseY, 0, 0, -amount * 2);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float delta) {
        this.drawDefaultBackground();
        List<ScriptThreadWrapper> tl = Core.instance.getThreads();
        
        for (RunningThreadContainer r : ImmutableList.copyOf(this.running)) {
            tl.remove(r.t);
            r.render(mouseX, mouseY, delta);
        }
        
        for (ScriptThreadWrapper t : tl) {
            addContainer(t);
        }

        for (GuiButton b : ImmutableList.copyOf(this.buttonList)) {
            b.drawButton(mc, mouseX, mouseY);
        }
    }
    
    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void onClose() {
        this.openParent();
    }

    public static class RTCSort implements Comparator<RunningThreadContainer> {
        @Override
        public int compare(RunningThreadContainer arg0, RunningThreadContainer arg1) {
            try {
            return arg0.t.t.getName().compareTo(arg1.t.t.getName());
            } catch(NullPointerException e) {
                return 0;
            }
        }

    }
}
