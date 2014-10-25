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

import ygame.framework.core.YABaseDomain;
import ygame.framework.core.YISceneClockerPlugin;
import ygame.framework.core.YSystem;
import ygame.utils.YLog;

public class YWorld extends World implements YISceneClockerPlugin
{
	public YWorld(Vec2 gravity, IWorldPool pool, BroadPhase broadPhase,
			YSystem system)
	{
		super(gravity, pool, broadPhase);
		init(system);
	}

	public YWorld(Vec2 gravity, IWorldPool pool,
			BroadPhaseStrategy strategy, YSystem system)
	{
		super(gravity, pool, strategy);
		init(system);
	}

	public YWorld(Vec2 gravity, IWorldPool pool, YSystem system)
	{
		super(gravity, pool);
		init(system);
	}

	public YWorld(Vec2 gravity, YSystem system)
	{
		super(gravity);
		init(system);
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

	private void init(YSystem system)
	{
		setContactListener(new YWorldContactLsn(system));
	}

	private static class YWorldContactLsn implements ContactListener
	{
		private YSystem system;

		public YWorldContactLsn(YSystem system)
		{
			this.system = system;
		}

		@Override
		public void beginContact(Contact contact)
		{
			Fixture fixtureA = contact.getFixtureA();
			Fixture fixtureB = contact.getFixtureB();
			YIOnContactListener lsnA = fixtureA
					.getOnContactListener();
			YIOnContactListener lsnB = fixtureB
					.getOnContactListener();
//			if (null != lsnA)
//				lsnA.beginContact(fixtureA, fixtureB, fixtureB
//						.getBody().getDomain());
//			if (null != lsnB)
//				lsnB.beginContact(fixtureB, fixtureA, fixtureA
//						.getBody().getDomain());
			if (null != lsnA)
			{
				YABaseDomain domain = fixtureB.getBody()
						.getDomain();
				if (null == domain)
				{
					domain = system.queryDomainByKey(fixtureB
							.getBody()
							.getDomainKey());
					fixtureB.getBody().setDomain(domain);
				}
				lsnA.beginContact(fixtureA, fixtureB, domain , contact);
			}
			if (null != lsnB)
			{
				YABaseDomain domain = fixtureA.getBody()
						.getDomain();
				if (null == domain)
				{
					domain = system.queryDomainByKey(fixtureA
							.getBody()
							.getDomainKey());
					fixtureA.getBody().setDomain(domain);
				}
				lsnB.beginContact(fixtureB, fixtureA, domain , contact);
			}
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
			{
				YABaseDomain domain = fixtureB.getBody()
						.getDomain();
				if (null == domain)
				{
					domain = system.queryDomainByKey(fixtureB
							.getBody()
							.getDomainKey());
					fixtureB.getBody().setDomain(domain);
				}
				lsnA.endContact(fixtureA, fixtureB, domain , contact);
			}
			if (null != lsnB)
			{
				YABaseDomain domain = fixtureA.getBody()
						.getDomain();
				if (null == domain)
				{
					domain = system.queryDomainByKey(fixtureA
							.getBody()
							.getDomainKey());
					fixtureA.getBody().setDomain(domain);
				}
				lsnB.endContact(fixtureB, fixtureA, domain , contact);
			}
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
