package ru.ostrov77.factions;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import ru.ostrov77.factions.signProtect.LockListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.komiss77.ApiOstrov;
import ru.komiss77.modules.menuItem.MenuItem;
import ru.komiss77.modules.menuItem.MenuItemBuilder;
import ru.komiss77.modules.player.PM;
import ru.komiss77.modules.warp.WarpManager;
import ru.komiss77.modules.world.WorldManager;
import ru.komiss77.utils.ItemBuilder;
import ru.ostrov77.factions.jobs.JobListener;
import ru.ostrov77.factions.listener.ChatListen;
import ru.ostrov77.factions.listener.BlockListen;
import ru.ostrov77.factions.listener.InteractListen;
import ru.ostrov77.factions.listener.MainListen;
import ru.ostrov77.factions.listener.PlayerListen;
import ru.ostrov77.factions.map.DynmapHook;
import ru.ostrov77.factions.objects.Fplayer;
import ru.ostrov77.factions.religy.ReligyListener;
import ru.ostrov77.factions.setup.SetupManager;
import ru.ostrov77.factions.turrets.TM;


public class Main extends JavaPlugin {
    
    public static final String LOBBY_WORLD_NAME = "prefecture"; //минут

    public static Main plugin;
    public static SetupManager setupManager;
    public static FilesManager filesManager;    
    public static int createPrice = 0;
    public static Random r;
    public static boolean canLogin = false;
    public static ItemStack book;
    
