package net.apinoita.FailedScreenTryAgainSometime.item.custom;

import net.apinoita.FailedScreenTryAgainSometime.screen.SextantScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModTestItem extends Item implements ExtendedScreenHandlerFactory {

    public ServerPlayNetworkHandler networkHandler;

    public ModTestItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, @NotNull PlayerEntity user, Hand hand) {

        NamedScreenHandlerFactory menuProvider = new NamedScreenHandlerFactory() {

            @Override
            public @NotNull ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new SextantScreenHandler(syncId, playerInventory, player.getStackInHand(player.getActiveHand()));
            }

            @Override
            public Text getDisplayName() {
                return Text.literal("moi");
            }
        };

        user.openHandledScreen(menuProvider);

        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBoolean(true);
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("www");
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SextantScreenHandler(syncId, playerInventory,player.getStackInHand(player.getActiveHand()));
    }
}
