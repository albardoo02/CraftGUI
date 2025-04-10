package net.azisaba.life;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class MessageFormatter {

    private final Map<String, BiFunction<Player, Object[], String>> placeholderMap = new HashMap<>();

    public void registerPlaceholder(String placeholder, BiFunction<Player, Object[], String> function) {
        placeholderMap.put("%" + placeholder + "%", function);
    }

    public String format(String message, Player player, Object... args) {
        String formattedMessage = message;
        for (Map.Entry<String, BiFunction<Player, Object[], String>> entry : placeholderMap.entrySet()) {
            formattedMessage = formattedMessage.replace(entry.getKey(), entry.getValue().apply(player, args));
        }
        return formattedMessage;
    }
}