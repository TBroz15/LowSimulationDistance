package dev.tuxebro.low_simulation_distance.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public abstract class DistanceMixin {

    @Shadow @Final @Mutable
    private OptionInstance<Integer> simulationDistance;

    @Shadow @Final @Mutable
    private OptionInstance<Integer> renderDistance;

    @Unique
    private static Component genericValueLabel(Component component, Component component2) {
        return Component.translatable("options.generic_value", component, component2);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void replaceOptions(CallbackInfo ci) {
        boolean bl = Runtime.getRuntime().maxMemory() >= 1000000000L;

        this.renderDistance = new OptionInstance<>(
                "options.renderDistance",
                OptionInstance.noTooltip(),
                (optionText, value) -> genericValueLabel(optionText, Component.translatable("options.chunks", value)),
                new OptionInstance.IntRange(1, bl ? 32 : 16, false),
                12,
                value -> Minecraft.getInstance().levelRenderer.needsUpdate()
        );

        this.simulationDistance = new OptionInstance<>(
                "options.simulationDistance",
                OptionInstance.noTooltip(),
                (optionText, value) -> genericValueLabel(optionText, Component.translatable("options.chunks", value)),
                new OptionInstance.IntRange(1, bl ? 32 : 16, false),
                12,
                value -> {}
        );
    }
}
