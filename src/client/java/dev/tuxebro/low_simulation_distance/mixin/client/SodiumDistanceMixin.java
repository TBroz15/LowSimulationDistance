package dev.tuxebro.low_simulation_distance.mixin.client;

import com.google.common.collect.ImmutableList;
import net.caffeinemc.mods.sodium.client.gui.options.*;
import net.caffeinemc.mods.sodium.client.gui.options.control.Control;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlValueFormatter;
import net.caffeinemc.mods.sodium.client.gui.options.control.SliderControl;
import net.caffeinemc.mods.sodium.client.gui.options.storage.MinecraftOptionsStorage;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import net.caffeinemc.mods.sodium.client.gui.SodiumGameOptionPages;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

// dear future self, I felt like brain is big ouch
// i wanna go back using golang
// -TuxeBro

@Mixin(value = SodiumGameOptionPages.class, remap = false)
public class SodiumDistanceMixin {
    @Unique
    private static OptionImpl<Options, Integer> alterOptions(
            Option<?> option,
            BiConsumer<Options, Integer> bindingSetter,
            Function<Options, Integer> bindingGetter
            ) {

       return OptionImpl.createBuilder(int.class, (MinecraftOptionsStorage) option.getStorage())
                .setName(option.getName())
                .setTooltip(option.getTooltip())
                .setControl(option1 -> new SliderControl(option1, 1, 32, 1, ControlValueFormatter.translateVariable("options.chunks")))
                .setBinding(bindingSetter, bindingGetter)
                .setImpact(OptionImpact.HIGH)
                .setFlags(OptionFlag.REQUIRES_RENDERER_RELOAD)
                .build();
    }

    @Inject(method = "general", at = @At("RETURN"), cancellable = true)
    private static void onGetGeneralPage(CallbackInfoReturnable<OptionPage> cir) {
        OptionPage page = cir.getReturnValue();
        List<OptionGroup> newGroups = new ArrayList<>();

        var renderDistTooltip =  Component.translatable("sodium.options.view_distance.tooltip");
        var simulationDistTooltip =  Component.translatable("sodium.options.simulation_distance.tooltip");


        for (OptionGroup group : page.getGroups()) {
            OptionGroup.Builder groupBuilder = OptionGroup.createBuilder();

            for (Option<?> option : group.getOptions()) {
                if (option.getTooltip().equals(renderDistTooltip)) {
                    option = alterOptions(option, (options, value) -> options.renderDistance().set(value), options -> options.renderDistance().get());
                } else if (option.getTooltip().equals(simulationDistTooltip)) {
                    option = alterOptions(option, (options, value) -> options.simulationDistance().set(value), options -> options.simulationDistance().get());
                }

                groupBuilder.add(option);
            }
            newGroups.add(groupBuilder.build());
        }

        // Return the modified page
        cir.setReturnValue(new OptionPage(page.getName(), ImmutableList.copyOf(newGroups)));
    }
}