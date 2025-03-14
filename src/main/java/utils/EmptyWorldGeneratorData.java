package utils;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public interface EmptyWorldGeneratorData {
    ChunkGenerator.ChunkData generateChunkData(World world, Random random, int x, int z, ChunkGenerator.ChunkData data);
}
