package ru.komiss77.hook;

import java.io.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.io.ByteArrayDataInput;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.Ostrov;
import ru.komiss77.enums.Chanell;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.SmartInventory;

public class SkinRestorerHook {

  protected static final String TEXTURES_NAME = "textures";

  public static void onMsg(final Player p, final ByteArrayDataInput in) {
    if (p==null || !p.isOnline()) {
      return;
    }

      String subChannel = in.readUTF();

      if (subChannel.equalsIgnoreCase("returnSkinsV3")) {
        in.readUTF(); //playerName
        final int page = in.readInt();
        final short len = in.readShort();
//Ostrov.log("onMsg subChannel="+subChannel+" name="+p.getName()+" page="+page);

        final byte[] msgBytes = new byte[len];
        in.readFully(msgBytes);
        final Map<String, String> skinList = convertToMap(msgBytes);

        SmartInventory
          .builder()
          .id(p.getName()+"skins")
          .provider(new SkinGui(page, skinList))
          .size(6, 9)
          .title("§fНастройка скина"+(page==0?"":" §7стр."+page))
          .build()
          .open(p);

      } else if (subChannel.equalsIgnoreCase("SkinUpdateV2")) {

        if (p.getVehicle() != null) {
          p.getVehicle().removePassenger(p);
        }
        final PlayerProfile profile = p.getPlayerProfile();
        profile.getProperties().removeIf(profileProperty -> profileProperty.getName().equals(TEXTURES_NAME));
        final String value = in.readUTF();
        final String signature = in.readUTF();
        profile.getProperties().add(new ProfileProperty(TEXTURES_NAME, value, signature));
        p.setPlayerProfile(profile);
        //hideAndShow - вроде не надо в папер
      }


  }

  private static Map<String, String> convertToMap(byte[] byteArr) {
    try {
      DataInputStream ois = new DataInputStream(new GZIPInputStream(new ByteArrayInputStream(byteArr)));
      int size = ois.readInt();
      Map<String, String> map = new LinkedHashMap<>(size);
      for (int i = 0; i < size; i++) {
        String key = ois.readUTF();
        String value = ois.readUTF();
        map.put(key, value);
      }
      return map;
    } catch (IOException e) {
      e.printStackTrace();
      return Collections.emptyMap();
    }
  }


  public static void requestPage(Player p, int page) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    DataOutputStream out = new DataOutputStream(bytes);
    try {
      out.writeUTF("getSkins");
      out.writeUTF(p.getName());
      out.writeInt(page);
      p.sendPluginMessage(Ostrov.instance, Chanell.SKIN.name, bytes.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}















 class SkinGui implements InventoryProvider {

    private int page;
    final Map<String, String> skinList;

  private static final ClickableItem fill = ClickableItem.empty(new ItemBuilder(Material.NETHER_SPROUTS).name("§8.").build());


  public SkinGui(final int page, final Map<String, String> skinList) {
    this.page = page;
    this.skinList = skinList;
  }



  @Override
  public void init(final Player p, final InventoryContent content) {
    p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);

    content.fillRow(4, fill);
  //  final Oplayer op = PM.getOplayer(p);



    if (skinList.isEmpty()) {

      content.add(ClickableItem.empty(new ItemBuilder(Material.GLASS_BOTTLE)
        .name("§7нет записей!")
        .build()
      ));

    } else {

      int skinCount = 0;
      for (Map.Entry<String,String> en : skinList.entrySet()) {
        if (skinCount >= 36) {
          Ostrov.log_warn("SkinsGUI: Skin count is more than 36, skipping...");
          break;
        }
        final ItemStack is = new ItemBuilder(Material.PLAYER_HEAD)
          .name(en.getKey())
            .setCustomHeadTexture(en.getValue())
              .build();

        content.add(ClickableItem.of( is, e-> {

          } )
        );
        skinCount++;
      }

    }



                /*
                case HEAD -> {
                    String skinName = event.displayName();
                    adapter.runAsync(() -> adapter.sendToMessageChannel(event.player(), out -> {
                        out.writeUTF("setSkin");
                        out.writeUTF(player.getName());
                        out.writeUTF(skinName);
                    }));
                    player.closeInventory();
                }
                case RED_PANE -> {
                    adapter.runAsync(() -> adapter.sendToMessageChannel(event.player(), out -> {
                        out.writeUTF("clearSkin");
                        out.writeUTF(player.getName());
                    }));
                    player.closeInventory();
                }
 */

    if (page>0) {
      content.set(5, 0, ClickableItem.of(ItemUtils.previosPage, e
          -> {
        SkinRestorerHook.requestPage(p, page-1);
        })
      );
    }

    if (page<999 && skinList.size()==36) {
      content.set(5, 8, ClickableItem.of(ItemUtils.nextPage, e
          -> {
          SkinRestorerHook.requestPage(p, page+1);
        }
      ));
    }


    }









}
