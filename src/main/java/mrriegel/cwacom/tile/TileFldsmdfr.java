package mrriegel.cwacom.tile;

import mrriegel.cwacom.config.ConfigurationHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileFldsmdfr extends TileEntity implements IFluidHandler {

	FluidTank tank = new FluidTank(FluidContainerRegistry.BUCKET_VOLUME
			* ConfigurationHandler.waterCapacity);
	private float angle;
	private boolean active;

	public TileFldsmdfr() {
		super();
		angle = 0.0F;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		tank.readFromNBT(tag);
		angle = tag.getFloat("angle");
		active = tag.getBoolean("active");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tank.writeToNBT(tag);
		tag.setFloat("angle", angle);
		tag.setBoolean("active", active);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		this.writeToNBT(syncData);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord,
				this.zCoord, 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT(pkt.func_148857_g());
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		double renderExtention = 1.0d;
		AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(xCoord
				- renderExtention, yCoord - renderExtention, zCoord
				- renderExtention, xCoord + 1 + renderExtention, yCoord + 1
				+ renderExtention, zCoord + 1 + renderExtention);
		return bb;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (resource == null || !canFill(from, resource.getFluid())) {
			return 0;
		}
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		return tank.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource,
			boolean doDrain) {
		if (tank.getFluid() == null || resource == null
				|| !resource.isFluidEqual(tank.getFluid())) {
			return null;
		}
		return tank.drain(resource.amount, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return tank.drain(maxDrain, doDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return fluid.equals(FluidRegistry.WATER);
	}

	public boolean canFill(ForgeDirection from, Fluid fluid, int amount) {
		if (!canFill(from, fluid))
			return false;
		FluidTankInfo fti = getTankInfo(from)[0];
		if (fti.fluid == null)
			return true;
		if (fti.capacity >= fti.fluid.amount + amount)
			return true;
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { tank.getInfo() };

	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
