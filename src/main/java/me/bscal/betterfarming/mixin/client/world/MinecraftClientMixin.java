package me.bscal.betterfarming.mixin.client.world;

import me.bscal.betterfarming.common.events.ClientWorldEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin
{

	@Inject(method = "joinWorld", at = @At(value = "TAIL"))
	public void OnJoinWorld(ClientWorld world, CallbackInfo ci)
	{
		ClientWorldEvent.CLIENT_JOIN_WORLD_EVENT.invoker().OnClientJoinWorld((MinecraftClient) (Object) this, world);
	}

	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At(value = "HEAD"))
	public void OnDisconnect(Screen screen, CallbackInfo ci)
	{
		MinecraftClient client = (MinecraftClient) (Object) this;
		ClientWorldEvent.CLIENT_DISCONNECT_WORLD_EVENT.invoker().OnClientDisconnectWorld(client, client.world);
	}

}
