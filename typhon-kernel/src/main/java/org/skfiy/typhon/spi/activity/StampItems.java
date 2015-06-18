package org.skfiy.typhon.spi.activity;

/**
 * @author Administrator
 *
 */
public class StampItems {
    private String costId;
    private int costCounts;
    private String itemId;
    private int itemCounts;

    public int getCostCounts() {
        return costCounts;
    }

    public void setCostCounts(int costCounts) {
        this.costCounts = costCounts;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getItemCounts() {
        return itemCounts;
    }

    public void setItemCounts(int itemCounts) {
        this.itemCounts = itemCounts;
    }

    public String getCostId() {
        return costId;
    }

    public void setCostId(String costId) {
        this.costId = costId;
    }
}
