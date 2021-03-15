package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.village.MerchantRecipe;
import xyz.wagyourtail.jsmacros.client.access.IMerchantScreen;
import xyz.wagyourtail.jsmacros.client.api.helpers.TradeOfferHelper;

import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class VillagerInventory extends Inventory<GuiMerchant> {
    
    protected VillagerInventory(GuiMerchant inventory) {
        super(inventory);
    }
    
    /**
    *  select the trade by it's index
    *
     * @param index
     *
     * @return self for chaining
     */
    public VillagerInventory selectTrade(int index) {
        ((IMerchantScreen)inventory).selectIndex(index);
        return this;
    }
    
    /**
     * @return
     */
    public int getExperience() {
        return 0;
    }
    
    /**
     * @return
     */
    public int getLevelProgress() {
        return 0;
    }
    
    /**
     * @return
     */
    public int getMerchantRewardedExperience() {
        return 0;
    }
    
    /**
     * @return
     */
    public boolean canRefreshTrades() {
        return false;
    }
    
    /**
     * @return
     */
    public boolean isLeveled() {
        return false;
    }
    
    /**
     * @return list of trade offers
     */
    public List<TradeOfferHelper> getTrades() {
        List<TradeOfferHelper> offers = new LinkedList<>();
        int i = -1;
        for (MerchantRecipe offer : inventory.getMerchant().getRecipes(mc.thePlayer)) {
            offers.add(new TradeOfferHelper(offer, ++i, this));
        }
        return offers;
    }
}
