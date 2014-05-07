package ygame.extension.with_third_party;

import org.jbox2d.dynamics.Fixture;

import ygame.framework.core.YABaseDomain;

public interface YIOnContactListener
{
	void beginContact(Fixture fixture, Fixture fixtureOther,
			YABaseDomain domainOther);

	void endContact(Fixture fixture, Fixture fixtureOther,
			YABaseDomain domainOther);
}
