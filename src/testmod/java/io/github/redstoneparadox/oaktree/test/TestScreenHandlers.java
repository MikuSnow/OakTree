package io.github.redstoneparadox.oaktree.test;

import io.github.redstoneparadox.oaktree.util.BackingSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.feature_flags.FeatureFlags;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class TestScreenHandlers {
	public static final ScreenHandlerType<TestScreenHandler> TEST_INVENTORY_SCREEN_HANDLER;

	static {
		TEST_INVENTORY_SCREEN_HANDLER = new ScreenHandlerType<>(TestScreenHandler::new, FeatureFlags.DEFAULT_SET);
	}

	public static void init() {
		Registry.register(Registries.SCREEN_HANDLER_TYPE, new Identifier("oaktree:test"), TEST_INVENTORY_SCREEN_HANDLER);
	}

	static class TestScreenHandler extends ScreenHandler {
		public final PlayerInventory playerInventory;
		private final List<BackingSlot> backingSlots = new ArrayList<>();

		protected TestScreenHandler(int syncId, PlayerInventory playerInventory) {
			super(TEST_INVENTORY_SCREEN_HANDLER, syncId);
			this.playerInventory = playerInventory;

			for (int i = 0; i < 36; i++) addSlot(new BackingSlot(playerInventory, i));
		}

		@Override
		protected Slot addSlot(Slot slot) {
			if (slot instanceof BackingSlot) backingSlots.add((BackingSlot) slot);
			return super.addSlot(slot);
		}

		@Override
		public ItemStack quickTransfer(PlayerEntity player, int fromIndex) {
			return null;
		}

		public List<BackingSlot> getBackingSlots() {
			return new ArrayList<>(backingSlots);
		}

		@Override
		public boolean canUse(PlayerEntity player) {
			return true;
		}

		@Override
		public void close(PlayerEntity player) {
			super.close(player);
		}
	}
}
