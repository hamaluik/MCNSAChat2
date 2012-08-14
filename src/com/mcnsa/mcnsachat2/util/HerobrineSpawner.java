package com.mcnsa.mcnsachat2.util;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
//import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.mcnsa.mcnsachat2.MCNSAChat2;

public class HerobrineSpawner implements Runnable {
	// our class
	@SuppressWarnings("unused")
	private MCNSAChat2 plugin;
	
	// our RNG
	private Random random = null;
	
	// keep track of blocks that don't count as being in the way
	// TODO: implement this later?
	private ArrayList<Material> transparentBlocks = new ArrayList<Material>();
	
	// keep track of everyone who has Herobrine enabled
	private ArrayList<Player> playersWithMod = new ArrayList<Player>();
	
	// what radius should Herobrine spawn at?
	private double radius = 32d;
	private double randomAmount = 8d;
	
	public HerobrineSpawner(MCNSAChat2 instance) {
		plugin = instance;
		
		// initialize things
		random = new Random();
		
		// the transparent blocks list
		transparentBlocks.add(Material.AIR);
		transparentBlocks.add(Material.BED);
		transparentBlocks.add(Material.BREWING_STAND);
		transparentBlocks.add(Material.CACTUS);
		transparentBlocks.add(Material.CAKE_BLOCK);
		transparentBlocks.add(Material.CAULDRON);
		transparentBlocks.add(Material.CHEST);
		transparentBlocks.add(Material.DEAD_BUSH);
		transparentBlocks.add(Material.DETECTOR_RAIL);
		transparentBlocks.add(Material.DIODE_BLOCK_OFF);
		transparentBlocks.add(Material.DIODE_BLOCK_ON);
		transparentBlocks.add(Material.DRAGON_EGG);
		transparentBlocks.add(Material.ENCHANTMENT_TABLE);
		transparentBlocks.add(Material.FENCE);
		transparentBlocks.add(Material.FENCE_GATE);
		transparentBlocks.add(Material.FIRE);
		transparentBlocks.add(Material.GLASS);
		transparentBlocks.add(Material.ICE);
		transparentBlocks.add(Material.IRON_DOOR_BLOCK);
		transparentBlocks.add(Material.LADDER);
		transparentBlocks.add(Material.LEAVES);
		transparentBlocks.add(Material.LEVER);
		transparentBlocks.add(Material.LONG_GRASS);
		transparentBlocks.add(Material.MELON_STEM);
		transparentBlocks.add(Material.MOB_SPAWNER);
		transparentBlocks.add(Material.NETHER_FENCE);
		transparentBlocks.add(Material.NETHER_WARTS);
		transparentBlocks.add(Material.NETHER_STALK);
		transparentBlocks.add(Material.PAINTING);
		transparentBlocks.add(Material.PISTON_EXTENSION);
		transparentBlocks.add(Material.PORTAL);
		transparentBlocks.add(Material.POWERED_RAIL);
		transparentBlocks.add(Material.PUMPKIN_STEM);
		transparentBlocks.add(Material.RAILS);
		transparentBlocks.add(Material.RED_ROSE);
		transparentBlocks.add(Material.REDSTONE);
		transparentBlocks.add(Material.REDSTONE_TORCH_OFF);
		transparentBlocks.add(Material.REDSTONE_TORCH_ON);
		transparentBlocks.add(Material.REDSTONE_WIRE);
		transparentBlocks.add(Material.SAPLING);
		transparentBlocks.add(Material.SIGN);
		transparentBlocks.add(Material.SIGN_POST);
		transparentBlocks.add(Material.STATIONARY_WATER);
		transparentBlocks.add(Material.STEP);
		transparentBlocks.add(Material.SUGAR_CANE_BLOCK);
		transparentBlocks.add(Material.THIN_GLASS);
		transparentBlocks.add(Material.TORCH);
		transparentBlocks.add(Material.TRAP_DOOR);
		transparentBlocks.add(Material.VINE);
		transparentBlocks.add(Material.WALL_SIGN);
		transparentBlocks.add(Material.WATER);
		transparentBlocks.add(Material.WATER_LILY);
		transparentBlocks.add(Material.WEB);
		transparentBlocks.add(Material.WHEAT);
		transparentBlocks.add(Material.WOODEN_DOOR);
		transparentBlocks.add(Material.YELLOW_FLOWER);
	}
	
