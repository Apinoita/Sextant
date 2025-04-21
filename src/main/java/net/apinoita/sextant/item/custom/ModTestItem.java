package net.apinoita.sextant.item.custom;

import net.apinoita.sextant.screen.SextantScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.config.builder.api.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ModTestItem extends Item {

    public ServerPlayNetworkHandler networkHandler;

    public ModTestItem(Settings settings) {
        super(settings);
    }



    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        if (world.isClient()){ user.sendMessage(Text.literal("using"));}
        else { user.sendMessage(Text.literal("using1"));}

        NamedScreenHandlerFactory menuProvider = new NamedScreenHandlerFactory() {

            @Override
            public @NotNull Text getDisplayName() {
                return Text.literal("aaaa");
            }

            @Override
            public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new SextantScreenHandler(syncId, playerInventory, player.getStackInHand(player.getActiveHand()));
            }
        };

        if (!world.isClient()) {
            user.openHandledScreen(menuProvider);
            user.sendMessage(Text.literal("using2"));
        }
        else { user.sendMessage(Text.literal("using3"));
            user.openHandledScreen(menuProvider);}
        /*if(world.isClient()){
            if (itemStack.isOf(ModItems.SEXTANT)) {
                MinecraftClient.getInstance().setScreen(new SextantSreen(user, itemStack, hand, , user.getInventory(), Text.literal("aaa")));
            }
        }
        else{
            if (itemStack.isOf(ModItems.SEXTANT)) {
                if (WrittenBookItem.resolve(itemStack, user.getCommandSource(), user)) {
                    user.currentScreenHandler.sendContentUpdates();
                }
                this.networkHandler.sendPacket(new OpenWrittenBookS2CPacket(hand));
            }
        }*/

        return super.use(world, user, hand);
    }
}
