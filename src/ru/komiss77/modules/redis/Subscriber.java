package ru.komiss77.modules.redis;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import ru.komiss77.Ostrov;

  public class Subscriber implements Runnable {

  //private final ReentrantLock lock = new ReentrantLock();
    private static final JedisPubSubHandler jpsh;
    private static final Set<String> addedChannels;

    static {
      addedChannels = new HashSet<String>();
      jpsh = new JedisPubSubHandler();
    }

    @Override
    public void run() {
      RedisTask<Void> subTask = new RedisTask<Void>() {
        @Override
        public Void unifiedJedisTask(UnifiedJedis unifiedJedis) {
          addedChannels.add("ostrov");
          addedChannels.add("arenadata");
          unifiedJedis.subscribe(jpsh, addedChannels.toArray(new String[0]));
          return null;
        }
      };

      try {

        subTask.execute();

      } catch (Exception ex) {

        Ostrov.log_err("RDS  Subscriber : "+ex.getMessage());
        RDS.unsubscribe();
        //Bukkit.getScheduler().runTaskLaterAsynchronously(Ostrov.getInstance(), ()-> {
        //  Ostrov.log_warn("RDS  Subscriber : restore connection");
        //  RDS.subscriber.run();
        //}, 15*20);

        //plugin.executeAsyncAfter(this, TimeUnit.SECONDS, 5);
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
      try {
        jpsh.unsubscribe();
      } catch (JedisConnectionException ex) {
        Ostrov.log_warn("RDS Subscribe poison : "+ex.getMessage());
      }
    }



}

class JedisPubSubHandler  extends JedisPubSub {//BinaryJedisPubSub {
  @Override
  //public void onMessage(byte[] channel, byte[] message) {
  public void onMessage(final String channelName, final String msg) {
    //String channelName = new String(channel, StandardCharsets.UTF_8);
    //String msg = new String(message, StandardCharsets.UTF_8);
    try {
      Ostrov.log_warn("RedisLst : onMessage s="+channelName+" s2="+msg);
      if (msg.isBlank()) return;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}




/*
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
      Ostrov.log_ok("§aRedisLst : subscribed to channel "+new String(channel, StandardCharsets.UTF_8));
  }

  @Override
  public void onUnsubscribe(byte[] channel, int subscribedChannels) {
    String channelName = new String(channel, StandardCharsets.UTF_8);
    Ostrov.log_ok("§aRedisLst : unsubscribed to channel "+channelName);
    subscribed.remove(channelName);
  }*/


