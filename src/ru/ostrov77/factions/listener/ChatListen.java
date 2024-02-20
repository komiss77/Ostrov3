package ru.ostrov77.factions.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import ru.komiss77.events.ChatPrepareEvent;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.LocationUtil;
import ru.komiss77.utils.TCUtils;
import ru.ostrov77.factions.Enums.Relation;
import ru.ostrov77.factions.Enums.Role;
import ru.ostrov77.factions.FM;
import ru.ostrov77.factions.Relations;
import ru.ostrov77.factions.objects.Faction;
import ru.ostrov77.factions.objects.Fplayer;






public class ChatListen implements Listener  {
    
    
    public ChatListen(final Plugin plugin) {
    }
    
    
    public enum ChatType {
            Остров("Ваши сообщения увидят","на всех серверах."),
            Союзный("Сообщения увидит ваш и союзный клан.","Написать в Глобальный - ! в начале."),
            Клан("Сообщения увидит только ваш клан.","Написать в Глобальный - ! в начале."),
            Локальный("Ваши сообщения видны в радиусе 300м."," ! в начале - Написать в Глобальный"),
            ;
            
        public String desk1;
        public String desk2;
        private ChatType (final String desk1, final String desk2) {
            this.desk1 = desk1;
            this.desk2 = desk2;
        }
        
        public static ChatType fromString(final String s) {
            for (ChatType c : values()) {
                if (c.toString().equalsIgnoreCase(s)) return c;
            }
            return Остров;
        }
    }
    
    public static void setChatType(final Fplayer fp, final ChatType ct) {
        fp.chatType = ct;
        final Oplayer op = PM.getOplayer(fp.name);
        switch (ct) {
            case Остров -> op.setLocalChat(false);//DchatHook.setGlobal(fp.getPlayer());
            case Локальный, Союзный, Клан -> op.setLocalChat(true);//DchatHook.setLocal(fp.getPlayer());
        }
    }


    
    
    public static void switchChat(final Fplayer fp) {
        switch (fp.chatType) {
            case Остров -> setChatType(fp, ChatType.Союзный);
            case Союзный -> setChatType(fp, ChatType.Клан);
            case Клан -> setChatType(fp, ChatType.Локальный);
            case Локальный -> setChatType(fp, ChatType.Остров);
        }
    }


        
        
        

    
    
    
    
    
           
                /*
                Глобальный чат пишется префикс Г - палить настройки фактион чат
                Локальный чат цвет &8 префикс Л
                Клановый чат &2 цвет Префикс К
                Чат союзников &3 Префикс С
                Тип чата Мидгард - убрать вообще 
            
                FactionChat: '&2[FACTION] &f[FACTIONRANK] TITLEPREFIX PLAYER SUFFIX: MESSAGE'
                AllyChat: '&aAlly: [FACTION] &f[FACTIONRANK] TITLEPREFIX PLAYER SUFFIX: MESSAGE'
                TruceChat: '&aTruce: [FACTION] &f[FACTIONRANK] TITLEPREFIX PLAYER SUFFIX: MESSAGE'
                AllyTruceChat: '&aALLY-TRUCE: [FACTION] &f[FACTIONRANK] TITLEPREFIX PLAYER SUFFIX: MESSAGE'
                OfficerChat: '&eOfficerChat: [FACTION] &f[FACTIONRANK] TITLEPREFIX PLAYER SUFFIX: MESSAGE'
                LeaderChat: '&eLeaderChat: [FACTION] &f[FACTIONRANK] TITLEPREFIX PLAYER SUFFIX: MESSAGE'
                EnemyChat: '&cEnemy: [FACTION] &f[FACTIONRANK] TITLEPREFIX PLAYER SUFFIX: MESSAGE'
                OtherFactionChatTo: '&5[@:OTHERFACTION] &f[FACTION] TITLEPREFIX PLAYER SUFFIX: MESSAGE'
                OtherFactionChatFrom: '&5[From:FACTION] &f[{OTHERFACTION}] TITLEPREFIX PLAYER SUFFIX: MESSAGE'
                SpyChat: '&2Spy:&r MESSAGE'
                */
                
 
    
    //обработка AsyncPlayerChatEvent (исходящих от игрока) этапы 1,2,3
    
