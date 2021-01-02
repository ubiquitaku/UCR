package ubiquitaku.uchiro;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Uchiro extends JavaPlugin {
    FileConfiguration config;
    boolean uc = false;
    boolean pl;
    String prefix = "§a§l[UCR]§r";
    Player p;
    List<Player> list = new ArrayList<>();
    int mo;
    int max;
    int stock;
    Random random = new Random();

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        config = getConfig();
        pl = config.getBoolean("info.pl",false);
        prefix = config.getString("info.name","§a§l[UCR]§r");
        stock = config.getInt("stock",0);
        saveConfig();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("ucr")) {
            if (args.length == 0) {
                sender.sendMessage(prefix);
                sender.sendMessage("----------------------------------------");
                sender.sendMessage("/ucr new <金額> <人数> : ucrを開始します");
                sender.sendMessage("/ucr join : ucrに参加します");
                if (sender.isOp()) {
                    sender.sendMessage("------------------isOp------------------");
                    sender.sendMessage("/ucr stop : ucrを停止します");
                    sender.sendMessage("/ucr on : ucrが可能になるようにします");
                    sender.sendMessage("※強制停止は不可能です");
                }
                sender.sendMessage("----------------------------------------");
                return true;
            }
            if (args[0].equals("new")) {
                if (args.length != 3) {
                    sender.sendMessage(prefix+"引数の数が違っています");
                    return true;
                }
                if (!isNumber(args[1])) {
                    sender.sendMessage(prefix+"金額が数字ではありません");
                    return true;
                }
                if (!isNumber(args[2])) {
                    sender.sendMessage(prefix+"人数が数字ではありません");
                    return true;
                }
                if (!pl) {
                    sender.sendMessage(prefix+"現在UCRは利用できません");
                    return true;
                }
                if (uc) {
                    sender.sendMessage(prefix+"現在UCRが行われているため開始できません");
                    return true;
                }
                if (!HasMoney((Player)sender,Integer.parseInt(args[1]))) {
                    sender.sendMessage(prefix+"あなたの所持金が足りていません");
                    return true;
                }
                uc = true;
                p = (Player) sender;
                mo = Integer.parseInt(args[1]);
                max = Integer.parseInt(args[2]);
                Bukkit.broadcastMessage(prefix+"§l"+sender.getName()+"§r§lが"+args[1]+"円のUCRを始めました\n§l募集人数"+args[2]+"人");
                Runnable();
                return true;
            }
            if (args[0].equals("join")) {
                if (!uc) {
                    sender.sendMessage(prefix+"現在UCRは行われていません");
                }
                if (sender.getName().equals(p.getName())) {
                    sender.sendMessage(prefix+"あなたは親のため現在子としての参かはできません");
                    return true;
                }
                if (list.contains((Player) sender)) {
                    sender.sendMessage(prefix+"あなたは既に参加しています");
                    return true;
                }
                if (!HasMoney((Player) sender,mo)) {
                    sender.sendMessage(prefix+"あなたの所持金が足りていません");
                    return true;
                }
                if (list.size() == max) {
                    sender.sendMessage(prefix+"人数が集まっているため参加できません");
                    return true;
                }
                list.add((Player) sender);
                sender.sendMessage(prefix+"参加しました");
                p.sendMessage(prefix+sender.getName()+"が参加しました");
                for (Player n : list) {
                    n.sendMessage(prefix+sender.getName()+"が参加しました");
                }
                return true;
            }
            if (!sender.isOp()) {
                return true;
            }
            if (args[0].equals("stop")) {
                if (!pl) {
                    sender.sendMessage(prefix+"既にoffになっています");
                    return true;
                }
                pl = false;
                config.set("info.pl",false);
                saveConfig();
                sender.sendMessage(prefix+"停止しました(今UCR中であれば次の開始ができない状態です)");
                return true;
            }
            if (args[0].equals("on")) {
                if (pl) {
                    sender.sendMessage(prefix+"既にonになっています");
                    return true;
                }
                pl = true;
                config.set("info.pl",true);
                saveConfig();
                sender.sendMessage(prefix+"UCRがonになりました");
                return true;
            }
        }
        return true;
    }

    public boolean isNumber(String s) {
        boolean result = true;
        for(int i = 0; i < s.length(); i++) {
            if(Character.isDigit(s.charAt(i))) {
                continue;
            } else {
                result = false;
                break;
            }
        }
        return (boolean) result;
    }

    public boolean HasMoney(Player s, int m) {
        //ここで所持金が足りているかの判定をする、エラー吐かれたくないから今はreturn true;で終わらせておく
        Bukkit.broadcastMessage(prefix+"ヽ(ﾟ∀｡)ﾉｳｪ");
        return true;
    }

    public void Runnable() {
        final int i = 10;
        BukkitRunnable task = new BukkitRunnable() {
            int count = i;
            @Override
            public void run() {
                if (list.size() == max) {
                    p = null;
                    list = new ArrayList<>();
                    max = 0;
                    mo = 0;
                    int r1,r2,r3;
                    r1 = random.nextInt(6);
                    r2 = random.nextInt(6);
                    r3 = random.nextInt(6);
                    Bukkit.broadcastMessage(prefix+"UCRが開始されました");
                    Bukkit.broadcastMessage(prefix+"親はダイスを回して"+r1+"   "+r2+"   "+r3+"が出た");
                    //親が役を出したときのなんか
                    for (Player s :list) {
                        int rr1,rr2,rr3;
                        rr1 = random.nextInt(6);
                        rr2 = random.nextInt(6);
                        rr3 = random.nextInt(6);
                        Bukkit.broadcastMessage(prefix+s.getName()+"はダイスを回して"+rr1+"   "+rr2+"   "+rr3+"が出た");
                        //ここでなんの役なのかとか
                    }
                    //親が役を出さなかったときのなんか
                    cancel();
                }
                if (count == 0) {
                    p = null;
                    list = new ArrayList<>();
                    max = 0;
                    mo = 0;
                    uc = false;
                    Bukkit.broadcastMessage(prefix+"人数が集まらなかったためキャンセルされました");
                    cancel();
                }
                count--;
            }
        };
        task.runTaskTimer(this,20,20);
    }
}
