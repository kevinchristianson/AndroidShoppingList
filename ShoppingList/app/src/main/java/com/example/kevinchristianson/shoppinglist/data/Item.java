package com.example.kevinchristianson.shoppinglist.data;

import com.example.kevinchristianson.shoppinglist.R;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Item extends RealmObject {

    public enum ItemType {
        FOOD (0, R.drawable.food_icon),
        HOUSEHOLD (1, R.drawable.household_icon),
        OFFICE (2, R.drawable.office_icon);

        // set types based on arrays.xml?

        private int value;
        private int icon;

        ItemType(int value, int icon) {
            this.value = value;
            this.icon = icon;
        }

        public int getValue() {
            return value;
        }

        public int getIcon() {
            return icon;
        }

        public static Item.ItemType fromInt(int value) {
            for (Item.ItemType i : Item.ItemType.values()) {
                if (i.value == value) {
                    return i;
                }
            }
            return FOOD;
        }
    }

    @PrimaryKey
    private String id;
    private String name;
    private String description;
    private int type;
    private float price;
    private boolean isBought;

    public Item() {}

    public Item(String name, String description, long price, boolean isBought) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.isBought = isBought;
    }

    public String getId() {
        return id;
    }

    public ItemType getType() {
        return ItemType.fromInt(type);
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public float getPrice() {
        return price;
    }

    public boolean isBought() {
        return isBought;
    }
}
