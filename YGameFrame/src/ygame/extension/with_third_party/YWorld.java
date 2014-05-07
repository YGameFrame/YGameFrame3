package ygame.extension.with_third_party;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.broadphase.BroadPhase;
import org.jbox2d.collision.broadphase.BroadPhaseStrategy;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.pooling.IWorldPool;

import ygame.framework.core.YISceneClockerPlugin;
import ygame.utils.YLog;

public class YWorld extends World implements YISceneClockerPlugin
{

	public YWorld(Vec2 gravity, IWorldPool pool, BroadPhase broadPhase)
	{
		super(gravity, pool, broadPhase);
		init();
	}

	public YWorld(Vec2 gravity, IWorldPool pool, BroadPhaseStrategy strategy)
	{
		super(gravity, pool, strategy);
		init();
	}

	public YWorld(Vec2 gravity, IWorldPool pool)
	{
		super(gravity, pool);
		init();
	}

	public YWorld(Vec2 gravity)
	{
		super(gravity);
		init();
	}

	@Override
	public void setContactListener(ContactListener listener)
	{
		if (bContactLsnSetted)
		{
			YLog.w(getClass().getName(), "不能再设置碰撞监听器");
			return;
		}
		bContactLsnSetted = true;
		super.setContactListener(listener);
	}

	private boolean bContactLsnSetted;

	private void init()
	{
		setContactListener(new YWorldContactLsn());
	}

	private static class YWorldContactLsn implements ContactListener
	{
		@Override
		public void beginContact(Contact contact)
		{
			Fixture fixtureA = contact.getFixtureA();
			Fixture fixtureB = contact.getFixtureB();

			YIOnContactListener lsnA = fixtureA
					.getOnContactListener();
			YIOnContactListener lsnB = fixtureB
					.getOnContactListener();
			if (null != lsnA)
				lsnA.beginContact(fixtureA, fixtureB, fixtureB
						.getBody().getDomain());
			if (null != lsnB)
				lsnB.beginContact(fixtureB, fixtureA, fixtureA
						.getBody().getDomain());
		}

		@Override
		public void endContact(Contact contact)
		{
			Fixture fixtureA = contact.getFixtureA();
			Fixture fixtureB = contact.getFixtureB();

			YIOnContactListener lsnA = fixtureA
					.getOnContactListener();
			YIOnContactListener lsnB = fixtureB
					.getOnContactListener();
			if (null != lsnA)
				lsnA.endContact(fixtureA, fixtureB, fixtureB
						.getBody().getDomain());
			if (null != lsnB)
				lsnB.endContact(fixtureB, fixtureA, fixtureA
						.getBody().getDomain());
		}

		@Override
		public void preSolve(Contact contact, Manifold oldManifold)
		{
		}

		@Override
		public void postSolve(Contact contact, ContactImpulse impulse)
		{
		}
	}

	@Override
	public void onClock(double dbDeltaTime_s)
	{
		step(1.0f / 60, 8, 3);
	}
}
