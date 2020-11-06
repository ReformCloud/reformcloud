/*
 * This file is part of reformcloud2, licensed under the MIT License (MIT).
 *
 * Copyright (c) ReformCloud <https://github.com/ReformCloud>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package systems.reformcloud.reformcloud2.signs.sponge.adapter;

import com.flowpowered.math.vector.Vector3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.block.DirectionalData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import systems.reformcloud.reformcloud2.executor.api.process.ProcessInformation;
import systems.reformcloud.reformcloud2.signs.SharedSignSystemAdapter;
import systems.reformcloud.reformcloud2.signs.sponge.command.SpongeCommandSigns;
import systems.reformcloud.reformcloud2.signs.sponge.listener.SpongeListener;
import systems.reformcloud.reformcloud2.signs.util.PlaceHolderUtil;
import systems.reformcloud.reformcloud2.signs.util.converter.SignConverter;
import systems.reformcloud.reformcloud2.signs.util.sign.CloudSign;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignConfig;
import systems.reformcloud.reformcloud2.signs.util.sign.config.SignSubLayout;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SpongeSignSystemAdapter extends SharedSignSystemAdapter<Sign> {

    private static final Map<String, BlockType> BLOCK_TYPES = new ConcurrentHashMap<>();
    private static SpongeSignSystemAdapter instance;

    static {
        Arrays.stream(BlockTypes.class.getDeclaredFields()).filter(
            e -> Modifier.isFinal(e.getModifiers()) && Modifier.isStatic(e.getModifiers())
        ).forEach(e -> {
            try {
                BLOCK_TYPES.put(e.getName(), (BlockType) e.get(BlockTypes.class));
            } catch (final IllegalAccessException ex) {
                ex.printStackTrace();
            }
        });
    }

    private final Object plugin;

    public SpongeSignSystemAdapter(@NotNull SignConfig signConfig, @NotNull Object plugin) {
        super(signConfig);

        instance = this;

        this.plugin = plugin;
        Sponge.getEventManager().registerListeners(plugin, new SpongeListener());

        CommandSpec signs = CommandSpec
            .builder()
            .description(Text.of("The default signs command of the cloud system"))
            .permission("reformcloud.command.signs")
            .arguments(
                GenericArguments.optional(GenericArguments.string(Text.of("Execute type"))),
                GenericArguments.optional(GenericArguments.string(Text.of("Target group")))
            )
            .executor(new SpongeCommandSigns())
            .build();
        Sponge.getCommandManager().register(plugin, signs, "signs");
    }

    public static SpongeSignSystemAdapter getInstance() {
        return instance;
    }

    @Override
    protected void setSignLines(@NotNull CloudSign cloudSign, @NotNull String[] lines) {
        Sign sign = this.getSignConverter().from(cloudSign);
        if (sign == null) {
            return;
        }

        SignData data = sign.getSignData();
        for (int i = 0; i <= 3; i++) {
            data.setElement(i, Text.of(lines[i]));
        }

        sign.offer(data);
    }

    @Override
    protected void runTasks() {
        Sponge.getScheduler()
            .createTaskBuilder()
            .execute(this::updateSigns)
            .delayTicks(0)
            .intervalTicks(Math.round(20 / super.signConfig.getUpdateInterval()))
            .submit(this.plugin);

        double distance = super.signConfig.getKnockBackDistance();
        Sponge.getScheduler()
            .createTaskBuilder()
            .execute(() -> {
                for (CloudSign sign : this.signs) {
                    Sign spongeSign = this.getSignConverter().from(sign);
                    if (spongeSign == null) {
                        continue;
                    }

                    Vector3d vector = spongeSign.getLocation().getPosition();
                    for (Entity entity : spongeSign.getWorld().getNearbyEntities(vector, distance)) {
                        if (!(entity instanceof Player)) {
                            continue;
                        }

                        Player player = (Player) entity;
                        if (player.hasPermission(super.signConfig.getKnockBackBypassPermission())) {
                            continue;
                        }

                        Vector3d vector3d = player
                            .getLocation()
                            .getPosition()
                            .sub(vector)
                            .normalize()
                            .mul(super.signConfig.getKnockBackStrength());
                        player.setVelocity(new Vector3d(vector3d.getX(), 0.2D, vector3d.getZ()));
                    }
                }
            })
            .delayTicks(20)
            .intervalTicks(5)
            .submit(this.plugin);
    }

    @Override
    protected @NotNull String replaceAll(@NotNull String line, @NotNull String group, @Nullable ProcessInformation processInformation) {
        if (processInformation == null) {
            line = line.replace("%group%", group);
            return TextSerializers.FORMATTING_CODE.deserialize(line).toPlain();
        }

        return PlaceHolderUtil.format(line, group, processInformation, TextSerializers.FORMATTING_CODE::deserialize).toPlain();
    }

    @Override
    public void changeBlock(@NotNull CloudSign sign, @NotNull SignSubLayout layout) {
        Sign spongeSign = this.getSignConverter().from(sign);
        if (spongeSign == null) {
            return;
        }

        this.changeBlock0(spongeSign, layout);
    }

    @Override
    public @NotNull SignConverter<Sign> getSignConverter() {
        return SpongeSignConverter.INSTANCE;
    }

    @Override
    public void handleSignConfigUpdate(@NotNull SignConfig config) {
        super.signConfig = config;
        this.restartTasks();
    }

    private void changeBlock0(@NotNull Sign sign, @NotNull SignSubLayout layout) {
        Optional<DirectionalData> directionalData = sign.getLocation().get(DirectionalData.class);
        if (directionalData.isEmpty()) {
            return;
        }

        DirectionalData data = directionalData.get();
        data.get(Keys.DIRECTION).ifPresent(e -> {
            Location<World> blockRelative = sign.getLocation().getBlockRelative(e.getOpposite());
            BlockType blockType = BLOCK_TYPES.get(layout.getBlock());
            if (blockType == null) {
                return;
            }

            blockRelative.setBlockType(blockType);
        });
    }

    private void restartTasks() {
        Sponge.getScheduler().getScheduledTasks().forEach(Task::cancel);
        this.runTasks();
    }
}