    // 1
    //dchat получает AsyncPlayerChatEvent и создаёт DeluxeChatEvent, отмена делает return из AsyncPlayerChatEvent
    //можно играть getRecipients
    @EventHandler 
    public void chat(ChatPrepareEvent e) {
        final Player sender = e.getPlayer();
//System.out.println("1 DeluxeChatEvent sender="+sender.getName()+" reciep="+e.getRecipients());
//
        final Fplayer senderFP = FM.getFplayer(sender);
        senderFP.updateActivity();
        final Faction senderFaction = FM.getPlayerFaction(sender);
//System.out.println("1 DeluxeChatEvent name="+p.getName()+" local?"+DeluxeChat.isLocal(p.getUniqueId().toString())+" arena="+arena);
        
        //Player recipient;
        Faction recieveFaction;
        //Fplayer recieveFP;
        Role role;
        
        
        //только если из клана пишем с !
        //получат так же те, у кого клановый чат
        if ( senderFaction!=null && e.getMessage().startsWith("!") ) { 
            e.setMessage(e.getMessage().replaceFirst("!", ""));
            role = senderFaction.getRole(sender.getName());
            final Component c = TCUtils.format("§7[§fВсем§7] §e[§6"+senderFaction.displayName()+"§e] "+role.chatPrefix+"§7");
            e.setSenderGameInfo(c);
            e.setViewerGameInfo(c);
            //e.getDeluxeFormat().setPrefix( "§7[§fВсем§7] §e[§6"+senderFaction.getName()+"§e] "+role.chatPrefix+"§7" );
            return;
        }
        
        
        //у отправителя есть клан и чат клановый - оставляем только соклановцам и союзникам
        if ( senderFaction!=null) {
            
            if (senderFP.chatType == ChatType.Союзный || senderFP.chatType == ChatType.Клан) { 
            
                //обработка по отправителю
                e.setCancelled(true);
                role = senderFaction.getRole(sender.getName());

                for (final Player p:Bukkit.getOnlinePlayers()) { //если чат клановый, дальше сервера и других кланов точно не пойдёт.

                    recieveFaction = FM.getPlayerFaction(p);

                    if (recieveFaction!=null) { // обрабатываем только тех, у кого есть клан

                        if (senderFaction.factionId==recieveFaction.factionId) { //если в одном клане

                            p.sendMessage("§8["+role.displayName+"§8] §8"+sender.getName()+" §7: "+e.getMessage());

                        } else if (Relations.getRelation(senderFaction, recieveFaction)==Relation.Союз) { //если союзный клан

                            p.sendMessage("§8[§6"+senderFaction.displayName()+"§8] §8["+role.displayName+"§8] §8"+sender.getName()+" §7: "+e.getMessage());

                        }

                    }

                }
            
            } else if (senderFP.chatType == ChatType.Локальный) {
            
                //обработка по отправителю
                e.setCancelled(true);
                role = senderFaction.getRole(sender.getName());

                for (final Player p:Bukkit.getOnlinePlayers()) { 

                    recieveFaction = FM.getPlayerFaction(p);

                    if (recieveFaction!=null) { //у получателя есть клан

                        if (senderFaction.factionId==recieveFaction.factionId) { //если в одном клане

                            p.sendMessage("§5[§dЗона300§5] §f["+role.displayName+"§f] §2"+sender.getName()+" §7: "+e.getMessage());

                        } else if (Relations.getRelation(senderFaction, recieveFaction)==Relation.Союз) { //если союзный клан

                            p.sendMessage("§5[§dЗона300§5] §e[§6"+senderFaction.displayName()+"§e] §f["+role.displayName+"§f] §2"+sender.getName()+" §7: "+e.getMessage());

                        } else if (LocationUtil.getDistance(sender.getLocation(), p.getLocation()) < 900) { //разные миры учтены в LocationUtil
                            
                            p.sendMessage("§5[§dЗона300§5] §e[§6"+senderFaction.displayName()+"§e] §2"+sender.getName()+" §7: "+e.getMessage());
                            
                        }

                    }

                }
            
            }
            return;

        } 
            
        //обработка по получателю
           /* 
        Iterator<Player> recipients = e.getRecipients().iterator();

        while (recipients.hasNext()) {
            recipient = recipients.next(); 
            recieveFaction = FM.getPlayerFaction(recipient);
            recieveFP = FM.getFplayer(recipient);  //берем  получателя

            if ( recieveFaction!=null && recieveFP!=null && (senderFP.chatType == ChatType.Союзный || senderFP.chatType == ChatType.Клан) ) { //если у получателя есть клан и там клановый чат
                //if (senderFaction!=null && (senderFaction.factionId==recieveFaction.factionId || FM.getRelation(senderFaction, recieveFaction)==Relations.Relation.Союз) ) {
                    //оставляем, получить как обычное клановое сообщения в свой и союзный клан
                //} else {
                    recipients.remove();
                //}
            }


        }
         */   
        
        
        // "prefixLeader": "★★",  "prefixOfficer": "★",  "prefixMember": "☆",  "prefixRecruit": "*",
        if (senderFaction==null) {
            if (senderFP.chatType == ChatType.Локальный) {
                e.setCancelled(true);
                    //role = senderFaction.getRole(sender.getName());
                for (final Player p:Bukkit.getOnlinePlayers()) { 
                    if (LocationUtil.getDistance(sender.getLocation(), p.getLocation()) < 900) { //разные миры учтены в LocationUtil
                            p.sendMessage("§5[§dЗона300§5] §e[§6Дикарь§e] §2"+sender.getName()+" §7: "+e.getMessage());
                    }
                }
            } else {
                final Component c = TCUtils.format("§e[§6Дикарь§e] §7");
                e.setSenderGameInfo(c);
                e.setViewerGameInfo(c);
                //e.getDeluxeFormat().setPrefix( "§e[§6Дикарь§e] §7" );
            }
            
        } else {
            
            role = senderFaction.getRole(sender.getName());
                final Component c = TCUtils.format("§e[§6"+senderFaction.displayName()+"§e] "+role.chatPrefix+"§7");
                e.setSenderGameInfo(c);
                e.setViewerGameInfo(c);
            //e.getDeluxeFormat().setPrefix( "§e[§6"+senderFaction.getName()+"§e] "+role.chatPrefix+"§7" );
        
        }



            
    }
        
    
    
    
    
    

    
        
}
  




















    
    // 2
    //после DeluxeChatEvent сообщение форматируется и рассылается локально тем, кто остался в deluxeChatEvent.getRecipients(). 
    //getJSONFormat() == null || getJSONChatMessage() == null || getJSONFormat().isEmpty(), return из AsyncPlayerChatEvent
    //после этого эвента локальные получатели удаляются
    //@EventHandler 
    //public void chat(DeluxeChatJSONEvent e) { 
