package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.gui.screen.ingame.EnchantingScreen;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;

public class EnchantInventory extends Inventory<EnchantingScreen> {
    
    protected EnchantInventory(EnchantingScreen inventory) {
        super(inventory);
    }
    
    /**
     * @return xp level required to do enchantments
     */
    public int[] getRequiredLevels() {
        return inventory.getContainer().enchantmentPower;
    }
    
    /**
     * @return list of enchantments text.
     */
    public TextHelper[] getEnchantments() {
        TextHelper[] enchants = new TextHelper[3];
        for (int j = 0; j < 3; ++j) {
            Enchantment enchantment = Enchantment.byRawId(inventory.getContainer().enchantmentId[j]);
            if ((enchantment) != null) {
                enchants[j] = new TextHelper(enchantment.getName(inventory.getContainer().enchantmentLevel[j]));
            }
        }
        return enchants;
    }
    
    /**
     * @return id for enchantments
     */
    public String[] getEnchantmentIds() {
        String[] enchants = new String[3];
        for (int j = 0; j < 3; ++j) {
            Enchantment enchantment = Enchantment.byRawId(inventory.getContainer().enchantmentId[j]);
            if ((enchantment) != null) {
                enchants[j] = Registry.ENCHANTMENT.getId(enchantment).toString();
            }
        }
        return enchants;
    }
    
    /**
     * @return level of enchantments
     */
    public int[] getEnchantmentLevels() {
        return inventory.getContainer().enchantmentLevel;
    }
    
    /**
    *  clicks the button to enchant.
    *
     * @param index
     * @return success
     */
    public boolean doEnchant(int index) {
        assert mc.interactionManager != null;
        if (inventory.getContainer().onButtonClick(mc.player, index)) {
            mc.interactionManager.clickButton(syncId, index);
            return true;
        }
        return false;
    }
    
}
