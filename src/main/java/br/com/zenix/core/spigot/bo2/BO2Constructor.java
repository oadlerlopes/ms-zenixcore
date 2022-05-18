package br.com.zenix.core.spigot.bo2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.zenix.core.spigot.Core;
import br.com.zenix.core.spigot.manager.CoreManager;
import br.com.zenix.core.spigot.manager.management.Management;
import net.minecraft.server.v1_7_R4.IContainer;
import net.minecraft.server.v1_7_R4.TileEntity;

/**
 * Copyright (C) Guilherme Fane, all rights reserved unauthorized copying of
 * this file, via any medium is strictly prohibited proprietary and confidential
 */

public class BO2Constructor extends Management {

	private static HashSet<Location> blocksForUpdate = new HashSet<>();
	private ArenaConstructor arena;

	public BO2Constructor(CoreManager coreManager) {
		super(coreManager);
	}

	public boolean initialize() {
		this.arena = new ArenaConstructor(this);
		startUpdate();
		return true;
	}

	public ArenaConstructor getArena() {
		return arena;
	}

	public void addBlockUpdate(Location location) {
		blocksForUpdate.add(location);
	}

	public void startUpdate() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if (!blocksForUpdate.isEmpty()) {
					net.minecraft.server.v1_7_R4.World world = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();
					for (Location location : blocksForUpdate) {
						world.notify(location.getBlockX(), location.getBlockY(), location.getBlockZ());
					}
					blocksForUpdate.clear();
				}

