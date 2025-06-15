package com.bendersdestiny.ciscov.abilities.avatar.combos;

import com.bendersdestiny.ciscov.CisCov;
import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.AvatarAbility;
import com.projectkorra.projectkorra.ability.ComboAbility;
import com.projectkorra.projectkorra.ability.util.ComboManager;
import com.projectkorra.projectkorra.util.ClickType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Fog extends AvatarAbility implements AddonAbility, ComboAbility {
    public Fog(Player player) {
        super(player);

        if (!bPlayer.canBend(this)) return;
        if (!bPlayer.hasElement(Element.AVATAR) && !bPlayer.hasElement(Element.WATER) && !bPlayer.hasElement(Element.FIRE)) return;
    }

    @Override
    public void progress() {

    }

    @Override
    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public long getCooldown() {
        return 5000;
    }

    @Override
    public String getName() {
        return "Fog";
    }

    @Override
    public Location getLocation() {
        return null;
    }

    @Override
    public void load() {

    }

    @Override
    public void stop() {

    }

    @Override
    public String getAuthor() {
        return CisCov.getInstance().getDescription().getAuthors().getFirst();
    }

    @Override
    public String getVersion() {
        return CisCov.getInstance().getDescription().getVersion();
    }

    @Override
    public Object createNewComboInstance(Player player) {
        return new Fog(player);
    }

    @Override
    public ArrayList<ComboManager.AbilityInformation> getCombination() {
        ArrayList<ComboManager.AbilityInformation> combo = new ArrayList<>();
        combo.add(new ComboManager.AbilityInformation("", ClickType.SHIFT_DOWN));

        return combo;
    }
}
