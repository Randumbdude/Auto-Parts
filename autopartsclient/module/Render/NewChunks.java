package autopartsclient.module.Render;

import java.awt.Color;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import autopartsclient.module.Mod;
import autopartsclient.util.ColorUtil;
import autopartsclient.util.Render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class NewChunks extends Mod {
	private static MinecraftClient mc = MinecraftClient.getInstance();

	private int yOffset = 0;
	private static final Direction[] skipDirs = new Direction[] { Direction.DOWN, Direction.EAST, Direction.NORTH,
			Direction.WEST, Direction.SOUTH };

	private static Set<ChunkPos> newChunks = Collections.synchronizedSet(new HashSet<>());
	private static Set<ChunkPos> oldChunks = Collections.synchronizedSet(new HashSet<>());

	public NewChunks() {
		super("NewChunks", "", Category.RENDER);
	}

	@Override
	public void onDisable() {
		newChunks.clear();
		oldChunks.clear();

		super.onDisable();
	}
	
	public static void readpacket(Packet<?> packeter) {
		Direction[] searchDirs = new Direction[] { Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH,
				Direction.UP };
		if (packeter instanceof ChunkDeltaUpdateS2CPacket) {
			ChunkDeltaUpdateS2CPacket packet = (ChunkDeltaUpdateS2CPacket) packeter;
			packet.visitUpdates((pos, state) -> {
				if (!state.getFluidState().isEmpty() && !state.getFluidState().isStill()) {
					ChunkPos chunkPos = new ChunkPos(pos);

					for (Direction dir : searchDirs) {
						if (mc.world.getBlockState(pos.offset(dir)).getFluidState().isStill()
								&& !oldChunks.contains(chunkPos)) {
							newChunks.add(chunkPos);
							return;
						}
					}
				}
			});
		} else if (packeter instanceof BlockUpdateS2CPacket) {
			BlockUpdateS2CPacket packet = (BlockUpdateS2CPacket) packeter;

			if (!packet.getState().getFluidState().isEmpty() && !packet.getState().getFluidState().isStill()) {
				ChunkPos chunkPos = new ChunkPos(packet.getPos());

				for (Direction dir : searchDirs) {
					if (mc.world.getBlockState(packet.getPos().offset(dir)).getFluidState().isStill()
							&& !oldChunks.contains(chunkPos)) {
						newChunks.add(chunkPos);
						return;
					}
				}
			}
		}
	}

	@Override
	public void render(MatrixStack matrixStack, float partialTicks) {
		int renderY = (int) (mc.world.getBottomY() + yOffset);

		int color = Color.RED.getRGB();
		// QuadColor outlineColor = QuadColor.single(0xff000000 | color);

		synchronized (newChunks) {
			for (ChunkPos c : newChunks) {
				if (mc.getCameraEntity().getBlockPos().isWithinDistance(c.getStartPos(), 1024)) {
					Box box = new Box(c.getStartX(), renderY, c.getStartZ(), c.getStartX() + 16, renderY,
							c.getStartZ() + 16);
					// BoxUtil.drawBoxOutline(matrixStack, box, outlineColor, 2f, skipDirs);
					// ChatUtils.message("Generating new chunks");
					// RenderUtils.drawWaypoint(matrixStack, new Vec3d(box.maxX, box.maxY,
					// box.maxZ), new ColorUtil(255, 0,0));

					Vec3d start = new Vec3d(c.getStartX(), renderY, c.getStartZ());
					Vec3d end = new Vec3d(c.getStartX() + 16, renderY, c.getStartZ() + 16);

					RenderUtils.drawNewChunks(matrixStack, start, end, new ColorUtil(255, 0, 0));
				}
			}

		}
	}
}
