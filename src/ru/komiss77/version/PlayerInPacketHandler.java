package ru.komiss77.version;

import java.lang.reflect.Field;
import java.util.Iterator;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.komiss77.Ostrov;
import ru.komiss77.modules.bots.BotEntity;
import ru.komiss77.modules.bots.BotManager;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.utils.PlayerInput;
import ru.komiss77.utils.inventory.InputButton;


public class PlayerInPacketHandler extends ChannelDuplexHandler {

    private final Oplayer op;
  public static Field interactIdField, moveIdField;

  static {
    try {
      interactIdField = ServerboundInteractPacket.class.getDeclaredFields()[0]; //по entityId не прокатит - на запущеном имена обфусцированны!
      interactIdField.setAccessible(true);
      moveIdField = ClientboundMoveEntityPacket.class.getDeclaredFields()[0];
      moveIdField.setAccessible(true);
    } catch (ArrayIndexOutOfBoundsException ex) {
      Ostrov.log_err("PlayerPacketHandler getIdField : " + ex.getMessage());
      //ex.printStackTrace();
    }
  }



    public PlayerInPacketHandler(final Oplayer op) {
        this.op = op;
    }

    @Override
    public void channelRead(final @NotNull ChannelHandlerContext chc, final @NotNull Object packet) throws Exception {
        
        if (packet instanceof final ServerboundInteractPacket pk) { // Paper start - PlayerUseUnknownEntityEvent
//Ostrov.log(""+pk.);
            if (BotManager.enable.get()) {
                final int id = pk.getEntityId();
                for (final BotEntity bot : BotManager.botById.values()) {
                    if (bot.hashCode() == id) {
                        interactIdField.set(pk, bot.rid);
                        break;
                    }
                }
//                if (useEntityPacket.getActionType() == PacketPlayInUseEntity.b.b) {}
            }
            
        } else if (packet instanceof ServerboundSignUpdatePacket signPacket) {
            final Player p = op.getPlayer();//Bukkit.getPlayerExact(name);
            if (p!=null && PlayerInput.inputData.containsKey(p)) {  // в паспорте final String[] split = msg.split(" ");
                final String result = signPacket.getLines()[0] + " " + signPacket.getLines()[1] + " " + signPacket.getLines()[2] + " " + signPacket.getLines()[3];
                Ostrov.sync(  () -> PlayerInput.onInput(p, InputButton.InputType.SIGN, result), 0 );
                return; //пакет ввода с таблички не отдаём в сервер!
            }
        }
        
        super.channelRead(chc, packet);
    }

    
    @Override
    public void write(final ChannelHandlerContext chc, final Object packet, final ChannelPromise channelPromise) throws Exception {

        if (BotManager.enable.get()) {
            int id = 0;
            if (packet instanceof final ClientboundAddEntityPacket p) {
                id = p.getId();
            } else if (packet instanceof final ClientboundSetEntityDataPacket p) {
                id = p.id();
            } else if (packet instanceof final ClientboundTeleportEntityPacket p) {
                id = p.getId();
            } else if (packet instanceof final ClientboundUpdateAttributesPacket p) {
                id = p.getEntityId();
            } else if (packet instanceof ClientboundMoveEntityPacket) {
                id = (int) moveIdField.get(packet);
            }

            if (id != 0 && BotManager.botById.containsKey(id)) {
                return; //не пропускать пакеты дальше
            }

            if (packet instanceof ClientboundBundlePacket clientboundBundlePacket) {
                final Iterator<Packet<ClientGamePacketListener>> pit = clientboundBundlePacket.subPackets().iterator();
                while (pit.hasNext()) {
                    final Packet<?> pc = pit.next();
                    if (pc instanceof final ClientboundAddEntityPacket p) {
                        id = p.getId();
                    } else if (pc instanceof final ClientboundSetEntityDataPacket p) {
                        id = p.id();
                    } else if (pc instanceof final ClientboundTeleportEntityPacket p) {
                        id = p.getId();
                    } else if (pc instanceof final ClientboundUpdateAttributesPacket p) {
                        id = p.getEntityId();
                    } else if (pc instanceof ClientboundMoveEntityPacket) {
                        id = (int) moveIdField.get(pc);
                    }

                    if (id != 0 && BotManager.botById.containsKey(id)) {
                        pit.remove(); //вырезать пакет из кучи
                    }
                }
            }
        }

        super.write(chc, packet, channelPromise);
    }
}





