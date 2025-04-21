package net.apinoita.sextant.screen;

import net.apinoita.sextant.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;

public class SextantScreenHandler extends ScreenHandler {

    private final Inventory inventory;

    public SextantScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf){
        this(syncId, playerInventory, buf.readItemStack());
    }

    public SextantScreenHandler(int syncId, PlayerInventory playerInventory, ItemStack itemStack) {
        super(ModScreenHandlers.SEXTANT_SCREEN_HANDLER, syncId);

        ItemStack[] array = {new ItemStack(ModItems.SEXTANT, 1)};
        SimpleInventory inventory = new SimpleInventory(array);


        this.inventory = inventory;

        addSlot(new Slot(inventory, 0, 3,3));

        addPlayerInventory(playerInventory);
        addPlayerInventory(playerInventory);

    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }



    @Override
    public boolean canUse(PlayerEntity player) {
        return false;
    }
}