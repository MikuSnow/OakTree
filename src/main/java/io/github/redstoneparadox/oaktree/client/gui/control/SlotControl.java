package io.github.redstoneparadox.oaktree.client.gui.control;

import io.github.redstoneparadox.oaktree.client.gui.ControlGui;
import io.github.redstoneparadox.oaktree.client.gui.style.ColorStyleBox;
import io.github.redstoneparadox.oaktree.client.gui.style.StyleBox;
import io.github.redstoneparadox.oaktree.client.gui.util.RGBAColor;
import io.github.redstoneparadox.oaktree.client.networking.OakTreeClientNetworking;
import io.github.redstoneparadox.oaktree.util.InventoryScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.BiFunction;

public class SlotControl extends InteractiveControl<SlotControl> {
    public StyleBox highlightStyle = new ColorStyleBox(new RGBAColor(0.75f, 0.75f, 0.75f, 0.5f));
    public int slotBorder = 1;

    public BiFunction<SlotControl, ItemStack, Boolean> canInsert = (slotControl, stack) -> true;
    public BiFunction<SlotControl, ItemStack, Boolean> canTake = (slotControl, stack) -> true;

    private final int slot;
    private final int inventoryID;

    public SlotControl(int slot, int inventoryID) {
        this.slot = slot;
        this.inventoryID = inventoryID;
        this.id = "item_slot";
        this.tooltip = new LabelControl().id("tooltip").resizable(true);
        this.size(18, 18);
    }

    public SlotControl highlightStyle(StyleBox highlightStyle) {
        this.highlightStyle = highlightStyle;
        return this;
    }

    public SlotControl slotBorder(int slotBorder) {
        this.slotBorder = slotBorder;
        return this;
    }

    public SlotControl canInsert(BiFunction<SlotControl, ItemStack, Boolean> canInsert) {
        this.canInsert = canInsert;
        return this;
    }

    public SlotControl filter(Item... items) {
        this.canInsert = ((slotControl, stack) -> {
            for (Item item: items) {
                if (stack.getItem() != item) return false;
            }
            return true;
        });
        return this;
    }

    public SlotControl filter(Tag<Item>... tags) {
        this.canInsert = ((slotControl, stack) -> {
            for (Tag<Item> tag: tags) {
                if (!tag.contains(stack.getItem())) return false;
            }
            return true;
        });
        return this;
    }

    public SlotControl canTake(BiFunction<SlotControl, ItemStack, Boolean> canTake) {
        this.canTake = canTake;
        return this;
    }

    @Override
    public void setup(MinecraftClient client, ControlGui gui) {
        super.setup(client, gui);
        if (tooltip != null) tooltip.setup(client, gui);
    }

    @Override
    public void preDraw(ControlGui gui, int offsetX, int offsetY, int containerWidth, int containerHeight, int mouseX, int mouseY) {
        gui.getScreenHandler().ifPresent(handler -> {
            if (handler instanceof InventoryScreenHandler) {
                super.preDraw(gui, offsetX, offsetY, containerWidth, containerHeight, mouseX, mouseY);

                PlayerEntity player = ((InventoryScreenHandler) handler).getPlayer();
                PlayerInventory playerInventory = player.inventory;
                Inventory inventory = ((InventoryScreenHandler) handler).getInventory(inventoryID);

                if (isMouseWithin && inventory != null) {
                    boolean stackChanged = false;
                    ItemStack stackInSlot = inventory.getStack(slot);

                    if (playerInventory.getCursorStack().isEmpty()) {
                        if (canTake.apply(this, stackInSlot)) {
                            if (gui.mouseButtonJustClicked("left")) {
                                playerInventory.setCursorStack(inventory.removeStack(slot));
                                stackChanged = true;
                            }
                            else if (gui.mouseButtonJustClicked("right")) {
                                playerInventory.setCursorStack(inventory.removeStack(slot, stackInSlot.getCount()/2));
                                stackChanged = true;
                            }
                        }
                    }
                    else {
                        ItemStack cursorStack = playerInventory.getCursorStack();

                        if (canInsert.apply(this, cursorStack)) {
                            if (gui.mouseButtonJustClicked("left")) {
                                if (stackInSlot.isEmpty()) {
                                    inventory.setStack(slot, playerInventory.getCursorStack());
                                    playerInventory.setCursorStack(ItemStack.EMPTY);
                                    stackChanged = true;
                                }
                                else {
                                    combineStacks(cursorStack, stackInSlot, cursorStack.getCount());
                                    stackChanged = true;
                                }
                            }
                            else if (gui.mouseButtonJustClicked("right")) {
                                if (stackInSlot.isEmpty()) {
                                    inventory.setStack(slot, playerInventory.getCursorStack().split(1));
                                    stackChanged = true;
                                }
                                else {
                                    combineStacks(cursorStack, stackInSlot, 1);
                                    stackChanged = true;
                                }
                            }
                        }
                    }

                    if (stackChanged) {
                        OakTreeClientNetworking.syncStack(slot, inventoryID, handler.syncId, inventory.getStack(slot));
                    }
                    if (!stackInSlot.isEmpty()) {
                        List<Text> texts = stackInSlot.getTooltip(player, TooltipContext.Default.NORMAL);
                        if (tooltip instanceof LabelControl) {
                            ((LabelControl) tooltip).text(texts);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void draw(MatrixStack matrices, int mouseX, int mouseY, float deltaTime, ControlGui gui) {
        gui.getScreenHandler().ifPresent(screenHandler -> {
            if (screenHandler instanceof InventoryScreenHandler) {
                super.draw(matrices, mouseX, mouseY, deltaTime, gui);
                ItemStack stack = ((InventoryScreenHandler) screenHandler).getInventory(inventoryID).getStack(slot);
                ItemRenderer renderer = MinecraftClient.getInstance().getItemRenderer();

                int itemX = (area.width - 16)/2 + trueX;
                int itemY = (area.height - 16)/2 + trueY;

                renderer.renderGuiItemIcon(stack, itemX, itemY);
                TextRenderer textRenderer = gui.getTextRenderer();
                renderer.renderGuiItemOverlay(textRenderer, stack, itemX, itemY);

                if (isMouseWithin) {
                    highlightStyle.draw(trueX + slotBorder, trueY + slotBorder, area.width - (2 * slotBorder), area.height - (2 * slotBorder), gui);
                }
            }
        });
    }

    private void combineStacks(ItemStack from, ItemStack to, int amount) {
        if (from.getCount() < amount) {
            combineStacks(from, to, from.getCount());
        }
        else if (to.getMaxCount() - to.getCount() < amount) {
            combineStacks(from, to, to.getMaxCount() - to.getCount());
        }
        else if (ItemStack.areItemsEqual(from, to)) {
            from.decrement(amount);
            to.increment(amount);
        }
    }
}
