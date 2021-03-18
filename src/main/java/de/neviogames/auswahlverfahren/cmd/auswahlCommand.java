package de.neviogames.auswahlverfahren.cmd;

import de.neviogames.auswahlverfahren.main.awv;
import de.neviogames.auswahlverfahren.utils.BookAPI;
import de.neviogames.auswahlverfahren.utils.House;
import de.neviogames.auswahlverfahren.utils.edits.argument;
import de.neviogames.auswahlverfahren.utils.edits.command;
import de.neviogames.auswahlverfahren.utils.edits.perm;
import de.neviogames.auswahlverfahren.utils.randomizer;
import de.neviogames.nglib.utils.io.MathUtil;
import de.neviogames.nglib.utils.io.sendMessage;
import de.neviogames.nglib.utils.misc.FormatUtil;
import de.neviogames.nglib.utils.utility;
import de.neviogames.auswahlverfahren.utils.Configuration;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class auswahlCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        //SET COMMAND PROPERTIES
        cmd.setPermissionMessage(sendMessage.getNoPermMessage(awv.getPrefix()));
        cmd.setDescription("Ziehe die Teilnehmer");
        cmd.setUsage(sendMessage.createUsage(awv.getPrefix(), command.auswahl, new String[][]{{argument.Gryffindor, argument.Hufflepuff, argument.Ravenclaw, argument.Slytherin}, {"[RandomZahl]"}}));

        //EXECUTE COMMAND
        if(cmd.getName().equalsIgnoreCase(command.auswahl)) {
            if(sender.hasPermission(perm.auswahl)) {
                if(sender instanceof Player) {
                    Player p = (Player) sender;
                    if(utility.isAdmin(p) && utility.isWizard(p)) {
                        if(!Configuration.getInstance().isEnabled()) {
                            sender.sendMessage(awv.getPrefix() + "Es gibt zurzeit kein Event.");
                        }

                        if(args.length < 2) {
                            sendMessage.lessArgs(sender, awv.getPrefix());
                            return false;
                        }

                        if(args.length == 2) {

                            House house = House.fromName(args[0]);
                            if(house==null) {
                                sender.sendMessage(awv.getPrefix()+ ChatColor.RED + "Das Haus " +ChatColor.GOLD+ args[0] +ChatColor.RED+ " existiert nicht.");
                                return false;
                            }

                            if(utility.isNumber(args[1])){
                                int eingabezahl = Integer.parseInt(args[1]);

                                ArrayList<UUID> randomList = randomizer.randomlist(house, eingabezahl);
                                if(randomList.size() >= 8) {
                                    String s1 = "Folgende Spieler haben sich für die "+ChatColor.RED+ChatColor.BOLD+"erste Aufgabe "+ChatColor.BLACK+"im Haus "+house.getColor()+house.name()+ChatColor.BLACK+" qualifiziert:";
                                    awv.getInstance().getLogger().info(FormatUtil.stripFormat(s1));
                                    String page1 = s1+"\n\n";
                                    for(int i = 0; i < 4; i++) {
                                        UUID kandidat = randomList.get(MathUtil.fairRoundedRandom(0, randomList.size()));
                                        String KandidatName = utility.getNameFromUUID(kandidat);
                                        awv.getInstance().getLogger().info(i+". "+ KandidatName);
                                        page1 = page1 + ChatColor.BOLD + i+". " + house.getColor()+ KandidatName +"\n";
                                        randomList.remove(kandidat);
                                    }

                                    String s2 ="Folgende Spieler haben sich für die "+ChatColor.DARK_BLUE+ChatColor.BOLD+"zweite Aufgabe "+ChatColor.BLACK+"im Haus "+house.getColor()+house.name()+ChatColor.BLACK+" qualifiziert:";
                                    awv.getInstance().getLogger().info(FormatUtil.stripFormat(s2));
                                    String page2 = s2+"\n\n";
                                    for(int i = 0; i < 4; i++) {
                                        UUID kandidat = randomList.get(MathUtil.fairRoundedRandom(0, randomList.size()));
                                        String KandidatName = utility.getNameFromUUID(kandidat);
                                        awv.getInstance().getLogger().info(i+". "+ KandidatName);
                                        page2 = page2 + ChatColor.BOLD + i+". " + house.getColor() + KandidatName +"\n";
                                        randomList.remove(kandidat);
                                    }

                                    BookAPI book = new BookAPI();
                                    book.setAuthor(ChatColor.AQUA+"Auswahlverfahren");
                                    book.setDisplayName(""+house.getColor()+ChatColor.BOLD+house.name());
                                    book.setTitle("Die Auserw"+utility.ae+"hlten von " +house.getColor()+ house.name());
                                    book.setPages(page1, page2);


                                    for (int i = 0; i < 9; i++) {
                                        if(p.getInventory().getItem(i) == null) {
                                            p.getInventory().setItem(i, book.create());
                                            String time = new SimpleDateFormat("hh:mm:ss").format(System.currentTimeMillis()) + " ";
                                            saveKandidaten(time+page1+"\n\n"+time+page2+"\n\n\n");
                                            return true;
                                        }
                                    }

                                    sender.sendMessage(awv.getPrefix() + ChatColor.RED + ChatColor.BOLD + "Leere deine Inventarleiste aus! Befehl konnte nicht ausgef"+utility.ue+"hrt werden.");

                                } else {
                                    sender.sendMessage(awv.getPrefix() + ChatColor.RED+ "Es haben sich nicht genug Spieler im Haus " +house.getColor()+ house.name() +ChatColor.RED+ " angemeldet.");
                                }

                            }
                            return true;
                        }
                        return false;
                    } else {
                        sender.sendMessage(awv.getPrefix() + "Du musst ein Admin sein, um diesen Befehl auszuf"+utility.ue+"rehen.");
                    }
                } else {
                    sendMessage.noPlayer(sender, awv.getPrefix());
                }
            } else {
                sendMessage.noPerm(sender, awv.getPrefix());
            }
            return true;
        }

        return false;
    }

    private static void saveKandidaten(String kandidatName) {
        try {
            String error = FormatUtil.stripFormat(kandidatName);
            error += "\n\n";

            FileWriter outStream = new FileWriter("plugins/Auswahlverfahren/auswahl.txt", true);
            BufferedWriter out = new BufferedWriter(outStream);
            out.append(error.replaceAll("\n", System.getProperty("line.separator")));
            out.close();
            outStream.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    //EXECUTE TAB-COMPLETE
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase(command.auswahl)) {
            List<String> completions = new ArrayList<>();
            List<String> nextArgs = new ArrayList<>();
            if(sender.hasPermission(perm.auswahl)) {
                if(args.length == 1) {
                    nextArgs.add(argument.Gryffindor);
                    nextArgs.add(argument.Hufflepuff);
                    nextArgs.add(argument.Slytherin);
                    nextArgs.add(argument.Ravenclaw);
                    StringUtil.copyPartialMatches(args[0], nextArgs, completions);

                } else if(args.length == 2) {
                    nextArgs.add("1");
                    nextArgs.add("5");
                    nextArgs.add("7");
                    StringUtil.copyPartialMatches(args[1], nextArgs, completions);
                }
            }

            Collections.sort(completions);
            return completions;
        }
        return Collections.emptyList();
    }
}