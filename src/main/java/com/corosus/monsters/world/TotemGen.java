package com.corosus.monsters.world;

import com.corosus.monsters.CommonProxy;
import com.corosus.monsters.Monsters;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Corosus on 1/15/2017.
 */
public class TotemGen implements IWorldGenerator {

    public TotemGen() {

    }



    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        //System.out.println("random = [" + random + "], chunkX = [" + chunkX + "], chunkZ = [" + chunkZ + "], world = [" + world + "], chunkGenerator = [" + chunkGenerator + "], chunkProvider = [" + chunkProvider + "]");

        List<IBlockState> listBlocks = new ArrayList<>();
        listBlocks.add(Blocks.GRASS.getDefaultState());
        listBlocks.add(Blocks.DIRT.getDefaultState());
        listBlocks.add(Blocks.SAND.getDefaultState());
        listBlocks.add(Blocks.STONE.getDefaultState());

        Random rand = new Random();

        if (chunkX % 10 == 0 && chunkZ % 10 == 0) {
            for (int i = 0; i < 10; i++) {
                BlockPos pos = new BlockPos(chunkX * 16 + 8 + rand.nextInt(14) - 7, 0, chunkZ * 16 + 8 + rand.nextInt(14) - 7);
                pos = world.getHeight(pos).add(0, -1, 0);
                IBlockState state = world.getBlockState(pos);
                //System.out.println(state);
                if (listBlocks.contains(state)) {
                    BlockPos posUp = pos.add(0, 1, 0);
                    IBlockState stateUp = world.getBlockState(posUp);
                    if (stateUp == Blocks.AIR.getDefaultState()) {
                        world.setBlockState(posUp, CommonProxy.blockTotem.getDefaultState());
                        System.out.println("genned totem");
                        break;
                    }
                }
                break;
            }
        }
    }
}
