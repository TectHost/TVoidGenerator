package utils;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.World;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class FlatWorldGenerator extends ChunkGenerator {

    @Override
    public @NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int chunkX, int chunkZ, @NotNull BiomeGrid biome) {
        ChunkData chunkData = createChunkData(world);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunkData.setBlock(x, 3, z, Material.GRASS_BLOCK);
                chunkData.setBlock(x, 2, z, Material.DIRT);
                chunkData.setBlock(x, 1, z, Material.DIRT);
                chunkData.setBlock(x, 0, z, Material.BEDROCK);
            }
        }

        return chunkData;
    }
}
