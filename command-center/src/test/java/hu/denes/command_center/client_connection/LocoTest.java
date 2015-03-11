package hu.denes.command_center.client_connection;

import hu.denes.command_center.roco_connection.PrintoutConnection;
import junit.framework.Assert;

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
	PrintoutConnection mockedConn;

	@Before
	public void init() {
		loco = new Loco(1, mockedConn);
		loco.setMaxSpeed(128);
	}

	@Test
	public void testSpeedIsSetCorrectly() {
		loco.setSpeed(10);
		Mockito.verify(mockedConn).setSpeed(1, 10);
	}

	@Test
	public void testSpeedIsSetCorrectlyWhenRemoteLocos() {
		final Loco loco2 = new Loco(2, mockedConn);
		loco.addRemoteLoco(loco2);
		loco.setSpeed(50);
		Mockito.verify(mockedConn).setSpeed(1, 50);
		Mockito.verify(mockedConn).setSpeed(2, 50);
	}

	@Test
	public void testSetSpeedGreaterThanMax() {
		loco.setSpeed(200);
		Assert.assertEquals(128, loco.getSpeed());
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
	    	Loco newLoco = new Loco(5, mockedConn);
		loco.addRemoteLoco(newLoco);
		loco.addRemoteLoco(newLoco);
		Assert.assertEquals(1, loco.getRemoteLocos().size());
	}
}
