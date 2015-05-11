package hu.denes.command_center.client_connection;

import static org.mockito.Mockito.when;
import hu.denes.command_center.roco_connection.XpressNetRailwayConnection;
import hu.denes.command_center.storage.Storage;
import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NetworkConnectionTest {

	@Mock
	XpressNetRailwayConnection mockedConn;
	@Mock
	Storage storage;

	Loco loco;
	NetworkConnection conn;

	@Before
	public void init() {
		loco = new Loco(1, mockedConn);
		loco.setMaxSpeed(127);
		conn = new NetworkConnection(storage, mockedConn);
	}

	private JSONObject getJValueForLocoDetails(final String s) {
		final JSONObject jAnswer = new JSONObject(s);
		Assert.assertEquals("client", jAnswer.get("target"));
		final JSONObject jFunction = jAnswer.getJSONObject("function");
		Assert.assertEquals("answer-get-loco-details", jFunction.get("type"));
		return jFunction.getJSONObject("value");
	}

	@Test
	public void testGetLocoDetails() {
		when(storage.getLocoByAddress(1)).thenReturn(loco);
		final String l = conn.getLocoDetails(1);
		final JSONObject jValue = getJValueForLocoDetails(l);
		Assert.assertEquals(1, jValue.get("address"));
		Assert.assertEquals(127, jValue.get("max-speed"));
		Assert.assertEquals(0, jValue.get("speed"));
		Assert.assertEquals(128, jValue.get("direction"));
		Assert.assertEquals("", jValue.get("activated-functions"));
		Assert.assertEquals(false, jValue.get("lightsOn"));
	}

	@Test
	public void testGetLocoDetailsAfterControllingLoco() {
		loco.setSpeed(50);
		loco.activateFunction("4");
		loco.activateFunction("5");
		loco.activateFunction("9");
		loco.turnLightsOn();
		loco.changeDirection();
		when(storage.getLocoByAddress(1)).thenReturn(loco);
		final String l = conn.getLocoDetails(1);
		final JSONObject jValue = getJValueForLocoDetails(l);

		Assert.assertEquals(1, jValue.get("address"));
		Assert.assertEquals(127, jValue.get("max-speed"));
		Assert.assertEquals(50, jValue.get("speed"));
		Assert.assertEquals(0, jValue.get("direction"));
		Assert.assertEquals("4,5,9", jValue.get("activated-functions"));
		Assert.assertEquals(true, jValue.get("lightsOn"));
	}

}