    public static HashMap <Integer, String> worldNames;
    public static AchievementsManager achievementsManager;
    public static boolean dynMap;

    
    @Override
    public void onEnable() {
        plugin = this;
        worldNames = new HashMap<>();
        
        //! перед checkWorldNames
        World lobbyWorld = Bukkit.getWorld(LOBBY_WORLD_NAME);
        if (lobbyWorld==null) lobbyWorld = WorldManager.load(Bukkit.getConsoleSender(), LOBBY_WORLD_NAME, World.Environment.NORMAL, WorldManager.Generator.Empty);
        if (lobbyWorld==null) lobbyWorld = WorldManager.create(Bukkit.getConsoleSender(), LOBBY_WORLD_NAME, World.Environment.NORMAL, WorldManager.Generator.Empty, false);
        
        for (final World w : Bukkit.getWorlds()) {
            checkWorldNames(w);
        }
        
        r = new Random();
        Load_book();
        filesManager = new FilesManager(this);
        setupManager = new SetupManager(plugin);
        achievementsManager = new AchievementsManager(this);
        
     
        PM.setOplayerFun(he -> new Fplayer(he), true);        
        final FM fm = new FM(plugin);
        Land.init(); //там и загрузкоа!
        Relations.init(); //перед загрузкой! там инится хранилище!
        Wars.init(); //перед загрузкой! там инится хранилище!
        Sciences.init();
        
        
        Bukkit.getPluginManager().registerEvents(new Econ(this), this);
        
        //оключил лимитер
        Level lvl = new Level(this); //Bukkit.getPluginManager().registerEvents(new Level(this), this);
        
        //Bukkit.getPluginManager().registerEvents(new Structures(this), this);
        TM turrets = new TM(plugin); // +там Bukkit.getPluginManager().registerEvents(new TurretsListener(), this);
        
        Bukkit.getPluginManager().registerEvents(new MainListen(), this);
        Bukkit.getPluginManager().registerEvents(new BlockListen(), this);
        Bukkit.getPluginManager().registerEvents(new InteractListen(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListen(), this);
        Bukkit.getPluginManager().registerEvents(new JobListener(), this);
        Bukkit.getPluginManager().registerEvents(new ReligyListener(), this);
        
        this.getServer().getPluginManager().registerEvents(new LockListener(), this);//LockettePro.onEnable();
        
        Bukkit.getPluginManager().registerEvents(new ChatListen(this), this);
        
        //создаём предмет - менюшку
        MenuItem mi = new MenuItemBuilder ("factions",
                new ItemBuilder(Material.MOJANG_BANNER_PATTERN)
                    .name("§aЛенная грамота")
                    .addLore("§aПКМ §7- меню кланов")
                    .addLore("§aШифт+ПКМ §7- руководство")
                    .addLore("§aЛКМ §7- серверное меню")
                    .build()
                )
            .rightClickCmd("f")
            .rightShiftClickCmd("f openbook")
            .leftClickCmd("serv")
            .create();
        
        
        //final SpecItem swMenu = new SpecItem("factions", new ItemBuilder(Material.MOJANG_BANNER_PATTERN)
        //    .name("§aЛенная грамота")
        //    .addLore("§aПКМ §7- меню кланов")
        //    .addLore("§aШифт+ПКМ §7- руководство")
         //   .addLore("§aЛКМ §7- серверное меню")
         //   .build()
         //   );
        //swMenu.slot = 8;
        //swMenu.anycase = false;
        //swMenu.can_drop = true;
        //swMenu.can_move = true;
        //swMenu.can_pickup = true;
        //swMenu.duplicate = false;
        //swMenu.give_on_join = true;
       // swMenu.give_on_respavn = true;
        //swMenu.give_on_world_change = true;
        //swMenu.on_right_click = p -> p.performCommand("f");
        //swMenu.on_right_sneak_click = p -> p.performCommand("f openbook");
        //swMenu.on_left_click = p -> p.performCommand("menu");
        //Ostrov.lobby_items.addItem(swMenu);
        
        if (Bukkit.getPluginManager().getPlugin("dynmap")!=null) {
            dynMap = true;
            DynmapHook.createMaps();
        }
        
        
        plugin.getCommand("f").setExecutor(new CommandFaction());
        
        canLogin = true;
    }
    
    
    @Override
    public void onDisable() {
        //for (Island is : IM.islands.values()) { - не надо, сохраняются по мере нужды
        //    is.save(false);
        //}
        
        log_ok("выгружен");
        
    }

    
    
    
    
    

    
   private static void Load_book() {
       
        book = new ItemBuilder(Material.WRITTEN_BOOK)
                .name("Дневник дикаря")
                .build();
        BookMeta bm = (BookMeta) book.getItemMeta();
        bm.setTitle("Miðgarðr");
        bm.setAuthor("комисс77");
        
        TextComponent page = Component.text("§1Впервые попав на ");
        page.append( Component.text("§bсрединные земли").hoverEvent(HoverEvent.showText(Component.text("Miðgarðr — «срединная земля»"))) );
        page.append( Component.text(" §1меня удивило, насколько здесь развита цивилизация. Сердце этой империи - старинный клан, достигший небывалых высот в техническом развитии. Он занимает оргомную территорию, кажется, не меньше 800 ") );
        page.append(  Component.text("§bтерриконов").hoverEvent(HoverEvent.showText(Component.text("Террикон - базовая единица площади Мидгарда, 16*16 блоков."))) );
        page.append( Component.text("§1.") );
        bm.addPages( page);


        page = Component.text("§1И хотя он давно не участвует в дрязгах остальных жителей мида, все защитные механизмы исправны, и никто не осмелится бросить вызов ему или любому другому клану, который смог получить ");
        page.append(  Component.text("§bпокровительсво").hoverEvent(HoverEvent.showText(Component.text("Получают все новые кланы на неделю. Даёт иммунитет к объявлению войны.Можно отказаться, или в дальнейшем продлить."))) );
        page.append( Component.text(" §1этого королевства.") );
        bm.addPages( page);
        

        page = Component.text("§1Итак, я на спавне. Всё, что у меня есть - ");
        page.append(  Component.text("§bленная грамота").hoverEvent(HoverEvent.showText(Component.text("Предмет - меню. Если держать в руке и ПКМ - меню кланов, ЛКМ - серверное меню, Шифт+ПКМ - читать дневник."))) );
        page.append( Component.text(" §1. Все вокруг суетятся, похваляются своими родословными и кричат ") );
        page.append(  Component.text("§b«дикарь, дикарь!»").hoverEvent(HoverEvent.showText(Component.text("Безклановый игрок"))) );
        page.append( Component.text(" §1 Сначала это кажется унизительным. Но потом я понимаю, что переполох вызван не моим странным видом.") );
        bm.addPages( page);

        page = Component.text("§1Изучив ленную грамоту замечаю, что она переполнена предложениями ");
        page.append(  Component.text("§bвступить в клан").hoverEvent(HoverEvent.showText(Component.text("Морковка на удочке в меню"))) );
        page.append( Component.text("§1. Это сейчас я понимаю, что свежая кровь нужна всем как воздух - ведь чем больше жителей в клане, тем больше он может ") );
        page.append(  Component.text("§bприсоединить терриконов").hoverEvent(HoverEvent.showText(Component.text("Нужно поддерживать оптимальный баланс земель: на каждого игрока 2-4 террикона. При перенаселении отключается клан-приват, при недонаселении нет дохода в казну."))) );
        page.append( Component.text("§1.") );
        bm.addPages( page);
        
        page = Component.text("§1И тогда все действительно хотели принять меня в свою семью, а не глумились. Я мог спокойно принять приглашение самого достойного клана, и жить долго и счастливо. Но тогда я «закусил удила» и пошел изучать местность.");
        bm.addPages( page);

        page = Component.text("§1Я познакомился на спавне со многими его обитателями, все они говорили о том, что ");
        page.append(  Component.text("§bДикарям").hoverEvent(HoverEvent.showText(Component.text("Чтобы перестать быть дикарём, нужно войти в клан или создать свой."))) );
        page.append( Component.text(" §1ничем не могут помочь. Некоторые запросили за свою работу оплату, несколько ") );
        page.append(  Component.text("§bлони").hoverEvent(HoverEvent.showText(Component.text("Основная валюта Мидгарда - Звезда Ада. Может применяться в натуральном виде, или безналичном."))) );
        page.append( Component.text("§1. А у меня в кармане мышь повесилась. Нашелся некий местный барон, звали его Майкл.") );
        bm.addPages( page);

        page = Component.text("§1 Он предложил мне работу, что бы получить эти самые лони. Дело казалось нехитрым: в основном, заниматься переработкой или добычей. Конечно, был вариант добыть лони самому в Аду, но чёрт побери, с голой жёпой охотиться на Визеров?! ");
        bm.addPages( page);
        
        page = Component.text("§1Я не в игре, и жизнь у меня только одна. Не найдя никаких подводных камней, согласился подработать ");
        page.append(  Component.text("§bсанитаром").hoverEvent(HoverEvent.showText(Component.text("Один из нескольких видов работ. Выбрать можно только один! При смене прогресс теряется!"))) );
        page.append( Component.text("§1. \"Для начала пойдет\", - подумал я. Далее мое внимание привлек Путешевственник.") );
        bm.addPages( page);

        page = Component.text("§1Мы с ним разговорились, и поведал он мне о многих местах, где еще не бывала нога человека. Предложил показать к ним путь. Заманчиво, ноо... И здесь мало где бывала моя нога. Я отблагодарил его и пошел дальше.");
        bm.addPages( page);

        page = Component.text("§1А дальше вышел на просторы. И тут ждал меня неприятный сюрприз - моя книга рецептов куда-то пропала, и в голове пусто.. Сделать почти ничего невозможно. Только каменный топор. Бродил несколько дней вокруг да около.");
        bm.addPages( page);

        page = Component.text("§1Сделать ничего толком не могу, а жрать охота. Ну, в общем, решился создать свой клан. И снова незадача - сколько хватало глаз, всё вокруг застроено и занято. Отовсюду гонят. А в одном месте ");
        page.append(  Component.text("§bкакое-то строение молниями ").hoverEvent(HoverEvent.showText(Component.text("скорее всего, это была турель Тесла какого-то развитого клана, настроенная на атаку на дикарей."))) );
        page.append( Component.text(" §1меня хорошенько так ") );
        bm.addPages( page);
      
        page = Component.text("§1отжарило, волосы дыбом, еле ноги унёс. И тут вспомнился Путешественник. Да и сумма небольшая к тому времени накопилась, ");
        page.append(  Component.text("§b3 требования для создания своего клана").hoverEvent(HoverEvent.showText(Component.text("В меню дикаря требования на иконке создания - быть на диких землях, 10 терриконов до другого клана, 10 лони пошлина"))) );
        page.append( Component.text(" §1готов был удовлетворить. Нехватало только пару лони.") );
        bm.addPages( page);

        page = Component.text("§1И вот, с помощью доброго НПС, меня перенесло в какое-то далёкое место. Обустроил себе землянку, накопил заветные 10 лони. Местность в целом мне понравилась - красивый ландшафт, зверья да ресурсов сколько хочешь.");
        bm.addPages( page);
        
        page = Component.text("§1Решил обосноваться здесь. И вот, заветный штамп в ");
        page.append(  Component.text("§bленной грамоте").hoverEvent(HoverEvent.showText(Component.text("клик на иконку создания клана в меню дикаря"))) );
        page.append( Component.text(" §1 - и я ") );
        page.append(  Component.text("§bЛИДЕР").hoverEvent(HoverEvent.showText(Component.text("главное звание в клане. Кроме лидера, есть еще офицеры, техники, рядовые и рекруты."))) );
        page.append( Component.text(" §1собственного клана, в котором только я, чикибамбони и пара куриц!!! Сразу навалилось куча обязанностей, и какое-то время ушло, чтобы освоить управление.") );
        bm.addPages( page);

        page = Component.text("§1Но хоть в одном меня точно не обманули - мой ");
        page.append(  Component.text("§bОчень Крутой Клан").hoverEvent(HoverEvent.showText(Component.text("Начальные настройки. Конечно, вы можете сделать всё как надо: название, девиз,цвет и т.д."))) );
        page.append( Component.text(" §1получил заветное покровительство того самого замка, в котором я был, и они будут меня охранять неделю. Видя, какие замки и механизмы есть у других кланов,") );
        bm.addPages( page);

        page = Component.text("§1это было хорошей новостью. Иначе, мне могли объявить войну и просто раскатать в грязь. Конечно, изначально клан был немного лоховcким, выживали с трудом, но от покровительсва не отказывался, продлевал каждую неделю.");
        bm.addPages( page);
        
        page = Component.text("§1Явно требовалось какое-то развитие - прокачка, что-ли... Я решил вернуться на территорию замка и поинтересоваться на этот счёт. Познакомился с Саймоном, он предложил мне помощь в развитии статуса клана, не за просто так, конечно, ");
        bm.addPages( page);

        page = Component.text("§1а в обмен на интересующие его ресурсы. Запросы такие не хилые, но делать нечего, пошел добывать. Одному тяжело как-то. Как тут слышу в городе шум. Прислушался. А там кричат \"Дикарь! Дикарь!\"");
        bm.addPages( page);

       
        page = Component.text("§1И в голове мысль - а почему бы и нет? Помошник нужен. Срочно отправляю сообщение. А, чёрт, забыл.. ");
        page.append(  Component.text("§bЗона 300").hoverEvent(HoverEvent.showText(Component.text("Один из режимов работы чата, сообщения видны в радиусе 300 блоков."))) );
        page.append( Component.text(" §1ломанулся бегом в город. Да, точно дикарь. Свеженький. Кланяюсь и говорю: айда в мой клан, не обижу, в любой момент ") );
        page.append(  Component.text("§bуйти сможешь").hoverEvent(HoverEvent.showText(Component.text("Выйти из клана можно через меню, раздел личное дело"))) );
        page.append( Component.text("§1, выходное пособие") );
        bm.addPages( page);

        page = Component.text("§1обеспечу. Человек согласился, и теперь нас двое - лидер и Рекрут. Вдвоём работа заспорилась. Саймон рассказал мне, что тут очень много кто, предлагает свои услуги по продвижению клана в топ за определенные ресурсы,");
        bm.addPages( page);
        
        page = Component.text("§1для этого их надо искать в старинном городе. Я начал осматриваться и знакомиться со всеми и паралельно развивая свой клан. Так и начнётся моя великая и очень долгая война за звание самой могущественной империи.");
        bm.addPages( page);

        page = Component.text("§1Чувствую, впереди ожидают меня реки.. нет, моря пролитой крови, и великих побед. Но это стоит того, что бы стать самым главным и знаменитым кланом во всем среднем мире. \n\n§4§lВперед к победе!!!");
        bm.addPages( page);

        book.setItemMeta(bm);
       
   } 
    
    
    
    public static void tpLobby(final Player p, final boolean force) {
        if (p.getVehicle()!=null) {
            p.getVehicle().eject();
        }
        if (!force && WarpManager.exist("spawn")) {
            p.performCommand("warp spawn");
        } else {
            //ApiOstrov.teleportSave(p, Bukkit.getWorlds().get(0).getSpawnLocation(), true);
            ApiOstrov.teleportSave(p, Bukkit.getWorld(LOBBY_WORLD_NAME).getSpawnLocation(), true);
        }
        //player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
    }
    
    
    
    
    
    public static void sync(final Runnable runnable, final int delayTicks) { //sync ( ()->{} ,1 );
        if (runnable==null) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(plugin, delayTicks);
    }
    
    public static void async(final Runnable runnable, final int delayTicks) { //sync ( ()->{} ,1 );
        if (runnable==null) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLaterAsynchronously(plugin, delayTicks);
    }
    
    
    
    
    
    
    
    
    
    
    public static void log_ok(String s) {   Bukkit.getConsoleSender().sendMessage("§6[§eКланы§6] "+"§2"+ s); }
    public static void log_warn(String s) {   Bukkit.getConsoleSender().sendMessage("§6[§eКланы§6] "+"§6"+ s); }
    public static void log_err(String s) {
        Bukkit.getConsoleSender().sendMessage("§6[§eКланы§6] "+"§c"+ s);
        try {
            final Connection connection = ApiOstrov.getLocalConnection();
            final PreparedStatement pst1 = connection.prepareStatement("INSERT INTO `errors` (`msg`) VALUES (?);");
//System.out.println("1");
            pst1.setString(1, s);
            pst1.execute();
            pst1.close();

        } catch (SQLException ex) {
            //SW.log_err("не удалось сохранить статистику острова "+owner+" : "+ex.getMessage());  !!Нельзя, зациклит!
            ex.printStackTrace();
        }    
    }

    public static void checkWorldNames(final World w) {
        final File file = new File(plugin.getDataFolder() , "worldNames.yml");
        final YamlConfiguration worldNamesConfig = YamlConfiguration.loadConfiguration(file);
        
        final String nameLenght = String.valueOf(w.getName().length());
        
        if (worldNamesConfig.getString(nameLenght)==null || worldNamesConfig.getString(nameLenght).isEmpty()) { 
            worldNamesConfig.set(nameLenght, w.getName());
            worldNames.put(w.getName().length(), w.getName());
            try {
                worldNamesConfig.save(file);  //если такая длинна не использовалась ранее, первый раз просто сохраняет
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return;
        }
        
        if (worldNamesConfig.getString(nameLenght).equals(w.getName())) { //если длина совпадает со старым названием
            worldNames.put(w.getName().length(), w.getName());
            return; //всё ок
        } 
        //сюда доходит, если длинна не совпадает со старым названием
        if (worldNames.containsKey(w.getName().length()) ) { //если пытаемся загрузить еще один мир с одинаковой длинной
            Bukkit.getConsoleSender().sendMessage("§c=========================================");
            Bukkit.getConsoleSender().sendMessage("§cАлгоритм быстрой обработки локаций");
            Bukkit.getConsoleSender().sendMessage("§cтребует уникальной длинны названия мира!");
            Bukkit.getConsoleSender().sendMessage("§cДлинна названия мира "+w.getName()+" уже занята.");
            Bukkit.getConsoleSender().sendMessage("§cРабота невозможна.");
            Bukkit.getConsoleSender().sendMessage("§c=========================================");
        } else {
            Bukkit.getConsoleSender().sendMessage("§c=========================================");
            Bukkit.getConsoleSender().sendMessage("§cАлгоритм быстрой обработки локаций");
            Bukkit.getConsoleSender().sendMessage("§cтребует уникальной длинны названия мира!");
            Bukkit.getConsoleSender().sendMessage("§cК длинне "+nameLenght+" раннее был привязан мир "+w.getName()+",");
            Bukkit.getConsoleSender().sendMessage("§cСохранённые в БД данные могут работать некорректно!.");
            Bukkit.getConsoleSender().sendMessage("§c=========================================");
        }
        Bukkit.shutdown();

    }
   
    
    
    
    
    
    
    
    
    
    
}
