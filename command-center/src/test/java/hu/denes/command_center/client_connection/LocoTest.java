package hu.denes.command_center.client_connection;

import hu.denes.command_center.roco_connection.XpressNetRailwayConnection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LocoTest {

	Loco loco;

	@Mock
	XpressNetRailwayConnection mockedConn;

	@Before
	public void init() {
		loco = new Loco(1, mockedConn);
		loco.setMaxSpeed(127);
	}

	@Test
	public void testSpeedIsSetCorrectly() {
		loco.setSpeed(10);
		Mockito.verify(mockedConn).setSpeed(1, 10 + 128, loco.getMaxSpeed());
	}

	@Test
	public void testSpeedIsSetCorrectlyReversed() {
		loco.setDirection(0);
		loco.setSpeed(10);
		Mockito.verify(mockedConn).setSpeed(1, 10, loco.getMaxSpeed());
	}

	@Test
	public void testSpeedIsSetCorrectlyWhenRemoteLocos() {
		final Loco loco2 = new Loco(2, mockedConn);
		loco.addRemoteLoco(loco2);
		loco.setSpeed(50);
		Mockito.verify(mockedConn).setSpeed(1, 178, loco.getMaxSpeed());
		Mockito.verify(mockedConn).setSpeed(2, 178, loco.getMaxSpeed());
	}

	@Test
	public void testSpeedIsSetCorrectlyWhenRemoteLocosOneOfThemReversed() {
		final Loco loco2 = new Loco(2, mockedConn);
		loco2.setDirection(0);
		loco.addRemoteLoco(loco2);
		loco.setSpeed(50);
		Mockito.verify(mockedConn).setSpeed(1, 178, loco.getMaxSpeed());
		Mockito.verify(mockedConn).setSpeed(2, 50, loco.getMaxSpeed());
	}

	@Test
	public void testSetSpeedGreaterThanMax() {
		loco.setSpeed(200);
		Assert.assertEquals(127, loco.getSpeed());
	}

	@Test
	public void testSetSpeedLowerThanZero() {
		loco.setSpeed(-200);
		Assert.assertEquals(0, loco.getSpeed());
	}

	@Test
	public void testSelfCannotBeAddedAsRemote() {
		loco.addRemoteLoco(loco);
		Assert.assertFalse(loco.getRemoteLocos().contains(loco));
	}

	@Test
	public void testGivenLocoCannotBeAddedTwice() {
		final Loco newLoco = new Loco(5, mockedConn);
		loco.addRemoteLoco(newLoco);
		loco.addRemoteLoco(newLoco);
		Assert.assertEquals(1, loco.getRemoteLocos().size());
	}

	@Test
	public void testFunctionGroup1FullOn() {
		loco.activateFunction("1");
		Assert.assertEquals(1, loco.getFunctionGroups()[0]);
		loco.activateFunction("2");
		Assert.assertEquals(3, loco.getFunctionGroups()[0]);
		loco.activateFunction("3");
		Assert.assertEquals(7, loco.getFunctionGroups()[0]);
		loco.activateFunction("4");
		Assert.assertEquals(15, loco.getFunctionGroups()[0]);
	}

	@Test
	public void testFunctionGroup1FullOnThenOff() {
		loco.activateFunction("1");
		Assert.assertEquals(1, loco.getFunctionGroups()[0]);
		loco.activateFunction("2");
		Assert.assertEquals(3, loco.getFunctionGroups()[0]);
		loco.activateFunction("3");
		Assert.assertEquals(7, loco.getFunctionGroups()[0]);
		loco.activateFunction("4");
		Assert.assertEquals(15, loco.getFunctionGroups()[0]);

		loco.deactivateFunction("4");
		Assert.assertEquals(7, loco.getFunctionGroups()[0]);
		loco.deactivateFunction("3");
		Assert.assertEquals(3, loco.getFunctionGroups()[0]);
		loco.deactivateFunction("2");
		Assert.assertEquals(1, loco.getFunctionGroups()[0]);
		loco.deactivateFunction("1");
		Assert.assertEquals(0, loco.getFunctionGroups()[0]);
	}

	@Test
	public void testFunctionGroup2FullOnThenOff() {
		loco.activateFunction("5");
		Assert.assertEquals(1, loco.getFunctionGroups()[1]);
		loco.activateFunction("6");
		Assert.assertEquals(3, loco.getFunctionGroups()[1]);
		loco.activateFunction("7");
		Assert.assertEquals(7, loco.getFunctionGroups()[1]);
		loco.activateFunction("8");
		Assert.assertEquals(15, loco.getFunctionGroups()[1]);

		loco.deactivateFunction("8");
		Assert.assertEquals(7, loco.getFunctionGroups()[1]);
		loco.deactivateFunction("7");
		Assert.assertEquals(3, loco.getFunctionGroups()[1]);
		loco.deactivateFunction("6");
		Assert.assertEquals(1, loco.getFunctionGroups()[1]);
		loco.deactivateFunction("5");
		Assert.assertEquals(0, loco.getFunctionGroups()[1]);
	}

	@Test
	public void testFunctionGroup3FullOnThenOff() {
		loco.activateFunction("9");
		Assert.assertEquals(1, loco.getFunctionGroups()[2]);
		loco.activateFunction("10");
		Assert.assertEquals(3, loco.getFunctionGroups()[2]);
		loco.activateFunction("11");
		Assert.assertEquals(7, loco.getFunctionGroups()[2]);
		loco.activateFunction("12");
		Assert.assertEquals(15, loco.getFunctionGroups()[2]);

		loco.deactivateFunction("12");
		Assert.assertEquals(7, loco.getFunctionGroups()[2]);
		loco.deactivateFunction("11");
		Assert.assertEquals(3, loco.getFunctionGroups()[2]);
		loco.deactivateFunction("10");
		Assert.assertEquals(1, loco.getFunctionGroups()[2]);
		loco.deactivateFunction("9");
		Assert.assertEquals(0, loco.getFunctionGroups()[2]);
	}

	@Test
	public void testFunctionGroups() {
		loco.activateFunction("1");
		Assert.assertEquals(1, loco.getFunctionGroups()[0]);
		Assert.assertEquals(0, loco.getFunctionGroups()[1]);
		Assert.assertEquals(0, loco.getFunctionGroups()[2]);
		loco.activateFunction("5");
		Assert.assertEquals(1, loco.getFunctionGroups()[0]);
		Assert.assertEquals(1, loco.getFunctionGroups()[1]);
		Assert.assertEquals(0, loco.getFunctionGroups()[2]);
		loco.activateFunction("6");
		Assert.assertEquals(1, loco.getFunctionGroups()[0]);
		Assert.assertEquals(3, loco.getFunctionGroups()[1]);
		Assert.assertEquals(0, loco.getFunctionGroups()[2]);
		loco.activateFunction("9");
		Assert.assertEquals(1, loco.getFunctionGroups()[0]);
		Assert.assertEquals(3, loco.getFunctionGroups()[1]);
		Assert.assertEquals(1, loco.getFunctionGroups()[2]);
	}

	@Test
	public void testFunctionGroup1AndLights() {
		loco.activateFunction("1");
		Assert.assertEquals(1, loco.getFunctionGroups()[0]);
		loco.turnLightsOn();
		Assert.assertEquals(17, loco.getFunctionGroups()[0]);
		loco.turnLightsOff();
		Assert.assertEquals(1, loco.getFunctionGroups()[0]);
	}
}
