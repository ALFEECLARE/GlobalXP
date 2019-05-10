package bl4ckscor3.mod.globalxp;

import java.util.Arrays;

import bl4ckscor3.mod.globalxp.blocks.XPBlock;
import bl4ckscor3.mod.globalxp.itemblocks.ItemBlockXPBlock;
import bl4ckscor3.mod.globalxp.network.ServerProxy;
import bl4ckscor3.mod.globalxp.network.packets.CPacketRequestXPBlockUpdate;
import bl4ckscor3.mod.globalxp.network.packets.SPacketUpdateXPBlock;
import bl4ckscor3.mod.globalxp.tileentity.TileEntityXPBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.GameData;

@Mod(modid=GlobalXP.MOD_ID, name=GlobalXP.NAME, version=GlobalXP.VERSION, acceptedMinecraftVersions="[" + GlobalXP.MC_VERSION + "]", guiFactory=GlobalXP.GUI_FACTORY)
public class GlobalXP
{
	public static final String MOD_ID = "globalxp";
	public static final String NAME = "Global XP";
	public static final String VERSION = "v1.4.3";
	public static final String MC_VERSION = "1.12"; //1.12.1 and 1.12.2 also work
	public static final String GUI_FACTORY = "bl4ckscor3.mod.globalxp.gui.GUIFactory";
	public static Block xp_block;
	@SidedProxy(clientSide = "bl4ckscor3.mod.globalxp.network.ClientProxy", serverSide = "bl4ckscor3.mod.globalxp.network.ServerProxy")
	public static ServerProxy serverProxy;
	public static SimpleNetworkWrapper network;
	public static Config config;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ModMetadata meta = event.getModMetadata();

		meta.authorList = Arrays.asList(new String[]{"bl4ckscor3"});
		meta.autogenerated = false;
		meta.description = "Adds a fast way to store XP and share it with your friends!";
		meta.modId = MOD_ID;
		meta.name = NAME;
		meta.version = VERSION;
		MinecraftForge.EVENT_BUS.register(new bl4ckscor3.mod.globalxp.handlers.EventHandler());
		config = new Config(event.getSuggestedConfigurationFile());
		network = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);
		network.registerMessage(new SPacketUpdateXPBlock.Handler(), SPacketUpdateXPBlock.class, 0, Side.CLIENT);
		network.registerMessage(new CPacketRequestXPBlockUpdate.Handler(), CPacketRequestXPBlockUpdate.class, 1, Side.SERVER);
		xp_block = new XPBlock(Material.IRON);
		GameData.register_impl(xp_block);
		GameData.register_impl(new ItemBlockXPBlock(xp_block).setRegistryName(xp_block.getRegistryName().toString()));
		GameRegistry.registerTileEntity(TileEntityXPBlock.class, xp_block.getRegistryName().toString());
		serverProxy.loadModels();
		serverProxy.registerRenderers();
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		FMLInterModComms.sendMessage("waila", "register", "bl4ckscor3.mod.globalxp.imc.waila.WailaDataProvider.callbackRegister");
		FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "bl4ckscor3.mod.globalxp.imc.top.GetTheOneProbe");
	}
}
