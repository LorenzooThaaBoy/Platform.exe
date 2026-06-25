package io.github.some_example_name;

public class ModelSHOP {
    private final String[] items = {"Item 1", "Item 2", "Item 3"};
    private int selectedIndex;
    private boolean itemSelected;

    public String[] getItems() {
        return items;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public String getSelectedItem() {
        return items[selectedIndex];
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

    public void resetForShop() {
        selectedIndex = 0;
        itemSelected = false;
    }
}
