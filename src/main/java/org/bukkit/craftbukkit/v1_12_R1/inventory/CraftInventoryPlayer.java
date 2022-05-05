package org.bukkit.craftbukkit.v1_12_R1.inventory;

import com.google.common.base.Preconditions;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketSetSlot;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class CraftInventoryPlayer extends CraftInventory implements org.bukkit.inventory.PlayerInventory, EntityEquipment {
    public CraftInventoryPlayer(InventoryPlayer inventory) {
        super(inventory);
    }

    @Override
    public InventoryPlayer getInventory() {
        return (InventoryPlayer) inventory;
    }

    @Override
    public ItemStack[] getStorageContents() {
        return asCraftMirror(getInventory().mainInventory);
    }

    @Override
    public void setStorageContents(ItemStack[] items) throws IllegalArgumentException {
        setSlots(items, 0, getInventory().mainInventory.size());
    }

    @Override
    public ItemStack getItemInMainHand() {
        return CraftItemStack.asCraftMirror(getInventory().getCurrentItem());
    }

    @Override
    public void setItemInMainHand(ItemStack item) {
        setItem(getHeldItemSlot(), item);
    }

    @Override
    public ItemStack getItemInOffHand() {
        return CraftItemStack.asCraftMirror(getInventory().offHandInventory.get(0));
    }

    @Override
    public void setItemInOffHand(ItemStack item) {
        ItemStack[] extra = getExtraContents();
        extra[0] = item;
        setExtraContents(extra);
    }

    @Override
    public ItemStack getItemInHand() {
        return getItemInMainHand();
    }

    @Override
    public void setItemInHand(ItemStack stack) {
        setItemInMainHand(stack);
    }

    @Override
    public void setItem(int index, ItemStack item) {
        super.setItem(index, item);
        if (this.getHolder() == null) {
            return;
        }
        EntityPlayerMP player = ((CraftPlayer) this.getHolder()).getHandle();
        if (player.connection == null) {
            return;
        }
        if (index < InventoryPlayer.getHotbarSize()) {
            index += 36;
        } else if (index > 39) {
            index += 5; // Off hand
        } else if (index > 35) {
            index = 8 - (index - 36);
        }
        player.connection.sendPacket(new SPacketSetSlot(player.inventoryContainer.windowId, index, CraftItemStack.asNMSCopy(item)));
    }

    public int getHeldItemSlot() {
        return getInventory().currentItem;
    }

    public void setHeldItemSlot(int slot) {
        Validate.isTrue(slot >= 0 && slot < InventoryPlayer.getHotbarSize(), "Slot is not between 0 and 8 inclusive");
        this.getInventory().currentItem = slot;
        ((CraftPlayer) this.getHolder()).getHandle().connection.sendPacket(new SPacketHeldItemChange(slot));
    }

    public ItemStack getHelmet() {
        return getItem(getSize() - 2);
    }

    public void setHelmet(ItemStack helmet) {
        setItem(getSize() - 2, helmet);
    }

    public ItemStack getChestplate() {
        return getItem(getSize() - 3);
    }

    public void setChestplate(ItemStack chestplate) {
        setItem(getSize() - 3, chestplate);
    }

    public ItemStack getLeggings() {
        return getItem(getSize() - 4);
    }

    public void setLeggings(ItemStack leggings) {
        setItem(getSize() - 4, leggings);
    }

    public ItemStack getBoots() {
        return getItem(getSize() - 5);
    }

    public void setBoots(ItemStack boots) {
        setItem(getSize() - 5, boots);
    }

    public ItemStack[] getArmorContents() {
        return asCraftMirror(getInventory().armorInventory);
    }

    @Override
    public void setArmorContents(ItemStack[] items) {
        setSlots(items, getInventory().mainInventory.size(), getInventory().armorInventory.size());
    }

    private void setSlots(ItemStack[] items, int baseSlot, int length) {
        if (items == null) {
            items = new ItemStack[length];
        }
        Preconditions.checkArgument(items.length <= length, "items.length must be < %s", length);

        for (int i = 0; i < length; i++) {
            if (i >= items.length) {
                setItem(baseSlot + i, null);
            } else {
                setItem(baseSlot + i, items[i]);
            }
        }
    }

    @Override
    public ItemStack[] getExtraContents() {
        return asCraftMirror(getInventory().offHandInventory);
    }

    @Override
    public void setExtraContents(ItemStack[] items) {
        setSlots(items, getInventory().mainInventory.size() + getInventory().armorInventory.size(), getInventory().offHandInventory.size());
    }

    public int clear(int id, int data) {
        int count = 0;
        ItemStack[] items = getContents();

        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            if (item == null) {
                continue;
            }
            if (id > -1 && item.getTypeId() != id) {
                continue;
            }
            if (data > -1 && item.getData().getData() != data) {
                continue;
            }

            count += item.getAmount();
            setItem(i, null);
        }

        return count;
    }

    @Override
    public HumanEntity getHolder() {
        return (HumanEntity) inventory.getOwner();
    }

    @Override
    public float getItemInHandDropChance() {
        return getItemInMainHandDropChance();
    }

    @Override
    public void setItemInHandDropChance(float chance) {
        setItemInMainHandDropChance(chance);
    }

    @Override
    public float getItemInMainHandDropChance() {
        return 1;
    }

    @Override
    public void setItemInMainHandDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getItemInOffHandDropChance() {
        return 1;
    }

    @Override
    public void setItemInOffHandDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    public float getHelmetDropChance() {
        return 1;
    }

    public void setHelmetDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    public float getChestplateDropChance() {
        return 1;
    }

    public void setChestplateDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    public float getLeggingsDropChance() {
        return 1;
    }

    public void setLeggingsDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    public float getBootsDropChance() {
        return 1;
    }

    public void setBootsDropChance(float chance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
        Validate.noNullElements(items, "Item cannot be null");
        HashMap<Integer, ItemStack> leftover = new HashMap<>();

        /* TODO: some optimization
         *  - Create a 'firstPartial' with a 'fromIndex'
         *  - Record the lastPartial per Material
         *  - Cache firstEmpty result
         */
        boolean status = true;
        Player p = (Player) getHolder();
        int size = 0;
        if (p.hasPermission("morespace.row.6")){
            size = 6;
        } else if (p.hasPermission("morespace.row.5")){
            size = 5;
        } else if (p.hasPermission("morespace.row.4")){
            size = 4;
        } else if (p.hasPermission("morespace.row.3")){
            size = 3;
        } else if (p.hasPermission("morespace.row.2")){
            size = 2;
        } else if (p.hasPermission("morespace.row.1")){
            size = 1;
        } else {
            status = false;
            size = 1;
        }
        String data = System.getProperty("user.dir") + "/MoreSpaceData/";
        Inventory inv = Bukkit.createInventory(null,size*9,color("&7[&b更多背包&7]"));
        File file = new File(data + p.getDisplayName() + ".yml");
        if (!file.exists()){
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config.get("items")!=null){
            for (String key : config.getConfigurationSection("items").getKeys(false)){
                ItemStack is = config.getItemStack("items."+key);
                int local = Integer.parseInt(key);
                if (local<(size*9)){
                    inv.setItem(local,is);
                }
            }
        }

        for (int i = 0; i < items.length; i++) {
            ItemStack item = items[i];
            while (true) {
                // Do we already have a stack of it?
                int firstPartial = firstPartial(item);

                // Drat! no partial stack
                if (firstPartial == -1) {
                    // Find a free spot!
                    int firstFree = firstEmpty();

                    if (firstFree == -1) {
                        // No space at all!
                        if (!status){
                            leftover.put(i,item);
                        } else {
                            HashMap<Integer,ItemStack> map = inv.addItem(item);
                            if (map.size()!=0){
                                for (int a : map.keySet()){
                                    p.getWorld().dropItemNaturally(p.getLocation(),map.get(a));
                                }
                            }
                        }
                        break;
                    } else {
                        // More than a single stack!
                        if (item.getAmount() > getMaxItemStack()) {
                            CraftItemStack stack = CraftItemStack.asCraftCopy(item);
                            stack.setAmount(getMaxItemStack());
                            setItem(firstFree, stack);
                            item.setAmount(item.getAmount() - getMaxItemStack());
                        } else {
                            // Just store it
                            setItem(firstFree, item);
                            break;
                        }
                    }
                } else {
                    // So, apparently it might only partially fit, well lets do just that
                    ItemStack partialItem = getItem(firstPartial);

                    int amount = item.getAmount();
                    int partialAmount = partialItem.getAmount();
                    int maxAmount = partialItem.getMaxStackSize();

                    // Check if it fully fits
                    if (amount + partialAmount <= maxAmount) {
                        partialItem.setAmount(amount + partialAmount);
                        // To make sure the packet is sent to the client
                        setItem(firstPartial, partialItem);
                        break;
                    }

                    // It fits partially
                    partialItem.setAmount(maxAmount);
                    // To make sure the packet is sent to the client
                    setItem(firstPartial, partialItem);
                    item.setAmount(amount + partialAmount - maxAmount);
                }
            }
        }
        if (status){
            for (int i = 0;i<inv.getContents().length;i++){
                config.set("items." + i,inv.getContents()[i]);
            }
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return leftover;
    }

    private String color(String text){
        return ChatColor.translateAlternateColorCodes('&',text);
    }

    private int firstPartial(ItemStack item) {
        ItemStack[] inventory = getStorageContents();
        ItemStack filteredItem = CraftItemStack.asCraftCopy(item);
        if (item == null) {
            return -1;
        }
        for (int i = 0; i < inventory.length; i++) {
            ItemStack cItem = inventory[i];
            if (cItem != null && cItem.getAmount() < cItem.getMaxStackSize() && cItem.isSimilar(filteredItem)) {
                return i;
            }
        }
        return -1;
    }

    private int getMaxItemStack() {
        return getInventory().getInventoryStackLimit();
    }
}
