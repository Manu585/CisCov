package com.bendersdestiny.ciscov.util;

import com.bendersdestiny.ciscov.CisCov;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CisCovUtil {
    public static void sendCisCovInfo(CommandSender sender) {
        // Get dynamic values from plugin description
        String pluginName = CisCov.getInstance().getDescription().getName();
        String pluginVersion = CisCov.getInstance().getDescription().getVersion();

        sendLine("""
                
                <st><color:#787878>----------</color></st> <color:#e8c410><plugin_name></color> <white>-</white> <color:#e8c410><plugin_version></color> <st><color:#787878>----------</color></st>
                
                             <u><gold>ProjectKorra Addon</gold></u>
                
                                <color:#2efffc>Author:</color> <hover:show_text:'<color:#e8c410>Minecraft:</color> <color:#82ffff>Manunu_</color>\s
                <color:#e8c410>Discord:</color> <color:#82ffff>manuel04_</color>
                <color:#e8c410>Website:</color> <u><color:#82ffff>https://bendersdestiny.com</color></u> \n \n<i><dark_gray>click to open website</dark_gray></i>'><u><color:#fd8aff><click:open_url:'https://bendersdestiny.com'>Manu</click></color></u></hover>
                                <color:#2efffc>Website:</color> <click:open_url:'https://github.com/Manu585/CisCov'><hover:show_text:'<color:#e8c410>Visit the Github repo for the latest build \n \n<dark_gray><i>click to open download link</i></dark_gray></color>'><u><color:#fd8aff>Github</color></u></hover></click>
                
                <st><color:#787878>-------------------------------------</color></st>
                """, sender, pluginName, pluginVersion);
    }


    private static void sendLine(@NotNull String tpl, @NotNull CommandSender sender, String pluginName, String pluginVersion) {
        Component comp = MiniMessage.miniMessage().deserialize(tpl,
                Placeholder.unparsed("plugin_name", pluginName),
                Placeholder.unparsed("plugin_version", pluginVersion)
        );

        // Send directly using Paper's native Adventure support
        sender.sendMessage(comp);
    }
}
