package xyz.wagyourtail.jsmacros.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventKey;
import xyz.wagyourtail.jsmacros.client.gui.screens.BaseScreen;
import xyz.wagyourtail.jsmacros.client.tick.TickBasedEvents;

public class ForgeEventListener {

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent keyEvent) {
        new EventKey(Keyboard.getEventKey(), 0, Keyboard.getEventKeyState() ? 1 : 0, BaseScreen.createModifiers());
    }
    
    @SubscribeEvent
    public void onMouse(InputEvent.MouseInputEvent mouseEvent) {
        if (Mouse.getEventButtonState() || Mouse.getEventButton() > -1)
            new EventKey(Mouse.getEventButton()-100, 0, Mouse.getEventButtonState() ? 1 : 0, BaseScreen.createModifiers());
    }
    
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent tick) {
        if (tick.phase == TickEvent.Phase.END) {
            TickBasedEvents.onTick(Minecraft.getMinecraft());
        }
    }
}
