package utils;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class EmptyWorldGenerator extends ChunkGenerator implements EmptyWorldGeneratorData {

    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, ChunkData data) {
        return data;
    }
}
