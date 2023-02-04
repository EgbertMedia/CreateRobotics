package com.workert.robotics.entities;

import com.workert.robotics.Robotics;
import com.workert.robotics.lists.ItemList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.world.ForgeChunkManager;

public class CodeDrone extends AbstractRobotEntity implements FlyingAnimal {
	private final SimpleContainer inventory = new SimpleContainer(9);

	public int last_chunk_x;
	public int last_chunk_z;

	public CodeDrone(EntityType<? extends PathfinderMob> entity, Level world) {
		super(entity, world);
		this.moveControl = new FlyingMoveControl(this, 128, true);
		this.last_chunk_x = this.chunkPosition().x;
		this.last_chunk_z = this.chunkPosition().z;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		compound.putInt("lastChunkX", this.last_chunk_x);
		compound.putInt("lastChunkZ", this.last_chunk_z);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		this.last_chunk_x = compound.getInt("lastChunkX");
		this.last_chunk_z = compound.getInt("lastChunkZ");
	}

	@Override
	public void tick() {
		if (!this.level.isClientSide) {
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					ForgeChunkManager.forceChunk((ServerLevel) this.level, Robotics.MOD_ID, this, this.last_chunk_x + i,
							this.last_chunk_z + j,
							false, true);
					ForgeChunkManager.forceChunk((ServerLevel) this.level, Robotics.MOD_ID, this,
							this.chunkPosition().x + i, this.chunkPosition().z + j,
							false, true);
				}
			}
		}
		super.tick();
	}

	@Override
	public void remove(RemovalReason pReason) {
		if (!this.level.isClientSide()) {
			for (int i = -1; i <= 1; i++) {
				for (int j = -1; j <= 1; j++) {
					ForgeChunkManager.forceChunk((ServerLevel) this.level, Robotics.MOD_ID, this, this.last_chunk_x + i,
							this.last_chunk_z + j,
							false, true);
				}
			}
		}
		super.remove(pReason);
	}

	@Override
	public boolean isFlying() {
		return !this.isOnGround();
	}

	public static AttributeSupplier createAttributes() {
		return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.2F).add(Attributes.MAX_HEALTH, 1.0D)
				.add(Attributes.FLYING_SPEED, 0.8F).build();
	}

	@Override
	protected PathNavigation createNavigation(Level level) {
		FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, level);
		flyingpathnavigation.setCanOpenDoors(false);
		flyingpathnavigation.setCanFloat(true);
		flyingpathnavigation.setCanPassDoors(false);
		return flyingpathnavigation;
	}

	@Override
	public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
		return false;
	}

	@Override
	public Item getRobotItem() {
		return ItemList.CODE_DRONE.get();
	}

	@Override
	public boolean isProgrammable() {
		return true;
	}

	@Override
	public boolean hasInventory() {
		return true;
	}

}