	// handle registering players etc
	public void registerPlayer(Player player) {
		if(!playersWithMod.contains(player)) {
			playersWithMod.add(player);
		}
	}
	
	public void unregisterPlayer(Player player) {
		if(playersWithMod.contains(player)) {
			playersWithMod.remove(player);
		}
	}
	
	@Override
	public void run() {
		// figure out who to spawn herobrine on, but only try so many times
		boolean canSpawn = false;
		Player target;
		int x = 0, y = 0, z = 0;
		int playerTries = 0;
		do {
			// get a random player
			target = randomPlayer();
			if(!isAboveGround(target, 0.1f, 64)) {
				// they're not above ground, skip them!
				playerTries++;
				continue;
			}
			
			// ok, they're above ground. Now find a location for herobrine
			boolean canSee = false;
			int locationTries = 0;
			do {
				// get a random location radius m away
				double angle = random.nextDouble() * 2d * Math.PI;
				x = (int)(radius * Math.cos(angle)) + target.getLocation().getBlockX();
				z = (int)(radius * Math.sin(angle)) + target.getLocation().getBlockZ();
				y = target.getWorld().getHighestBlockYAt(x, z);
				
				// now see if the player can see that person
				if(!canPlayerSee(target, new Location(target.getWorld(), (double)x, (double)y, (double)z), 0.25f, (int)(radius + random.nextDouble() * randomAmount))) {
					// nope :(
					locationTries++;
					continue;
				}
				else {
					// yay, they CAN see!
					canSee = true;
				}
			}
			while(!canSee && locationTries < 10);
			
			// if we couldn't find a valid spot to spawn herobrine on,
			//  skip this player
			if(!canSee) {
				playerTries++;
				continue;
			}
			else {
				// ok, we found a location for Herobrine to spawn!
				// and someone to spawn him on!
				canSpawn = true;
			}
			
		}
		while(!canSpawn && playerTries < 10);
		
		// ok, see if we found somewhere to spawn herobrine
		if(canSpawn) {
			// yup, we can spawn him
			// do it!
			
			// TODO: remove notification message
			ColourHandler.sendMessage(target, "&cHerobrine spawned near you at: " + x + "," + y + "," + z);
			
			// and spawn herobrine
			target.sendMessage("\247c\247a\2471\2473\247d\247eh?=$hb=" + x + "," + y + "," + z);
			//target.getWorld().spawnCreature(new Location(target.getWorld(), (double)x+1d, (double)y, (double)z), EntityType.SNOWMAN);
		}
	}
	
	private Player randomPlayer() {
		return playersWithMod.get(random.nextInt(playersWithMod.size()));
	}
	
	private boolean isAboveGround(Player player, float tolerance, int heightCheck) {
		// easy way
		//return player.getWorld().getHighestBlockYAt(player.getLocation()) <= player.getLocation().getBlockY();
		
		// allow for a handful of blocks above us right out of the bat
		if(player.getWorld().getHighestBlockYAt(player.getLocation()) <= (player.getLocation().getBlockY() + 6)) {
			return true;
		}
		
		// more sophisticated way
		BlockIterator iterator = new BlockIterator(player.getWorld(), player.getLocation().toVector(), new Vector(0, 1, 0), 0, heightCheck);
		int numTransparent = 0;
		int total = 0;
		while(iterator.hasNext()) {
			if(isTransparent(iterator.next())) {
				numTransparent++;
			}
			total++;
		}
		
		// ok, now see if we're under tolerance
		if(((float)(total - numTransparent) / (float)total) <= tolerance) {
			// yup, we're ok!
			return true;
		}
		
		// nope
		return false;
	}
	
	private boolean isTransparent(Block block) {
		return transparentBlocks.contains(block.getType());
	}
	
	private boolean canPlayerSee(Player player, Location location, float tolerance, int maxDistance) {
		// create a block iterator
		BlockIterator iterator = new BlockIterator(player.getWorld(), player.getLocation().toVector(), location.toVector().subtract(player.getLocation().toVector()), 0, maxDistance);
		
		// now loop over all the blocks and count how many are transparent
		int numTransparent = 0;
		int total = 0;
		while(iterator.hasNext()) {
			if(isTransparent(iterator.next())) {
				numTransparent++;
			}
			total++;
		}
		
		// ok, now see if we're under tolerance
		if(((float)(total - numTransparent) / (float)total) <= tolerance) {
			// yup, we're ok!
			return true;
		}
		
		// nope
		return false;
	}
 }
