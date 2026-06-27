package io.github.some_example_name;

import com.badlogic.gdx.math.MathUtils;

public class ModelSHOP {
    private static final int SHOP_SLOT_COUNT = 3;

    public enum Item { // all da items cash money
        DAMAGE_UP,
        RANGE_UP,
        HP_UP,
        SPEED_UP,
        DASH,
        MAGIC_HAT,
        LIGHTNING,
        SCATTER,
        BRIMSTONE
    }

    private final Item[] itemPool = {
        Item.DAMAGE_UP,
        Item.RANGE_UP,
        Item.HP_UP,
        Item.SPEED_UP,
        Item.DASH,
        Item.MAGIC_HAT,
        Item.LIGHTNING,
        Item.SCATTER,
        Item.BRIMSTONE
    };
    private final Item[] items = new Item[SHOP_SLOT_COUNT];
    private int selectedIndex;
    private boolean itemSelected;

    public ModelSHOP() {
        rollItems();
    }

    public Item[] getItems() {
        return items;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public String getSelectedItem() {
        return getItemName(selectedIndex);
    }

    public String getItemName(int index) { //item names 
        switch (items[index]) {
            case DAMAGE_UP:
                return "Damage Up";
            case RANGE_UP:
                return "Range Up";
            case HP_UP:
                return "HP Up";
            case SPEED_UP:
                return "Speed Up";
            case DASH:
                return "Dash";
            case MAGIC_HAT:
                return "Magic Hat";
            case LIGHTNING:
                return "Lightning";
            case SCATTER:
                return "Scatter";
            case BRIMSTONE:
                return "Brimstone";
            default:
                return "";
        }
    }

    public String getItemDescription(int index, ModelPLAYER player) { //player advice for items 
        switch (items[index]) {
            case DAMAGE_UP:
                return player.canUpgradeDamage() ? "x" + player.getNextDamageMultiplier() + " dmg" : "Max dmg";
            case RANGE_UP:
                return player.canUpgradeRange() ? "+" + (int)player.getNextRangeBonus() + " reach" : "Max range";
            case HP_UP:
                return "+1 heart, full heal";
            case SPEED_UP:
                return player.canUpgradeSpeed() ? "x" + player.getNextSpeedMultiplier() + " speed" : "Max speed";
            case DASH:
                return player.canUpgradeDash() ? (int)player.getNextDashSpeed() + " dash, " + player.getNextDashDamage() + " dmg" : "Max dash";
            case MAGIC_HAT:
                return "Primary: arrow orb";
            case LIGHTNING:
                return "Secondary: C zap";
            case SCATTER:
                return player.hasScatter() ? "Owned passive" : "Passive death burst";
            case BRIMSTONE:
                return "Secondary: hold C";
            default:
                return "";
        }
    }

    public boolean isItemSelected() {
        return itemSelected;
    }

    public void selectPrevious() {
        if (itemSelected) return;
        selectedIndex = (selectedIndex + items.length - 1) % items.length;
    }

    public void selectNext() {
        if (itemSelected) return;
        selectedIndex = (selectedIndex + 1) % items.length;
    }

    public void selectItem() {
        itemSelected = true;
    }

    public void applySelectedItem(ModelPLAYER player) { // BAAADDDDD DSWICTH STATMENTSSSSSS
        switch (items[selectedIndex]) {
            case DAMAGE_UP:
                player.upgradeDamage();
                break;
            case RANGE_UP:
                player.upgradeRange();
                break;
            case HP_UP:
                player.upgradeHealth();
                break;
            case SPEED_UP:
                player.upgradeSpeed();
                break;
            case DASH:
                player.upgradeDash();
                break;
            case MAGIC_HAT:
                player.equipPrimaryItem(ModelPLAYER.PrimaryItem.MAGIC_HAT);
                break;
            case LIGHTNING:
                player.equipSecondaryItem(ModelPLAYER.SecondaryItem.LIGHTNING);
                break;
            case SCATTER:
                player.unlockScatter();
                break;
            case BRIMSTONE:
                player.equipSecondaryItem(ModelPLAYER.SecondaryItem.BRIMSTONE);
                break;
            default:
                break;
        }
    }

    public void resetForShop() {
        rollItems();
        selectedIndex = 0;
        itemSelected = false;
    }

    private void rollItems() {
        Item[] availableItems = itemPool.clone();
        for (int i = 0; i < items.length; i++) {
            int rolledIndex = MathUtils.random(i, availableItems.length - 1);
            Item rolledItem = availableItems[rolledIndex];
            availableItems[rolledIndex] = availableItems[i];
            availableItems[i] = rolledItem;
            items[i] = rolledItem;
        }
    }
}
