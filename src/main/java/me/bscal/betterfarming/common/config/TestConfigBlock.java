package me.bscal.betterfarming.common.config;

import com.google.gson.annotations.JsonAdapter;
import me.bscal.betterfarming.common.database.blockdata.WorldPos;
import me.bscal.betterfarming.common.utils.Color;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.List;


public class TestConfigBlock extends ConfigBlock
{

	public int value = 12345;

	public String someMessage = "hello this is message";

	public List<Float> list = List.of(1.1f, 5.20f, 100.5f);

	public Color color = new Color(255, 0, 100, 255);

	@JsonAdapter(Identifier.Serializer.class)
	public Identifier id = new Identifier("dirt");

	public HashMap<Integer, WorldPos> map = new HashMap<>();

	public TestConfigBlock()
	{
		super("test");
		map.put(1, new WorldPos(new Identifier("world"), new BlockPos(1, 2, 3)));
		map.put(5, new WorldPos(new Identifier("my","world_nether"),new BlockPos(100, 500, 20000)));
	}

}
