package net.apinoita.sextant.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class InventoryTestItem extends Item {


    public InventoryTestItem(Settings settings) {
        super(settings);
    }

    private final SimpleInventory itemInventory = new SimpleInventory(1) {
    };

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack sextantStack = user.getStackInHand(hand);

        fromNBT(sextantStack);
        if(!world.isClient()) {

            if (itemInventory.getStack(0).isEmpty()) {
                //if (!(user.getInventory().getStack(1).getItem() == Items.SPYGLASS)){
                if (!user.getInventory().getStack(0).isEmpty()) {
                    itemInventory.setStack(0, user.getInventory().getStack(1));
                    user.getInventory().removeStack(0);
                    user.sendMessage(Text.literal("nomnomnom"));
                }
            }
            else {
                user.getInventory().offerOrDrop(itemInventory.getStack(0));
                itemInventory.removeStack(0);
                user.sendMessage(Text.literal("gbrlbgbm"));
            }
            toNBT(sextantStack);
        }

        /*
                int itemOverflowAmount = user.getOffHandStack().getCount()+itemInventory.getStack(1).getCount() - itemInventory.getMaxCountPerStack();
                itemInventory.setStack((itemOverflowAmount < 0) ? itemInventory.getMaxCountPerStack()+itemOverflowAmount :  itemInventory.getMaxCountPerStack(), new ItemStack(user.getOffHandStack().getItem()));
                 */
        return super.use(world, user, hand);
    }

    public void toNBT(ItemStack stack){
        NbtCompound NbtData = new NbtCompound();
        NbtData.put("sextant.test.spyglass", itemInventory.toNbtList());
        stack.setNbt(NbtData);
    }

    public void fromNBT(ItemStack stack) {
        if (stack.hasNbt()) {
            if (stack.getNbt().getList("sextant.test.spyglass",10) != null)
                itemInventory.readNbtList(stack.getNbt().getList("sextant.test.spyglass", 10));
        }
    }
}
