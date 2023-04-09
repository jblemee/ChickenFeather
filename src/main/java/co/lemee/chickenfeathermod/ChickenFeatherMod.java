package co.lemee.chickenfeathermod;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.text.MessageFormat;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ChickenFeatherMod.MODID)
public class ChickenFeatherMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "chickenfeathermod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final String DROPPED_TAG = "chickenfeather:dropped";

    public ChickenFeatherMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Chicken Feather SETUP");
    }

    @SubscribeEvent
    public void onPlayerEntityInteract(PlayerInteractEvent.EntityInteract entityInteract) {
        if (entityInteract.getTarget() instanceof Chicken chicken) {
            if (!chicken.getTags().contains(DROPPED_TAG)) {
                chicken.addTag(DROPPED_TAG);
                chicken.spawnAtLocation(new ItemStack(Items.FEATHER));
            }
        }
        ;
    }

    @SubscribeEvent
    public void onChickenTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Chicken chicken) {
            if(chicken.tickCount % 6000 == 0) {
                chicken.removeTag(DROPPED_TAG);
            }
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("Chicken Feather Mod ACTIVE");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("Chicken Feather Mod SETUP");
        }
    }
}
