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
    int sum;

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
                runnable();
                return true;
            }
            if (args[0].equals("join")) {
                if (!uc) {
                    sender.sendMessage(prefix+"現在UCRは行われていません");
                    return true;
                }
                try {
                    if (sender.getName().equals(p.getName())) {
                        sender.sendMessage(prefix + "あなたは親のため現在子としての参かはできません");
                        return true;
                    }
                } catch (NullPointerException e) {

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
        //ここで所持金が足りているかの判定をする(sがm以上持っているか)、エラー吐かれたくないから今はreturn true;で終わらせておく
        return true;
    }

    public void runnable() {
        final int i = 10;
        BukkitRunnable task = new BukkitRunnable() {
            int count = i;
            @Override
            public void run() {
                if (list.size() == max) {
                    List<Player> pls = list;
                    String yaku = "noyaku";
                    p = null;
                    sum = mo*(list.size()+1);
                    list = new ArrayList<>();
                    max = 0;
                    mo = 0;
                    if (!HasMoney(p,mo)) {
                        Bukkit.broadcastMessage(prefix+"親の所持金が足りていなかったため中断します");
                        cancel();
                        return;
                    }
                    PickMoney(p,mo);
                    for (Player p:pls) {
                        if (!HasMoney(p,mo)) {
                            Bukkit.broadcastMessage(prefix+"子の所持金が足りていなかったため中断します");
                            cancel();
                            return;
                        }
                        PickMoney(p,mo);
                    }
                    int r1,r2,r3;
                    r1 = random.nextInt(6)+1;
                    r2 = random.nextInt(6)+1;
                    r3 = random.nextInt(6)+1;
                    Bukkit.broadcastMessage(prefix+"UCRが開始されました");
                    Bukkit.broadcastMessage(prefix+"親はダイスを回して"+r1+"   "+r2+"   "+r3+"が出た");
                    //親が役を出したときのなんか
                    yaku = config.getString("hit."+r1+"."+r2+"."+r3);
                    if (yaku == null) {
                        yaku = "noyaku";
                    }
                    if (!yaku.equals("noyaku")) {
                        GiveMoney(p,sum);
                        Bukkit.broadcastMessage(prefix+yaku);
                        Bukkit.broadcastMessage(prefix+"親が役を出したため親が全て回収します");
                        uc = false;
                        cancel();
                        return;
                    }
                    if (yaku.equals("out")) {
                        config.set("stock",config.getInt("stock",0)+sum);
                        saveConfig();
                        Bukkit.broadcastMessage(prefix+"没収！\n没収されたお金はストックされます");
                        uc = false;
                        cancel();
                        return;
                    }
                    Bukkit.broadcastMessage(prefix+"親役無し");
                    int hit = 0;
                    int roll = 0;
                    List<Player> l = new ArrayList<>();
                    for (Player s : pls) {
                        yaku = "noyaku";
                        int rr1,rr2,rr3;
                        rr1 = random.nextInt(6)+1;
                        rr2 = random.nextInt(6)+1;
                        rr3 = random.nextInt(6)+1;
                        Bukkit.broadcastMessage(prefix+s.getName()+"はダイスを回して"+rr1+"   "+rr2+"   "+rr3+"が出た");
                        //ここでなんの役なのかとか
                        try {
                            yaku = config.getString("hit."+rr1+"."+rr2+"."+rr3);
                        } catch (NullPointerException e) {
                            yaku = "noyaku";
                        }
                        if (yaku == null) {
                            yaku = "noyaku";
                        }
                        if (yaku.equals("out")) {
                            config.set("stock",config.getInt("stock",0)+sum);
                            saveConfig();
                            Bukkit.broadcastMessage(prefix+"没収！\n没収されたお金はストックされます");
                            uc = false;
                            cancel();
                            return;
                        }
                        Bukkit.broadcastMessage("ヽ(ﾟ∀｡)ﾉｳｪ");
                        if (!yaku.equals("noyaku")) {
                            l.add(pls.get(roll));
                            hit++;
                        } else {
                            Bukkit.broadcastMessage(prefix+"子役無し");
                        }
                        roll++;
                    }
                    if (hit == 0) {
                        Bukkit.broadcastMessage(prefix+"役を出した人がいなかったため没収されます");
                        config.set("stock",config.getInt("stock")+sum);
                        saveConfig();
                    } else {
                        Bukkit.broadcastMessage(prefix+"役を出したプレイヤーにおかねを配分します");
                        for (int z = 0 ;z > hit;z++) {
                            GiveMoney(list.get(z),sum/hit);
                        }
                    }
                    uc = false;
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

    public void PickMoney(Player p,int money) {
        //moneyをpから取る
        return;
    }

    public void GiveMoney(Player p,int money) {
        //money円をpに配る
        return;
    }
}
