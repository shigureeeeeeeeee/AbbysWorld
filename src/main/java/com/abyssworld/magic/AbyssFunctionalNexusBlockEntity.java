package com.abyssworld.magic;

import com.abyssworld.block.AbyssFunctionalNexusBlock;
import com.abyssworld.entity.AbyssBossEntity;
import com.abyssworld.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;
import java.util.List;

public class AbyssFunctionalNexusBlockEntity extends BlockEntity implements Container, MenuProvider {
    private static final int NETWORK_RANGE = 64;
    private static final int INVENTORY_SIZE = 9;
    private static final int CULTIVATION_SCAN_BUDGET = 96;
    private static final int MAX_ACTIONS_PER_CYCLE = 6;

    private final NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
    private LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> new InvWrapper(this));
    private Status status = Status.IDLE;
    private int operations;
    private int scanCursor;

    public AbyssFunctionalNexusBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ABYSS_FUNCTIONAL_NEXUS.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                  AbyssFunctionalNexusBlockEntity nexus) {
        if (!(level instanceof ServerLevel serverLevel)
                || level.getGameTime() % 20L != Math.floorMod(pos.asLong(), 20)) {
            return;
        }
        if (level.hasNeighborSignal(pos)) {
            nexus.setStatus(Status.REDSTONE_PAUSED);
            return;
        }
        AbyssFunctionalNexusBlock.Kind kind = nexus.kind();
        if (kind == null) {
            nexus.setStatus(Status.IDLE);
            return;
        }
        ManaSession mana = new ManaSession(serverLevel, pos);
        switch (kind) {
            case VERDANT -> nexus.cultivate(serverLevel, mana, kind);
            case GATHERING -> nexus.gather(serverLevel, mana, kind);
            case WARDING -> nexus.ward(serverLevel, mana, kind);
        }
    }

    private void cultivate(ServerLevel level, ManaSession mana, AbyssFunctionalNexusBlock.Kind kind) {
        int width = kind.radius() * 2 + 1;
        int volume = width * width * width;
        int grown = 0;
        boolean foundTarget = false;
        for (int checked = 0; checked < CULTIVATION_SCAN_BUDGET && grown < 3; checked++) {
            int index = Math.floorMod(scanCursor++, volume);
            int dx = index % width - kind.radius();
            int dz = index / width % width - kind.radius();
            int dy = index / (width * width) - kind.radius();
            BlockPos target = worldPosition.offset(dx, dy, dz);
            if (!level.hasChunkAt(target)) {
                continue;
            }
            BlockState targetState = level.getBlockState(target);
            if (!(targetState.getBlock() instanceof BonemealableBlock bonemealable)
                    || !bonemealable.isValidBonemealTarget(level, target, targetState, false)) {
                continue;
            }
            foundTarget = true;
            if (!bonemealable.isBonemealSuccess(level, level.random, target, targetState)) {
                continue;
            }
            if (!mana.consume(kind.manaCost())) {
                setStatus(Status.NO_MANA);
                return;
            }
            bonemealable.performBonemeal(level, level.random, target, targetState);
            sendBeam(level, Vec3.atCenterOf(worldPosition).add(0.0D, 0.6D, 0.0D),
                    Vec3.atCenterOf(target), ParticleTypes.HAPPY_VILLAGER, 32);
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, target.getX() + 0.5D,
                    target.getY() + 0.7D, target.getZ() + 0.5D, 10,
                    0.35D, 0.35D, 0.35D, 0.02D);
            grown++;
            operations++;
        }
        setStatus(grown > 0 ? Status.ACTIVE : foundTarget ? Status.IDLE : Status.NO_TARGET);
    }

    private void gather(ServerLevel level, ManaSession mana, AbyssFunctionalNexusBlock.Kind kind) {
        List<ItemEntity> entities = level.getEntitiesOfClass(ItemEntity.class,
                new AABB(worldPosition).inflate(kind.radius()), entity -> entity.isAlive() && !entity.getItem().isEmpty());
        if (entities.isEmpty()) {
            setStatus(Status.NO_TARGET);
            return;
        }
        entities.sort((left, right) -> Double.compare(left.distanceToSqr(Vec3.atCenterOf(worldPosition)),
                right.distanceToSqr(Vec3.atCenterOf(worldPosition))));
        int gathered = 0;
        boolean full = true;
        for (ItemEntity entity : entities) {
            if (gathered >= MAX_ACTIONS_PER_CYCLE) {
                break;
            }
            ItemStack source = entity.getItem();
            ItemStack simulatedRemainder = insert(source.copy(), true);
            int movable = source.getCount() - simulatedRemainder.getCount();
            if (movable <= 0) {
                continue;
            }
            full = false;
            int cost = kind.manaCost() + movable;
            if (!mana.consume(cost)) {
                setStatus(Status.NO_MANA);
                return;
            }
            ItemStack moving = source.copyWithCount(movable);
            ItemStack remainder = insert(moving, false);
            int inserted = movable - remainder.getCount();
            if (inserted <= 0) {
                continue;
            }
            source.shrink(inserted);
            if (source.isEmpty()) {
                entity.discard();
            } else {
                entity.setItem(source);
            }
            sendBeam(level, entity.position().add(0.0D, 0.25D, 0.0D),
                    Vec3.atCenterOf(worldPosition).add(0.0D, 0.7D, 0.0D), ParticleTypes.REVERSE_PORTAL, 36);
            gathered += inserted;
            operations += inserted;
        }
        setStatus(gathered > 0 ? Status.ACTIVE : full ? Status.OUTPUT_FULL : Status.IDLE);
    }

    private void ward(ServerLevel level, ManaSession mana, AbyssFunctionalNexusBlock.Kind kind) {
        List<Monster> monsters = level.getEntitiesOfClass(Monster.class,
                new AABB(worldPosition).inflate(kind.radius()), monster -> monster.isAlive()
                        && !(monster instanceof AbyssBossEntity) && !(monster instanceof WitherBoss));
        if (monsters.isEmpty()) {
            setStatus(Status.NO_TARGET);
            return;
        }
        monsters.sort((left, right) -> Double.compare(left.distanceToSqr(Vec3.atCenterOf(worldPosition)),
                right.distanceToSqr(Vec3.atCenterOf(worldPosition))));
        int affected = 0;
        for (Monster monster : monsters) {
            if (affected >= MAX_ACTIONS_PER_CYCLE) {
                break;
            }
            if (!mana.consume(kind.manaCost())) {
                setStatus(Status.NO_MANA);
                return;
            }
            Vec3 away = monster.position().subtract(Vec3.atCenterOf(worldPosition));
            if (away.lengthSqr() < 0.01D) {
                away = new Vec3(0.0D, 0.0D, 1.0D);
            }
            monster.setDeltaMovement(monster.getDeltaMovement().scale(0.2D)
                    .add(away.normalize().scale(0.85D)).add(0.0D, 0.18D, 0.0D));
            monster.hurtMarked = true;
            monster.hurt(level.damageSources().magic(), 2.0F);
            sendBeam(level, Vec3.atCenterOf(worldPosition).add(0.0D, 0.8D, 0.0D),
                    monster.getEyePosition(), ParticleTypes.SOUL_FIRE_FLAME, 36);
            affected++;
            operations++;
        }
        setStatus(affected > 0 ? Status.ACTIVE : Status.NO_TARGET);
    }

    private ItemStack insert(ItemStack stack, boolean simulate) {
        ItemStack remainder = stack;
        IItemHandler handler = itemHandler.orElse(null);
        return handler == null ? stack : ItemHandlerHelper.insertItemStacked(handler, remainder, simulate);
    }

    public void showRange() {
        if (!(level instanceof ServerLevel serverLevel) || kind() == null) {
            return;
        }
        int radius = kind().radius();
        for (int point = 0; point < 48; point++) {
            double angle = Math.PI * 2.0D * point / 48.0D;
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    worldPosition.getX() + 0.5D + Math.cos(angle) * radius,
                    worldPosition.getY() + 0.35D,
                    worldPosition.getZ() + 0.5D + Math.sin(angle) * radius,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        List<AbyssManaPoolBlockEntity> pools = AbyssManaNetwork.pools(serverLevel, worldPosition, NETWORK_RANGE);
        if (!pools.isEmpty()) {
            sendBeam(serverLevel, Vec3.atCenterOf(pools.get(0).getBlockPos()),
                    Vec3.atCenterOf(worldPosition), ParticleTypes.WITCH, 64);
        }
    }

    private AbyssFunctionalNexusBlock.Kind kind() {
        return getBlockState().getBlock() instanceof AbyssFunctionalNexusBlock block ? block.kind() : null;
    }

    public Status status() {
        return status;
    }

    public int operations() {
        return operations;
    }

    private void setStatus(Status next) {
        if (status != next) {
            status = next;
            setChanged();
        }
    }

    private static void sendBeam(ServerLevel level, Vec3 start, Vec3 end,
                                 ParticleOptions particle, int maxSteps) {
        double distance = start.distanceTo(end);
        int steps = Mth.clamp((int) (distance * 2.0D), 2, maxSteps);
        for (int step = 0; step <= steps; step++) {
            Vec3 point = start.lerp(end, (double) step / steps);
            level.sendParticles(particle, point.x, point.y, point.z,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public Component getDisplayName() {
        AbyssFunctionalNexusBlock.Kind kind = kind();
        return Component.translatable(switch (kind == null ? AbyssFunctionalNexusBlock.Kind.VERDANT : kind) {
            case VERDANT -> "block.abyssworld.verdant_nexus";
            case GATHERING -> "block.abyssworld.gathering_nexus";
            case WARDING -> "block.abyssworld.warding_nexus";
        });
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return kind() == AbyssFunctionalNexusBlock.Kind.GATHERING
                ? new ChestMenu(MenuType.GENERIC_9x1, id, inventory, this, 1) : null;
    }

    @Override
    public int getContainerSize() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack result = ContainerHelper.removeItem(items, slot, count);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) {
            stack.setCount(getMaxStackSize());
        }
        setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return level != null && level.getBlockEntity(worldPosition) == this
                && player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D,
                worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction side) {
        return kind() == AbyssFunctionalNexusBlock.Kind.GATHERING && capability == ForgeCapabilities.ITEM_HANDLER
                ? itemHandler.cast() : super.getCapability(capability, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        itemHandler = LazyOptional.of(() -> new InvWrapper(this));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
        tag.putInt("Operations", operations);
        tag.putInt("ScanCursor", scanCursor);
        tag.putString("Status", status.name());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, items);
        operations = Math.max(0, tag.getInt("Operations"));
        scanCursor = Math.max(0, tag.getInt("ScanCursor"));
        try {
            status = Status.valueOf(tag.getString("Status"));
        } catch (IllegalArgumentException ignored) {
            status = Status.IDLE;
        }
    }

    public enum Status {
        IDLE,
        ACTIVE,
        NO_TARGET,
        NO_MANA,
        OUTPUT_FULL,
        REDSTONE_PAUSED
    }

    private static final class ManaSession {
        private final ServerLevel level;
        private final BlockPos nexusPos;
        private final List<AbyssManaPoolBlockEntity> pools;
        private boolean visualized;

        private ManaSession(ServerLevel level, BlockPos nexusPos) {
            this.level = level;
            this.nexusPos = nexusPos;
            this.pools = AbyssManaNetwork.pools(level, nexusPos, NETWORK_RANGE);
        }

        private boolean consume(int amount) {
            if (AbyssManaNetwork.storedMana(pools) < amount
                    || AbyssManaNetwork.consumeUpTo(pools, amount) != amount) {
                return false;
            }
            if (!visualized && !pools.isEmpty()) {
                sendBeam(level, Vec3.atCenterOf(pools.get(0).getBlockPos()),
                        Vec3.atCenterOf(nexusPos), ParticleTypes.WITCH, 64);
                visualized = true;
            }
            return true;
        }
    }
}
