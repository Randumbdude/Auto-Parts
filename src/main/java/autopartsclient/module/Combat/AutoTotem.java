package autopartsclient.module.Combat;

import java.awt.Color;

import autopartsclient.module.Mod;
import autopartsclient.module.settings.BooleanSetting;
import autopartsclient.util.Player.Inventory.FindItemResult;
import autopartsclient.util.Player.Inventory.InventoryUtils;
import net.minecraft.item.Items;

public class AutoTotem extends Mod{
	public BooleanSetting lock = new BooleanSetting("Lock", false);
	public BooleanSetting smart = new BooleanSetting("Smart", true);
	
	int totems;
	private boolean locked;
	
	public AutoTotem() {
		super("AutoTotem", "", Category.COMBAT);
		addSetting(lock,smart);
	}
	
	@Override
	public void onTick() {
		FindItemResult result = InventoryUtils.find(Items.TOTEM_OF_UNDYING);
        totems = result.getCount();
        //this.setDisplayName("AutoTotem " + Color.GRAY.getRGB() + totems);
		if (mc.player.getInventory().contains(Items.TOTEM_OF_UNDYING.getDefaultStack())) {
			if (this.lock.isEnabled()) this.locked = true;
			if (mc.player.getOffHandStack().getItem() != Items.TOTEM_OF_UNDYING) {
				if (!smart.isEnabled()) {
					this.locked = true;
					InventoryUtils.move().from(result.getSlot()).to(InventoryUtils.OFFHAND);
				} else {
					if (mc.player.getHealth() < 15 || mc.player.isFallFlying() || mc.player.fallDistance > 6) {
						this.locked = true;
						InventoryUtils.move().from(result.getSlot()).to(InventoryUtils.OFFHAND);
					} else {
						this.locked = false;
					}
				}
			}
			if (!smart.isEnabled()) {
				this.locked = true;
			} else {
				if (mc.player.getHealth() < 10 || mc.player.isFallFlying() || mc.player.fallDistance > 6) {
					this.locked = true;
				} else {
					this.locked = false;
				}
			}
		}
		super.onTick();
	}
	
	public boolean isLocked() {
		return locked;
	}
}