//System.out.println("2 DeluxeChatJSONEvent");
    //}
    
    
    
    // 3
    //после рассылки локальным игрокам по списку getRecipients, результат этого эвента отправляется в банжи
    //getJSONFormat() == null || getChatMessage() == null || getJSONFormat().isEmpty() || getChatMessage().isEmpty())  return из AsyncPlayerChatEvent
    //@EventHandler 
    //public void chat(ChatToPlayerEvent e) { 
//System.out.println("3 ChatToPlayerEvent");
    //}
    
   
    
    
    
    
    

    


//по входящим с банжи
    //после входящего PluginMessageReceived сообщение отправляется в sendBungeeChat (CompatibilityManager)
    //в sendBungeeChat по какому-то флагу либо сразу рассылается, либо рассылается тем, у кого не локальный
    //переключение локальный/глобальный: e.getPlayer().getUniqueId().toString()
    /*
        public boolean setLocal(final String s) {
        if (DeluxeChat.localPlayers == null) {
            (DeluxeChat.localPlayers = new ArrayList<String>()).add(s);
            return true;
        }
        if (DeluxeChat.localPlayers.contains(s)) {
            return false;
        }
        DeluxeChat.localPlayers.add(s);
        return true;
    }
    
    public boolean setGlobal(final String s) {
        if (DeluxeChat.localPlayers == null) {
            DeluxeChat.localPlayers = new ArrayList<String>();
            return false;
        }
        if (!DeluxeChat.localPlayers.contains(s)) {
            return false;
        }
        DeluxeChat.localPlayers.remove(s);
        return true;
    }
    
    public static boolean isLocal(final String s) {
        return DeluxeChat.localPlayers != null && DeluxeChat.localPlayers.contains(s);
    }

    */

    
    

    
    