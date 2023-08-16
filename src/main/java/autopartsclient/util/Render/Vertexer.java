package autopartsclient.util.Render;

import org.apache.commons.lang3.ArrayUtils;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Vertexer {
	public static void vertexBoxLines(MatrixStack matrices, VertexConsumer vertexConsumer, Box box, QuadColor quadColor, Direction... excludeDirs) {
		float x1 = (float) box.minX;
		float y1 = (float) box.minY;
		float z1 = (float) box.minZ;
		float x2 = (float) box.maxX;
		float y2 = (float) box.maxY;
		float z2 = (float) box.maxZ;

		boolean exDown = ArrayUtils.contains(excludeDirs, Direction.DOWN);
		boolean exWest = ArrayUtils.contains(excludeDirs, Direction.WEST);
		boolean exEast = ArrayUtils.contains(excludeDirs, Direction.EAST);
		boolean exNorth = ArrayUtils.contains(excludeDirs, Direction.NORTH);
		boolean exSouth = ArrayUtils.contains(excludeDirs, Direction.SOUTH);
		boolean exUp = ArrayUtils.contains(excludeDirs, Direction.UP);

		int[] color = quadColor.getAllColors();

		if (!exDown) {
			vertexLine(matrices, vertexConsumer, x1, y1, z1, x2, y1, z1, LineColor.single(color[0], color[1], color[2], color[3]));
			vertexLine(matrices, vertexConsumer, x2, y1, z1, x2, y1, z2, LineColor.single(color[4], color[5], color[6], color[7]));
			vertexLine(matrices, vertexConsumer, x2, y1, z2, x1, y1, z2, LineColor.single(color[8], color[9], color[10], color[11]));
			vertexLine(matrices, vertexConsumer, x1, y1, z2, x1, y1, z1, LineColor.single(color[12], color[13], color[14], color[15]));
		}

		if (!exWest) {
			if (exDown) vertexLine(matrices, vertexConsumer, x1, y1, z1, x1, y1, z2, LineColor.single(color[0], color[1], color[2], color[3]));
			vertexLine(matrices, vertexConsumer, x1, y1, z2, x1, y2, z2, LineColor.single(color[4], color[5], color[6], color[7]));
			vertexLine(matrices, vertexConsumer, x1, y1, z1, x1, y2, z1, LineColor.single(color[8], color[9], color[10], color[11]));
			if (exUp) vertexLine(matrices, vertexConsumer, x1, y2, z1, x1, y2, z2, LineColor.single(color[12], color[13], color[14], color[15]));
		}

		if (!exEast) {
			if (exDown) vertexLine(matrices, vertexConsumer, x2, y1, z1, x2, y1, z2, LineColor.single(color[0], color[1], color[2], color[3]));
			vertexLine(matrices, vertexConsumer, x2, y1, z2, x2, y2, z2, LineColor.single(color[4], color[5], color[6], color[7]));
			vertexLine(matrices, vertexConsumer, x2, y1, z1, x2, y2, z1, LineColor.single(color[8], color[9], color[10], color[11]));
			if (exUp) vertexLine(matrices, vertexConsumer, x2, y2, z1, x2, y2, z2, LineColor.single(color[12], color[13], color[14], color[15]));
		}

		if (!exNorth) {
			if (exDown) vertexLine(matrices, vertexConsumer, x1, y1, z1, x2, y1, z1, LineColor.single(color[0], color[1], color[2], color[3]));
			if (exEast) vertexLine(matrices, vertexConsumer, x2, y1, z1, x2, y2, z1, LineColor.single(color[4], color[5], color[6], color[7]));
			if (exWest) vertexLine(matrices, vertexConsumer, x1, y1, z1, x1, y2, z1, LineColor.single(color[8], color[9], color[10], color[11]));
			if (exUp) vertexLine(matrices, vertexConsumer, x1, y2, z1, x2, y2, z1, LineColor.single(color[12], color[13], color[14], color[15]));
		}

		if (!exSouth) {
			if (exDown) vertexLine(matrices, vertexConsumer, x1, y1, z2, x2, y1, z2, LineColor.single(color[0], color[1], color[2], color[3]));
			if (exEast) vertexLine(matrices, vertexConsumer, x2, y1, z2, x2, y2, z2, LineColor.single(color[4], color[5], color[6], color[7]));
			if (exWest) vertexLine(matrices, vertexConsumer, x1, y1, z2, x1, y2, z2, LineColor.single(color[8], color[9], color[10], color[11]));
			if (exUp) vertexLine(matrices, vertexConsumer, x1, y2, z2, x2, y2, z2, LineColor.single(color[12], color[13], color[14], color[15]));
		}

		if (!exUp) {
			vertexLine(matrices, vertexConsumer, x1, y2, z1, x2, y2, z1, LineColor.single(color[0], color[1], color[2], color[3]));
			vertexLine(matrices, vertexConsumer, x2, y2, z1, x2, y2, z2, LineColor.single(color[4], color[5], color[6], color[7]));
			vertexLine(matrices, vertexConsumer, x2, y2, z2, x1, y2, z2, LineColor.single(color[8], color[9], color[10], color[11]));
			vertexLine(matrices, vertexConsumer, x1, y2, z2, x1, y2, z1, LineColor.single(color[12], color[13], color[14], color[15]));
		}
	}
	
	public static void vertexLine(MatrixStack matrices, VertexConsumer vertexConsumer, float x1, float y1, float z1, float x2, float y2, float z2, LineColor lineColor) {
		Matrix4f model = matrices.peek().getPositionMatrix();
		Matrix3f normal = matrices.peek().getNormalMatrix();

		Vec3d normalVec = getNormal(normal, x1, y1, z1, x2, y2, z2);

		int[] color1 = lineColor.getColor(x1, y1, z1, 0);
		int[] color2 = lineColor.getColor(x2, y2, z2, 1);

		vertexConsumer.vertex(model, x1, y1, z1).color(color1[0], color1[1], color1[2], color1[3]).normal(normal, (float)normalVec.getX(), (float)normalVec.getY(), (float)normalVec.getZ()).next();
		vertexConsumer.vertex(model, x2, y2, z2).color(color2[0], color2[1], color2[2], color2[3]).normal(normal, (float)normalVec.getX(), (float)normalVec.getY(), (float)normalVec.getZ()).next();
	}

	public static Vec3d getNormal(Matrix3f normal, float x1, float y1, float z1, float x2, float y2, float z2) {
		float xNormal = x2 - x1;
		float yNormal = y2 - y1;
		float zNormal = z2 - z1;
		float normalSqrt = MathHelper.sqrt(xNormal * xNormal + yNormal * yNormal + zNormal * zNormal);

		return new Vec3d(xNormal / normalSqrt, yNormal / normalSqrt, zNormal / normalSqrt);
	}
}