				if (!blocksForUpdate.isEmpty()) {
					if (Bukkit.getWorld("gladiator") != null) {
						net.minecraft.server.v1_7_R4.World world = ((CraftWorld) Bukkit.getWorlds().get(1)).getHandle();
						for (Location location : blocksForUpdate) {
							world.notify(location.getBlockX(), location.getBlockY(), location.getBlockZ());
						}
						blocksForUpdate.clear();
					}
					if (Bukkit.getWorld("arena") != null) {
						net.minecraft.server.v1_7_R4.World world = ((CraftWorld) Bukkit.getWorlds().get(1)).getHandle();
						for (Location location : blocksForUpdate) {
							world.notify(location.getBlockX(), location.getBlockY(), location.getBlockZ());
						}
						blocksForUpdate.clear();
					}
				}
			}
		}.runTaskTimer(Core.getPlugin(Core.class), 1, 1);
	}

	public List<Block> spawn(Location location, File file) {
		BufferedReader reader;
		ArrayList<Block> blocks = new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.contains(",") || !line.contains(":")) {
					continue;
				}
				String[] parts = line.split(":");
				String[] coordinates = parts[0].split(",");
				String[] blockData = parts[1].split("\\.");

				setBlockFast(location.getWorld(), location.getBlockX() + Integer.valueOf(coordinates[0]),
						location.getBlockY() + Integer.valueOf(coordinates[2]),
						location.getBlockZ() + Integer.valueOf(coordinates[1]), Integer.valueOf(blockData[0]),
						blockData.length > 1 ? Byte.valueOf(blockData[1]) : 0);
				blocks.add(location.getWorld().getBlockAt(location.getBlockX() + Integer.valueOf(coordinates[0]),
						location.getBlockY() + Integer.valueOf(coordinates[2]),
						location.getBlockZ() + Integer.valueOf(coordinates[1])));
			}
			reader.close();
		} catch (Exception e) {
			getLogger().error(
					"Error to spawn the bo2file " + file.getName() + " in the location " + location.toString(), e);
		}
		return blocks;
	}

	public List<FutureBlock> load(Location location, File file) {
		BufferedReader reader;
		ArrayList<FutureBlock> blocks = new ArrayList<>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.contains(",") || !line.contains(":")) {
					continue;
				}
				String[] parts = line.split(":");
				String[] coordinates = parts[0].split(",");
				String[] blockData = parts[1].split("\\.");
				blocks.add(new FutureBlock(
						location.clone().add(Integer.valueOf(coordinates[0]), Integer.valueOf(coordinates[2]),
								Integer.valueOf(coordinates[1])),
						Integer.valueOf(blockData[0]), blockData.length > 1 ? Byte.valueOf(blockData[1]) : 0));
			}
			reader.close();
		} catch (Exception e) {
			getLogger().error("Error to load the bo2file " + file.getName() + " in the location " + location.toString(),
					e);
		}
		return blocks;
	}

	public boolean setBlockFast(World world, int x, int y, int z, int blockId, byte data) {
		if (y >= 255 || y < 0) {
			return false;
		}
		net.minecraft.server.v1_7_R4.World w = ((CraftWorld) world).getHandle();
		net.minecraft.server.v1_7_R4.Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
		boolean b = data(chunk, x & 0x0f, y, z & 0x0f, net.minecraft.server.v1_7_R4.Block.getById(blockId), data);
		addBlockUpdate(new Location(Bukkit.getWorlds().get(0), x, y, z));
		return b;
	}

	@SuppressWarnings("deprecation")
	public boolean setBlockFast(Location location, Material material, byte data) {
		return setBlockFast(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(),
				material.getId(), data);
	}

	private boolean data(net.minecraft.server.v1_7_R4.Chunk that, int i, int j, int k,
			net.minecraft.server.v1_7_R4.Block block, int l) {
		int i1 = k << 4 | i;

		if (j >= that.b[i1] - 1) {
			that.b[i1] = -999;
		}

		int j1 = that.heightMap[i1];
		net.minecraft.server.v1_7_R4.Block block1 = that.getType(i, j, k);
		int k1 = that.getData(i, j, k);

		if (block1 == block && k1 == l) {
			return false;
		} else {
			boolean flag = false;
			net.minecraft.server.v1_7_R4.ChunkSection chunksection = that.getSections()[j >> 4];

			if (chunksection == null) {
				if (block == net.minecraft.server.v1_7_R4.Blocks.AIR) {
					return false;
				}

				chunksection = that.getSections()[j >> 4] = new net.minecraft.server.v1_7_R4.ChunkSection(j >> 4 << 4,
						!that.world.worldProvider.g);
				flag = j >= j1;
			}

			int l1 = that.locX * 16 + i;
			int i2 = that.locZ * 16 + k;

			if (!that.world.isStatic) {
				block1.f(that.world, l1, j, i2, k1);
			}
			if (!(block1 instanceof IContainer)) {
				chunksection.setTypeId(i, j & 15, k, block);
			}
			if (!that.world.isStatic) {
				block1.remove(that.world, l1, j, i2, block1, k1);
			} else if (block1 instanceof IContainer && block1 != block) {
				that.world.p(l1, j, i2);
			}
			if (block1 instanceof IContainer) {
				chunksection.setTypeId(i, j & 15, k, block);
			}
			if (chunksection.getTypeId(i, j & 15, k) != block) {
				return false;
			} else {
				chunksection.setData(i, j & 15, k, l);
				if (flag) {
					that.initLighting();
				}
				TileEntity tileentity;

				if (block1 instanceof IContainer) {
					tileentity = that.e(i, j, k);
					if (tileentity != null) {
						tileentity.u();
					}
				}
				if (!that.world.isStatic && (!that.world.captureBlockStates
						|| (block instanceof net.minecraft.server.v1_7_R4.BlockContainer))) {
					block.onPlace(that.world, l1, j, i2);
				}

				if (block instanceof IContainer) {
					if (that.getType(i, j, k) != block) {
						return false;
					}
					tileentity = that.e(i, j, k);
					if (tileentity == null) {
						tileentity = ((IContainer) block).a(that.world, l);
						that.world.setTileEntity(l1, j, i2, tileentity);
					}

					if (tileentity != null) {
						tileentity.u();
					}
				}

				that.n = true;
				return true;
			}
		}
	}

	public class FutureBlock {
		private Location location;
		private int id;
		private byte data;

		public FutureBlock(Location location, int id, byte data) {
			this.location = location;
			this.id = id;
			this.data = data;
		}

		public byte getData() {
			return data;
		}

		public Location getLocation() {
			return location;
		}

		public int getId() {
			return id;
		}

		@SuppressWarnings("deprecation")
		public void place() {
			location.getBlock().setTypeIdAndData(id, data, true);
		}
	}

}
