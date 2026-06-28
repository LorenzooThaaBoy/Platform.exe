package io.github.platform;

import com.badlogic.gdx.math.MathUtils;
import java.util.function.Consumer;
import java.util.function.Function;

//shop model, item rolls + selected item
public class ModelSHOP {
    private static final int SHOP_SLOT_COUNT = 3;

    //item table, name + text + effect
    public enum Item {
        DAMAGE_UP("Damage Up", player -> player.canUpgradeDamage() ? "x" + player.getNextDamageMultiplier() + " dmg" : "Max dmg", ModelPLAYER::upgradeDamage),
        RANGE_UP("Range Up", player -> player.canUpgradeRange() ? "+" + (int)player.getNextRangeBonus() + " reach" : "Max range", ModelPLAYER::upgradeRange),
        HP_UP("HP Up", player -> "+1 heart, full heal", ModelPLAYER::upgradeHealth),
        SPEED_UP("Speed Up", player -> player.canUpgradeSpeed() ? "x" + player.getNextSpeedMultiplier() + " speed" : "Max speed", ModelPLAYER::upgradeSpeed),
        DASH("Dash", player -> player.canUpgradeDash() ? (int)player.getNextDashSpeed() + " dash, " + player.getNextDashDamage() + " dmg" : "Max dash", ModelPLAYER::upgradeDash),
        MAGIC_WAND("Magic Wand", player -> player.getPrimaryItem() == ModelPLAYER.PrimaryItem.MAGIC_WAND
            ? player.canUpgradeMagicOrb() ? "x" + player.getNextMagicOrbMultiplier() + " orb dmg" : "Max orb"
            : "Primary: arrow orb", ModelPLAYER::buyMagicWand),
        LIGHTNING("Lightning", player -> "Secondary: C zap", player -> player.equipSecondaryItem(ModelPLAYER.SecondaryItem.LIGHTNING)),
        SCATTER("Scatter", player -> player.hasScatter() ? "Owned passive" : "Passive death burst", ModelPLAYER::unlockScatter),
        LASER("Laser", player -> "Secondary: hold C", player -> player.equipSecondaryItem(ModelPLAYER.SecondaryItem.LASER));

        private final String displayName;
        private final Function<ModelPLAYER, String> description;
        private final Consumer<ModelPLAYER> apply;

        Item(String displayName, Function<ModelPLAYER, String> description, Consumer<ModelPLAYER> apply) {
            this.displayName = displayName;
            this.description = description;
            this.apply = apply;
        }

        String getDisplayName() {
            return displayName;
        }

        String getDescription(ModelPLAYER player) {
            return description.apply(player);
        }

        void apply(ModelPLAYER player) {
            apply.accept(player);
        }
    }

    private final Item[] itemPool = {
        Item.DAMAGE_UP,
        Item.RANGE_UP,
        Item.HP_UP,
        Item.SPEED_UP,
        Item.DASH,
        Item.MAGIC_WAND,
        Item.LIGHTNING,
        Item.SCATTER,
        Item.LASER
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

    public String getItemName(int index) {
        return items[index].getDisplayName();
    }

    public String getItemDescription(int index, ModelPLAYER player) {
        return items[index].getDescription(player);
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

    public void applySelectedItem(ModelPLAYER player) {
        items[selectedIndex].apply(player);
    }

    public void resetForShop() {
        rollItems();
        selectedIndex = 0;
        itemSelected = false;
    }

    private void rollItems() {
        //random 3, no duplicates
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
