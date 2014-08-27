package com.vivi7865.RedisTPS;

import java.io.File;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class Config {
	static File file;
	static FileConfiguration conf;
	RedisTPS plugin;
	static String ntpHost;
	static int checkInterval;

	public Config(RedisTPS plug, File dataFolder) {
		this.plugin = plug;
		file = new File(dataFolder, "config.yml");
		conf = YamlConfiguration.loadConfiguration(file);
		
		final String redisServer = conf.getString("redis-server", "localhost");
		final int redisPort = conf.getInt("redis-port", 6379);
        String redisPassword = conf.getString("redis-password");
        String serverID = conf.getString("server-id");
        ntpHost = conf.getString("NTP-host");
        checkInterval = conf.getInt("check-interval", 3);
		
        if (redisPassword != null && (redisPassword.isEmpty() || redisPassword.equals("none"))) {
            redisPassword = null;
        }
        
        if (serverID == null || serverID.isEmpty()) {
            throw new RuntimeException("server-id not specified in configuration or empty");
        }
        plugin.setServerID(serverID);

        if (redisServer != null && !redisServer.isEmpty()) {
            final String finalRedisPassword = redisPassword;
            Bukkit.getScheduler().runTask(plugin, new Runnable() {

				public void run() {
					Jedis rsc = null;
					try {
	                    JedisPoolConfig config = new JedisPoolConfig();
	                    config.setMaxTotal(-1);
	                    config.setJmxEnabled(false);
	                    plugin.setPool(new JedisPool(config, redisServer, redisPort, 0, finalRedisPassword));
	                    rsc = plugin.getPool().getResource();
	                    if (rsc.hexists("RedisTPS_heartbeats", plugin.getServerID())) {
	                    	Long lastHB = Long.parseLong(rsc.hget("RedisTPS_heartbeats", plugin.getServerID()));
	                    	if (lastHB != null && plugin.getTime() < lastHB + 15000) {
	                    		plugin.getLogger().log(Level.SEVERE, "This instance is a possible imposter instance");
	                    		plugin.getLogger().log(Level.SEVERE, "For security reason RedisTPS will now disable itself");
	                    		plugin.getLogger().log(Level.SEVERE, "If this instance restart from crash please wait 15 seconds before restart");
	                    		throw new RuntimeException("RedisTPS possible imposter instance");
	                    	}
	                    }
					} catch (NumberFormatException ignored) {} catch (JedisConnectionException e) {
						if (rsc != null)
		                    plugin.getPool().returnBrokenResource(rsc);
						plugin.getPool().destroy();
						plugin.setPool(null);
		                rsc = null;
		                throw e;
					}
				}
            });
            
            
        }
	}
	
	public static String getNTPHost() {
		return ntpHost;
	}

	public static int getCheckInterval() {
		return checkInterval;
	}

}
