package net.apinoita.sextant.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;


public class SextantScreen extends HandledScreen<SextantScreenHandler> {

    private static final Identifier TEXTURE = new Identifier("textures/gui/container/sextantgui.png");

    protected PlayerEntity player;
    protected ItemStack itemStack;
    protected Hand hand;
    protected PlayerInventory inventory;

    public SextantScreen(SextantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        NbtCompound nbtCompound = itemStack.getNbt();

    }

    @Override
    protected void init() {
        super.init();
        int titleY = 1000;
        int inventoryY = 1000;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        x = backgroundWidth/2;
        y = backgroundHeight/2;


        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f,1f,1f,1f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        context.drawTexture(TEXTURE,x,y,0,0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}

