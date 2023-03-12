package de.eldoria.pickmeup.services.hooks.protection;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.world.PlotAreaManager;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlotSquaredHook extends AProtectionHook {
    private PlotSquared plotSquared;

    public PlotSquaredHook() {
        super("PlotSquared");
    }

    @Override
    public void init(Plugin plugin) {
        plotSquared = PlotSquared.get();
    }

    @Override
    public boolean canInteract(Player player, Entity entity, Location location) {
        PlotAreaManager areaManager = plotSquared.getPlotAreaManager();
        if (!areaManager.hasPlotArea(location.getWorld().getName())) {
            return true;
        }
        PlotArea plotArea = areaManager.getPlotArea(BukkitUtil.adapt(location));
        if (plotArea == null) return true;
        Plot plot = plotArea.getPlot(BukkitUtil.adapt(location));
        if (plot == null) return true;
        return plot.isAdded(player.getUniqueId());
    }
}
