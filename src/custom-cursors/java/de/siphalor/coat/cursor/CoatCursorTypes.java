package de.siphalor.coat.cursor;

import com.mojang.blaze3d.platform.cursor.CursorType;
//- import com.mojang.blaze3d.platform.cursor.CursorTypes;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;
//- import org.lwjgl.glfw.GLFW;
//- import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.sdl.SDLMouse;
import org.lwjgl.sdl.SDLPixels;
import org.lwjgl.sdl.SDLSurface;
import org.lwjgl.sdl.SDL_Surface;
import org.lwjgl.system.MemoryUtil;
import java.nio.ByteBuffer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CoatCursorTypes {
	private static Pair<CursorType, ByteBuffer> helpCursor;

	public static CursorType helpCursor() {
		return helpCursor.getFirst();
	}

	public static void initialize() {
		helpCursor = createCursor("help");
	}

	private static Pair<CursorType, @Nullable ByteBuffer> createCursor(String name) {
		try (var is = CoatCursorTypes.class.getClassLoader().getResourceAsStream("coat/cursor/" + name + ".png")) {
			if (is == null) {
				throw new IllegalStateException("Failed to load help cursor");
			}

			var nativeImage = NativeImage.read(is);
			int[] pixels = nativeImage.getPixels();

			//# if MC_VERSION_NUMBER >= 260300
			int width = nativeImage.getWidth();
			int height = nativeImage.getHeight();

			// Allocate native memory that SDL can use
			ByteBuffer pixelBuffer = MemoryUtil.memAlloc(pixels.length * 4);
			pixelBuffer.asIntBuffer().put(pixels);

			SDL_Surface surface = SDLSurface.SDL_CreateSurfaceFrom(
					width,
					height,
					SDLPixels.SDL_PIXELFORMAT_ARGB8888,
					pixelBuffer,
					width * 4
			);
			if (surface == null) {
				MemoryUtil.memFree(pixelBuffer);
				throw new IllegalStateException("Failed to create SDL surface for cursor " + name);
			}
			long helpCursorHandle = SDLMouse.SDL_CreateColorCursor(surface, 0, 0);
			SDLSurface.SDL_DestroySurface(surface);

			//# else
			//- var glfwImage = GLFWImage.create();

			//- // Allocate native memory that GLFW can use
			//- ByteBuffer pixelBuffer = MemoryUtil.memAlloc(pixels.length * 4);
			//- pixelBuffer.asIntBuffer().put(pixels);

			//- glfwImage.set(nativeImage.getWidth(), nativeImage.getHeight(), pixelBuffer);
			//- long helpCursorHandle = GLFW.glfwCreateCursor(glfwImage, 0, 0);
			//# end

			return Pair.of(new CursorType("coat:" + name, helpCursorHandle), pixelBuffer);
		} catch (Exception e) {
			log.error("Failed to load help cursor", e);
			return Pair.of(CursorType.DEFAULT, null);
		}
	}
}
