package ru.komiss77.modules.redis;
/*
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.UnifiedJedis;
import ru.komiss77.Ostrov;


public class PubSubListener implements Runnable {
  private JedisPubSubHandler jpsh;
  private final Set<String> addedChannels = new HashSet<String>();

  private static final Ostrov plugin;

  static {
    plugin = Ostrov.getInstance();
  }


  @Override
  public void run() {
//Ostrov.log_warn("PubSubListener run()");
    RedisTask<Void> subTask = new RedisTask<Void>() {
      @Override
      public Void unifiedJedisTask(UnifiedJedis unifiedJedis) {
        jpsh = new JedisPubSubHandler();
        addedChannels.add("ostrov");
//Ostrov.log_warn("new JedisPubSubHandler + addedChannels");
        //addedChannels.add("redisbungee-allservers");
        //addedChannels.add("redisbungee-data");
        unifiedJedis.subscribe(jpsh, addedChannels.toArray(new String[0]));
//Ostrov.log_warn("subscribe!");
        return null;
      }
    };

    try {
      subTask.execute();
    } catch (Exception e) {
      Ostrov.log_err("PubSub error, attempting to recover in 5 secs.");
      //plugin.executeAsyncAfter(this, TimeUnit.SECONDS, 5);
      new BukkitRunnable() {
        @Override
        public void run() {
          this.run();
        }
      }.runTaskLaterAsynchronously(plugin, 100);
    }
  }

  public void addChannel(String... channel) {
    addedChannels.addAll(Arrays.asList(channel));
    jpsh.subscribe(channel);
  }

  public void removeChannel(String... channel) {
    Arrays.asList(channel).forEach(addedChannels::remove);
    jpsh.unsubscribe(channel);
  }

  public void poison() {
    addedChannels.clear();
    jpsh.unsubscribe();
  }
}



class JedisPubSubHandler extends JedisPubSub {

  private static final Ostrov plugin;

  static {
    plugin = Ostrov.getInstance();
  }

  @Override
  public void onSubscribe(final String chanell, int subscribedChannels) {
    Ostrov.log_ok("§aRedisLst : subscribed to channel "+chanell);
  }

  @Override
  public void onUnsubscribe(final String chanell, int subscribedChannels) {
    //String channelName = new String(channel, StandardCharsets.UTF_8);
    Ostrov.log_ok("§aRedisLst : unsubscribed to channel "+chanell);
  }
  @Override
  public void onMessage(final String chanell, final String msg) {
Ostrov.log_warn("RedisLst : onMessage s="+chanell+" s2="+msg);
    if (msg.isBlank()) return;
    if (s2.trim().length() == 0) return;
    plugin.executeAsync(new Runnable() {
      @Override
      public void run() {
        Object event = plugin.createPubSubEvent(s, s2);
        plugin.fireEvent(event);
      }
    });
  }
}
*/



/*
public class PubSubListener extends BinaryJedisPubSub {

  private final ReentrantLock lock = new ReentrantLock();
  private final Set<String> subscribed = ConcurrentHashMap.newKeySet();

  @Override
  public void subscribe(byte[]... channels) {
    this.lock.lock();
    try {
      for (byte[] channel : channels) {
        String channelName = new String(channel, StandardCharsets.UTF_8);
        if (this.subscribed.add(channelName)) {
          super.subscribe(channel);
        }
      }
    } finally {
      this.lock.unlock();
    }
  }

  @Override
  public void unsubscribe(byte[]... channels) {
    this.lock.lock();
    try {
      super.unsubscribe(channels);
    } finally {
      this.lock.unlock();
    }
  }

  @Override
  public void onSubscribe(byte[] channel, int subscribedChannels) {
    //if (api.isDebug())
    //  api.getLogger().log("Successfully subscribed to channel: " +
    //    new String(channel, StandardCharsets.UTF_8));
  }

  @Override
  public void onUnsubscribe(byte[] channel, int subscribedChannels) {
    String channelName = new String(channel, StandardCharsets.UTF_8);
  //  if (api.isDebug())
   //   api.getLogger().log("Successfully unsubscribed from channel: "
    //    + channelName);
    this.subscribed.remove(channelName);
  }

  @Override
  public void onMessage(byte[] channel, byte[] message) {
    String channelName = new String(channel, StandardCharsets.UTF_8);
    try {
    //  api.handleMessage(channelName, message);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

*/
